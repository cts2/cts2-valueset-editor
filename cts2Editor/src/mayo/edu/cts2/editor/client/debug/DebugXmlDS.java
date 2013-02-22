package mayo.edu.cts2.editor.client.debug;

import com.smartgwt.client.data.DataSource;

public class DebugXmlDS extends DataSource {

	private static DebugXmlDS instance = null;

	public static DebugXmlDS getInstance() {
		if (instance == null) {
			instance = new DebugXmlDS("DebugXmlDS");
		}
		return instance;
	}

	public DebugXmlDS(String id) {

		setID(id);
		// setRecordXPath("/List/country");
		// DataSourceIntegerField pkField = new DataSourceIntegerField("pk");
		// pkField.setHidden(true);
		// pkField.setPrimaryKey(true);
		//
		// DataSourceTextField countryCodeField = new
		// DataSourceTextField("countryCode", "Code");
		// countryCodeField.setRequired(true);
		//
		// DataSourceTextField countryNameField = new
		// DataSourceTextField("countryName", "Country");
		// countryNameField.setRequired(true);
		//
		// DataSourceTextField capitalField = new DataSourceTextField("capital",
		// "Capital");
		// DataSourceTextField governmentField = new
		// DataSourceTextField("government", "Government", 500);
	}
}
