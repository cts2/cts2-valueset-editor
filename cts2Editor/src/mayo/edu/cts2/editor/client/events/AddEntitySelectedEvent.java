package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class AddEntitySelectedEvent extends GwtEvent<AddEntitySelectedEventHandler> {

	public static Type<AddEntitySelectedEventHandler> TYPE = new Type<AddEntitySelectedEventHandler>();

	private String uri;
	private String name;
	private String designation;
	private String namespace;
	private String codeSystemVersion;

	public AddEntitySelectedEvent(String uri, String name, String designation, String namespace, String codeSystemVersion) {
		this.uri = uri;
		this.name = name;
		this.designation = designation;
		this.namespace = namespace;
		this.codeSystemVersion = codeSystemVersion;
	}

	@Override
	public Type<AddEntitySelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddEntitySelectedEventHandler handler) {
		handler.onEntitySelected(this);

	}

	public String getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public String getDesignation() {
		return designation;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getCodeSystemVersion() {
		return codeSystemVersion;
	}
}
