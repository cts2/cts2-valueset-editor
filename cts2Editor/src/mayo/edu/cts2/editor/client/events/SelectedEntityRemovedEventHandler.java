package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface SelectedEntityRemovedEventHandler  extends EventHandler {

	void onEntityRemoved(SelectedEntityRemovedEvent event);
}
