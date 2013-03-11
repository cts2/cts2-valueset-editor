package mayo.edu.cts2.editor.client.debug;

import java.util.LinkedHashMap;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

/**
 * Simple ListGrid to display debug information.
 * 
 */
public class DebugListGrid extends ListGrid {

	public static final String ID_DATE = "date";
	public static final String ID_SEVERITY = "severity";
	public static final String ID_LOG = "log";

	public DebugListGrid() {
		super();

		setWidth100();
		setHeight100();

		ListGridField dateField = new ListGridField(ID_DATE, "Date");
		dateField.setWidth("15%");

		ListGridField severityLevelField = new ListGridField(ID_SEVERITY, "Severity");
		severityLevelField.setWidth("15%");

		LinkedHashMap<String, String> logValuesMap = new LinkedHashMap<String, String>();
		logValuesMap.put("0", "DEBUG");
		logValuesMap.put("1", "INFO");
		logValuesMap.put("2", "ERROR");

		severityLevelField.setValueMap(logValuesMap);

		ListGridField logField = new ListGridField(ID_LOG, "Log");
		logField.setWidth("*");

		setFields(dateField, severityLevelField, logField);

		setShowAllRecords(true);
		setShowRowNumbers(true);

		setSortField(0);
	}

}
