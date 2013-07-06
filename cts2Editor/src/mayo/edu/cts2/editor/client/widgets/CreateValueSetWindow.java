package mayo.edu.cts2.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import mayo.edu.cts2.editor.client.Cts2Editor;
import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.client.Cts2EditorServiceAsync;
import mayo.edu.cts2.editor.client.events.NewValueSetCreatedEvent;
import mayo.edu.cts2.editor.client.widgets.search.EntitySearchWindow;
import mayo.edu.cts2.editor.shared.CTS2Result;
import mayo.edu.cts2.editor.shared.Definition;

public class CreateValueSetWindow extends Window {

	private DynamicForm form;
	private TextItem valueSetName;
	private TextItem valueSetUri;
	private TextItem definitionAuthor;
	private TextItem definitionName;
	private TextItem definitionVersion;
	private TextAreaItem definitionDescription;

	private IButton saveBtn;
	private IButton cancelBtn;
	private IButton entitiesBtn;
	private Label entitiesLbl;
	private EntitySearchWindow entitySearchWindow;

	public CreateValueSetWindow() {
		super();
		init();
		initEntityWindow();
	}

	public IButton getCancelButton() {
		return cancelBtn;
	}

	public IButton getSaveButton() {
		return saveBtn;
	}

	public void setInitialFocus() {
		form.focusInItem(valueSetName);
		form.focus();
	}

	private void init() {
		setTitle("Value Set Metadata");
		setWidth(400);
		setHeight(496);
		setMargin(20);
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		setCanDragResize(true);
		centerInPage();

		HeaderItem valueSetHeader = new HeaderItem("valueSetHeader");
		valueSetHeader.setDefaultValue("Value Set Details");

		valueSetName = new TextItem("metadataName", "Value Set Name");
		valueSetUri = new TextItem("metadataAbout", "Value Set URI");

		SpacerItem spacer = new SpacerItem("spacerItem");
		spacer.setHeight("32px");

		HeaderItem definitionHeader = new HeaderItem("definitionHeader");
		definitionHeader.setDefaultValue("Definition Details");

		definitionName = new TextItem("metadataDefName", "Definition Name");
		definitionVersion = new TextItem("metadataDefVersion", "Definition Version");
		definitionAuthor = new TextItem("definitionAuthor", "Author");
//		definitionAuthor.setDisabled(true);
		definitionDescription = new TextAreaItem("definitionDescription", "Description");
//		definitionDescription.setDisabled(true);

		form = new DynamicForm();
		form.setFields(valueSetHeader,
		  valueSetName,
		  valueSetUri,
		  spacer,
		  definitionHeader,
		  definitionName,
		  definitionVersion,
		  definitionAuthor,
		  definitionDescription);

		for (FormItem item : form.getFields()) {
			item.setWrapTitle(false);
			item.setRequired(true);
			item.addChangedHandler(new ChangedHandler() {
				@Override
				public void onChanged(ChangedEvent event) {
					enableButtons();
				}
			});
		}

		HLayout buttonLayout = new HLayout(10);
		buttonLayout.setAlign(Alignment.RIGHT);
		buttonLayout.setWidth100();
		buttonLayout.setHeight100();
		buttonLayout.setLayoutMargin(6);

		cancelBtn = new IButton("Cancel");
		cancelBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}
		});

		saveBtn = new IButton(("Save"));
//		saveBtn.disable();
		saveBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);
				final Definition definition = new Definition();
				definition.setValueSetOid(valueSetName.getValueAsString());
				definition.setValueSetUri(valueSetUri.getValueAsString());
				definition.setName(definitionName.getValueAsString() != null ? definitionName.getValueAsString()
				  .replace(" ", "_") : null);
				definition.setVersion(definitionVersion.getValueAsString());
				definition.setAbout("urn:id:" + definition.getName());
				definition.setEntries(entitySearchWindow.getSelectedEntities());
				definition.setCreator(definitionAuthor.getValueAsString() != null ? definitionAuthor.getValueAsString() : "");
				definition.setFormalName(definitionName.getValueAsString());
				definition.setResourceSynopsis(definitionDescription.getValueAsString());
				definition.setNote("");
				/* changeSetUri
				* documentUri */

				service.createValueSet(definition, new AsyncCallback<CTS2Result>() {
					@Override
					public void onFailure(Throwable caught) {
						SC.warn("An error has occurred while attempting to create the new value set.<br/>Error: " +caught.getMessage());
					}

					@Override
					public void onSuccess(CTS2Result result) {
						NewValueSetCreatedEvent createdEvent = new NewValueSetCreatedEvent();
						createdEvent.setDefinition(definition);
						Cts2Editor.EVENT_BUS.fireEvent(createdEvent);
						destroy();
					}
				});
			}
		});

		entitiesBtn = new IButton("Select Entities...");
//		entitiesBtn.disable();
		entitiesBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				openEntityWindow();
			}
		});

		entitiesLbl = new Label("0 entities selected.");
		entitiesLbl.setWrap(false);

		HLayout entitiesLayout = new HLayout(32);
		entitiesLayout.setWidth100();
		entitiesLayout.setAlign(Alignment.RIGHT);
		entitiesLayout.addMember(entitiesBtn);
		entitiesLayout.addMember(entitiesLbl);

		buttonLayout.addMember(saveBtn);
		buttonLayout.addMember(cancelBtn);

		VLayout spacerLayout = new VLayout();
		spacerLayout.setHeight("32px");

		VLayout formLayout = new VLayout(6);
		formLayout.addMember(form);
		formLayout.addMember(entitiesLayout);
		formLayout.addMember(spacerLayout);
		formLayout.addMember(buttonLayout);

		addItem(formLayout);

		enableButtons();
	}

	private void initEntityWindow() {
		entitySearchWindow = new EntitySearchWindow();
		entitySearchWindow.getCloseButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (entitySearchWindow.getSelectedEntities().size() > 0) {
					entitiesLbl.setContents(entitySearchWindow.getSelectedEntities().size() + " entities selected.");
				} else {
					SC.warn("You must add at lease one entity to the value set.");
				}
			}
		});
	}

	private void openEntityWindow() {
		entitySearchWindow.show();
	}

	private void enableButtons() {
		enableSaveButton();
		enableEntitiesButton();
	}

	private void enableSaveButton() {
		boolean valueSet = isValueEmpty(valueSetName) || isValueEmpty(valueSetUri);
		boolean definition = false;
		if (!isValueEmpty(definitionName) || !isValueEmpty(definitionVersion)) {
			definition = isValueEmpty(definitionName)
			  || isValueEmpty(definitionVersion)
			  || entitySearchWindow.getSelectedEntities().size() == 0;
		}
		saveBtn.setDisabled(valueSet || definition);
	}

	private void enableEntitiesButton() {
		boolean disable = isValueEmpty(definitionName) || isValueEmpty(definitionVersion);
		entitiesBtn.setDisabled(disable);
		definitionAuthor.setDisabled(disable);
		definitionDescription.setDisabled(disable);
	}

	private boolean isValueEmpty(TextItem item) {
		return item.getValueAsString() == null || item.getValueAsString().trim().isEmpty();
	}

}
