package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class AddEntityDeselectedEvent extends GwtEvent<AddEntityDeselectedEventHandler> {

	public static Type<AddEntityDeselectedEventHandler> TYPE = new Type<AddEntityDeselectedEventHandler>();
	private String href;

	public AddEntityDeselectedEvent(String href) {
		this.href = href;
	}

	@Override
	public Type<AddEntityDeselectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddEntityDeselectedEventHandler handler) {
		handler.onEntityDeselected(this);
	}

	public String getHref() {
		return href;
	}
}
