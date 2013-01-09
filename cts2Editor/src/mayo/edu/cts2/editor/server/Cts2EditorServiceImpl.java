package mayo.edu.cts2.editor.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertPathParameters;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.mayo.cts2.framework.core.client.Cts2RestClient;
import edu.mayo.cts2.framework.core.constants.URIHelperInterface;
import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.model.core.RoleReference;
import edu.mayo.cts2.framework.model.core.SourceAndRoleReference;
import edu.mayo.cts2.framework.model.core.SourceReference;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.core.types.EntryState;
import edu.mayo.cts2.framework.model.core.types.FinalizableState;
import edu.mayo.cts2.framework.model.service.core.UpdateChangeSetMetadataRequest;
import edu.mayo.cts2.framework.model.service.core.UpdatedChangeInstructions;
import edu.mayo.cts2.framework.model.service.core.UpdatedCreator;
import edu.mayo.cts2.framework.model.service.core.UpdatedState;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.SpecificEntityList;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionMsg;
import edu.mayo.cts2.framework.model.wsdl.valuesetdefinitionmaintenance.UpdateChangeSetMetadata;
import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.server.rest.Cts2Client;
import mayo.edu.cts2.editor.server.rest.EntityClient;
import mayo.edu.cts2.editor.shared.ValueSetDefinitionEntry;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * The server side implementation of the RPC service.
 */
public class Cts2EditorServiceImpl extends BaseEditorServlet implements Cts2EditorService {

	private static final long serialVersionUID = 1L;
	private static final String XML_ROOT = "ValueSetCatalogEntryMsgList";
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	private static final String XML_ROOT_START = "<" + XML_ROOT + ">\n";
	private static final String XML_ROOT_END = "</" + XML_ROOT + ">\n";
	private static Logger logger = Logger.getLogger(Cts2EditorServiceImpl.class.getName());

	public static final String XPATH_VALUESETS_BASE = "/" + XML_ROOT;

	private final int MAX_RECORDS = 100;

	public Cts2EditorServiceImpl() {
		super();
	}

	@Override
	public String getValueSet(String oid) throws IllegalArgumentException {
		return getCts2Client().getValueSet(getAuthorizationHeader(), oid);
	}

	@Override
	/**
	 * For each OID in the list, return the XML representation of the value sets.
	 */
	public String getValueSets(List<String> oids) throws IllegalArgumentException {
		StringBuilder sb = new StringBuilder(XML_HEADER + XML_ROOT_START);

		for (String oid : oids) {
			String xmlResponse = getCts2Client().getValueSet(getAuthorizationHeader(), oid);
			int end = xmlResponse.indexOf(XML_HEADER) + XML_HEADER.length();
			xmlResponse = xmlResponse.substring(end);
			sb.append(xmlResponse);
		}
		sb.append(XML_ROOT_END);

		return sb.toString();
	}

	@Override
	public String getValueSetDefinition(String oid) throws IllegalArgumentException {
		return getValueSetDefinition(oid, "1");
	}

	public String getValueSetDefinition(String oid, String version) throws IllegalArgumentException {
		return getCts2Client().getValueSetDefinition(getAuthorizationHeader(), oid, version);
	}

	@Override
	public String getResolvedValueSet(String oid) throws IllegalArgumentException {
		return getCts2Client().getResolvedValueSet(getAuthorizationHeader(), oid, "1", MAX_RECORDS);
	}

	@Override
	public String getDefinitions(String oid) throws IllegalArgumentException {
		return getCts2Client().getDefinitions(getAuthorizationHeader(), oid, MAX_RECORDS);
	}

	@Override
	public String getMatchingValueSets(String matchValue) throws IllegalArgumentException {
		return getCts2Client().getValueSets(getAuthorizationHeader(), MAX_RECORDS, matchValue);
	}

	@Override
	public String createChangeSet() {
		ClientResponse<String> response = getCts2Client().createChangeSet(getAuthorizationHeader());
		String uri = null;
		if (response.getStatus() == 201) {
			uri = response.getHeaders().get("location").get(0);
			uri = uri.substring(uri.lastIndexOf("/") + 1);
		}
		response.releaseConnection();
		return uri;
	}

	@Override
	public String deleteChangeSet(String uri) throws IllegalArgumentException {
		return getCts2Client().deleteChangeSet(getAuthorizationHeader(), uri);
	}

