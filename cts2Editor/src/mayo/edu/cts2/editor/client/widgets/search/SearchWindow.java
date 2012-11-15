package mayo.edu.cts2.editor.client.widgets.search;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class SearchWindow extends Window {

	private static final String BACKGROUND_COLOR = "#ECECEC";
	private static final String TITLE = "Search";

	private static final int WIDTH = 850;
	private static final int HEIGHT = 600;

	protected TextItem i_searchItem;
	protected ButtonItem i_searchButton;

	protected Label i_label;
	protected Button i_addButton;
	protected Button i_cancelButton;

	private SearchPanel i_searchPanel;
	// private SearchValueSetItemsListGrid i_searchListGrid;
	ListGrid i_searchListGrid;

	public SearchWindow(ListGrid listGrid, String message) {
		super();

		i_searchListGrid = listGrid;

		VLayout layout = new VLayout(5);

		setWidth(WIDTH);
		setHeight(HEIGHT);
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

		layout.addMember(createDisplayLabel(message));
		layout.addMember(createSearchPanel());
		layout.addMember(getButtons());

		addItem(layout);
		show();
	}

	private VLayout createDisplayLabel(String message) {
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
		// vLayoutLayoutSpacers.setBorder("1px dashed red");

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

		i_searchPanel = new SearchPanel();
		// i _searchListGrid = new SearchValueSetItemsListGrid();

		searchLayout.addMember(i_searchPanel);
		searchLayout.addMember(i_searchListGrid);

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
		i_addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// close the window
				destroy();
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
}
