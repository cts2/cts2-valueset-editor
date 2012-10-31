package mayo.edu.cts2.editor.client.widgets;

import java.util.Date;

import mayo.edu.cts2.editor.client.datasource.ValueSetsXmlDS;

import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ValueSetsListGrid extends ListGrid {

	private static final int HEIGHT = 200;

	private static final String EMPTY_MESSAGE = "No value sets to display.";

	private final ValueSetsXmlDS i_valueSetsXmlDS;
	private String i_xmlData;

	public ValueSetsListGrid(String oid) {
		super();

		i_valueSetsXmlDS = new ValueSetsXmlDS(oid);

		setWidth100();
		setHeight(HEIGHT);
		setShowAllRecords(true);
		setWrapCells(false);
		setDataSource(i_valueSetsXmlDS);
		setEmptyMessage(EMPTY_MESSAGE);

		// this value needs to be set to create the expander list grid.
		setCanExpandRecords(true);

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

		// When hover is on the arrow to open the sub list grid has a hover and
		// is blank...
		// setCanHover(true);
		// setShowHover(true);
		// setShowHoverComponents(true);

		// set the initial sort
		SortSpecifier[] sortspec = new SortSpecifier[1];
		sortspec[0] = new SortSpecifier("formalName", SortDirection.ASCENDING);
		setInitialSort(sortspec);

	}

	@Override
	/**
	 * Create the ListGrid that has the entities for this parent record.
	 */
	protected Canvas getExpansionComponent(final ListGridRecord record) {

		final ListGrid grid = this;

		VLayout layout = new VLayout(5);
		layout.setPadding(5);

		String vsId = record.getAttribute("valueSetName");
		String uniqueId = "valueSetIdentiers" + new Date().getTime();

		final ValueSetItemsListGrid valueSetItemsListGrid = new ValueSetItemsListGrid(uniqueId, vsId);
		layout.addMember(valueSetItemsListGrid);

		HLayout buttonLayout = new HLayout(10);
		buttonLayout.setAlign(Alignment.CENTER);

		IButton saveButton = new IButton("Save");
		saveButton.setTop(250);
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				valueSetItemsListGrid.saveAllEdits();
			}
		});
		buttonLayout.addMember(saveButton);

		IButton discardButton = new IButton("Discard");
		discardButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				valueSetItemsListGrid.discardAllEdits();
			}
		});
		buttonLayout.addMember(discardButton);

		IButton closeButton = new IButton("Close");
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				grid.collapseRecord(record);
			}
		});

		buttonLayout.addMember(closeButton);
		layout.addMember(buttonLayout);
		return layout;
	}

	// @Override
	// protected Canvas getCellHoverComponent(Record record, Integer rowNum,
	// Integer colNum) {
	// // only show a custom DetailViewer for the description column only
	// if (colNum == 1) {
	//
	// DetailViewer detailViewer = new DetailViewer();
	// detailViewer.setWidth(400);
	//
	// // Define the fields that we want to display in the details popup.
	// // These fields are populated from the record of the selected
	// // ValueSets.
	// DetailViewerField descripitonField = new DetailViewerField("value",
	// "Description");
	// DetailViewerField formalNameField = new DetailViewerField("formalName",
	// "Formal Name");
	// detailViewer.setFields(formalNameField, descripitonField);
	//
	// detailViewer.setData(new Record[]{record});
	// return detailViewer;
	// }
	// return null;
	// }

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

}
