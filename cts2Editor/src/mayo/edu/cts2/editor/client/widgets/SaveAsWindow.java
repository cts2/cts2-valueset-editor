package mayo.edu.cts2.editor.client.widgets;

import mayo.edu.cts2.editor.client.Cts2Editor;
import mayo.edu.cts2.editor.client.events.SaveAsEvent;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class SaveAsWindow extends Window {

	private static final String BACKGROUND_COLOR = "#ECECEC";
	private static final String TITLE = "Save As";

	private static final String MESSASE = "Add a Comment to the New Value Set Version.";

	private static final int WIDTH = 500;
	private static final int HEIGHT = 325;

	private static final int FORM_WIDGTET_WIDTH = 300;

	private final String i_comment;
	private final String i_version;
	private final String i_creator;
	private final String i_layoutId;

	private final VLayout i_mainLayout;

	private DynamicForm i_saveAsForm;
	private TextItem i_commentTextItem;

	private Label i_label;
	private Button i_saveButton;
	private Button i_cancelButton;

	public SaveAsWindow(String version, String comment, String creator, String layoutId) {
		super();

		i_comment = comment;
		i_version = version;
		i_creator = creator;
		i_layoutId = layoutId;

		i_mainLayout = new VLayout(5);

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

		i_mainLayout.addMember(createDisplayLabel(MESSASE));
		i_mainLayout.addMember(createMainPanel());
		i_mainLayout.addMember(getButtons());

		addItem(i_mainLayout);
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

		vLayoutLayoutSpacers.addMember(i_label);

		return vLayoutLayoutSpacers;
	}

	/**
	 * Panel to hold the comment text field.
	 * 
	 * @return
	 */
	private VLayout createMainPanel() {

		VLayout mainLayout = new VLayout();
		mainLayout.setWidth100();
		mainLayout.setHeight100();
		mainLayout.setLayoutMargin(10);
		mainLayout.setLayoutMargin(6);
		mainLayout.setMembersMargin(25);

		// Form to hold the data that can be set
		i_saveAsForm = new DynamicForm();
		i_saveAsForm.setWidth100();

		// Form to hold the static information that is not editable
		DynamicForm staticInfoForm = new DynamicForm();
		staticInfoForm.setWidth100();
		// staticInfoForm.setBackgroundColor(BACKGROUND_COLOR);
		staticInfoForm.setIsGroup(true);
		staticInfoForm.setGroupTitle("Current Details");

		StaticTextItem staticTextItemCurrentComment = new StaticTextItem("currentComment", "Current Comment");
		staticTextItemCurrentComment.setWrap(false);
		staticTextItemCurrentComment.setWrapTitle(false);
		staticTextItemCurrentComment.setValue(i_comment);
		staticTextItemCurrentComment.setWidth(FORM_WIDGTET_WIDTH);

		StaticTextItem staticTextItemCurrentVersion = new StaticTextItem("currentVersion", "Current Version");
		staticTextItemCurrentComment.setWrapTitle(false);
		staticTextItemCurrentComment.setWrap(false);
		staticTextItemCurrentVersion.setValue(i_version);
		staticTextItemCurrentVersion.setWidth(FORM_WIDGTET_WIDTH);

		StaticTextItem staticTextItemCurrentCreator = new StaticTextItem("currentCreator", "Creator");
		staticTextItemCurrentCreator.setWrapTitle(false);
		staticTextItemCurrentCreator.setWrap(false);
		staticTextItemCurrentCreator.setValue(i_creator);
		staticTextItemCurrentCreator.setWidth(FORM_WIDGTET_WIDTH);

		i_commentTextItem = new TextItem("comments", "Comment");
		i_commentTextItem.setWidth(FORM_WIDGTET_WIDTH);
		i_commentTextItem.setHint("Enter a comment for this new version");
		i_commentTextItem.setShowHintInField(true);
		i_commentTextItem.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				int commentLength = 0;
				if (i_commentTextItem.getValueAsString() != null) {
					commentLength = i_commentTextItem.getValueAsString().length();
				}

				i_saveButton.setDisabled(commentLength == 0);
			}
		});

		staticInfoForm.setFields(staticTextItemCurrentVersion, staticTextItemCurrentComment,
		        staticTextItemCurrentCreator);

		i_saveAsForm.setFields(i_commentTextItem);

		mainLayout.addMember(staticInfoForm);
		mainLayout.addMember(i_saveAsForm);

		return mainLayout;
	}

	private HLayout getButtons() {

		// Buttons on the bottom
		HLayout buttonLayout = new HLayout();
		buttonLayout.setWidth100();
		buttonLayout.setHeight(40);
		buttonLayout.setLayoutMargin(6);
		buttonLayout.setMembersMargin(10);
		buttonLayout.setAlign(Alignment.CENTER);

		i_saveButton = new Button("Save");

		// initially disable
		i_saveButton.setDisabled(true);
		i_saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// let others know that the saveAs button was pressed.
				Cts2Editor.EVENT_BUS.fireEvent(new SaveAsEvent(getCommentText(), i_layoutId));

				i_saveButton.setDisabled(true);

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

		buttonLayout.addMember(i_saveButton);
		buttonLayout.addMember(i_cancelButton);

		return buttonLayout;
	}

	/**
	 * Get the user entered comment.
	 * 
	 * @return
	 */
	public String getCommentText() {
		return i_commentTextItem.getValueAsString();
	}

}
