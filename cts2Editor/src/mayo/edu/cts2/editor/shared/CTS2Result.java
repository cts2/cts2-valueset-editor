package mayo.edu.cts2.editor.shared;

import java.io.Serializable;

public class CTS2Result implements Serializable {

	boolean error;
	String message;
	String changeSetUri;
	String valueSetOid;
	String valueSetDefinitionUri;
	String valueSetVersion;

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getChangeSetUri() {
		return changeSetUri;
	}

	public void setChangeSetUri(String changeSetUri) {
		this.changeSetUri = changeSetUri;
	}

	public String getValueSetOid() {
		return valueSetOid;
	}

	public void setValueSetOid(String valueSetOid) {
		this.valueSetOid = valueSetOid;
	}

	public String getValueSetDefinitionUri() {
		return valueSetDefinitionUri;
	}

	public void setValueSetDefinitionUri(String valueSetDefinitionUri) {
		this.valueSetDefinitionUri = valueSetDefinitionUri;
	}

	public String getValueSetVersion() {
		return valueSetVersion;
	}

	public void setValueSetVersion(String valueSetVersion) {
		this.valueSetVersion = valueSetVersion;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("error: " + error);
		sb.append(" message: " + message);
		sb.append(" changeSetUri: " + changeSetUri);
		sb.append(" valueSetOid: " + valueSetOid);
		sb.append(" valueSetDefinitionUri: " + valueSetDefinitionUri);
		sb.append(" valueSetVersion: " + valueSetVersion);
		return sb.toString();
	}

}
