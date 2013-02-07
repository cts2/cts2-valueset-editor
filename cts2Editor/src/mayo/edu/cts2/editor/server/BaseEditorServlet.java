package mayo.edu.cts2.editor.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseEditorServlet extends RemoteServiceServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Determine if the app is in development mode. To do this get the request
	 * URL and if it contains 127.0.0.1, then it is in development mode.
	 *
	 * @return
	 */
	private boolean isDevelopmentMode() {
		return getThreadLocalRequest().getHeader("Referer").contains("127.0.0.1");
	}

}
