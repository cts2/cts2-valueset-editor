package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class SelectedEntityAddedEvent extends GwtEvent<SelectedEntityAddedEventHandler> {
	public static GwtEvent.Type<SelectedEntityAddedEventHandler> TYPE = new GwtEvent.Type<SelectedEntityAddedEventHandler>();

	@Override
	public GwtEvent.Type<SelectedEntityAddedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectedEntityAddedEventHandler handler) {
		handler.onEntityAdded(this);
	}
}
