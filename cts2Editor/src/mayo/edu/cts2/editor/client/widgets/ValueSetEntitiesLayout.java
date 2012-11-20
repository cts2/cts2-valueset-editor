package mayo.edu.cts2.editor.client.widgets;

import mayo.edu.cts2.editor.client.widgets.search.SearchValueSetItemsListGrid;
import mayo.edu.cts2.editor.client.widgets.search.SearchWindow;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedEvent;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * VLayout to contain the ListGrid of entities and the buttons that action on
 * it.
 * 
 */
public class ValueSetEntitiesLayout extends VLayout {

	private boolean i_additionsMade = false;
	private boolean i_removalsMade = false;

	public ValueSetEntitiesLayout(final ListGridRecord record, DataSource childDatasource, final ListGrid parentGrid) {
		super();

		setPadding(5);

		String oid = record.getAttribute("valueSetName");
		Criteria criteria = new Criteria();
		criteria.setAttribute("oid", oid);

		final ValueSetItemsListGrid valueSetItemsListGrid = new ValueSetItemsListGrid();

		valueSetItemsListGrid.setDataSource(childDatasource);
		valueSetItemsListGrid.fetchData(criteria);

		addMember(valueSetItemsListGrid);

		HLayout buttonLayout = new HLayout(10);
		buttonLayout.setMargin(5);
		buttonLayout.setAlign(Alignment.CENTER);

		final IButton addButton = new IButton("Add...");
		addButton.setTop(250);
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				String message = "Search for entities.  Select the entities by checking the checkbox.";
				SearchWindow searchWindow = new SearchWindow(new SearchValueSetItemsListGrid(), message);
				searchWindow.show();

				// ... after the addition, set boolean
				i_additionsMade = true;
			}
		});

		final IButton saveButton = new IButton("Save");
		saveButton.setTop(250);
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				valueSetItemsListGrid.saveAllEdits();
			}
		});

		final IButton saveAsButton = new IButton("Save As...");
		saveAsButton.setTop(250);
		saveAsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				valueSetItemsListGrid.saveAllEdits();

			}
		});

		final IButton closeButton = new IButton("Close");
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				if (i_additionsMade || i_removalsMade) {

					String message = "Changes to the value set have been made.  Closing will discard your current changes for this value set.\n\nDo you want to discard your changes?";
					String title = "Changes Made";
					SC.ask(title, message, new BooleanCallback() {

						@Override
						public void execute(Boolean value) {
							if (value != null && value.booleanValue()) {

								// collapse the parent ListGrid
								parentGrid.collapseRecord(record);

								// User chose to discard changes
								// remove their Datasource so it will recreated
								// fresh next time.
								String datasourceId = valueSetItemsListGrid.getDataSource().getID();
								// ValueSetItemXmlDS.removeInstance(datasourceId);

							}

						}
					});

				} else {
					// collapse the parent ListGrid
					parentGrid.collapseRecord(record);
				}
			}
		});

		final IButton deleteButton = new IButton("Delete Row(s)");
		deleteButton.setTop(250);
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				i_removalsMade = true;

				// enable these buttons.
				saveButton.setDisabled(false);
				saveAsButton.setDisabled(false);

				ListGridRecord[] records = valueSetItemsListGrid.getSelectedRecords();
				for (ListGridRecord selectedRecord : records) {
					selectedRecord.setAttribute(ValueSetItemsListGrid.ID_HIDDEN_ACTION,
					        ValueSetItemsListGrid.ACTION_DELETE);
					valueSetItemsListGrid.updateData(selectedRecord);
				}

				// refresh the icons in Action column
				valueSetItemsListGrid.invalidateRecordComponents();
			}
		});

		// default these buttons to disabled.
		deleteButton.setDisabled(true);
		saveButton.setDisabled(true);
		saveAsButton.setDisabled(true);

		// add the buttons to the layout
		buttonLayout.addMember(addButton);
		buttonLayout.addMember(deleteButton);
		buttonLayout.addMember(saveButton);
		buttonLayout.addMember(saveAsButton);
		buttonLayout.addMember(closeButton);

		addMember(buttonLayout);

		// listen for selection changes to update the buttons
		valueSetItemsListGrid.addSelectionUpdatedHandler(new SelectionUpdatedHandler() {

			@Override
			public void onSelectionUpdated(SelectionUpdatedEvent event) {
				ListGridRecord[] records = valueSetItemsListGrid.getSelectedRecords();
				deleteButton.setDisabled(records.length == 0);
			}
		});
	}

}
