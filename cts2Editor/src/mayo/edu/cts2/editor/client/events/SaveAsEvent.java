package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class SaveAsEvent extends GwtEvent<SaveAsEventHandler> {

	public static Type<SaveAsEventHandler> TYPE = new Type<SaveAsEventHandler>();

	private final String i_comment;

	public SaveAsEvent(String comment) {
		super();
		i_comment = comment;
	}

	@Override
	public Type<SaveAsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveAsEventHandler handler) {
		handler.onSaveAs(this);
	}

	public String getComment() {
		return i_comment;
	}
}
