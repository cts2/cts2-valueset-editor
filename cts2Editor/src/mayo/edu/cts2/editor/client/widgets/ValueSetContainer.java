package mayo.edu.cts2.editor.client.widgets;

import com.smartgwt.client.util.SC;
import mayo.edu.cts2.editor.client.Cts2Editor;
import mayo.edu.cts2.editor.client.events.AddRecordsEvent;
import mayo.edu.cts2.editor.client.events.AddRecordsEventHandler;
import mayo.edu.cts2.editor.client.events.NewValueSetCreatedEvent;
import mayo.edu.cts2.editor.client.events.NewValueSetCreatedEventHandler;
import mayo.edu.cts2.editor.client.events.UpdateValueSetVersionEvent;
import mayo.edu.cts2.editor.client.events.UpdateValueSetVersionEventHandler;
import mayo.edu.cts2.editor.client.widgets.search.SearchValueSetsListGrid;
import mayo.edu.cts2.editor.client.widgets.search.SearchWindow;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Vertical Layout to hold a title and a ListGrid.
 */
public class ValueSetContainer extends VLayout {

	private static final int TITLE_HEIGHT = 30;
	private static final String TITLE = "<em style=\"font-size:1.2em;font-weight:bold; margin-left:5px\">Value Sets</em>";

	private static final int BUTTON_LAYOUT_HEIGHT = 25;
	private static final String BUTTON_ADD_TITLE = "Add Existing...";

	private final Label i_title;
	private final ValueSetsListGrid i_valueSetsListGrid;
	private IButton i_addButton;
	private SearchWindow i_searchWindow;

	public ValueSetContainer(ValueSetsListGrid valueSetListGrid) {
		super();

		setWidth100();
		setMargin(5);

		i_title = createTitle();
		i_valueSetsListGrid = valueSetListGrid;
		HLayout buttonLayout = createButtonLayout();

		addMember(i_title);
		addMember(i_valueSetsListGrid);

		// Don't add the buttons if we are in readOnly mode.
		if (!Cts2Editor.getReadOnly()) {
			// addMember(buttonLayout); // don't need this add button at all.
		}

		createEventHandlers();
	}

	private HLayout createButtonLayout() {
		HLayout layout = new HLayout();
		layout.setWidth100();
		layout.setMargin(10);
		layout.setMembersMargin(5);
		layout.setHeight(BUTTON_LAYOUT_HEIGHT);
		layout.setAlign(Alignment.CENTER);

		i_addButton = new IButton(BUTTON_ADD_TITLE);
		i_addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				i_searchWindow = new SearchWindow();
				i_searchWindow.setInitialFocus();
				i_searchWindow.show();
			}
		});

		IButton createButton = new IButton("Create New...");
		createButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CreateValueSetWindow metadataWindow = new CreateValueSetWindow();
				metadataWindow.show();
				metadataWindow.setInitialFocus();
			}
		});

		layout.addMember(i_addButton);
		layout.addMember(createButton);

		return layout;
	}

	/**
	 * Create the Label that contains the title
	 * 
	 * @return
	 */
	private Label createTitle() {
		Label titleLabel = new Label("<b>" + TITLE + "</b>");
		titleLabel.setWidth100();
		titleLabel.setHeight(TITLE_HEIGHT);

		return titleLabel;
	}

	private void createEventHandlers() {
		createAddRecordEvent();
		createUpdateRecordEvent();
		createNewValueSetEvent();
	}

	private void createAddRecordEvent() {

		Cts2Editor.EVENT_BUS.addHandler(AddRecordsEvent.TYPE, new AddRecordsEventHandler() {

			@Override
			public void onRecordsAdded(AddRecordsEvent event) {

				if (i_searchWindow == null) {
					return;
				}

					SearchValueSetsListGrid searchValueSetsListGrid = i_searchWindow.getSearchListGrid();

					Record[] records = searchValueSetsListGrid.getRecords();
					for (int i = 0; i < records.length; i++) {

						// if the checkbox is checked, then we need to add this
						// record
						if (records[i].getAttributeAsBoolean(SearchValueSetsListGrid.ID_ADD)) {
							addValueSetRecord(records[i]);
						}
					}
			}
		});

	}

	/**
	 * Listen for when a value set version has been updated.
	 */
	private void createUpdateRecordEvent() {

		Cts2Editor.EVENT_BUS.addHandler(UpdateValueSetVersionEvent.TYPE, new UpdateValueSetVersionEventHandler() {

			@Override
			public void onValueSetVersionUpdated(UpdateValueSetVersionEvent event) {
				String comment = event.getComment();
				String valueSetId = event.getValueSetId();
				String versionId = event.getVersionId();
				String changeSetId = event.getChangeSetUri();
				String documentUri = event.getDocumentUri();

				// find the record to update and update it.
				ListGridRecord recordToUpdate = findRecord(valueSetId);

				if (recordToUpdate != null) {
					i_valueSetsListGrid.updateRecord(recordToUpdate, valueSetId, versionId, comment, changeSetId,
					        documentUri);
				}
			}
		});
	}

	private void createNewValueSetEvent() {
		Cts2Editor.EVENT_BUS.addHandler(NewValueSetCreatedEvent.TYPE, new NewValueSetCreatedEventHandler() {
			@Override
			public void onNewValueSetCreated(NewValueSetCreatedEvent event) {
				/* TODO: Deal with new value set. */
				SC.say("New Value Set created:<br/>VS:<br/>Name: " + event.getDefinition().getValueSetOid()
				+ "<br/>Uri: " + event.getDefinition().getValueSetUri()
				+ "<br/>DefName: " + event.getDefinition().getName()
				+ "<br/>DefVersion: " + event.getDefinition().getVersion()
				+ "<br/>Entities: " + event.getDefinition().getEntries().size());
			i_valueSetsListGrid.createNewRecord(event.getDefinition().getName(), event.getDefinition().getValueSetOid(), "");
			}
		});
	}

	/**
	 * Find the record with the matching oid/value set id.
	 * 
	 * @param oid
	 * @return
	 */
	private ListGridRecord findRecord(String oid) {

		ListGridRecord[] records = i_valueSetsListGrid.getRecords();
		for (ListGridRecord record : records) {
			if (record.getAttribute(ValueSetsListGrid.ID_VALUE_SET_NAME).equals(oid)) {
				return record;
			}
		}

		return null;
	}

	/**
	 * Add a record that the user selected to the ValueSets.
	 * 
	 * @param record
	 */
	private void addValueSetRecord(Record record) {

		String formalName = record.getAttribute(SearchValueSetsListGrid.ID_FORMAL_NAME);
		String vsIdentifier = record.getAttribute(SearchValueSetsListGrid.ID_VALUE_SET_NAME);
		String description = record.getAttribute(SearchValueSetsListGrid.ID_DESCRIPTION);

		i_valueSetsListGrid.createNewRecord(formalName, vsIdentifier, description);
	}
}
