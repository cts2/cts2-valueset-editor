package mayo.edu.cts2.editor.client.widgets.search;

import mayo.edu.cts2.editor.client.Cts2Editor;
import mayo.edu.cts2.editor.client.datasource.ValueSetsSearchXmlDS;
import mayo.edu.cts2.editor.client.events.AddRecordsEvent;
import mayo.edu.cts2.editor.client.events.ValueSetsReceivedEvent;
import mayo.edu.cts2.editor.client.events.ValueSetsReceivedEventHandler;

import com.smartgwt.client.core.DataClass;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class SearchWindow extends Window {

	private static final String BACKGROUND_COLOR = "#ECECEC";
	private static final String TITLE = "Search";
	private static final String ROWS_RETRIEVED_TITLE = "Rows Matching Criteria:";

	private static final String CLEAR_BUTTON_TITLE = "Clear";
	private static final String SEARCH_HINT = "Enter Search Text";

	private DynamicForm i_searchForm;

	private TextItem i_searchTextItem;
	private IButton i_clearButton;
	private String i_previousText = "";
	private Label i_rowsRetrievedLabel;

	protected Label i_label;
	protected Button i_addButton;
	protected Button i_cancelButton;

	SearchValueSetsListGrid i_searchListGrid;

	public SearchWindow() {
		super();

		i_searchListGrid = new SearchValueSetsListGrid();

		VLayout layout = new VLayout(5);

		setWidth100();
		setHeight100();
		setMargin(20);

		setTitle(TITLE);
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		setCanDragResize(true);
		centerInPage();

		addCloseClickHandler(new CloseClickHandler() {

			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		layout.addMember(createDisplayLabel());
		layout.addMember(createSearchPanel());
		layout.addMember(getButtons());

		addItem(layout);

		createValueSetsReceivedEvent();
	}

	private VLayout createDisplayLabel() {
		String message = "Search for value sets.  Select the value sets by checking the checkbox and then click Add to add them.";
		i_label = new Label("<b>" + message + "<b>");
		i_label.setWidth100();
		i_label.setHeight(30);
		i_label.setMargin(2);
		i_label.setValign(VerticalAlignment.CENTER);
		i_label.setBackgroundColor(BACKGROUND_COLOR);

		final VLayout vLayoutLayoutSpacers = new VLayout();
		vLayoutLayoutSpacers.setWidth100();
		vLayoutLayoutSpacers.setHeight(30);
		vLayoutLayoutSpacers.setBackgroundColor(BACKGROUND_COLOR);
		vLayoutLayoutSpacers.setLayoutMargin(6);
		vLayoutLayoutSpacers.setMembersMargin(6);

		vLayoutLayoutSpacers.addMember(i_label);

		return vLayoutLayoutSpacers;
	}

	private VLayout createSearchPanel() {

		VLayout searchLayout = new VLayout();
		searchLayout.setWidth100();
		searchLayout.setHeight100();
		searchLayout.setLayoutMargin(10);
		searchLayout.setLayoutMargin(6);
		searchLayout.setMembersMargin(15);

		searchLayout.addMember(createSearchLayout());
		searchLayout.addMember(i_searchListGrid);

		i_searchListGrid.getField(SearchValueSetsListGrid.ID_ADD).addCellSavedHandler(new CellSavedHandler() {
			@Override
			public void onCellSaved(CellSavedEvent event) {

				boolean hasRecordsToAdd = i_searchListGrid.hasAddRecords();
				i_addButton.setDisabled(!hasRecordsToAdd);
			}
		});

		return searchLayout;
	}

	private HLayout createSearchLayout() {
		HLayout searchLayout = new HLayout();

		searchLayout.setWidth100();
		searchLayout.setHeight(20);
		searchLayout.setMembersMargin(15);

		i_searchForm = new DynamicForm();
		i_searchForm.setWidth(250);
		i_searchForm.setHeight100();
		i_searchForm.setAlign(Alignment.LEFT);
		i_searchForm.setAutoFocus(true);

		i_searchTextItem = new TextItem();
		i_searchTextItem.setTitle("Search");
		i_searchTextItem.setWidth(200);
		i_searchTextItem.setHint(SEARCH_HINT);
		i_searchTextItem.setShowHintInField(true);
		i_searchTextItem.setCanFocus(true);
		i_searchTextItem.setSelectOnFocus(true);

		i_searchTextItem.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				event.getKeyName();

				// ignore the arrow keys
				if (isValidSearchText()) {
					i_searchListGrid.getData(i_searchTextItem.getValueAsString());
					i_addButton.setDisabled(true);
				}
			}
		});

		i_searchForm.setFields(i_searchTextItem);

		// add button to a Vlayout so we can position it correctly with the form
		// search text.
		VLayout buttonVlayout = new VLayout();
		buttonVlayout.setWidth(40);
		buttonVlayout.setAlign(VerticalAlignment.CENTER);
		buttonVlayout.setMargin(2);

		i_clearButton = new IButton(CLEAR_BUTTON_TITLE);
		i_clearButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				i_searchTextItem.setValue("");
				i_rowsRetrievedLabel.setContents("");
				i_searchListGrid.clearData();
				i_addButton.setDisabled(true);
			}
		});
		buttonVlayout.addMember(i_clearButton);

		// add Label to a Vlayout so we can position it correctly with the form
		// search text.
		VLayout labelVlayout = new VLayout();
		labelVlayout.setWidth(100);
		labelVlayout.setAlign(VerticalAlignment.BOTTOM);
		labelVlayout.setMargin(2);

		i_rowsRetrievedLabel = new Label();
		i_rowsRetrievedLabel.setWrap(false);
		i_rowsRetrievedLabel.setWidth100();
		i_rowsRetrievedLabel.setHeight(23);

		labelVlayout.addMember(i_rowsRetrievedLabel);

		searchLayout.addMember(i_searchForm);
		searchLayout.addMember(buttonVlayout);
		searchLayout.addMember(labelVlayout);

		return searchLayout;
	}
	private HLayout getButtons() {

		// Buttons on the bottom
		HLayout buttonLayout = new HLayout();
		buttonLayout.setWidth100();
		buttonLayout.setHeight(40);
		buttonLayout.setLayoutMargin(6);
		buttonLayout.setMembersMargin(10);
		buttonLayout.setAlign(Alignment.CENTER);

		i_addButton = new Button("Add");

		// initially disable
		i_addButton.setDisabled(true);

		i_addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// let others know that records are to be added.
				Cts2Editor.EVENT_BUS.fireEvent(new AddRecordsEvent());

				// hide the window, but don't destroy
				hide();
			}
		});

		i_cancelButton = new Button("Cancel");
		i_cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// close the window
				destroy();
			}
		});

		buttonLayout.addMember(i_addButton);
		buttonLayout.addMember(i_cancelButton);

		return buttonLayout;
	}

	public Button getCloseButton() {
		return i_addButton;
	}

	/**
	 * A valid search text would be different than the previous search text.
	 * 
	 * @return
	 */
	public boolean isValidSearchText() {

		String currentText = i_searchTextItem.getValueAsString();
		currentText = currentText == null ? "" : currentText;

		boolean isValid = !i_previousText.equals(currentText);

		if (isValid) {
			i_previousText = currentText;
		}

		return isValid;
	}

	public SearchValueSetsListGrid getSearchListGrid() {
		return i_searchListGrid;
	}

	/**
	 * Listen for the event that Value Sets were retrieved.
	 */
	private void createValueSetsReceivedEvent() {
		Cts2Editor.EVENT_BUS.addHandler(ValueSetsReceivedEvent.TYPE, new ValueSetsReceivedEventHandler() {

			@Override
			public void onValueSetsReceived(ValueSetsReceivedEvent event) {
				DataClass[] dc = ValueSetsSearchXmlDS.getInstance().getTestData();
				updateRowsRetrieved(dc);
			}
		});
	}


	/**
	 * Update the rows retrieved label based on the search results.
	 * 
	 * @param dc
	 */
	private void updateRowsRetrieved(DataClass[] dc) {
		if (dc.length >= 1) {

			String numEntries = dc[0].getAttribute("numEntries");
			String complete = dc[0].getAttribute("complete");

			if (complete != null && !complete.equals("COMPLETE")) {
				i_rowsRetrievedLabel.setContents(ROWS_RETRIEVED_TITLE + "<b> " + numEntries + "</b>+");
			} else {
				i_rowsRetrievedLabel.setContents(ROWS_RETRIEVED_TITLE + " <b>" + numEntries + "</b>");
			}

			String searchText = i_searchTextItem.getValueAsString();
			if (searchText == null || searchText.length() == 0) {
				i_rowsRetrievedLabel.setContents("");
			}
		} else {
			i_rowsRetrievedLabel.setContents(ROWS_RETRIEVED_TITLE + " <b>0</b>");
		}
	}

	public void setInitialFocus() {
		i_searchForm.focusInItem(i_searchTextItem);
		i_searchForm.focus();
	}

}
