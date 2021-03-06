package mayo.edu.cts2.editor.client.widgets;

import mayo.edu.cts2.editor.client.Cts2Editor;
import mayo.edu.cts2.editor.client.datasource.ValueSetItemXmlDS;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
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
import mayo.edu.cts2.editor.client.events.AddSelectedEntitiesEvent;
import mayo.edu.cts2.editor.client.events.AddSelectedEntitiesEventHandler;

public class ValueSetItemsListGrid extends ListGrid {

	private static final int HEIGHT = 150;
	private static final String EMPTY_MESSAGE = "No entities to display.";

	public static final String ACTION_NONE = "NONE";
	public static final String ACTION_DELETE = "MARKED TO DELETE";
	public static final String ACTION_ADD = "MARKED TO ADD";

	public static final String ID_PK = "primaryKey";
	public static final String ID_URI = "uri";
	public static final String ID_NAME_SPACE = "namespace";
	public static final String ID_NAME = "name";
	public static final String ID_DESIGNATION = "designation";
	public static final String ID_ACTION = "action";
	public static final String ID_HIDDEN_ACTION = "hiddenValue";
	public static final String ID_CODE_SYSTEM_VERSION = "codeSystemVersion";

	public static final String TITLE_NAME_SPACE = "Code System Name";
	public static final String TITLE_NAME = "Code";
	public static final String TITLE_DESIGNATION = "Description";
	public static final String TITLE_ACTION = "Action";
	public static final String TITLE_CODE_SYSTEM_VERSION = "Code System Version";

	public ValueSetItemsListGrid() {
		super();

		setWidth100();
		setHeight(HEIGHT);
		setShowAllRecords(true);
		setShowAllColumns(true);
		setWrapCells(false);

		// hover attributes
		setCanHover(true);
		setShowHover(true);
		setShowHoverComponents(true);
		setHoverMoveWithMouse(true);
		setHoverWidth(200);
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

		ListGridField codeSystemVersion = new ListGridField(ID_CODE_SYSTEM_VERSION, TITLE_CODE_SYSTEM_VERSION);
		codeSystemVersion.setHidden(true);

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
		actionField.setWidth("126px");
		actionField.setCanEdit(false);
		actionField.setAttribute(ID_HIDDEN_ACTION, ACTION_NONE);
		if (Cts2Editor.getReadOnly())
			actionField.setHidden(true);

		setFields(nameField, designationField, nameSpaceField, actionField, codeSystemVersion);
		setAutoFetchData(true);

		setCanDragResize(true);
		setDragAppearance(DragAppearance.TARGET);

		addEventhandlers();
	}

	@Override
	/**
	 * Update the Action field
	 */
	protected Canvas createRecordComponent(final ListGridRecord record, final Integer colNum) {

		String fieldName = this.getFieldName(colNum);

		if (fieldName.equals(ID_ACTION) && record.getAttribute(ID_HIDDEN_ACTION) != null) {

			// if this is a row marked for delete, then show the "undo delete"
			// option
			if (record.getAttribute(ID_HIDDEN_ACTION).equals(ACTION_DELETE)) {

				HLayout recordCanvas = new HLayout(1);
				recordCanvas.setHeight(22);
				recordCanvas.setAlign(Alignment.LEFT);
				ImgButton undoImg = createImage("undo.png", "Undo Delete");

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

				Label dataLabel = new Label("<em style=\"font-weight:bold; color:red;margin-left:8px\">"
				        + ACTION_DELETE + "</em>");
				dataLabel.setAutoFit(true);
				dataLabel.setWrap(false);
				recordCanvas.addMember(dataLabel);

				return recordCanvas;
			}
			// if this is a row was added, then show the "delete" option
			else if (record.getAttribute(ID_HIDDEN_ACTION).equals(ACTION_ADD)) {

				HLayout recordCanvas = new HLayout(1);
				recordCanvas.setHeight(22);
				recordCanvas.setAlign(Alignment.LEFT);
				ImgButton undoImg = createImage("delete.png", "Remove");

				undoImg.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						removeData(record);
					}
				});

				recordCanvas.addMember(undoImg);

				Label dataLabel = new Label("<em style=\"font-weight:bold; color:green;margin-left:8px\">" + ACTION_ADD
				        + "</em>");
				dataLabel.setAutoFit(true);
				dataLabel.setWrap(false);
				recordCanvas.addMember(dataLabel);

				return recordCanvas;
			}
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

		setData(new ListGridRecord[0]);

		// all the datasource to get data.
		ValueSetItemXmlDS ds = (ValueSetItemXmlDS) getDataSource();
		ds.setShouldGetData(true);

		ds.fetchData(criteria, new DSCallback() {
			// getDataSource().fetchData(criteria, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {

				// populate the ListGrid with the data in the DataSource
				fetchData();
			}
		});

	}

	@Override
	public void fetchRelatedData(Record record, DataSource dataSource) {
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
		super.removeSelectedData();
	}

	@Override
	public void removeSelectedData(DSCallback callback, DSRequest requestProperties) {
		// TODO Auto-generated method stub
		super.removeSelectedData(callback, requestProperties);
	}

	@Override
	public boolean saveAllEdits() {
		return super.saveAllEdits();
	}

	/**
	 * Create and add a new record.
	 * 
	 * @param href
	 * @param code
	 * @param codeSystemName
	 * @param designation
	 */
	public void createNewRecord(String href, String code, String codeSystemName, String codeSystemVersion, String designation) {

		ListGridRecord newRecord = new ListGridRecord();

		// Set the PK -- Note: we are setting the uri with the href. The Value
		// Set Entities Search does not have a URI. This is only used as a
		// unique ID.
		newRecord.setAttribute(ID_URI, href);
		newRecord.setAttribute(ID_NAME, code);
		newRecord.setAttribute(ID_NAME_SPACE, codeSystemName);
		newRecord.setAttribute(ID_DESIGNATION, designation);
		newRecord.setAttribute("codeSystemVersion", codeSystemVersion);

		// add a hidden attribute to indicate it was added.
		newRecord.setAttribute(ID_HIDDEN_ACTION, ACTION_ADD);
		addData(newRecord);
	}

	private ImgButton createImage(String imgName, String prompt) {

		ImgButton undoImg = new ImgButton();
		undoImg.setShowDown(false);
		undoImg.setShowRollOver(false);
		undoImg.setLayoutAlign(Alignment.CENTER);
		undoImg.setSrc(imgName);
		undoImg.setPrompt(prompt);
		undoImg.setHeight(16);
		undoImg.setWidth(16);

		return undoImg;
	}

	private void addEventhandlers() {
		Cts2Editor.EVENT_BUS.addHandler(AddSelectedEntitiesEvent.TYPE, new AddSelectedEntitiesEventHandler() {
			@Override
			public void onSelectedEntriesAdded(AddSelectedEntitiesEvent event) {
				setData(event.getSelectedEntites());
			}
		});
	}

}
