package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class ChangesMadeEvent extends GwtEvent<ChangesMadeEventHandler> {

	public static Type<ChangesMadeEventHandler> TYPE = new Type<ChangesMadeEventHandler>();

	@Override
	public Type<ChangesMadeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangesMadeEventHandler handler) {
		handler.onChangeMade(this);
	}
}
