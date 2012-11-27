package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface ChangesMadeEventHandler extends EventHandler {

	void onChangeMade(ChangesMadeEvent event);
}