package mayo.edu.cts2.editor.client.widgets;

import mayo.edu.cts2.editor.client.datasource.ValueSetsXmlDS;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

public class ValueSetsListGrid extends ListGrid {

	private static final String EMPTY_MESSAGE = "No value sets to display.";
	private static final String ERROR_MESSAGE = "Value set service unavailable.";

	private final ValueSetsXmlDS i_valueSetsXmlDS;
	private String i_searchString;

	public ValueSetsListGrid(String oid, String data) {
		super();

		i_valueSetsXmlDS = new ValueSetsXmlDS(oid);

		setWidth100();
		setHeight100();
		setShowAllRecords(true);
		setWrapCells(false);
		setDataSource(i_valueSetsXmlDS);
		setEmptyMessage(EMPTY_MESSAGE);

		ListGridField resourceNamefField = new ListGridField("valueSetName", "Value Set Identifier");
		resourceNamefField.setWidth("*");
		resourceNamefField.setWrap(false);
		resourceNamefField.setShowHover(true);
		resourceNamefField.setCanEdit(false);

		ListGridField formalNameField = new ListGridField("formalName", "Formal Name");
		formalNameField.setWidth("55%");
		formalNameField.setWrap(false);
		formalNameField.setShowHover(true);
		formalNameField.setCanEdit(false);

		setFields(formalNameField, resourceNamefField);

		setSelectOnEdit(true);
		setSelectionAppearance(SelectionAppearance.ROW_STYLE);
		setSelectionType(SelectionStyle.SINGLE);

		setCanHover(true);
		setShowHover(true);
		setShowHoverComponents(true);

		// set the initial sort
		SortSpecifier[] sortspec = new SortSpecifier[1];
		sortspec[0] = new SortSpecifier("formalName", SortDirection.ASCENDING);
		setInitialSort(sortspec);
	}

	@Override
	protected Canvas getCellHoverComponent(Record record, Integer rowNum, Integer colNum) {
		// only show a custom DetailViewer for the description column only
		if (colNum == 1) {

			DetailViewer detailViewer = new DetailViewer();
			detailViewer.setWidth(400);

			// Define the fields that we want to display in the details popup.
			// These fields are populated from the record of the selected
			// ValueSets.
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
	public void getData(String serviceName, String searchText) {

		i_searchString = searchText;

		Criteria criteria = new Criteria();
		criteria.addCriteria("searchText", searchText);
		criteria.addCriteria("serviceName", serviceName);

		i_valueSetsXmlDS.fetchData(criteria, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {

				if ((response != null) && (response.getAttribute("reason") != null)) {
					setEmptyMessage("<b><font color=\"red\">" + ERROR_MESSAGE + "</font></b>");
				} else {
					setEmptyMessage(EMPTY_MESSAGE);
				}

				setData(new ListGridRecord[0]);
				fetchData();

				redraw();
				// let others know that the data has been retrieved.
				// Cts2Viewer.EVENT_BUS
				// .fireEvent(new ValueSetsReceivedEvent());
			}
		});

	}
}
