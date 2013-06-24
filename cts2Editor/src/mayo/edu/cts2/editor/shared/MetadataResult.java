package mayo.edu.cts2.editor.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetadataResult implements Serializable {

	private boolean error;
	private boolean existingValueSetName;
	private boolean existingValueSetUri;
	private boolean existingDefinitionName;
	private boolean existingDefinitionVersion;
	private List<String> messages;

	public MetadataResult() {
		messages = new ArrayList<String>();
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isExistingValueSetName() {
		return existingValueSetName;
	}

	public void setExistingValueSetName(boolean existingValueSetName) {
		this.existingValueSetName = existingValueSetName;
	}

	public boolean isExistingValueSetUri() {
		return existingValueSetUri;
	}

	public void setExistingValueSetUri(boolean existingValueSetUri) {
		this.existingValueSetUri = existingValueSetUri;
	}

	public boolean isExistingDefinitionName() {
		return existingDefinitionName;
	}

	public void setExistingDefinitionName(boolean existingDefinitionName) {
		this.existingDefinitionName = existingDefinitionName;
	}

	public boolean isExistingDefinitionVersion() {
		return existingDefinitionVersion;
	}

	public void setExistingDefinitionVersion(boolean existingDefinitionVersion) {
		this.existingDefinitionVersion = existingDefinitionVersion;
	}

	public void addMessage(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return messages;
	}
}
