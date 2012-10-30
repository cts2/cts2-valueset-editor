package mayo.edu.cts2.editor.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mayo.edu.cts2.editor.client.Cts2EditorService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
public class Cts2EditorServiceImpl extends RemoteServiceServlet
		implements
			Cts2EditorService {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Cts2EditorServiceImpl.class
			.getName());

	@Override
	/**
	 * For each OID in the list, return the XML representation of the value sets.
	 */
	public List<String> getValueSets(List<String> oids)
			throws IllegalArgumentException {

		ArrayList<String> valueSetGroups = new ArrayList<String>();

		return valueSetGroups;
	}

}
