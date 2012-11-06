package mayo.edu.cts2.editor.client.widgets;

import mayo.edu.cts2.editor.client.datasource.ValueSetItemXmlDS;
import mayo.edu.cts2.editor.client.datasource.ValueSetsXmlDS;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

public class ValueSetsListGrid extends ListGrid {

	private static final String EMPTY_MESSAGE = "No value sets to display.";

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

		// set the initial sort
		SortSpecifier[] sortspec = new SortSpecifier[1];
		sortspec[0] = new SortSpecifier("formalName", SortDirection.ASCENDING);
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

		System.out.println("getExpansionComponent called");

		VLayout layout = new VLayout(5);
		layout.setPadding(5);

		DataSource childDatasource = getRelatedDataSource(record);
		ValueSetEntitiesLayout valueSetEntitiesLayout = new ValueSetEntitiesLayout(record, childDatasource, this);

		return valueSetEntitiesLayout;
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

}
