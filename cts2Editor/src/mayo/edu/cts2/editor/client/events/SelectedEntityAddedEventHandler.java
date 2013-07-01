package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface SelectedEntityAddedEventHandler  extends EventHandler {

	void onEntityAdded(SelectedEntityAddedEvent event);
}
