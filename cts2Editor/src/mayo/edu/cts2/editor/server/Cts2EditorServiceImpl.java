package mayo.edu.cts2.editor.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import mayo.edu.cts2.editor.client.Cts2EditorService;

import mayo.edu.cts2.editor.server.rest.Cts2Client;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.util.Base64;

/**
 * The server side implementation of the RPC service.
 */
public class Cts2EditorServiceImpl extends BaseEditorServlet
		implements
			Cts2EditorService {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Cts2EditorServiceImpl.class.getName());
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
	public Map<String, String> getValueSets(List<String> oids) throws IllegalArgumentException {
		Map<String, String> valueSetMap = new HashMap<String, String>(oids.size());
		String authHeader  = "Basic " + Base64.encodeBytes(
		  (getCts2ValueSetRestUsername() + ":" + getCts2ValueSetRestPassword()).getBytes());
		Cts2Client client = ProxyFactory.create(Cts2Client.class, getCts2ValueSetRestUrl());
		for (String oid: oids) {
			String xmlResponse = client.getValueSet(authHeader, oid);
			valueSetMap.put(oid, xmlResponse);
		}

		return valueSetMap;
	}

	@Override
	public String getResolvedValueSet(String oid) throws IllegalArgumentException {
		String authHeader  = "Basic " + Base64.encodeBytes(
		  (getCts2ValueSetRestUsername() + ":" + getCts2ValueSetRestPassword()).getBytes());
		Cts2Client client = ProxyFactory.create(Cts2Client.class, getCts2ValueSetRestUrl());
		String xmlResponse = client.getResolvedValueSet(authHeader, oid, "1", MAX_RECORDS);

		return xmlResponse;
	}

	@Override
	public Map<String, String> getResolvedValueSets(List<String> oids) throws IllegalArgumentException {
		Map<String, String> resolvedValueSetMap = new HashMap<String, String>(oids.size());
		String authHeader  = "Basic " + Base64.encodeBytes(
		  (getCts2ValueSetRestUsername() + ":" + getCts2ValueSetRestPassword()).getBytes());
		Cts2Client client = ProxyFactory.create(Cts2Client.class, getCts2ValueSetRestUrl());
		for (String oid: oids) {
			String xmlResponse = client.getResolvedValueSet(authHeader, oid, "1", MAX_RECORDS);
			resolvedValueSetMap.put(oid, xmlResponse);
		}

		return resolvedValueSetMap;
	}

}
