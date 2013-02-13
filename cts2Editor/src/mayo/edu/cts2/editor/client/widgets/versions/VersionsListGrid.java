package mayo.edu.cts2.editor.client.widgets.versions;

import mayo.edu.cts2.editor.client.datasource.ValueSetVersionsXmlDS;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class VersionsListGrid extends ListGrid {

	public static final String EMPTY_MESSAGE = "No versions to display.";
	public static final String ERROR_MESSAGE = "Value set service unavailable.";

	public static final String ID_FORMAL_NAME = "formalName";
	public static final String ID_VERSION = "versionTag";
	public static final String ID_COMMENTS = "comment";

	public static final String TITLE_FORMAL_NAME = "Formal Name";
	public static final String TITLE_VERSION = "Version ID";
	public static final String TITLE_COMMENTS = "Comments";

	private ValueSetVersionsXmlDS i_valueSetVersionsXmlDS;

	public VersionsListGrid() {
		super();

		i_valueSetVersionsXmlDS = ValueSetVersionsXmlDS.getInstance();

		setWidth100();
		setHeight100();
		setShowAllRecords(true);
		setWrapCells(false);
		setDataSource(i_valueSetVersionsXmlDS);
		setEmptyMessage(EMPTY_MESSAGE);

		ListGridField formalNameField = new ListGridField(ID_FORMAL_NAME, TITLE_FORMAL_NAME);
		formalNameField.setWidth("40%");
		formalNameField.setWrap(false);
		formalNameField.setShowHover(true);

		ListGridField versionField = new ListGridField(ID_VERSION, TITLE_VERSION);
		versionField.setWidth(80);
		versionField.setWrap(false);
		versionField.setShowHover(true);

		ListGridField commentField = new ListGridField(ID_COMMENTS, TITLE_COMMENTS);
		commentField.setWidth("*");
		commentField.setWrap(false);
		commentField.setShowHover(true);

		setFields(formalNameField, versionField, commentField);

		setSelectionAppearance(SelectionAppearance.ROW_STYLE);
		setSelectionType(SelectionStyle.SINGLE);

		setAutoFetchData(false);

		setCanHover(true);
		setShowHover(true);
		setShowHoverComponents(true);

		// set the initial sort
		SortSpecifier[] sortspec = new SortSpecifier[1];
		sortspec[0] = new SortSpecifier(ID_VERSION, SortDirection.ASCENDING);
		setInitialSort(sortspec);
	}

	@Override
	public void destroy() {

		i_valueSetVersionsXmlDS.destroy();
		i_valueSetVersionsXmlDS = null;

		super.destroy();
	}

	/**
	 * Pass in the criteria to the Datasource to fetch.
	 * 
	 * @param criteria
	 */
	public void getData(Criteria criteria) {

		fetchData(criteria);
		i_valueSetVersionsXmlDS.fetchData(criteria, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				fetchData();
			}
		});

	}
}
