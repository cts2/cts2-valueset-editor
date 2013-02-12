package mayo.edu.cts2.editor.client.widgets;

import com.smartgwt.client.widgets.grid.ListGrid;

public class BaseValueSetsListGrid extends ListGrid {

	public static final String EMPTY_MESSAGE = "No value sets to display.";
	public static final String ERROR_MESSAGE = "Value sets service unavailable.";

	public static final String ACTION_NONE = "NONE";
	public static final String ACTION_DELETE = "MARKED TO DELETE";
	public static final String ACTION_ADD = "MARKED TO ADD";

	public static final String ID_FORMAL_NAME = "formalName";
	public static final String ID_VALUE_SET_NAME = "valueSetName";
	public static final String ID_DESCRIPTION = "value";
	public static final String ID_CURRENT_VERSION = "displayVersion";
	public static final String ID_CHANGE_SET_URI = "changeSetUri";
	public static final String ID_URI = "uri";
	public static final String ID_COMMENT = "comment";
	public static final String ID_CHANGE_VERSION = "changeVersion";

	public static final String ID_ACTION = "action";
	public static final String ID_HIDDEN_ACTION = "hiddenValue";

	public static final String TITLE_FORMAL_NAME = "Formal Name";
	public static final String TITLE_VALUE_SET_NAME = "Value Set Identifier";
	public static final String TITLE_DESCRIPTION = "Description";
	public static final String TITLE_ACTION = "Action";
	public static final String TITLE_CURRENT_VERSION = "Current Version";
	public static final String TITLE_CHANGE_VERSION = "Versions";
}
