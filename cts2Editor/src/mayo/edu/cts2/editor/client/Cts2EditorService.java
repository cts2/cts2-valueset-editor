package mayo.edu.cts2.editor.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("cts2Editor")
public interface Cts2EditorService extends RemoteService {
	String getValueSet(String oid) throws IllegalArgumentException;

	String getValueSets(List<String> oids) throws IllegalArgumentException;

	String getValueSetDefinition(String oid) throws IllegalArgumentException;

	String getResolvedValueSet(String oid) throws IllegalArgumentException;

	String getDefinitons(String oid) throws IllegalArgumentException;

	String getMatchingValueSets(String matchValue) throws IllegalArgumentException;

	String createChangeSet();

	String deleteChangeSet(String uri) throws IllegalArgumentException;

	String getChangeSet(String uri) throws IllegalArgumentException;

	String updateChangeSet(String uri) throws IllegalArgumentException;

	String getMatchingEntities(String matchValue) throws IllegalArgumentException;
}
