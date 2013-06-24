package mayo.edu.cts2.editor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntry;
import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.server.rest.Cts2Client;
import mayo.edu.cts2.editor.server.rest.EntityClient;
import mayo.edu.cts2.editor.shared.CTS2Result;
import mayo.edu.cts2.editor.shared.Definition;
import mayo.edu.cts2.editor.shared.DefinitionEntry;

import mayo.edu.cts2.editor.shared.MetadataResult;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.util.Base64;

import edu.mayo.cts2.framework.core.client.Cts2RestClient;
import edu.mayo.cts2.framework.core.constants.URIHelperInterface;
import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.model.core.ChangeableElementGroup;
import edu.mayo.cts2.framework.model.core.Comment;
import edu.mayo.cts2.framework.model.core.RoleReference;
import edu.mayo.cts2.framework.model.core.SourceAndNotation;
import edu.mayo.cts2.framework.model.core.SourceAndRoleReference;
import edu.mayo.cts2.framework.model.core.SourceReference;
import edu.mayo.cts2.framework.model.core.TsAnyType;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.core.ValueSetReference;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.core.types.FinalizableState;
import edu.mayo.cts2.framework.model.core.types.NoteType;
import edu.mayo.cts2.framework.model.core.types.SetOperator;
import edu.mayo.cts2.framework.model.service.core.UpdateChangeSetMetadataRequest;
import edu.mayo.cts2.framework.model.service.core.UpdatedChangeInstructions;
import edu.mayo.cts2.framework.model.service.core.UpdatedCreator;
import edu.mayo.cts2.framework.model.service.core.UpdatedState;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.SpecificEntityList;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionMsg;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The server side implementation of the RPC service.
 */
public class Cts2EditorServiceImpl extends BaseEditorServlet implements Cts2EditorService {

	private static final long serialVersionUID = 5L;
	private static final String XML_ROOT = "ValueSetCatalogEntryMsgList";
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	private static final String XML_ROOT_START = "<" + XML_ROOT + ">\n";
	private static final String XML_ROOT_END = "</" + XML_ROOT + ">\n";
	private static Logger logger = Logger.getLogger(Cts2EditorServiceImpl.class.getName());
	private Map<String, String> serviceProperties = new HashMap<String, String>();

	private final int MAX_RECORDS = Cts2EditorServiceProperties.getValueSetRestPageSize();

	public Cts2EditorServiceImpl() {
		super();
	}

	/**
	 * Valid properties:
	 * MaintenanceUrl = url to the CTS2 value set definition maintenance service
	 * MaintenanceUsername = username for the CTS2 value set definition maintenance service
	 * MaintenancePassword = password for the CTS2 value set definition maintenance service
	 * EntityUrl = url to the CTS2 entity service
	 *
	 * @param serviceProperties map containing the properties
	 * @throws IllegalArgumentException
	 */
	@Override
	public void setServiceProperties(Map<String, String> serviceProperties) throws IllegalArgumentException {
		if (serviceProperties == null)
			throw new IllegalArgumentException();
		this.serviceProperties = serviceProperties;
	}

	@Override
	public void addServiceProperty(String property, String value) throws IllegalArgumentException {
		if (property == null && value == null)
			throw new IllegalArgumentException();
		this.serviceProperties.put(property, value);
	}

	/**
	 * Returns the xml representation of the value set
	 * 
	 * @param oid
	 *            id of the value set to return
	 * @return the xml representation of the value set
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String getValueSet(String oid) throws IllegalArgumentException {
		if (oid == null) {
			throw new IllegalArgumentException("Argument can not be null.");
		}
		return getCts2Client().getValueSet(getAuthorizationHeader(), oid);
	}

	/**
	 * Returns the xml representation of the value sets in <code>oids</code>
	 * 
	 * @param oids
	 *            ids of the value sets to fetch
	 * @return xml representation of the value sets in <code>oids</code>
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String getValueSets(List<String> oids) throws IllegalArgumentException {
		if (oids == null) {
			throw new IllegalArgumentException("Argument can not be null.");
		}

		/*
		 * For each OID in the list, return the XML representation of the value
		 * sets.
		 */
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

