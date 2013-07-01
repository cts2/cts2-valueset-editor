package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class SelectedEntityRemovedEvent extends GwtEvent<SelectedEntityRemovedEventHandler> {

	public static Type<SelectedEntityRemovedEventHandler> TYPE = new GwtEvent.Type<SelectedEntityRemovedEventHandler>();

	private String uri;

	public SelectedEntityRemovedEvent(String href) {
		this.uri = href;
	}

	@Override
	public Type<SelectedEntityRemovedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectedEntityRemovedEventHandler handler) {
		handler.onEntityRemoved(this);
	}

	public String getUri() {
		return uri;
	}
}
