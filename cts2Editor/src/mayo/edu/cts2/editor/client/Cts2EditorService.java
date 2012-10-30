package mayo.edu.cts2.editor.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("cts2Editor")
public interface Cts2EditorService extends RemoteService {
	List<String> getValueSets(List<String> oids)
			throws IllegalArgumentException;
}
