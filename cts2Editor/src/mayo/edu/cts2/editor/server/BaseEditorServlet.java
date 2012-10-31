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

	private final String SERVICES_PROPERTIES_FILE = "services.properties";
	private final Logger logger = Logger.getLogger(BaseEditorServlet.class.getName());
	private static Properties serviceProperties = null;

	public String getCts2ValueSetRestUrl() {
		return getStartupProperties().getProperty("cts2ValueSetRestUrl");
	}

	public String getCts2ValueSetRestUsername() {
		return getStartupProperties().getProperty("cts2ValueSetRestUserId");
	}

	public String getCts2ValueSetRestPassword() {
		return getStartupProperties().getProperty("cts2ValueSetRestPassword");
	}

	protected Properties getStartupProperties() {
		if (serviceProperties == null) {
			Properties props = new Properties();
			try {
				String path = this.getClass().getPackage().getName().replaceAll(
				  "[.]", File.separator) + File.separator + SERVICES_PROPERTIES_FILE;
				InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
				props.load(in);
			} catch (IOException ioe) {
				logger.log(Level.WARNING, "Error loading service.properties: ", ioe);
			}
			serviceProperties = props;
		}

		return serviceProperties;
	}

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