	/**
	 * Returns the current definition for the value set
	 * 
	 * @param oid
	 *            id of the value set
	 * @return the current definition for the value set
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String getValueSetDefinition(String oid) throws IllegalArgumentException {
		if (oid == null) {
			throw new IllegalArgumentException("Argument can not be null.");
		}
		return getValueSetDefinition(oid, "1");
	}

	/**
	 * Returns the value set definition for the value set oid and definition
	 * version
	 * 
	 * @param oid
	 *            id of the value set
	 * @param version
	 *            id of the version
	 * @return value set definition matching the value set oid and definition
	 *         version
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	public String getValueSetDefinition(String oid, String version) throws IllegalArgumentException {
		if (oid == null || version == null) {
			throw new IllegalArgumentException("Argument can not be null.");
		}
		return getCts2Client().getValueSetDefinition(getAuthorizationHeader(), oid, version);
	}

	/**
	 * Returns the entities for the current version of the value
	 * @param oid id of the value set to fetch the current version for
	 * @param version of the value set definition
	 * @param changeSetUri id of the change set to apply
	 * @return Returns the entities for the value set. If version is <code>null</code> returns the entities for the current version. If the changeSetUri is <code>null</code> returns the enitites with no change set applied.
	 * @throws IllegalArgumentException if any argument is <code>null</code>
	 */
	@Override
	public String getResolvedValueSet(String oid, String version, String changeSetUri) throws IllegalArgumentException {
		if (oid == null) {
			throw new IllegalArgumentException("Argument can not be null.");
		}
		version = version == null || version.equals("") ? "1" : version;

		if (changeSetUri == null || changeSetUri.equals("")) {
			return getCts2Client().getResolvedValueSet(getAuthorizationHeader(), oid, version, MAX_RECORDS);
		} else {
			return getCts2Client().getResolvedValueSet(getAuthorizationHeader(), oid, version, changeSetUri,
			        MAX_RECORDS);
		}
	}

	/**
	 * Returns all the definitions of a value set
	 * 
	 * @param oid
	 *            id of the value set to fetch the definitions for
	 * @return the definitions of the value set
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String getDefinitions(String oid) throws IllegalArgumentException {
		if (oid == null) {
			throw new IllegalArgumentException("Argument can not be null.");
		}
		return getCts2Client().getDefinitions(getAuthorizationHeader(), oid, MAX_RECORDS);
	}

	/**
	 * Returns value sets matching the match value criteria
	 * 
	 * @param matchValue the value to search for
	 * @return value sets matching the match value criteria
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String getMatchingValueSets(String matchValue) throws IllegalArgumentException {
		if (matchValue == null) {
			throw new IllegalArgumentException("Argument can not be null.");
		}
		return getCts2Client().getValueSets(getAuthorizationHeader(), MAX_RECORDS, matchValue);
	}

	/**
	 * Creates a change set with the CTS2 service
	 * 
	 * @return the id of the created change set
	 */
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

