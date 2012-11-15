package mayo.edu.cts2.editor.server;

import java.util.List;
import java.util.logging.Logger;

import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.server.rest.Cts2Client;

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

	private final Cts2Client client;

	public Cts2EditorServiceImpl() {
		super();
		client = ProxyFactory.create(Cts2Client.class, getCts2ValueSetRestUrl());
	}

	@Override
	public String getValueSet(String oid) throws IllegalArgumentException {
		return client.getValueSet(getAuthorizationHeader(), oid);
	}

	@Override
	/**
	 * For each OID in the list, return the XML representation of the value sets.
	 */
	public String getValueSets(List<String> oids) throws IllegalArgumentException {
		StringBuilder sb = new StringBuilder(XML_HEADER + XML_ROOT_START);

		for (String oid : oids) {
			String xmlResponse = client.getValueSet(getAuthorizationHeader(), oid);
			int end = xmlResponse.indexOf(XML_HEADER) + XML_HEADER.length();
			xmlResponse = xmlResponse.substring(end);
			sb.append(xmlResponse);
		}
		sb.append(XML_ROOT_END);

		return sb.toString();
	}

	@Override
	public String getValueSetDefinition(String oid) throws IllegalArgumentException {
		return client.getValueSetDefinition(getAuthorizationHeader(), oid, "1");
	}

	@Override
	public String getResolvedValueSet(String oid) throws IllegalArgumentException {
		return client.getResolvedValueSet(getAuthorizationHeader(), oid, "1", MAX_RECORDS);
	}

	@Override
	public String getDefinitons(String oid) throws IllegalArgumentException {
		return client.getDefinitions(getAuthorizationHeader(), oid, MAX_RECORDS);
	}

	@Override
	public String getMatchingValueSets(String matchValue) throws IllegalArgumentException {
		return client.getValueSets(getAuthorizationHeader(), MAX_RECORDS, matchValue);
	}

	private String getAuthorizationHeader() {
		return "Basic "
		        + Base64.encodeBytes((getCts2ValueSetRestUsername() + ":" + getCts2ValueSetRestPassword()).getBytes());
	}

}
