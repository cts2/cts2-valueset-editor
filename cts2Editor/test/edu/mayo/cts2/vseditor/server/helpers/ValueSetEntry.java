package edu.mayo.cts2.vseditor.server.helpers;

import java.io.Serializable;

public class ValueSetEntry implements Serializable {
	private String code;
	private String codeSystem;
	private String codeSystemVersion;
	private String description;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeSystem() {
		return codeSystem;
	}

	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}

	public String getCodeSystemVersion() {
		return codeSystemVersion;
	}

	public void setCodeSystemVersion(String codeSystemVersion) {
		this.codeSystemVersion = codeSystemVersion;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
