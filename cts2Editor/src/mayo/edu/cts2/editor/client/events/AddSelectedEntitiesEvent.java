package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.data.Record;

public class AddSelectedEntitiesEvent extends GwtEvent<AddSelectedEntitiesEventHandler> {
	public static Type<AddSelectedEntitiesEventHandler> TYPE = new Type<AddSelectedEntitiesEventHandler>();

	private Record[] selectedEntites;

	public AddSelectedEntitiesEvent(Record[] selectedEntites) {
		this.selectedEntites = selectedEntites;
	}

	@Override
	public Type<AddSelectedEntitiesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddSelectedEntitiesEventHandler handler) {
		handler.onSelectedEntriesAdded(this);

	}

	public Record[] getSelectedEntites() {
		return selectedEntites;
	}
}
