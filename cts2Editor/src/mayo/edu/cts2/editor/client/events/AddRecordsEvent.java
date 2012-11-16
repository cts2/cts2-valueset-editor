package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class AddRecordsEvent extends GwtEvent<AddRecordsEventHandler> {

	public static Type<AddRecordsEventHandler> TYPE = new Type<AddRecordsEventHandler>();

	@Override
	public Type<AddRecordsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddRecordsEventHandler handler) {
		handler.onRecordsAdded(this);
	}
}
