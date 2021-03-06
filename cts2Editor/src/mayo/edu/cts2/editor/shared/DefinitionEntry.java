package mayo.edu.cts2.editor.shared;

import java.io.Serializable;

public class DefinitionEntry implements Serializable {

	String uri;
	String href;
	String namespace;
	String name;
	String description;
	String codeSystemVersion;

	public DefinitionEntry() {
		this.uri = "";
		this.href = "";
		this.namespace = "";
		this.name = "";
		this.description = "";
		this.codeSystemVersion = "";
	}

	public DefinitionEntry(String uri, String href, String namespace, String name, String description, String codeSystemVersion) {
		this.uri = uri;
		this.href = href;
		this.namespace = namespace;
		this.name = name;
		this.description = description;
		this.codeSystemVersion = codeSystemVersion;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCodeSystemVersion() {
		return codeSystemVersion;
	}

	public void setCodeSystemVersion(String codeSystemVersion) {
		this.codeSystemVersion = codeSystemVersion;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DefinitionEntry) {
			DefinitionEntry that = (DefinitionEntry) o;
			return  /* this.getUri().equals(that.getUri()) &&
			  this.getHref().equals(that.getHref()) && */
			  this.getNamespace().equals(that.getNamespace()) &&
			  this.getName().equals(that.getName()) &&
			  this.getCodeSystemVersion().equals(that.getCodeSystemVersion());
		}
		else {
			return false;
		}
	}
}
