package mayo.edu.cts2.editor.client.widgets;

import mayo.edu.cts2.editor.client.Cts2Editor;
import mayo.edu.cts2.editor.client.events.AddRecordsEvent;
import mayo.edu.cts2.editor.client.events.AddRecordsEventHandler;
import mayo.edu.cts2.editor.client.widgets.search.SearchListGrid;
import mayo.edu.cts2.editor.client.widgets.search.SearchValueSetItemsListGrid;
import mayo.edu.cts2.editor.client.widgets.search.SearchWindow;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
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

	private static final String TITLE_ENTITIES = "Entities";

	private final boolean i_additionsMade = false;
	private boolean i_removalsMade = false;
	private final ValueSetItemsListGrid i_valueSetItemsListGrid;
	private SearchWindow i_searchWindow;

	private final IButton i_addButton;
	private final IButton i_deleteButton;
	private final IButton i_saveButton;
	private final IButton i_saveAsButton;
	private final IButton i_closeButton;

	public ValueSetEntitiesLayout(final ListGridRecord record, DataSource childDatasource, final ListGrid parentGrid) {
		super();

		setPadding(5);

		String oid = record.getAttribute("valueSetName");
		Criteria criteria = new Criteria();
		criteria.setAttribute("oid", oid);

		Label titleLabel = getTitleLabel();
		addMember(titleLabel);

		i_valueSetItemsListGrid = new ValueSetItemsListGrid();

		i_valueSetItemsListGrid.setDataSource(childDatasource);
		i_valueSetItemsListGrid.fetchData(criteria);

		addMember(i_valueSetItemsListGrid);

		HLayout buttonLayout = new HLayout(10);
		buttonLayout.setMargin(5);
		buttonLayout.setAlign(Alignment.CENTER);

		i_addButton = new IButton("Add...");
		i_addButton.setTop(250);
		i_addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String message = "Search for entities.  Select the entities by checking the checkbox.";
				i_searchWindow = new SearchWindow(new SearchValueSetItemsListGrid(), message);
				i_searchWindow.setInitialFocus();
				i_searchWindow.show();
			}
		});

		i_saveButton = new IButton("Save");
		i_saveButton.setTop(250);
		i_saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				i_valueSetItemsListGrid.saveAllEdits();
			}
		});

		i_saveAsButton = new IButton("Save As...");
		i_saveAsButton.setTop(250);
		i_saveAsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				i_valueSetItemsListGrid.saveAllEdits();

			}
		});

		i_closeButton = new IButton("Close");
		i_closeButton.addClickHandler(new ClickHandler() {
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
								String datasourceId = i_valueSetItemsListGrid.getDataSource().getID();
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

		i_deleteButton = new IButton("Delete Row(s)");
		i_deleteButton.setTop(250);
		i_deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				i_removalsMade = true;

				// enable these buttons.
				i_saveButton.setDisabled(false);
				i_saveAsButton.setDisabled(false);

				ListGridRecord[] records = i_valueSetItemsListGrid.getSelectedRecords();
				for (ListGridRecord selectedRecord : records) {
					selectedRecord.setAttribute(ValueSetItemsListGrid.ID_HIDDEN_ACTION,
					        ValueSetItemsListGrid.ACTION_DELETE);
					i_valueSetItemsListGrid.updateData(selectedRecord);
				}

				// refresh the icons in Action column
				i_valueSetItemsListGrid.invalidateRecordComponents();
			}
		});

		// default these buttons to disabled.
		i_deleteButton.setDisabled(true);
		i_saveButton.setDisabled(true);
		i_saveAsButton.setDisabled(true);

		// add the buttons to the layout
		buttonLayout.addMember(i_addButton);
		buttonLayout.addMember(i_deleteButton);
		buttonLayout.addMember(i_saveButton);
		buttonLayout.addMember(i_saveAsButton);
		buttonLayout.addMember(i_closeButton);

		// Don't add the buttons if we are in readOnly mode.
		if (!Cts2Editor.getReadOnly()) {
			addMember(buttonLayout);
		}

		// listen for selection changes to update the buttons
		i_valueSetItemsListGrid.addSelectionUpdatedHandler(new SelectionUpdatedHandler() {

			@Override
			public void onSelectionUpdated(SelectionUpdatedEvent event) {
				ListGridRecord[] records = i_valueSetItemsListGrid.getSelectedRecords();

				boolean disableDelete = false;
				String hiddenAction;

				for (Record record : records) {

					hiddenAction = record.getAttribute(ValueSetItemsListGrid.ID_HIDDEN_ACTION);
					if (hiddenAction != null
					        && (hiddenAction.equals(ValueSetItemsListGrid.ACTION_ADD) || hiddenAction
					                .equals(ValueSetItemsListGrid.ACTION_DELETE))) {
						disableDelete = true;
						break;
					}
				}
				i_deleteButton.setDisabled(disableDelete);
			}
		});

		createAddRecordEvent();
	}

	/**
	 * Create a label for the entites list grid.
	 * 
	 * @return
	 */
	private Label getTitleLabel() {
		Label label = new Label("<b>" + TITLE_ENTITIES + "</b>");
		label.setWidth100();
		label.setHeight(25);

		return label;
	}

	private void createAddRecordEvent() {

		Cts2Editor.EVENT_BUS.addHandler(AddRecordsEvent.TYPE, new AddRecordsEventHandler() {

			@Override
			public void onRecordsAdded(AddRecordsEvent event) {

				if (i_searchWindow == null) {
					return;
				}

				SearchListGrid listGrid = i_searchWindow.getSearchListGrid();
				if (listGrid instanceof SearchValueSetItemsListGrid) {
					SearchValueSetItemsListGrid searchValueSetItemsListGrid = (SearchValueSetItemsListGrid) listGrid;

					Record[] records = searchValueSetItemsListGrid.getRecords();
					for (int i = 0; i < records.length; i++) {

						// if the checkbox is checked, then we need to add this
						// record
						if (records[i].getAttributeAsBoolean(SearchListGrid.ID_ADD)) {
							addValueSetRecord(records[i]);
						}
					}
				}
			}
		});
	}

	/**
	 * Add a record that the user selected to the Value Set Items.
	 * 
	 * @param record
	 */
	private void addValueSetRecord(Record record) {

		String code = record.getAttribute(SearchValueSetItemsListGrid.ID_NAME);
		String codeSystemName = record.getAttribute(SearchValueSetItemsListGrid.ID_NAME_SPACE);
		String designation = record.getAttribute(SearchValueSetItemsListGrid.ID_DESIGNATION);

		// this will be used as the PK
		String href = record.getAttribute(SearchValueSetItemsListGrid.ID_HREF);

		// System.out.println("href = " + href + " code = " + code +
		// " codeSystemName = " + codeSystemName
		// + " --- designation = " + designation);

		i_valueSetItemsListGrid.createNewRecord(href, code, codeSystemName, designation);
	}
}
