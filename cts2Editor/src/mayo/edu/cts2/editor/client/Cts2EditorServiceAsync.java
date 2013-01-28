package mayo.edu.cts2.editor.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import mayo.edu.cts2.editor.shared.CTS2Result;
import mayo.edu.cts2.editor.shared.Definition;

/**
 * The async counterpart of <code>Cts2EditorService</code>.
 */
public interface Cts2EditorServiceAsync {
	void getValueSet(String oid, AsyncCallback<String> async);

	void getValueSets(List<String> oids, AsyncCallback<String> async);

	void getResolvedValueSet(String oid, AsyncCallback<String> async);

	void getValueSetDefinition(String oid, AsyncCallback<String> async);

	void getDefinitions(String oid, AsyncCallback<String> async);

	void getMatchingValueSets(String matchValue, AsyncCallback<String> async);

	void createChangeSet(AsyncCallback<String> async);

	void deleteChangeSet(String uri, AsyncCallback<String> async);

	void getChangeSet(String uri, AsyncCallback<String> async);

	void updateChangeSet(String uri, String creator, String instructions, AsyncCallback<Void> async);

	void getMatchingEntities(String matchValue, AsyncCallback<String> async);

	void saveDefinitionAs(Definition definition, AsyncCallback<CTS2Result> async);

	void saveDefinition(Definition definition, AsyncCallback<CTS2Result> async);

	void getUserDefinitions(String oid, String username, AsyncCallback<String> async);

	void getDefinition(String oid, String version, String changeSetUri, AsyncCallback<String> async);

	void isFinal(Definition definition, AsyncCallback<Boolean> async);
}