	@Override
	public String getChangeSet(String uri) throws IllegalArgumentException {
		return getCts2Client().getChangeSet(getAuthorizationHeader(), uri);
	}

//	@Override
//	public String updateChangeSet(String uri, UpdateChangeSetMetadataRequest metadataRequest) throws IllegalArgumentException {
//		return null;
//	}

	@Override
	public String getMatchingEntities(String matchValue) throws IllegalArgumentException {
		return getEntityClient().getMatchingEntities(getAuthorizationHeader(), MAX_RECORDS, matchValue);
	}

	private String getAuthorizationHeader() {
		return "Basic "
		        + Base64.encodeBytes((getCts2ValueSetRestUsername() + ":" + getCts2ValueSetRestPassword()).getBytes());
	}

	private Cts2Client getCts2Client() {
		return ProxyFactory.create(Cts2Client.class, getCts2ValueSetRestUrl());
	}

	private EntityClient getEntityClient() {
		return ProxyFactory.create(EntityClient.class, getEntityRestUrl());
	}

	@Override
	public String saveValueSet(String valueSetDefinitionId, String creator, String description,
	                           List<ValueSetDefinitionEntry> addedEntries,
	                           List<ValueSetDefinitionEntry> removedEntries) {
		/* get ValueSetDefinintion
		 * if state = final
		 *     saveValuesetas
		 * else
		 *      create changeset => update
		 *      update definition
		 */

		String valueSetDefinitionXml = getValueSetDefinition(valueSetDefinitionId);
		ValueSetDefinition definition = unmarshallValueSetDefinition(valueSetDefinitionXml);

		if (definition.getState().equals(FinalizableState.FINAL)) {
			saveValueSetAs(definition, creator, description, addedEntries, removedEntries);
		} else {

		}
		/*  state: FINAL | OPEN
			creator: username
			about: user entered */

		/* entities: uri, href, namespace, name */

		return null;
	}

	@Override
	public String saveValueSetAs(String parentValueSetDefinitionId,
	                             String creator, String description,
	                             List<ValueSetDefinitionEntry> addedEntries,
	                             List<ValueSetDefinitionEntry> removedEntries) {
		String result = null;
		String valueSetDefinitionXml = getValueSetDefinition(parentValueSetDefinitionId);
		ValueSetDefinition definition = unmarshallValueSetDefinition(valueSetDefinitionXml);

		if (definition != null) {
			if (saveValueSetAs(definition, creator, description, addedEntries, removedEntries)) {
				result = getValueSetDefinition(parentValueSetDefinitionId, definition.getDocumentURI());
			}
		}

		return result;
	}

	@Override
	public String getUserDefinitions(String oid, String username) throws IllegalArgumentException {
		return getCts2Client().getUserDefinitions(getAuthorizationHeader(), oid, username, 100);
	}

	@Override
	public void updateChangeSet(String uri, String creator, String changeInstructions) {
		UpdateChangeSetMetadataRequest metadata = new UpdateChangeSetMetadataRequest();

		UpdatedCreator updatedCreator = new UpdatedCreator();
		updatedCreator.setCreator(ModelUtils.nameOrUriFromName(creator));
		metadata.setUpdatedCreator(updatedCreator);

		UpdatedChangeInstructions updatedChangeInstructions = new UpdatedChangeInstructions();
		updatedChangeInstructions.setChangeInstructions(ModelUtils.createOpaqueData(changeInstructions));
		metadata.setUpdatedChangeInstructions(updatedChangeInstructions);

		UpdatedState state = new UpdatedState();
		state.setState(FinalizableState.OPEN);
		metadata.setUpdatedState(state);

		Cts2RestClient restClient = Cts2RestClient.instance();
		restClient.postCts2Resource(getCts2ValueSetRestUrl() + "/changeset/" + uri, "dsuesse", "cts2Mayo()", metadata);
	}

	private boolean saveValueSet(ValueSetDefinition definition, String creator, String description) {
		boolean result = false;
		String changeSetUri = createChangeSet();
		updateChangeSet(changeSetUri, creator, description);

		if (changeSetUri != null) {
			try {
				persistValueSetToService(definition, changeSetUri);
				result = true;
			}
			catch (Exception e) {
				logger.log(Level.WARNING, "Unable to persist the value set definition.", e);
				result = false;
			}
		}
		return result;
	}

