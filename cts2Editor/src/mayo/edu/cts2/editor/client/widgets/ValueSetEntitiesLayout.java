package mayo.edu.cts2.editor.client.widgets;

import java.util.ArrayList;
import java.util.List;

import mayo.edu.cts2.editor.client.Cts2Editor;
import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.client.Cts2EditorServiceAsync;
import mayo.edu.cts2.editor.client.datasource.ValueSetItemXmlDS;
import mayo.edu.cts2.editor.client.debug.DebugPanel;
import mayo.edu.cts2.editor.client.events.AddRecordsEvent;
import mayo.edu.cts2.editor.client.events.AddRecordsEventHandler;
import mayo.edu.cts2.editor.client.events.SaveAsEvent;
import mayo.edu.cts2.editor.client.events.SaveAsEventHandler;
import mayo.edu.cts2.editor.client.events.UpdateValueSetVersionEvent;
import mayo.edu.cts2.editor.client.utils.ModalWindow;
import mayo.edu.cts2.editor.client.widgets.search.SearchListGrid;
import mayo.edu.cts2.editor.client.widgets.search.SearchValueSetItemsListGrid;
import mayo.edu.cts2.editor.client.widgets.search.SearchWindow;
import mayo.edu.cts2.editor.shared.CTS2Result;
import mayo.edu.cts2.editor.shared.Definition;
import mayo.edu.cts2.editor.shared.DefinitionEntry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
 * VLayout to contain the ListGrid of entities and the buttons that act on it.
 * 
 */
public class ValueSetEntitiesLayout extends VLayout {

	private static final String TITLE_ENTITIES = "Entities";

	private static final String HINT_ADD = "Add one or more value set entries";
	private static final String HINT_DELETE = "Delete one or more value set entries";
	private static final String HINT_SAVE_AS = "Save the value set as a new branch";
	private static final String HINT_SAVE = "Save the value set on the current branch";
	private static final String HINT_CLOSE = "Close the value set entries list";

	private final ListGridRecord i_valueSetRecord;

	private boolean i_additionsMade = false;
	private boolean i_removalsMade = false;
	private final ValueSetItemsListGrid i_valueSetItemsListGrid;
	private SearchWindow i_searchWindow;
	private final ListGrid i_parentGrid;

	private final IButton i_addButton;
	private final IButton i_deleteButton;
	private final IButton i_saveButton;
	private final IButton i_saveAsButton;
	private final IButton i_closeButton;

	private ModalWindow i_busyIndicator;

	// private boolean i_okToClose = false;

