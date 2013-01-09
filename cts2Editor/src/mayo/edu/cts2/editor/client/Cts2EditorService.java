package mayo.edu.cts2.editor.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import edu.mayo.cts2.framework.model.service.core.UpdateChangeSetMetadataRequest;
import mayo.edu.cts2.editor.shared.ValueSetDefinitionEntry;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("cts2Editor")
public interface Cts2EditorService extends RemoteService {
	String getValueSet(String oid) throws IllegalArgumentException;

	String getValueSets(List<String> oids) throws IllegalArgumentException;

	String getValueSetDefinition(String oid) throws IllegalArgumentException;

	String getResolvedValueSet(String oid) throws IllegalArgumentException;

	String getDefinitions(String oid) throws IllegalArgumentException;

	String getMatchingValueSets(String matchValue) throws IllegalArgumentException;

	String createChangeSet();

	String deleteChangeSet(String uri) throws IllegalArgumentException;

	String getChangeSet(String uri) throws IllegalArgumentException;

	void updateChangeSet(String uri, String creator, String instructions) throws IllegalArgumentException;

	String getMatchingEntities(String matchValue) throws IllegalArgumentException;

	String saveValueSet(String valueSetDefinitionId, String creator, String description,
	                    List<ValueSetDefinitionEntry> addedEntries,
	                    List<ValueSetDefinitionEntry> removedEntries) throws IllegalArgumentException;

	/**
	 * Creates a new value set definition based off of the parentValueSetDefinitionId
	 *
	 * @param parentValueSetDefinitionId
	 * @param creator
	 * @param description
	 * @param addedEntries
	 * @param removedEntries
	 * @return
	 * @throws IllegalArgumentException
	 */
	String saveValueSetAs(String parentValueSetDefinitionId, String creator, String description,
	                      List<ValueSetDefinitionEntry> addedEntries,
	                      List<ValueSetDefinitionEntry> removedEntries) throws IllegalArgumentException;

	String getUserDefinitions(String oid, String username) throws IllegalArgumentException;
}
