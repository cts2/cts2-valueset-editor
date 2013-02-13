package mayo.edu.cts2.editor.client.widgets.versions;

import mayo.edu.cts2.editor.client.Cts2Editor;
import mayo.edu.cts2.editor.client.events.UpdateValueSetVersionEvent;
import mayo.edu.cts2.editor.client.widgets.ValueSetsListGrid;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class VersionWindow extends Window {

	private static final String BACKGROUND_COLOR = "#ECECEC";
	private static final String TITLE = "Value Set Versions";

	private static final String MESSASE = "Select a value set version.";

	private static final int WIDTH = 650;
	private static final int HEIGHT = 400;

	private final VLayout i_mainLayout;

	private final Criteria i_criteria;

	private Label i_label;
	private Button i_selectButton;
	private Button i_cancelButton;

	private final VersionsListGrid i_versionListGrid;

	public VersionWindow(Criteria criteria) {

		i_criteria = criteria;

		i_versionListGrid = new VersionsListGrid();

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
		i_mainLayout.addMember(createVersionsPanel());
		i_mainLayout.addMember(getButtons());

		addItem(i_mainLayout);

		i_versionListGrid.addCellClickHandler(new CellClickHandler() {

			@Override
			public void onCellClick(CellClickEvent event) {
				// enable after a row is selected
				i_selectButton.setDisabled(false);

			}
		});

		i_versionListGrid.getData(i_criteria);
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
	 * Panel to hold the versions ListGrid.
	 * 
	 * @return
	 */
	private VLayout createVersionsPanel() {

		VLayout versionsLayout = new VLayout();
		versionsLayout.setWidth100();
		versionsLayout.setHeight100();
		versionsLayout.setLayoutMargin(10);
		versionsLayout.setLayoutMargin(6);
		versionsLayout.setMembersMargin(15);

		versionsLayout.addMember(i_versionListGrid);

		return versionsLayout;
	}

	private HLayout getButtons() {

		// Buttons on the bottom
		HLayout buttonLayout = new HLayout();
		buttonLayout.setWidth100();
		buttonLayout.setHeight(40);
		buttonLayout.setLayoutMargin(6);
		buttonLayout.setMembersMargin(10);
		buttonLayout.setAlign(Alignment.CENTER);

		i_selectButton = new Button("Select");

		// initially disable
		i_selectButton.setDisabled(true);
		i_selectButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				ListGridRecord selected = i_versionListGrid.getSelectedRecord();
				String versionId = selected.getAttribute(VersionsListGrid.ID_VERSION);
				String href = selected.getAttribute("href");
				String comment = selected.getAttribute("comment");

				// *********** TEMP *************
				// TODO - CME: changeSetUri is hard coded. Talk with Dale to
				// determine how to get it.

				String changeSetUri = "e3aaae65-5d11-4357-93b2-49685e88d222";

				// System.out.println("Selected version = " + versionId);
				// System.out.println("HREF = " + href);
				// System.out.println("comment = " + comment);

				String valueSetId = i_criteria.getAttribute(ValueSetsListGrid.ID_VALUE_SET_NAME);

				// if the versionId is 1, then this is the initial version, set
				// the changesetUri to null
				if (versionId != null && versionId.equals("1")) {
					changeSetUri = null;
				}

				// let others know that a record needs to be updated with a new
				// version.
				Cts2Editor.EVENT_BUS.fireEvent(new UpdateValueSetVersionEvent(valueSetId, changeSetUri, versionId,
				        comment));

				// close the window
				destroy();

			}
		});

		i_cancelButton = new Button("Cancel");
		i_cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				i_versionListGrid.destroy();

				// close the window
				destroy();
			}
		});

		buttonLayout.addMember(i_selectButton);
		buttonLayout.addMember(i_cancelButton);

		return buttonLayout;
	}

}
