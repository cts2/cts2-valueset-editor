package mayo.edu.cts2.editor.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.mayo.cts2.framework.model.service.core.UpdateChangeSetMetadataRequest;
import mayo.edu.cts2.editor.shared.ValueSetDefinitionEntry;

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

	void saveValueSetAs(String parentValueSetDefinitionId, String creator, String description,
	                    List<ValueSetDefinitionEntry> addedEntries,
	                    List<ValueSetDefinitionEntry> removedEntries, AsyncCallback<String> async);

	void saveValueSet(String valueSetDefinitionId, String creator, String description,
	                  List<ValueSetDefinitionEntry> addedEntries,
	                  List<ValueSetDefinitionEntry> removedEntries, AsyncCallback<String> async);

	void getUserDefinitions(String oid, String username, AsyncCallback<String> async);

	void getDefinition(String oid, String version, AsyncCallback<String> async);
}
