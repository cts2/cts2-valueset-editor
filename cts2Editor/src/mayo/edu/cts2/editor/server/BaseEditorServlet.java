package mayo.edu.cts2.editor.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseEditorServlet extends RemoteServiceServlet {

	private static final long serialVersionUID = 1L;

	private final Logger logger = Logger.getLogger(BaseEditorServlet.class.getName());
	private static Properties serviceProperties = null;

	public String getBasePath() {
		String dataPath;

		HttpSession httpSession = getThreadLocalRequest().getSession(true);
		ServletContext context = httpSession.getServletContext();

		String realContextPath = context.getRealPath(getThreadLocalRequest().getContextPath());

		if (isDevelopmentMode()) {
			dataPath = realContextPath;
		} else {
			dataPath = realContextPath + "/../";
		}

		return dataPath;
	}

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
				String propsPath = getBasePath() + "data/services.properties";
				FileInputStream in = new FileInputStream(propsPath);
				props.load(in);
			} catch (FileNotFoundException fnfe) {
				logger.log(Level.WARNING, "Error loading service.properties: ", fnfe);
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
