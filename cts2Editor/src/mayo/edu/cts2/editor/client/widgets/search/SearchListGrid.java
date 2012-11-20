package mayo.edu.cts2.editor.client.widgets.search;

import mayo.edu.cts2.editor.client.widgets.BaseValueSetsListGrid;

import com.smartgwt.client.data.Record;

public abstract class SearchListGrid extends BaseValueSetsListGrid {

	public static final String ID_ADD = "add";
	public static final String TITLE_ADD = "Add";

	abstract void getData(String searchText);
	abstract void clearData();

	/**
	 * Determine if there are any rows that are selected for add
	 * 
	 * @return
	 */
	public boolean hasAddRecords() {
		boolean checkedRow = false;

		Record[] records = getRecords();
		for (int i = 0; !checkedRow && i < records.length; i++) {
			checkedRow = records[i].getAttributeAsBoolean(ID_ADD);
		}

		return checkedRow;
	}
}
