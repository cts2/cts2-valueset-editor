package mayo.edu.cts2.editor.shared;

import java.io.Serializable;

public class ValueSetDefinitionEntry implements Serializable {

	String uri;
	String href;
	String namespace;
	String name;

	public ValueSetDefinitionEntry() {
		this.uri = "";
		this.href = "";
		this.namespace = "";
		this.name = "";
	}

	public ValueSetDefinitionEntry(String uri, String href, String namespace, String name) {
		this.uri = uri;
		this.href = href;
		this.namespace = namespace;
		this.name = name;
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof ValueSetDefinitionEntry) {
			ValueSetDefinitionEntry that = (ValueSetDefinitionEntry) o;
			return  /* this.getUri().equals(that.getUri()) &&
			  this.getHref().equals(that.getHref()) && */
			  this.getNamespace().equals(that.getNamespace()) &&
			  this.getName().equals(that.getName());
		}
		else {
			return false;
		}
	}
}
