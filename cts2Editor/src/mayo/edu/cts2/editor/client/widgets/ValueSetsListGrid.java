package mayo.edu.cts2.editor.client.widgets;

import mayo.edu.cts2.editor.client.datasource.ValueSetItemXmlDS;
import mayo.edu.cts2.editor.client.datasource.ValueSetsXmlDS;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ValueSetsListGrid extends BaseValueSetsListGrid {

	private final ValueSetsXmlDS i_valueSetsXmlDS;
	private String i_xmlData;

	public ValueSetsListGrid() {
		super();

		i_valueSetsXmlDS = ValueSetsXmlDS.getInstance();

		setWidth100();
		setHeight100();
		setShowAllRecords(true);
		setWrapCells(false);
		setDataSource(i_valueSetsXmlDS);
		setEmptyMessage(EMPTY_MESSAGE);

		setShowRecordComponents(true);
		setShowRecordComponentsByCell(true);

		// this value needs to be set to create the expander list grid.
		setCanExpandRecords(true);

		ListGridField resourceNamefField = new ListGridField(ID_VALUE_SET_NAME, TITLE_VALUE_SET_NAME);
		resourceNamefField.setWidth("40%");
		resourceNamefField.setWrap(false);
		resourceNamefField.setShowHover(true);
		resourceNamefField.setCanEdit(false);

		ListGridField formalNameField = new ListGridField(ID_FORMAL_NAME, TITLE_FORMAL_NAME);
		formalNameField.setWidth("40%");
		formalNameField.setWrap(false);
		formalNameField.setShowHover(true);
		formalNameField.setCanEdit(false);

		ListGridField actionField = new ListGridField(ID_ACTION, TITLE_ACTION);
		actionField.setWrap(false);
		actionField.setWidth("*");
		actionField.setCanEdit(false);
		actionField.setAttribute(ID_HIDDEN_ACTION, ACTION_NONE);

		setFields(formalNameField, resourceNamefField, actionField);

		setSelectOnEdit(true);
		setSelectionAppearance(SelectionAppearance.ROW_STYLE);
		setSelectionType(SelectionStyle.SINGLE);

		// set the initial sort
		SortSpecifier[] sortspec = new SortSpecifier[1];
		sortspec[0] = new SortSpecifier(ID_FORMAL_NAME, SortDirection.ASCENDING);
		setInitialSort(sortspec);
	}

	/**
	 * Get the related datasource to show the inner grid in.
	 */
	@Override
	public DataSource getRelatedDataSource(ListGridRecord record) {

		String oid = record.getAttribute("valueSetName");

		// create a unique ID for the datasource id.
		oid = oid.trim().replace('.', '_');
		oid = "ValueSetItemXmlDS" + oid;

		return ValueSetItemXmlDS.getInstance(oid);
	}
	@Override
	/**
	 * Create the ListGrid that has the entities for this parent record.
	 */
	protected Canvas getExpansionComponent(final ListGridRecord record) {

		VLayout layout = new VLayout(5);
		layout.setPadding(5);

		DataSource childDatasource = getRelatedDataSource(record);
		ValueSetEntitiesLayout valueSetEntitiesLayout = new ValueSetEntitiesLayout(record, childDatasource, this);

		return valueSetEntitiesLayout;
	}

	@Override
	/**
	 * Update the Action field
	 */
	protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {

		String fieldName = this.getFieldName(colNum);

		if (fieldName.equals(ID_ACTION) && record.getAttribute(ID_HIDDEN_ACTION) != null
		        && record.getAttribute(ID_HIDDEN_ACTION).equals(ACTION_ADD)) {

			HLayout recordCanvas = new HLayout(1);
			recordCanvas.setHeight(22);
			recordCanvas.setAlign(Alignment.LEFT);
			ImgButton undoImg = createUndoImage("Remove added value set");

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

		return super.createRecordComponent(record, colNum);
	}

	/**
	 * Call the search to get the matching data.
	 * 
	 * @param searchText
	 */
	public void populateData(String xmlData) {

		i_xmlData = xmlData;
		i_valueSetsXmlDS.setData(i_xmlData);

		fetchData();
		redraw();
	}

	/**
	 * Create and add a new record.
	 * 
	 * @param formalName
	 * @param vsIdentifier
	 */
	public void createNewRecord(String formalName, String vsIdentifier) {

		ListGridRecord newRecord = new ListGridRecord();
		newRecord.setAttribute(ID_FORMAL_NAME, formalName);
		newRecord.setAttribute(ID_VALUE_SET_NAME, vsIdentifier);

		// add a hidden attribute to indicate it was added.
		newRecord.setAttribute(ID_HIDDEN_ACTION, ACTION_ADD);
		addData(newRecord);
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
