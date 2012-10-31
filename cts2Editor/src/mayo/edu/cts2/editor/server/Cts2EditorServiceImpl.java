package mayo.edu.cts2.editor.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import mayo.edu.cts2.editor.client.Cts2EditorService;

import mayo.edu.cts2.editor.server.rest.Cts2Client;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.util.Base64;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

/**
 * The server side implementation of the RPC service.
 */
public class Cts2EditorServiceImpl extends BaseEditorServlet
		implements
			Cts2EditorService {

	private static final long serialVersionUID = 1L;
	private static final String XML_ROOT = "ValueSetCatalogEntryMsgList";
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	private static final String XML_ROOT_START = "<"+XML_ROOT+">\n";
	private static final String XML_ROOT_END = "</"+XML_ROOT+">\n";
	private static Logger logger = Logger.getLogger(Cts2EditorServiceImpl.class.getName());

	public static final String XPATH_VALUESETS_BASE = "/" + XML_ROOT;

	private final int MAX_RECORDS = 1000;

	@Override
	public String getValueSet(String oid) throws IllegalArgumentException {
		String authHeader  = "Basic " + Base64.encodeBytes((getCts2ValueSetRestUsername() + ":" + getCts2ValueSetRestPassword()).getBytes());
		Cts2Client client = ProxyFactory.create(Cts2Client.class, getCts2ValueSetRestUrl());
		String xmlResponse = client.getValueSet(authHeader, oid);

		return xmlResponse;
	}

	@Override
	/**
	 * For each OID in the list, return the XML representation of the value sets.
	 */
	public String getValueSets(List<String> oids) throws IllegalArgumentException {
		StringBuilder sb = new StringBuilder(XML_HEADER + XML_ROOT_START);

		String authHeader  = "Basic " + Base64.encodeBytes(
		  (getCts2ValueSetRestUsername() + ":" + getCts2ValueSetRestPassword()).getBytes());
		Cts2Client client = ProxyFactory.create(Cts2Client.class, getCts2ValueSetRestUrl());
		for (String oid: oids) {
			String xmlResponse = client.getValueSet(authHeader, oid);
			int end = xmlResponse.indexOf(XML_HEADER) + XML_HEADER.length();
			xmlResponse = xmlResponse.substring(end);
			sb.append(xmlResponse);
		}
		sb.append(XML_ROOT_END);

		return sb.toString();
	}

	@Override
	public String getResolvedValueSet(String oid) throws IllegalArgumentException {
		String authHeader  = "Basic " + Base64.encodeBytes(
		  (getCts2ValueSetRestUsername() + ":" + getCts2ValueSetRestPassword()).getBytes());
		Cts2Client client = ProxyFactory.create(Cts2Client.class, getCts2ValueSetRestUrl());
		String xmlResponse = client.getResolvedValueSet(authHeader, oid, "1", MAX_RECORDS);

		return xmlResponse;
	}

}
