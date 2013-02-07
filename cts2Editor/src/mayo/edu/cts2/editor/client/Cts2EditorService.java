package mayo.edu.cts2.editor.client;

import java.util.List;

import mayo.edu.cts2.editor.shared.CTS2Result;
import mayo.edu.cts2.editor.shared.Definition;

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

	String getDefinitions(String oid) throws IllegalArgumentException;

	String getMatchingValueSets(String matchValue) throws IllegalArgumentException;

	String createChangeSet();

	String deleteChangeSet(String uri) throws IllegalArgumentException;

	String getChangeSet(String uri) throws IllegalArgumentException;

	void updateChangeSet(String uri, String creator, String instructions) throws IllegalArgumentException;

	String getMatchingEntities(String matchValue) throws IllegalArgumentException;

	CTS2Result saveDefinition(Definition definition) throws IllegalArgumentException;

	CTS2Result saveDefinitionAs(Definition definition) throws IllegalArgumentException;

	String getUserDefinitions(String oid, String username) throws IllegalArgumentException;

	String getUserDefinitions(List<String> oids, String username) throws IllegalArgumentException;

	String getDefinition(String oid, String version, String changeSetUri) throws IllegalArgumentException;

	boolean isFinal(Definition definition) throws IllegalArgumentException;

}
