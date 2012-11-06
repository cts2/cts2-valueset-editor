package mayo.edu.cts2.editor.client.widgets;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class ValueSetItemsListGrid extends ListGrid {

	private static final int HEIGHT = 150;
	private static final String EMPTY_MESSAGE = "No entities to display.";

	public ValueSetItemsListGrid() {
		super();

		setWidth100();
		setHeight(HEIGHT);
		setShowAllRecords(true);
		setShowAllColumns(true);
		setWrapCells(false);

		setCanHover(true);
		setHoverWidth(100);
		setHoverWrap(false);
		setSelectionType(SelectionStyle.MULTIPLE);

		// Set edit and edit event to get the delete checkbox to work
		setCanEdit(true);

		setEmptyMessage(EMPTY_MESSAGE);

		ListGridField nameSpaceField = new ListGridField("nameSpace", "Code System Name");
		nameSpaceField.setWrap(false);
		nameSpaceField.setWidth("25%");
		nameSpaceField.setShowHover(false);
		nameSpaceField.setCanEdit(false);

		ListGridField nameField = new ListGridField("name", "Code");
		nameField.setWrap(false);
		nameField.setWidth("25%");
		nameField.setShowHover(false);
		nameField.setCanEdit(false);

		ListGridField designationField = new ListGridField("designation", "Description");
		designationField.setWrap(false);
		designationField.setWidth("*");
		designationField.setCanEdit(false);

		setFields(nameField, nameSpaceField, designationField);
		setAutoFetchData(true);
	}

	@Override
	public void fetchData(Criteria criteria) {

		getDataSource().fetchData(criteria, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {

				// populate the ListGrid with the data in the DataSource
				fetchData();
			}
		});

	}

	@Override
	public void fetchRelatedData(Record record, DataSource dataSource) {
		System.out.println("fetchRelatedData called");

		Criteria criteria = new Criteria();
		String oid = record.getAttribute("valueSetName");

		criteria.setAttribute("oid", oid);

		dataSource.fetchData(criteria, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {

				// populate the ListGrid with the data in the DataSource
				fetchData();
			}
		});
	}

	@Override
	public void removeSelectedData() {
		// TODO Auto-generated method stub
		super.removeSelectedData();
	}

	@Override
	public void removeSelectedData(DSCallback callback, DSRequest requestProperties) {
		// TODO Auto-generated method stub
		super.removeSelectedData(callback, requestProperties);
	}

	@Override
	public boolean saveAllEdits() {

		System.out.println("saveAllEdits called...");

		return super.saveAllEdits();
	}

}
