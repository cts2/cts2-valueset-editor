package mayo.edu.cts2.editor.client.widgets;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

public class ValueSetItemsListGrid extends ListGrid {

	private static final int HEIGHT = 150;
	private static final String EMPTY_MESSAGE = "No entities to display.";

	public static final String ACTION_NONE = "NONE";
	public static final String ACTION_DELETE = "MARKED TO DELETE";
	public static final String ACTION_ADD = "MARKED TO ADD";

	public static final String ID_NAME_SPACE = "nameSpace";
	public static final String ID_NAME = "name";
	public static final String ID_DESIGNATION = "designation";
	public static final String ID_ACTION = "action";
	public static final String ID_HIDDEN_ACTION = "hiddenValue";

	public static final String TITLE_NAME_SPACE = "Code System Name";
	public static final String TITLE_NAME = "Code";
	public static final String TITLE_DESIGNATION = "Description";
	public static final String TITLE_ACTION = "Action";

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

		setShowRecordComponents(true);
		setShowRecordComponentsByCell(true);

		// Set edit and edit event to get the delete checkbox to work
		setCanEdit(true);

		setEmptyMessage(EMPTY_MESSAGE);

		ListGridField nameSpaceField = new ListGridField(ID_NAME_SPACE, TITLE_NAME_SPACE);
		nameSpaceField.setWrap(false);
		nameSpaceField.setWidth("25%");
		nameSpaceField.setShowHover(false);
		nameSpaceField.setCanEdit(false);

		ListGridField nameField = new ListGridField(ID_NAME, TITLE_NAME);
		nameField.setWrap(false);
		nameField.setWidth("25%");
		nameField.setShowHover(false);
		nameField.setCanEdit(false);

		ListGridField designationField = new ListGridField(ID_DESIGNATION, TITLE_DESIGNATION);
		designationField.setWrap(false);
		designationField.setWidth("35%");
		designationField.setCanEdit(false);

		ListGridField actionField = new ListGridField(ID_ACTION, TITLE_ACTION);
		actionField.setWrap(false);
		actionField.setWidth("*");
		actionField.setCanEdit(false);
		actionField.setAttribute(ID_HIDDEN_ACTION, ACTION_NONE);

		setFields(nameField, nameSpaceField, designationField, actionField);
		setAutoFetchData(true);
	}

	@Override
	protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {

		String fieldName = this.getFieldName(colNum);

		if (fieldName.equals(ID_ACTION) && record.getAttribute(ID_HIDDEN_ACTION) != null
		        && record.getAttribute(ID_HIDDEN_ACTION).equals(ACTION_DELETE)) {

			HLayout recordCanvas = new HLayout(1);
			recordCanvas.setHeight(22);
			recordCanvas.setAlign(Alignment.LEFT);
			ImgButton undoImg = createUndoImage("Undo Delete");

			// undoImg.setID("delete");
			undoImg.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					record.setAttribute(ValueSetItemsListGrid.ID_HIDDEN_ACTION, ValueSetItemsListGrid.ACTION_NONE);
					updateData(record);

					// refresh the icons in Action column
					invalidateRecordComponents();
				}
			});

			recordCanvas.addMember(undoImg);

			Label dataLabel = new Label("<em style=\"font-weight:bold; color:red;margin-left:8px\">" + ACTION_DELETE
			        + "</em>");
			dataLabel.setAutoFit(true);
			dataLabel.setWrap(false);
			recordCanvas.addMember(dataLabel);

			return recordCanvas;
		}

		return super.createRecordComponent(record, colNum);
	}

	// @Override
	// protected String getCellCSSText(ListGridRecord record, int rowNum, int
	// colNum) {
	// if (getFieldName(colNum).equals("action") &&
	// record.getAttribute("action") != null) {
	//
	// if (record.getAttribute("action").equals(ACTION_NONE)) {
	// return "font-weight:bold;";
	// } else if (record.getAttribute("action").equals(ACTION_DELETE)) {
	//
	// return "font-weight:bold; color:red;margin-left:20px";
	//
	// } else if (record.getAttribute("action").equals(ACTION_ADD)) {
	// return "font-weight:bold; color:green;";
	// } else {
	// return super.getCellCSSText(record, rowNum, colNum);
	// }
	// } else {
	// return super.getCellCSSText(record, rowNum, colNum);
	// }
	// }
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

	private ImgButton createUndoImage(String prompt) {

		ImgButton undoImg = new ImgButton();
		undoImg.setShowDown(false);
		undoImg.setShowRollOver(false);
		undoImg.setLayoutAlign(Alignment.CENTER);
		undoImg.setSrc("undo.png");
		undoImg.setPrompt(prompt);
		undoImg.setHeight(16);
		undoImg.setWidth(16);

		return undoImg;
	}

}
