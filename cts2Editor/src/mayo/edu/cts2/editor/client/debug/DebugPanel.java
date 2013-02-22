package mayo.edu.cts2.editor.client.debug;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

public class DebugPanel extends VLayout {

	private static final int HEIGHT = 150;

	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int ERROR = 3;

	private static DebugListGrid i_debugListGrid;

	public DebugPanel() {
		super();

		setWidth100();
		setHeight(HEIGHT);

		i_debugListGrid = new DebugListGrid();
		addMember(i_debugListGrid);
	}

	public static void log(int errorLevel, String log) {

		ListGridRecord record = new ListGridRecord();

		Date today = new Date();
		String now = DateTimeFormat.getMediumDateTimeFormat().format(today);
		
		record.setAttribute(DebugListGrid.ID_DATE, now);
		record.setAttribute(DebugListGrid.ID_SEVERITY, errorLevel);
		record.setAttribute(DebugListGrid.ID_LOG, log);

		i_debugListGrid.addData(record);
	}

}
