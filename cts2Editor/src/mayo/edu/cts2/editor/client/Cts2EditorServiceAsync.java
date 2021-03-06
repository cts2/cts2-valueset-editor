package mayo.edu.cts2.editor.client;

import java.util.List;
import java.util.Map;

import mayo.edu.cts2.editor.shared.CTS2Result;
import mayo.edu.cts2.editor.shared.Definition;

import com.google.gwt.user.client.rpc.AsyncCallback;
import mayo.edu.cts2.editor.shared.MetadataResult;

/**
 * The async counterpart of <code>Cts2EditorService</code>.
 */
public interface Cts2EditorServiceAsync {
	void getValueSet(String oid, AsyncCallback<String> async);

	void getValueSets(List<String> oids, AsyncCallback<String> async);

	void getResolvedValueSet(String oid, String version, String changeSetUri, AsyncCallback<String> async);

	void getValueSetDefinition(String oid, AsyncCallback<String> async);

	void getDefinitions(String oid, AsyncCallback<String> async);

	void getMatchingValueSets(String matchValue, AsyncCallback<String> async);

	void createChangeSet(AsyncCallback<String> async);

	void deleteChangeSet(String uri, AsyncCallback<String> async);

	void getChangeSet(String uri, AsyncCallback<String> async);

	void updateChangeSet(String uri, String creator, String instructions, AsyncCallback<Void> async);

	void getMatchingEntities(String codeSystem, String codeSystemVersion, String matchValue, AsyncCallback<String> async);

	void saveDefinitionAs(Definition definition, AsyncCallback<CTS2Result> async);

	void saveDefinition(Definition definition, AsyncCallback<CTS2Result> async);

	void getUserDefinitions(String oid, String username, AsyncCallback<String> async);

	void getUserDefinitions(List<String> oids, String username, AsyncCallback<String> async);

	void getDefinition(String oid, String version, String changeSetUri, AsyncCallback<String> async);

	void isFinal(Definition definition, AsyncCallback<Boolean> async);

	void getCodeSystems(AsyncCallback<String[]> async);

	void getCodeSystemVersions(String codeSystem, AsyncCallback<String[]> async);

	void createValueSet(Definition definition, AsyncCallback<CTS2Result> async);

	void checkNewValueSetMetadata(String valueSetName, String valueSetUri, String definitionName, String definitionVersion, AsyncCallback<MetadataResult> async);

	void setServiceProperties(Map<String, String> serviceProperties, AsyncCallback<Void> async);

	void addServiceProperty(String property, String value, AsyncCallback<Void> async);
}
