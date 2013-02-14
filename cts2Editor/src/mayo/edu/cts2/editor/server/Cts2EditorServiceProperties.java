package mayo.edu.cts2.editor.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cts2EditorServiceProperties {

	private static final String SERVICES_PROPERTIES_FILE = "services.properties";

	private static Properties serviceProperties = null;
	private static Object valueSetDefMaintLock = new Object();
	private static String valueSetDefMaintUrl;
	private static String valueSetDefMaintUser;
	private static String valueSetDefMaintPassword;
	private static String valueSetDefMaintEntitiesUrl;
	private static int valueSetRestPageSize = -1;
	private static Logger logger = Logger.getLogger(Cts2EditorServiceProperties.class.getName());

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

	public static void setValueSetDefinitionMaintenanceUrl(String url) throws IllegalArgumentException {
		synchronized (valueSetDefMaintLock) {
			valueSetDefMaintUrl = url;
		}
	}

	public static String getValueSetDefinitionMaintenanceUrl() {
		synchronized (valueSetDefMaintLock) {
			if (valueSetDefMaintUrl == null) {
				valueSetDefMaintUrl = new Cts2EditorServiceProperties().getStartupProperties().getProperty("cts2ValueSetRestUrl");
			}
			return valueSetDefMaintUrl;
		}
	}

	public static void setValueSetDefinitionMaintenanceCredentials(String user, String password) throws IllegalArgumentException {
		synchronized (valueSetDefMaintLock) {
			valueSetDefMaintUser = user;
			valueSetDefMaintPassword = password;
		}
	}

	public static String getCts2ValueSetRestUsername() {
		synchronized (valueSetDefMaintLock) {
			if (valueSetDefMaintUser == null || valueSetDefMaintPassword == null) {
				valueSetDefMaintUser = new Cts2EditorServiceProperties().getStartupProperties().getProperty("cts2ValueSetRestUserId");
				valueSetDefMaintPassword = new Cts2EditorServiceProperties().getStartupProperties().getProperty("cts2ValueSetRestPassword");
			}
			return valueSetDefMaintUser;
		}
	}

	public static String getCts2ValueSetRestPassword() {
		synchronized (valueSetDefMaintLock) {
			if (valueSetDefMaintEntitiesUrl == null) {
				valueSetDefMaintEntitiesUrl = new Cts2EditorServiceProperties().getStartupProperties().getProperty("cts2EntityRestUrl");
			}
			return valueSetDefMaintPassword;
		}
	}

	public static void setValueSetDefinitionMaintenanceEntitiesUrl(String url) throws IllegalArgumentException {
		synchronized (valueSetDefMaintLock) {
			valueSetDefMaintEntitiesUrl = url;
		}
	}

	public static String getValueSetDefinitionMaintenanceEntitiesUrl() {
		synchronized (valueSetDefMaintLock) {
			return valueSetDefMaintEntitiesUrl;
		}
	}

	public static void setValueSetRestPageSize(int pageSize) {
		synchronized (valueSetDefMaintLock) {
			valueSetRestPageSize = pageSize;
		}
	}

	public static int getValueSetRestPageSize() {
		synchronized (valueSetDefMaintLock) {
			if (valueSetRestPageSize == -1) {
				valueSetRestPageSize = Integer.parseInt(new Cts2EditorServiceProperties().getStartupProperties().getProperty("cts2ValueSetRestPageSize"));
			}
			return valueSetRestPageSize;
		}
	}

}
