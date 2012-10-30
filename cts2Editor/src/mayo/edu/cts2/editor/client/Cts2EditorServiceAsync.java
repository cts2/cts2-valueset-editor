package mayo.edu.cts2.editor.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>Cts2EditorService</code>.
 */
public interface Cts2EditorServiceAsync {
	void getValueSets(List<String> oids, AsyncCallback<List<String>> callback)
			throws IllegalArgumentException;
}
