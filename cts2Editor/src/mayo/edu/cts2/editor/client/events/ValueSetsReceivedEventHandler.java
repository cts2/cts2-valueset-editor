package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface ValueSetsReceivedEventHandler extends EventHandler {

	void onValueSetsReceived(ValueSetsReceivedEvent event);
}