	/**
	 * Marks a change set for deletion with the CTS2 service
	 * 
	 * @param uri
	 *            id of the change set to marked for deletion
	 * @return the updated change set marked for deletion
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String deleteChangeSet(String uri) throws IllegalArgumentException {
		if (uri == null) {
			throw new IllegalArgumentException("Argument can not be null.");
		}
		return getCts2Client().deleteChangeSet(getAuthorizationHeader(), uri);
	}

	/**
	 * Returns the change set.
	 * 
	 * @param uri
	 *            id of the change set
	 * @return the change set
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String getChangeSet(String uri) throws IllegalArgumentException {
		return getCts2Client().getChangeSet(getAuthorizationHeader(), uri);
	}

	/**
	 * Returns the xml representation of the matching entities
	 * 
	 * @param codeSystem code system to search
	 * @param codeSystemVersion code system version to search
	 * @param matchValue the value to find
	 * @return the xml representation of the matching entities
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String getMatchingEntities(String codeSystem, String codeSystemVersion, String matchValue) throws IllegalArgumentException {
		if (matchValue == null) {
			throw new IllegalArgumentException("Argument can not be null.");
		}
		return getEntityClient().getMatchingEntities(getAuthorizationHeader(), codeSystem, codeSystemVersion, matchValue, MAX_RECORDS);
	}

	/**
	 * 
	 * @param definition
	 *            to save
	 * @return the results
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public CTS2Result saveDefinition(Definition definition) throws IllegalArgumentException {
		if (definition == null)
			throw new IllegalArgumentException("Argument can not be null.");

		CTS2Result result = new CTS2Result();
		if (isFinal(definition)) {
			result.setError(true);
			result.setMessage("The value set definition is final and can not be updated.");
		} else {
			result = saveValueSet(toValueSetDefinition(definition));
		}
		return result;
	}

	/**
	 * 
	 * @param definition
	 *            to clone and save
	 * @return the results
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public CTS2Result saveDefinitionAs(Definition definition) throws IllegalArgumentException {
		if (definition == null)
			throw new IllegalArgumentException("Argument can not be null.");

		/* Set new random version */
		definition.setVersion(UUID.randomUUID().toString());
		return saveValueSetAs(toValueSetDefinition(definition));
	}

	/**
	 * Returns the definitions for a value set by a specific creator.
	 * 
	 * @param oid
	 *            id of the value set
	 * @param username
	 *            id of the creator
	 * @return the xml representation of user's definitions
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String getUserDefinitions(String oid, String username) throws IllegalArgumentException {
		if (oid == null || username == null)
			throw new IllegalArgumentException("Arguments can not be null.");

		return getCts2Client().getUserDefinitions(getAuthorizationHeader(), oid, "creator", username, 5000);
	}

	/**
	 * Returns the definitions for a list of value sets by a specific creator.
	 * 
	 * @param oids
	 *            list of ids that represent the value set
	 * @param username
	 *            id of the creator
	 * @return the xml representation of user's definitions
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String getUserDefinitions(List<String> oids, String username) throws IllegalArgumentException {
		if (oids == null || username == null)
			throw new IllegalArgumentException("Arguments can not be null.");

		String test = "";

		for (String oid : oids) {
			test = getCts2Client().getUserDefinitions(getAuthorizationHeader(), oid, "creator", username, 5000);
			// System.out.print(test);
		}

		return test;
	}

	/**
	 * Returns a specific value set definition.
	 * 
	 * @param oid
	 *            id of the value set
	 * @param changeSetUri
	 *            id of the change set to apply
	 * @return the value set definition version
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public String getDefinition(String oid, String version, String changeSetUri) throws IllegalArgumentException {
		if (oid == null || version == null)
			throw new IllegalArgumentException("Oid and version can not be null.");
		if (changeSetUri == null || changeSetUri.equals("")) {
			return getCts2Client().getDefinition(getAuthorizationHeader(), oid, version);
		} else {
			return getCts2Client().getDefinition(getAuthorizationHeader(), oid, version, changeSetUri);
		}
	}

	/**
	 * Updates the change set metadata
	 * 
	 * @param uri
	 *            id of the change set
	 * @param creator
	 *            updated creator
	 * @param changeInstructions
	 *            updated change instructions
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public void updateChangeSet(String uri, String creator, String changeInstructions) throws IllegalArgumentException {
		if (uri == null || creator == null || changeInstructions == null)
			throw new IllegalArgumentException("Arguments can not be null.");

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
		restClient.postCts2Resource(Cts2EditorServiceProperties.getValueSetDefinitionMaintenanceUrl() + "/changeset/"
		        + uri, Cts2EditorServiceProperties.getCts2ValueSetRestUsername(),
		        Cts2EditorServiceProperties.getCts2ValueSetRestPassword(), metadata);
	}

	/**
	 * Returns <code>true</code> if the definition version is marked FINAL.
	 * Returns <code>false</code> if the definition version is not marked FINAL.
	 * 
	 * @param definition to determine state of
	 * @return <code>true</code> if the definition version is marked FINAL,
	 *         <code>false</code> if the definition version is not marked FINAL
	 * @throws IllegalArgumentException
	 *             if any argument is <code>null</code>
	 */
	@Override
	public boolean isFinal(Definition definition) throws IllegalArgumentException {
		if (definition == null)
			throw new IllegalArgumentException("Arguments can not be null.");

		String definitionXml = getCts2Client().getDefinition(getAuthorizationHeader(), definition.getValueSetOid(),
		        definition.getVersion(), definition.getChangeSetUri());
		ValueSetDefinition vsDefinition = unmarshallValueSetDefinition(definitionXml);
		return vsDefinition.getState() == FinalizableState.FINAL;
	}

	@Override
	public String[] getCodeSystems() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("CPT");
		list.add("SNOMEDCT");
		list.add("ICD09");
		list.add("ICD10");
		list.add("ICD10CM");
		list.add("ICD10PCS");
		list.add("ICD9CM");
		list.add("LOINC");
		list.add("RXNORM");
		list.add("NDFRT");
		/* TODO: Re-enable the actual service */
