package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;
import mayo.edu.cts2.editor.shared.Definition;

public class NewValueSetCreatedEvent extends GwtEvent<NewValueSetCreatedEventHandler> {

	public static Type<NewValueSetCreatedEventHandler> TYPE = new Type<NewValueSetCreatedEventHandler>();

	private Definition definition = new Definition();

	@Override
	public Type<NewValueSetCreatedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NewValueSetCreatedEventHandler handler) {
		handler.onNewValueSetCreated(this);
	}

	public Definition getDefinition() {
		return definition;
	}

	public void setDefinition(Definition definition) {
		this.definition = definition;
	}
}