	public ValueSetEntitiesLayout(final ListGridRecord record, DataSource childDatasource, final ListGrid parentGrid) {
		super();

		setPadding(5);

		i_parentGrid = parentGrid;
		i_valueSetRecord = record;
		Criteria criteria = getCriteriaFromValueSetRecord(i_valueSetRecord);

		Label titleLabel = getTitleLabel();
		addMember(titleLabel);

		i_valueSetItemsListGrid = new ValueSetItemsListGrid();
		i_valueSetItemsListGrid.setHeight(500);

		i_valueSetItemsListGrid.setDataSource(childDatasource);
		i_valueSetItemsListGrid.fetchData(criteria);

		addMember(i_valueSetItemsListGrid);

		HLayout buttonLayout = new HLayout(10);
		buttonLayout.setMargin(5);
		buttonLayout.setAlign(Alignment.CENTER);

		i_addButton = new IButton("Add...");
		i_addButton.setTop(250);
		i_addButton.setShowHover(true);
		i_addButton.setPrompt(HINT_ADD);
		i_addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String message = "Search for entities.  Select the entities by checking the checkbox.  Start typing in the search field to retrieve search results.";
				i_searchWindow = new SearchWindow(new SearchValueSetItemsListGrid(), message);
				i_searchWindow.setInitialFocus();
				i_searchWindow.show();
			}
		});

		i_saveButton = new IButton("Save");
		i_saveButton.setTop(250);
		i_saveButton.setShowHover(true);
		i_saveButton.setPrompt(HINT_SAVE);
		i_saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// i_valueSetItemsListGrid.saveAllEdits();
				ListGridRecord[] records = i_valueSetItemsListGrid.getRecords();
				String comment = i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_COMMENT);

				saveValueSetEntities(records, comment);
			}
		});

		i_saveAsButton = new IButton("Save As...");
		i_saveAsButton.setTop(250);
		i_saveAsButton.setPrompt(HINT_SAVE_AS);
		i_saveAsButton.setShowHover(true);
		i_saveAsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getCommentsForNewVersion();
			}
		});

		i_closeButton = new IButton("Close");
		i_closeButton.setShowHover(true);
		i_closeButton.setPrompt(HINT_CLOSE);
		i_closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				if (checkForUnsavedChanges()) {
					warnUserOfUnsavedChanges();

				} else {
					// collapse the parent ListGrid
					i_parentGrid.collapseRecord(record);
				}
				// i_parentGrid.collapseRecord(record);
			}
		});

		i_deleteButton = new IButton("Delete Row(s)");
		i_deleteButton.setTop(250);
		i_deleteButton.setShowHover(true);
		i_deleteButton.setPrompt(HINT_DELETE);
		i_deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				i_removalsMade = true;

				// enable these buttons.
				i_saveButton.setDisabled(disableSave(i_valueSetRecord) || false);
				i_saveAsButton.setDisabled(false);

				ListGridRecord[] records = i_valueSetItemsListGrid.getSelectedRecords();
				for (ListGridRecord selectedRecord : records) {
					selectedRecord.setAttribute(ValueSetItemsListGrid.ID_HIDDEN_ACTION,
					        ValueSetItemsListGrid.ACTION_DELETE);
					i_valueSetItemsListGrid.updateData(selectedRecord);
				}

				// refresh the icons in Action column
				i_valueSetItemsListGrid.invalidateRecordComponents();
				i_deleteButton.setDisabled(true);
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
		createSaveAsEvent();
	}

	/**
	 * Determine if a user can save. They cannot save if the version is "1".
	 * 
	 * @param valueSetRecord
	 * @return
	 */
	private boolean disableSave(ListGridRecord valueSetRecord) {
		String version = valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_URI);
		return canSaveVersion(version);
	}

	/**
	 * 
	 * @param version
	 * @return
	 */
	private boolean canSaveVersion(String version) {
		return version == null || version.equals("1");
	}

	/**
	 * Determine if there are any unsaved changes.
	 * 
	 * @return
	 */
	public boolean checkForUnsavedChanges() {
		return i_additionsMade || i_removalsMade;
	}

	/**
	 * Clear the flags that indicate there was a change made.
	 */
	public void clearChangeFlags() {
		i_additionsMade = false;
		i_removalsMade = false;
	}

	/**
	 * Confirm with the user if they want to discard their changes.
	 */
	public void warnUserOfUnsavedChanges(/* BooleanCallback booleanCallback */) {

		String message = "Changes to the value set have been made.  Closing will discard your current changes for this value set.\n\nDo you want to discard your changes?";
		String title = "Changes Made";
		SC.ask(title, message, new BooleanCallback() {

			@Override
			public void execute(Boolean value) {
				if (value != null && value.booleanValue()) {

					// collapse the parent ListGrid
					i_parentGrid.collapseRecord(i_valueSetRecord);

					// User chose to discard changes // remove their Datasource
					// so it
					// will recreated // fresh next time.
					// String datasourceId =
					// i_valueSetItemsListGrid.getDataSource().getID();
					// ValueSetItemXmlDS.removeInstance(datasourceId);
				}
			}
		});

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

					DebugPanel.log(
					        DebugPanel.DEBUG,
					        "Adding value set entries for "
					                + i_valueSetRecord.getAttribute(ValueSetsListGrid.ID_FORMAL_NAME));

					Record[] records = searchValueSetItemsListGrid.getRecords();
					for (int i = 0; i < records.length; i++) {

						// if the checkbox is checked, then we need to add this
						// record
						if (records[i].getAttributeAsBoolean(SearchListGrid.ID_ADD)) {
							addValueSetRecord(records[i]);
						}
					}

					// enable these buttons.
					i_saveButton.setDisabled(disableSave(i_valueSetRecord) || false);
					i_saveAsButton.setDisabled(false);
					i_additionsMade = true;
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

		i_valueSetItemsListGrid.createNewRecord(href, code, codeSystemName, designation);
		DebugPanel.log(DebugPanel.DEBUG, "Adding Value Set Entry with code = " + code + " codeSystemName = "
		        + codeSystemName + " and designation = " + designation);
	}

	/**
	 * Save all the entities for a given value set. Iterate through the
	 * ListGridRecord[] to get entities and fill the Definition class
	 * 
	 * @param records
	 */
	private void saveValueSetEntities(ListGridRecord[] records, final String comment) {

		Definition definition = getDefinition(records, comment);

		Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);

		// Need to send in the overall layout so the whole
		// screen is greyed out.
		i_busyIndicator = new ModalWindow(i_parentGrid, 40, "#dedede");
		i_busyIndicator.setLoadingIcon("loading_circle.gif");
		i_busyIndicator.show("Saving Value Set...", true);

		service.saveDefinition(definition, new AsyncCallback<CTS2Result>() {

			@Override
			public void onFailure(Throwable caught) {
				i_busyIndicator.hide();
				DebugPanel.log(DebugPanel.DEBUG, "Value set 'Save' failed. " + caught.getMessage());
				SC.warn("Unable to save the Value Set");
			}

			@Override
			public void onSuccess(CTS2Result result) {
				i_busyIndicator.hide();

				if (result.isError()) {
					DebugPanel.log(DebugPanel.DEBUG, "Value set 'Save' failed. " + result.getMessage());
					SC.warn("Unable to save the Value Set.\n\n" + result.getMessage());
				} else {

					clearChangeFlags();

					// Update the Value Set Listgrid with the new changeSetUri,
					// version and comments.
					String newChangeSetUri = result.getChangeSetUri();
					String versionId = result.getValueSetVersion();
					String valueSetId = result.getValueSetOid();
					String oid = result.getValueSetOid();
					String documentUri = result.getDocumentUri();

					// let others know that a record needs to be updated with a
					// new version.
					Cts2Editor.EVENT_BUS.fireEvent(new UpdateValueSetVersionEvent(valueSetId, newChangeSetUri,
					        versionId, comment, documentUri));

					DebugPanel.log(DebugPanel.DEBUG,
					        "Value set 'Save' successful. new changeSetUri = " + result.getChangeSetUri()
					                + " new definition URI = " + newChangeSetUri);
					SC.say("Value set has been saved.");

				}
			}
		});

	}

	private void getCommentsForNewVersion() {

		String comment = i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_COMMENT);
		String version = i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_URI);
		String creator = Cts2Editor.getUserName();

		if (version == null || version.equals("1")) {
			comment = "< " + ValueSetsListGrid.DEFAULT_VERSION_COMMENT + " >";
		}

		SaveAsWindow saveAsWindow = new SaveAsWindow(version, comment, creator);
		saveAsWindow.show();
	}

	/**
	 * Take changes to the value set entries and call SaveAs.
	 * 
	 * @param definition
	 * @param newComment
	 */
	private void saveAsValueSetEntities(Definition definition, final String newComment) {

		Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);

		i_busyIndicator = new ModalWindow(i_parentGrid, 40, "#dedede");
		i_busyIndicator.setLoadingIcon("loading_circle.gif");
		i_busyIndicator.show("Creating New Value Set Version...", true);

		service.saveDefinitionAs(definition, new AsyncCallback<CTS2Result>() {

			@Override
			public void onSuccess(CTS2Result result) {

				i_busyIndicator.hide();

				if (result.isError()) {
					DebugPanel.log(DebugPanel.DEBUG, "Value set 'Save As' failed. " + result.getMessage());
					SC.warn("Unable to save the Value Set.\n\n" + result.getMessage());
				} else {
					DebugPanel.log(DebugPanel.DEBUG,
					        "Value set 'Save As' successful.  changeSetUri = " + result.getChangeSetUri());
					SC.say("Value set has been saved.");

					clearChangeFlags();

					// Update the Value Set Record that was just saved.
					String valueSetId = result.getValueSetOid();
					String changeSetUri = result.getChangeSetUri();
					String versionId = result.getValueSetVersion();
					String documentUri = result.getDocumentUri();

					// let others know that a record needs to be updated with a
					// new version.
					Cts2Editor.EVENT_BUS.fireEvent(new UpdateValueSetVersionEvent(valueSetId, changeSetUri, versionId,
					        newComment, documentUri));
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				i_busyIndicator.hide();
				DebugPanel.log(DebugPanel.ERROR, "Value set 'Save As' failed. " + caught.getMessage());
				SC.warn("Unable to save the Value Set");
			}
		});
	}

	/**
	 * Listen for when a user selects to "save as" a value set.
	 */
	private void createSaveAsEvent() {

		Cts2Editor.EVENT_BUS.addHandler(SaveAsEvent.TYPE, new SaveAsEventHandler() {

			@Override
			public void onSaveAs(SaveAsEvent event) {
				String comment = event.getComment();

				ListGridRecord[] records = i_valueSetItemsListGrid.getRecords();
				Definition definition = getDefinition(records, comment);

				saveAsValueSetEntities(definition, comment);
			}
		});
	}

	/**
	 * Create and populate a Definition based on the ListGridRecord[] passed in.
	 * 
	 * @param records
	 * @return
	 */
	private Definition getDefinition(ListGridRecord[] records, String comment) {

		List<DefinitionEntry> entries = getDefinitionEntries(records);
		Definition definition = new Definition();
		definition.setEntries(entries);

		// String name =
		// i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_VALUE_SET_NAME);
		String changeSetUri = i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_CHANGE_SET_URI);
		String formalName = i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_FORMAL_NAME);
		// String comment =
		// i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_COMMENT);
		String about = i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_ABOUT);
		String version = i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_URI);
		String creator = Cts2Editor.getUserName();
		String oid = i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_VALUE_SET_NAME);
		String documentUri = i_valueSetRecord.getAttribute(BaseValueSetsListGrid.ID_DOCUMENT_URI);

		definition.setChangeSetUri(changeSetUri);
		definition.setFormalName(formalName);
		definition.setNote(comment);
		definition.setAbout(about);
		definition.setCreator(creator);
		definition.setVersion(version);
		definition.setValueSetOid(oid);
		definition.setDocumentUri(documentUri);

		return definition;
	}

	/**
	 * For a ListGridRecord[] list, iterate through the records and create a
	 * List<DefinitionEntry>.
	 * 
	 * @param records
	 * @return
	 */
	private List<DefinitionEntry> getDefinitionEntries(ListGridRecord[] records) {

		List<DefinitionEntry> entries = new ArrayList<DefinitionEntry>();

		for (ListGridRecord record : records) {

			String action = record.getAttribute(ValueSetItemsListGrid.ID_HIDDEN_ACTION);

			// Don't add if the user selected to delete this value set entry
			if (action == null || !action.equals(ValueSetItemsListGrid.ACTION_DELETE)) {

				String uri = record.getAttribute(ValueSetItemsListGrid.ID_URI);
				String name = record.getAttribute(ValueSetItemsListGrid.ID_NAME);
				String nameSpace = record.getAttribute(ValueSetItemsListGrid.ID_NAME_SPACE);

				DefinitionEntry definitionEntry = new DefinitionEntry();

				definitionEntry.setName(name);
				definitionEntry.setNamespace(nameSpace);
				definitionEntry.setUri(uri);

				entries.add(definitionEntry);
			}
		}

		return entries;
	}

	/**
	 * Get the attributes from the value set record and put them in a Criteria.
	 * 
	 * @param record
	 * @return
	 */
	public Criteria getCriteriaFromValueSetRecord(ListGridRecord record) {
		// get the attributes of this value set
		String oid = record.getAttribute(BaseValueSetsListGrid.ID_VALUE_SET_NAME);
		String changeSetUri = record.getAttribute(BaseValueSetsListGrid.ID_CHANGE_SET_URI);
		String version = record.getAttribute(BaseValueSetsListGrid.ID_URI);
		String documentUri = record.getAttribute(BaseValueSetsListGrid.ID_DOCUMENT_URI);

		// update the criteria that will be used to fetch the entities.
		Criteria criteria = new Criteria();
		criteria.setAttribute("oid", oid);
		criteria.setAttribute("changeSetUri", changeSetUri);
		criteria.setAttribute("version", version);
		criteria.setAttribute("documentURI", documentUri);

		return criteria;
	}

	/**
	 * Update the Value Set Entities List grid with a different Value Set
	 * version.
	 * 
	 * @param criteria
	 */
	public void updateEntitiesListGrid(Criteria criteria) {

		String oid = criteria.getAttribute("oid");
		String changeSetUri = criteria.getAttribute("changeSetUri");
		String version = criteria.getAttribute("version");
		String documentUri = criteria.getAttribute("documentURI");

		// update the stored record.
		i_valueSetRecord.setAttribute(BaseValueSetsListGrid.ID_URI, version);
		i_valueSetRecord.setAttribute(BaseValueSetsListGrid.ID_VALUE_SET_NAME, oid);
		i_valueSetRecord.setAttribute(BaseValueSetsListGrid.ID_CHANGE_SET_URI, changeSetUri);
		i_valueSetRecord.setAttribute(BaseValueSetsListGrid.ID_DOCUMENT_URI, documentUri);

		i_saveButton.setDisabled(true);
		i_saveAsButton.setDisabled(true);

		ValueSetItemXmlDS ds = (ValueSetItemXmlDS) i_valueSetItemsListGrid.getDataSource();
		ds.setShouldGetData(true);

		i_valueSetItemsListGrid.fetchData(criteria);

	}

	// public void setOkToClose(boolean okToClose) {
	// i_okToClose = okToClose;
	// }
	//
	// public boolean getOkToClose() {
	// return i_okToClose;
	// }

}
