package mayo.edu.cts2.editor.client.widgets.search;

import java.util.ArrayList;
import java.util.Map;

import mayo.edu.cts2.editor.client.datasource.ValueSetItemSearchXmlDS;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

/**
 * ListGrid for displaying search results for a entities.
 */
public class SearchValueSetItemsListGrid extends ListGrid {

	private static final String EMPTY_MESSAGE = "No entities to display.";
	private static final String ERROR_MESSAGE = "Entity service unavailable.";

	public static final String ID_NAME_SPACE = "nameSpace";
	public static final String ID_NAME = "name";
	public static final String ID_DESIGNATION = "designation";

	public static final String TITLE_NAME_SPACE = "Code System Name";
	public static final String TITLE_NAME = "Code";
	public static final String TITLE_DESIGNATION = "Description";

	private final ValueSetItemSearchXmlDS i_valueSetItemSearchXmlDS;
	private String i_searchString;

	public SearchValueSetItemsListGrid() {
		super();

		i_valueSetItemSearchXmlDS = ValueSetItemSearchXmlDS.getInstance();

		setWidth100();
		setHeight100();
		setShowAllRecords(true);
		setWrapCells(false);
		setDataSource(i_valueSetItemSearchXmlDS);
		setEmptyMessage(EMPTY_MESSAGE);

		ListGridField addField = new ListGridField("add", "Add");
		addField.setType(ListGridFieldType.BOOLEAN);
		addField.setShowHover(false);
		addField.setDefaultValue(false);
		addField.setCanEdit(true);

		ListGridField nameSpaceField = new ListGridField(ID_NAME_SPACE, TITLE_NAME_SPACE);
		nameSpaceField.setWrap(false);
		nameSpaceField.setWidth("25%");
		nameSpaceField.setShowHover(false);
		nameSpaceField.setCanEdit(false);

		nameSpaceField.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (value != null) {
					return addCellHighlights(value.toString());
				} else {
					return null;
				}
			}
		});

		ListGridField nameField = new ListGridField(ID_NAME, TITLE_NAME);
		nameField.setWrap(false);
		nameField.setWidth("25%");
		nameField.setShowHover(false);
		nameField.setCanEdit(false);

		nameField.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (value != null) {
					return addCellHighlights(value.toString());
				} else {
					return null;
				}
			}
		});

		ListGridField designationField = new ListGridField(ID_DESIGNATION, TITLE_DESIGNATION);
		designationField.setWrap(false);
		designationField.setWidth("*");
		designationField.setCanEdit(false);

		designationField.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (value != null) {
					return addCellHighlights(value.toString());
				} else {
					return null;
				}
			}
		});

		setFields(addField, nameField, nameSpaceField, designationField);

		setSelectOnEdit(true);
		setSelectionAppearance(SelectionAppearance.ROW_STYLE);
		setSelectionType(SelectionStyle.SINGLE);

		// Set edit and edit event to get the download checkbox to work
		// correctly.
		setCanEdit(true);

		setAutoFetchData(false);

		setCanHover(true);
		setShowHover(true);
		setShowHoverComponents(true);

		// set the initial sort
		SortSpecifier[] sortspec = new SortSpecifier[1];
		sortspec[0] = new SortSpecifier(ID_NAME, SortDirection.ASCENDING);
		setInitialSort(sortspec);
	}

	public ListGridRecord[] getRecordsToAdd() {

		boolean checkedRow;
		ArrayList<ListGridRecord> selectedRecords = new ArrayList<ListGridRecord>();

		ListGridRecord[] records = getRecords();
		for (ListGridRecord record : records) {
			checkedRow = record.getAttributeAsBoolean("download");

			if (checkedRow) {
				selectedRecords.add(record);
			}
		}

		ListGridRecord[] finalRecords = selectedRecords.toArray(new ListGridRecord[selectedRecords.size()]);
		return finalRecords;
	}

	@Override
	protected Canvas getCellHoverComponent(Record record, Integer rowNum, Integer colNum) {
		// only show a custom DetailViewer for the description column only
		if (colNum == 1) {

			DetailViewer detailViewer = new DetailViewer();
			detailViewer.setWidth(400);

			// Define the fields that we want to display in the details popup.
			// These
			// fields are populated from the record of the selected ValueSets.
			DetailViewerField descripitonField = new DetailViewerField("value", "Description");
			DetailViewerField formalNameField = new DetailViewerField("formalName", "Formal Name");
			detailViewer.setFields(formalNameField, descripitonField);

			detailViewer.setData(new Record[]{record});
			return detailViewer;
		}
		return null;
	}

	/**
	 * Call the search to get the matching data.
	 * 
	 * @param searchText
	 */
	public void getData(String serviceName, String searchText, Map<String, String> filters) {
		/*
		 * if (!Cts2Viewer.s_showAll &&
		 * Authentication.getInstance().getCredentials(serviceName) == null ||
		 * serviceName.equals(Cts2Panel.SELECT_SERVER_MSG)) { // set to empty.
		 * don't do a search. setData(new ListGridRecord[0]); redraw();
		 * 
		 * // let others know that the data has been retrieved.
		 * Cts2Viewer.EVENT_BUS.fireEvent(new ValueSetsReceivedEvent()); }
		 * 
		 * else { i_searchString = searchText;
		 * 
		 * Criteria criteria = new Criteria();
		 * criteria.addCriteria("searchText", searchText);
		 * criteria.addCriteria("serviceName", serviceName);
		 * 
		 * for (String filterComponent : filters.keySet()) {
		 * criteria.addCriteria(filterComponent, filters.get(filterComponent));
		 * }
		 * 
		 * i_valueSetsXmlDS.fetchData(criteria, new DSCallback() {
		 * 
		 * @Override public void execute(DSResponse response, Object rawData,
		 * DSRequest request) {
		 * 
		 * if ((response != null) && (response.getAttribute("reason") != null))
		 * { setEmptyMessage("<b><font color=\"red\">" + ERROR_MESSAGE +
		 * "</font></b>"); } else { setEmptyMessage(EMPTY_MESSAGE); }
		 * 
		 * setData(new ListGridRecord[0]); fetchData();
		 * 
		 * redraw(); // let others know that the data has been retrieved.
		 * Cts2Viewer.EVENT_BUS.fireEvent(new ValueSetsReceivedEvent()); } }); }
		 */
	}

	/**
	 * Highlight any of the text that matches the searchString.
	 * 
	 * @param value
	 * @return
	 */
	private String addCellHighlights(String cellText) {

		if (i_searchString == null || i_searchString.length() == 0) {
			return cellText;
		}

		String lowerCaseCellText = cellText.toLowerCase();
		int startIndex = lowerCaseCellText.indexOf(i_searchString.toLowerCase());

		if (startIndex >= 0) {
			int first = startIndex;

			cellText = cellText.substring(0, first) + "<b style=\"color:#e33b74\">"
			        + cellText.substring(startIndex, startIndex + i_searchString.length()) + "</b>"
			        + cellText.substring(startIndex + i_searchString.length());
		}

		return cellText;
	}

}
