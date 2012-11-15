package mayo.edu.cts2.editor.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>Cts2EditorService</code>.
 */
public interface Cts2EditorServiceAsync {
	void getValueSet(String oid, AsyncCallback<String> async);

	void getValueSets(List<String> oids, AsyncCallback<String> async);

	void getResolvedValueSet(String oid, AsyncCallback<String> async);

	void getValueSetDefinition(String oid, AsyncCallback<String> async)
	  ;

	void getDefinitons(String oid, AsyncCallback<String> async)
	  ;

	void getMatchingValueSets(String matchValue, AsyncCallback<String> async)
	  ;
}