	private boolean saveValueSetAs(ValueSetDefinition parentDefinition,
	                               String creator, String description,
	                               List<ValueSetDefinitionEntry> addedEntries,
	                               List<ValueSetDefinitionEntry> removedEntries) {
		boolean result = false;
		String changeSetUri = createChangeSet();
		updateChangeSet(changeSetUri, creator, description);

		if (changeSetUri != null) {
			/* todo: clone value set definition instead */
			parentDefinition.setDocumentURI(UUID.randomUUID().toString());
			parentDefinition.setVersionTag(
			  Collections.singletonList(
			    new VersionTagReference(UUID.randomUUID().toString())));
			parentDefinition.setState(FinalizableState.OPEN);
			parentDefinition.setEntryState(EntryState.ACTIVE);

			SourceReference source = new SourceReference();
			source.setContent(creator);
			SourceAndRoleReference sourceAndRole = new SourceAndRoleReference();
			sourceAndRole.setSource(source);
			RoleReference role = new RoleReference();
			role.setContent("creator");
			role.setUri("http://purl.org/dc/elements/1.1/creator");
			sourceAndRole.setRole(role);
			parentDefinition.setSourceAndRole(new SourceAndRoleReference[]{ sourceAndRole });

			/* Remove entities */
			edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry[] currentEntries = parentDefinition.getEntry();
			for (ValueSetDefinitionEntry entry : removedEntries) {
				for (edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry e : currentEntries) {
//					if (e.getCompleteCodeSystem().getCodeSystem().getContent().equals(entry.getNamespace())) {
						SpecificEntityList entityList = e.getEntityList();
						for (URIAndEntityName entity : entityList.getReferencedEntity()) {
							if (entity.getName().equalsIgnoreCase(entry.getName()) &&
							  entity.getNamespace().equalsIgnoreCase(entry.getNamespace())) {
								entityList.removeReferencedEntity(entity);
							}
						}
						e.setEntityList(entityList);
//					}
				}
			}

			/* Add entities */
			for (ValueSetDefinitionEntry entry : addedEntries) {
				boolean added = false;
				for (edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry e : currentEntries) {
//					if (e.getCompleteCodeSystem().getCodeSystem().getContent().equals(entry.getNamespace())) {
						SpecificEntityList entityList = e.getEntityList();
						URIAndEntityName entityToAdd = new URIAndEntityName();
						entityToAdd.setHref(entry.getHref());
						entityToAdd.setName(entry.getName());
						entityToAdd.setNamespace(entry.getNamespace());
						entityToAdd.setUri(entry.getUri());
						entityList.addReferencedEntity(entityToAdd);
						e.setEntityList(entityList);
						added = true;
						break;
//					}
				}
//				if (!added) {
//					/* TODO create new edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry and
//					 * add entity */
//				}
			}
			parentDefinition.setEntry(currentEntries);

			try {
				result = persistValueSetToService(parentDefinition, changeSetUri);
			}
			catch (Exception e) {
				logger.log(Level.WARNING, "Unable to persist the value set definition.", e);
				result = false;
			}
		}
		return result;
	}

	private boolean persistValueSetToService(ValueSetDefinition definition, String changeSetUri) throws Exception {
		Cts2RestClient restClient = Cts2RestClient.instance();
		URI uri = restClient.postCts2Resource(getCts2ValueSetRestUrl() + URIHelperInterface.PATH_VALUESETDEFINITION + "?" +
		  URIHelperInterface.PARAM_CHANGESETCONTEXT + "=" + changeSetUri,
		  getCts2ValueSetRestUsername(),
		  getCts2ValueSetRestPassword(),
		  definition);
		return uri != null;
	}

	private ValueSetDefinition unmarshallValueSetDefinition(String xml) {
		DelegatingMarshaller marshaller = new DelegatingMarshaller();
		StringReader reader = new StringReader(xml);
		ValueSetDefinitionMsg message = null;
		ValueSetDefinition definition = null;
		try {
			message = (ValueSetDefinitionMsg) marshaller.unmarshal(new StreamSource(reader));
			if (message != null) {
				definition = message.getValueSetDefinition();
			}
		} catch (IOException ioe) {
			logger.warning("Unable to unmarshal the returned value set definition xml. Message: " + ioe.getMessage()) ;
		}
		return definition;
	}

}
