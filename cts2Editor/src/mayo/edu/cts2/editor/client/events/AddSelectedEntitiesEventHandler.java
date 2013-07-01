package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface AddSelectedEntitiesEventHandler extends EventHandler {
	void onSelectedEntriesAdded(AddSelectedEntitiesEvent event);
}
