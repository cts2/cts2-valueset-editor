package mayo.edu.cts2.editor.server;

import java.util.List;
import java.util.logging.Logger;

import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.server.rest.Cts2Client;
import mayo.edu.cts2.editor.server.rest.EntityClient;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.util.Base64;

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
		return getCts2Client().getValueSetDefinition(getAuthorizationHeader(), oid, "1");
	}

	@Override
	public String getResolvedValueSet(String oid) throws IllegalArgumentException {
		return getCts2Client().getResolvedValueSet(getAuthorizationHeader(), oid, "1", MAX_RECORDS);
	}

	@Override
	public String getDefinitons(String oid) throws IllegalArgumentException {
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

	@Override
	public String updateChangeSet(String uri) throws IllegalArgumentException {
		/*
		 * TODO: Pass along the changed metadata (creator, changeInstructions,
		 * officialEffectiveDate)
		 */
		return getCts2Client().updateChangeSet(getAuthorizationHeader(), uri);
	}

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

}
