package mayo.edu.cts2.editor.client.widgets;

import mayo.edu.cts2.editor.client.datasource.ValueSetItemXmlDS;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class ValueSetItemsListGrid extends ListGrid {

	private static final int HEIGHT = 100;
	private static final String EMPTY_MESSAGE = "No entities to display.";
	private final ValueSetItemXmlDS i_valueSetItemXmlDSXmlDS;

	public ValueSetItemsListGrid(String uniqueId, String valueSetId) {
		super();

		i_valueSetItemXmlDSXmlDS = new ValueSetItemXmlDS(uniqueId);

		setWidth100();
		setHeight(HEIGHT);
		setShowAllRecords(true);
		setShowAllColumns(true);
		setWrapCells(false);

		setCanHover(true);
		setHoverWidth(100);
		setHoverWrap(false);
		setSelectionType(SelectionStyle.SINGLE);

		// don't allow the row to be edited.
		setCanEdit(false);

		setDataSource(i_valueSetItemXmlDSXmlDS);

		setEmptyMessage(EMPTY_MESSAGE);

		ListGridField nameSpaceField = new ListGridField("nameSpace", "Code System Name");
		nameSpaceField.setWrap(false);
		nameSpaceField.setWidth("25%");
		nameSpaceField.setShowHover(false);

		ListGridField nameField = new ListGridField("name", "Code");
		nameField.setWrap(false);
		nameField.setWidth("25%");
		nameField.setShowHover(false);

		ListGridField designationField = new ListGridField("designation", "Description");
		designationField.setWrap(false);
		designationField.setWidth("*");

		setFields(nameField, nameSpaceField, designationField);
		setAutoFetchData(false);
	}

}