//		String codeSystemsXml = getEntityClient().getCodeSystems(getAuthorizationHeader(), 1000);
//		try {
//			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder db = dbFactory.newDocumentBuilder();
//			Document document = db.parse(new InputSource(new ByteArrayInputStream(codeSystemsXml.getBytes("UTF-8"))));
//			XPath xPath = XPathFactory.newInstance().newXPath();
//			NodeList entries = (NodeList) xPath.evaluate("/CodeSystemCatalogEntryDirectory/entry", document, XPathConstants.NODESET);
//			for (int i = 0; i< entries.getLength(); i++) {
//				Node entry = entries.item(i);
//				if (entry.getNodeType() == Node.ELEMENT_NODE) {
//					String codeSystem = entry.getAttributes().getNamedItem("resourceName").getTextContent();
//					if (codeSystem != null && !codeSystem.isEmpty() && !list.contains(codeSystem)) {
//						list.add(codeSystem.toString());
//					}
//				}
//			}
//		} catch (ParserConfigurationException e) {
//			logger.log(Level.WARNING, "Unable to parse the returned xml.", e);
//
//		} catch (SAXException e) {
//			logger.log(Level.WARNING, "Unable to parse the returned xml.", e);
//
//		} catch (IOException e) {
//			logger.log(Level.WARNING, "Unable to parse the returned xml.", e);
//
//		}
//		catch (XPathExpressionException e) {
//			logger.log(Level.WARNING, "Unable to parse the returned xml.", e);
//
//		}
		Collections.sort(list);
		return list.toArray(new String[list.size()]);
	}



	@Override
	public String[] getCodeSystemVersions(String codeSystem) throws IllegalArgumentException {
		String codeSystemVersionsXml = getEntityClient().getCodeSystemVersions(getAuthorizationHeader(), codeSystem, 1000);
		ArrayList<String> list = new ArrayList<String>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbFactory.newDocumentBuilder();
			Document document = db.parse(new InputSource(new ByteArrayInputStream(codeSystemVersionsXml.getBytes("UTF-8"))));
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList entries = (NodeList) xPath.evaluate("/CodeSystemVersionCatalogEntryDirectory/entry", document, XPathConstants.NODESET);
			for (int i = 0; i< entries.getLength(); i++) {
				Node entry = entries.item(i);
				if (entry.getNodeType() == Node.ELEMENT_NODE) {
					NamedNodeMap attributes = entry.getAttributes();
					String version = attributes.getNamedItem("codeSystemVersionName").getTextContent();
					if (version != null && !version.isEmpty() && !list.contains(version)) {
						list.add(version);
//						NodeList properties = entry.getChildNodes();
//						for (int j = 0; j < properties.getLength(); j++) {
//							Node property = properties.item(j);
//							if (property.getNodeName().equals("core:officialResourceVersionId")) {
//								String officialResourceId = property.getTextContent();
//								if (officialResourceId != null && !officialResourceId.isEmpty()) {
//									list.add(version);
//									break;
//								}
//							}
//						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			logger.log(Level.WARNING, "Unable to parse the returned xml.", e);

		} catch (SAXException e) {
			logger.log(Level.WARNING, "Unable to parse the returned xml.", e);

		} catch (IOException e) {
			logger.log(Level.WARNING, "Unable to parse the returned xml.", e);

		}
		catch (XPathExpressionException e) {
			logger.log(Level.WARNING, "Unable to parse the returned xml.", e);

		}
		Collections.sort(list);
		return list.toArray(new String[list.size()]);
	}

	@Override
	public CTS2Result createValueSet(Definition definition) throws IllegalArgumentException {
		/* todo create vs and definition and pass to rest like saveAs
		    createChangeSet
			createValueSet(definition, changeSet)
			createChangeSet 2
			createValueSetDefinition(definition, changeSet)
		*/
		CTS2Result result = new CTS2Result();
		try {
			String changeSetUri = createChangeSet();
			ValueSetCatalogEntry valueSet = new ValueSetCatalogEntry();
			valueSet.setValueSetName(definition.getValueSetOid());
			valueSet.setFormalName(definition.getFormalName());
			valueSet.setAbout(definition.getValueSetUri());

			SourceAndRoleReference snrr = new SourceAndRoleReference();
			snrr.setRole(new RoleReference("Author"));
			snrr.setSource(new SourceReference(definition.getCreator()));
			valueSet.addSourceAndRole(snrr);

			Cts2RestClient restClient = Cts2RestClient.instance();
			String url = Cts2EditorServiceProperties.getValueSetDefinitionMaintenanceUrl() + "/valueset?changesetcontext=" + changeSetUri;
			URI uri = restClient.postCts2Resource(url,
			  Cts2EditorServiceProperties.getCts2ValueSetRestUsername(),
			  Cts2EditorServiceProperties.getCts2ValueSetRestPassword(),
			  valueSet);
			if (uri == null) {
				result.setError(true);
				result.setMessage("The value set could not be created.");
			}

			if (definition.getName() != null && !definition.getName().trim().isEmpty()
			  && definition.getVersion() != null && !definition.getVersion().trim().isEmpty()
			  && definition.getEntries().size() > 0) {
				saveDefinitionAs(definition);
			}

		} catch (Exception e) {
			result.setError(true);
			result.setMessage("An error occurred when attempting to create the value set.");
		} finally {
			return result;
		}
	}

	@Override
	public MetadataResult checkNewValueSetMetadata(String valueSetName, String valueSetUri, String definitionName, String definitionVersion) throws IllegalArgumentException {
		if (valueSetName == null || valueSetUri == null || definitionName == null || definitionVersion == null)
			throw new IllegalArgumentException();
		MetadataResult result = new MetadataResult();

		try {
			String xml = getCts2Client().getValueSet(getAuthorizationHeader(), valueSetName);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbFactory.newDocumentBuilder();
			Document document = db.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
			XPath xPath = XPathFactory.newInstance().newXPath();
			String n = xPath.evaluate("/ValueSetCatalogEntryMsg", document);
			result.setExistingValueSetName(n != null);
		} catch (Exception e) {
			if (e instanceof ClientResponseFailure) {
				/* 404 is expected if the value set does not exist */
				if (((ClientResponseFailure) e).getResponse().getStatus() != 404) {
					result.setError(true);
					result.addMessage("An unexpected response was received while validating the new value set name.");
				}
			} else {
				result.setError(true);
				result.addMessage("An error occurred while validating the new value set name. " + e.getMessage());
			}
		} finally {
			return result;
		}

	}

	private ValueSetDefinition toValueSetDefinition(Definition definition) {
		ValueSetDefinition vsd = new ValueSetDefinition();
		ValueSetReference valueSetReference = new ValueSetReference(definition.getValueSetOid());
		valueSetReference.setUri(definition.getValueSetUri());
		vsd.setDefinedValueSet(valueSetReference);
		vsd.setDocumentURI(definition.getDocumentUri() == null || definition.getDocumentUri().trim().equals("") ? UUID
		  .randomUUID().toString() : definition.getDocumentUri());
		vsd.setAbout(definition.getAbout());
		vsd.setFormalName(definition.getFormalName());


		EntryDescription desc = new EntryDescription();
		desc.setValue(ModelUtils.toTsAnyType(definition.getResourceSynopsis()));
		vsd.setResourceSynopsis(desc);

		VersionTagReference[] references = new VersionTagReference[]{new VersionTagReference(definition.getVersion())};
		vsd.setVersionTag(references);
		vsd.setState(FinalizableState.OPEN);
		vsd.setOfficialReleaseDate(Calendar.getInstance().getTime());

		ChangeableElementGroup group = new ChangeableElementGroup();
		vsd.setChangeableElementGroup(group);

		SourceAndRoleReference snrr = new SourceAndRoleReference();
		SourceReference sourceReference = new SourceReference();
		sourceReference.setContent(definition.getCreator());
		RoleReference roleReference = new RoleReference();
		roleReference.setContent("creator");
		roleReference.setUri("http://purl.org/dc/elements/1.1/creator");
		snrr.setSource(sourceReference);
		snrr.setRole(roleReference);
		SourceAndRoleReference[] sourceAndRoleReferences = new SourceAndRoleReference[]{snrr};
		vsd.setSourceAndRole(sourceAndRoleReferences);

		Comment comment = new Comment();
		comment.setType(NoteType.EDITORIALNOTE);
		TsAnyType anyType = new TsAnyType();
		anyType.setContent(definition.getNote());
		comment.setValue(anyType);
		vsd.setNote(new Comment[]{comment});

		SourceAndNotation snn = new SourceAndNotation();
		snn.setSourceDocument("");
		vsd.setSourceAndNotation(snn);

		edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry entry = new edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry();
		entry.setOperator(SetOperator.UNION);
		entry.setEntryOrder(1L);
		entry.setEntityList(createEntityList(definition.getEntries()));
		vsd.setEntry(new edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry[]{entry});

		return vsd;
	}

	private SpecificEntityList createEntityList(List<DefinitionEntry> definitionEntries) {
		SpecificEntityList entityList = new SpecificEntityList();

		for (DefinitionEntry definitionEntry : definitionEntries) {
			URIAndEntityName entity = new URIAndEntityName();
			entity.setName(definitionEntry.getName());
			entity.setNamespace(definitionEntry.getNamespace());
			entity.setUri(definitionEntry.getUri());
			entity.setHref(definitionEntry.getHref());
			/* TODO: Need to set the description in the service, waiting for the updated spec:
			 * https://github.com/cts2/cts2-specification/issues/143 */
//			entity.setDesignation(definitionEntry.getDescription());
//			entity.setNamespaceVersion(definitionEntry.getCodeSystemVersion());
			entityList.addReferencedEntity(entity);
		}

		return entityList;
	}

	private CTS2Result saveValueSet(ValueSetDefinition definition) {
		CTS2Result result = new CTS2Result();

		String changeSetUri = createChangeSet();
		if (changeSetUri != null) {
			updateChangeSet(changeSetUri, definition.getSourceAndRole(0).getSource().getContent(), definition
			        .getNote(0).getValue().getContent());
			try {
				if (saveToService(definition, changeSetUri)) {
					result.setChangeSetUri(changeSetUri);
					result.setValueSetOid(definition.getDefinedValueSet().getContent());
					result.setValueSetVersion(definition.getVersionTag(0).getContent());

					String doc = getCts2Client().getDefinition(getAuthorizationHeader(), result.getValueSetOid(), result.getValueSetVersion(), result.getChangeSetUri());
					String start = "documentURI=\"";
					int startIdx = doc.indexOf(start) + start.length();
					doc = doc.substring(startIdx, startIdx + 37);
					result.setDocumentUri(doc);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Unable to persist the value set definition.", e);
				result.setError(true);
				result.setMessage("Failed to persist the definition. Error: " + e.getMessage());
			}
		} else {
			result.setError(true);
			result.setMessage("An error occurred while creating the change set.");
		}
		return result;
	}

	private CTS2Result saveValueSetAs(ValueSetDefinition definition) {
		CTS2Result result = new CTS2Result();
		String changeSetUri = createChangeSet();

		if (changeSetUri != null) {
			updateChangeSet(changeSetUri, definition.getSourceAndRole(0).getSource().getContent(), definition
			        .getNote(0).getValue().getContent());

			try {
				if (saveAsToService(definition, changeSetUri)) {
					result.setChangeSetUri(changeSetUri);
					result.setValueSetOid(definition.getDefinedValueSet().getContent());
					result.setDocumentUri(definition.getDocumentURI());
					result.setValueSetVersion(definition.getVersionTag(0).getContent());
				} else {
					result.setError(true);
					result.setMessage("An error occurred while saving the definition to the service.");
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Unable to persist the value set definition.", e);
				result.setError(true);
				result.setMessage("Failed to persist the definition. Error: " + e.getMessage());
			}
		} else {
			result.setError(true);
			result.setMessage("An error occurred while creating the change set.");
		}
		return result;
	}

	private boolean saveToService(ValueSetDefinition definition, String changeSetUri) throws Exception {
		/*
		 * "PUT
		 * /valueset/{oid}/definition/{version}?changesetcontext={changeSetUri}
		 */
		Cts2RestClient restClient = Cts2RestClient.instance();
		String url = Cts2EditorServiceProperties.getValueSetDefinitionMaintenanceUrl() + "/valueset/"
		        + definition.getDefinedValueSet().getContent() + "/definition/"
		        + definition.getVersionTag(definition.getVersionTag().length - 1).getContent() + "?"
		        + URIHelperInterface.PARAM_CHANGESETCONTEXT + "=" + changeSetUri;
		restClient.putCts2Resource(url, Cts2EditorServiceProperties.getCts2ValueSetRestUsername(),
		        Cts2EditorServiceProperties.getCts2ValueSetRestPassword(), definition);
		return true;
	}

	private boolean saveAsToService(ValueSetDefinition definition, String changeSetUri) throws Exception {
		/*
		 * POST
		 * /valuesetdefinition?changesetcontext={changeSetUri}
		 */
		Cts2RestClient restClient = Cts2RestClient.instance();
		URI uri = restClient.postCts2Resource(Cts2EditorServiceProperties.getValueSetDefinitionMaintenanceUrl()
		        + URIHelperInterface.PATH_VALUESETDEFINITION + "?" + URIHelperInterface.PARAM_CHANGESETCONTEXT + "="
		        + changeSetUri, Cts2EditorServiceProperties.getCts2ValueSetRestUsername(),
		        Cts2EditorServiceProperties.getCts2ValueSetRestPassword(), definition);
		return uri != null;
	}

	private ValueSetDefinition unmarshallValueSetDefinition(String xml) {
		DelegatingMarshaller marshaller = new DelegatingMarshaller();
		StringReader reader = new StringReader(xml);
		ValueSetDefinitionMsg message;
		ValueSetDefinition definition = null;
		try {
			message = (ValueSetDefinitionMsg) marshaller.unmarshal(new StreamSource(reader));
			if (message != null) {
				definition = message.getValueSetDefinition();
			}
		} catch (IOException ioe) {
			logger.warning("Unable to unmarshal the returned value set definition xml. Message: " + ioe.getMessage());
		}
		return definition;
	}

	private String getAuthorizationHeader() {
		if (serviceProperties.containsKey("MaintenanceUsername") && serviceProperties.containsKey("MaintenancePassword")) {
			return "Basic "
			  + Base64.encodeBytes((serviceProperties.get("MaintenanceUsername")
			  + ":" + serviceProperties.get("MaintenancePassword")).getBytes());
		} else {
			return "Basic "
		      + Base64.encodeBytes((Cts2EditorServiceProperties.getCts2ValueSetRestUsername()
			  + ":" + Cts2EditorServiceProperties.getCts2ValueSetRestPassword()).getBytes());
		}
	}

	private Cts2Client getCts2Client() {
		if (serviceProperties.containsKey("MaintenanceUrl")) {
			return ProxyFactory.create(Cts2Client.class,
			  serviceProperties.get("MaintenanceUrl"));
		} else {
			return ProxyFactory.create(Cts2Client.class,
			  Cts2EditorServiceProperties.getValueSetDefinitionMaintenanceUrl());
		}
	}

	private EntityClient getEntityClient() {
		if (serviceProperties.containsKey("EntityUrl")) {
			return ProxyFactory.create(EntityClient.class,
			  serviceProperties.get("EntityUrl"));
		} else {
			return ProxyFactory.create(EntityClient.class,
			  Cts2EditorServiceProperties.getValueSetDefinitionMaintenanceEntitiesUrl());
		}
	}

}
