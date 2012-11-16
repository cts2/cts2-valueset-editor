package mayo.edu.cts2.editor.client.widgets.search;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Panel to hold the search text and button to clear the form.
 * 
 * @author m091864
 * 
 */
public class SearchPanel extends HLayout {

	private static final String CLEAR_BUTTON_TITLE = "Clear";
	private static final String SEARCH_HINT = "Enter Search Text";

	private final DynamicForm i_searchForm;
	private final TextItem i_searchTextItem;
	private final IButton i_clearButton;
	private String i_previousText = "";

	public SearchPanel() {
		super();

		setWidth100();
		setHeight(10);
		setMembersMargin(15);

		i_searchForm = new DynamicForm();
		i_searchForm.setWidth(250);
		i_searchForm.setHeight100();
		i_searchForm.setAlign(Alignment.LEFT);

		i_searchTextItem = new TextItem();
		i_searchTextItem.setTitle("Search");
		i_searchTextItem.setWidth(200);
		i_searchTextItem.setHint(SEARCH_HINT);
		i_searchTextItem.setShowHintInField(true);

		i_searchForm.setFields(i_searchTextItem);

		i_clearButton = new IButton(CLEAR_BUTTON_TITLE);
		i_clearButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				i_searchTextItem.setValue("");
			}
		});

		addMember(i_searchForm);
		addMember(i_clearButton);
	}
	public String getSearchText() {
		return i_searchTextItem.getValueAsString();
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

}
