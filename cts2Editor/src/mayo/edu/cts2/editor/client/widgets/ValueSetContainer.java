package mayo.edu.cts2.editor.client.widgets;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
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

	private static final int HEIGHT = 250;
	private static final int TITLE_HEIGHT = 30;
	private static final String TITLE = "Value Sets for Group ";

	private static final int BUTTON_LAYOUT_HEIGHT = 25;
	private static final String BUTTON_ADD_TITLE = "Add...";

	private static final String BACKGROUND_COLOR_BORDER = "#5479ef";
	private static final String BACKGROUND_COLOR_TITLE = "#efc953";

	private final Label i_title;
	private final ValueSetsListGrid i_valueSetsListGrid;
	private IButton i_addButton;

	public ValueSetContainer(String title, ValueSetsListGrid valueSetListGrid) {
		super();

		setWidth100();
		setHeight(HEIGHT);
		setMargin(15);
		setBorder("2px solid " + BACKGROUND_COLOR_BORDER);

		i_title = createTitle(title);
		i_valueSetsListGrid = valueSetListGrid;
		HLayout buttonLayout = createButtonLayout();

		addMember(i_title);
		addMember(i_valueSetsListGrid);
		addMember(buttonLayout);
	}

	private HLayout createButtonLayout() {
		HLayout layout = new HLayout();
		layout.setWidth100();
		layout.setHeight(BUTTON_LAYOUT_HEIGHT);
		layout.setAlign(Alignment.CENTER);

		i_addButton = new IButton(BUTTON_ADD_TITLE);
		i_addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SC.say("Add a Value Set here");
			}
		});
		layout.addMember(i_addButton);

		return layout;
	}

	/**
	 * Create the Label that contains the title
	 * 
	 * @param title
	 * @return
	 */
	private Label createTitle(String titleOid) {
		Label titleLabel = new Label("<b>" + TITLE + titleOid + "</b>");
		titleLabel.setWidth100();
		titleLabel.setHeight(TITLE_HEIGHT);
		titleLabel.setBackgroundColor(BACKGROUND_COLOR_TITLE);

		return titleLabel;
	}
}
