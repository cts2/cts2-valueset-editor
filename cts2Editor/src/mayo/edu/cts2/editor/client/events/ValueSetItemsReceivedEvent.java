package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class ValueSetItemsReceivedEvent extends GwtEvent<ValueSetItemsReceivedEventHandler> {

	public static Type<ValueSetItemsReceivedEventHandler> TYPE = new Type<ValueSetItemsReceivedEventHandler>();

	@Override
	public Type<ValueSetItemsReceivedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ValueSetItemsReceivedEventHandler handler) {
		handler.onValueSetItemsReceived(this);
	}
}
