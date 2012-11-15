package mayo.edu.cts2.editor.client.widgets;

import mayo.edu.cts2.editor.client.widgets.search.SearchValueSetsListGrid;
import mayo.edu.cts2.editor.client.widgets.search.SearchWindow;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Vertical Layout to hold a title and a ListGrid.
 */
public class ValueSetContainer extends VLayout {

	private static final int HEIGHT = 450;
	private static final int TITLE_HEIGHT = 30;
	private static final String TITLE = "<em style=\"font-size:1.2em;font-weight:bold; margin-left:5px\">Value Sets</em>";

	private static final int BUTTON_LAYOUT_HEIGHT = 25;
	private static final String BUTTON_ADD_TITLE = "Add...";

	private static final String BACKGROUND_COLOR_BORDER = "#5479ef";
	private static final String BACKGROUND_COLOR_TITLE = "#89a0ba";

	private final Label i_title;
	private final ValueSetsListGrid i_valueSetsListGrid;
	private IButton i_addButton;

	public ValueSetContainer(ValueSetsListGrid valueSetListGrid) {
		super();

		setWidth100();
		setHeight(HEIGHT);
		setMargin(15);
		setBorder("2px solid " + BACKGROUND_COLOR_BORDER);

		i_title = createTitle();
		i_valueSetsListGrid = valueSetListGrid;
		HLayout buttonLayout = createButtonLayout();

		addMember(i_title);
		addMember(i_valueSetsListGrid);
		addMember(buttonLayout);
	}

	private HLayout createButtonLayout() {
		HLayout layout = new HLayout();
		layout.setWidth100();
		layout.setMargin(10);
		layout.setHeight(BUTTON_LAYOUT_HEIGHT);
		layout.setAlign(Alignment.CENTER);

		i_addButton = new IButton(BUTTON_ADD_TITLE);
		i_addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String message = "Search for value sets.  Select the value sets by checking the checkbox.";
				SearchWindow searchWindow = new SearchWindow(new SearchValueSetsListGrid(), message);
				searchWindow.show();
			}
		});
		layout.addMember(i_addButton);

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
		titleLabel.setBackgroundColor(BACKGROUND_COLOR_TITLE);

		return titleLabel;
	}
}
