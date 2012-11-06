package mayo.edu.cts2.editor.client;

import java.util.ArrayList;

import mayo.edu.cts2.editor.client.utils.ModalWindow;
import mayo.edu.cts2.editor.client.widgets.ValueSetContainer;
import mayo.edu.cts2.editor.client.widgets.ValueSetsLayout;
import mayo.edu.cts2.editor.client.widgets.ValueSetsListGrid;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cts2Editor implements EntryPoint {

	private ModalWindow i_busyIndicator;

	private VLayout i_mainLayout;
	private ValueSetsLayout i_valueSetsLayout;

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {

		IButton testButton = new IButton("Get the Value Sets");
		testButton.setWidth(150);
		testButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				getValueSets();
			}
		});

		i_valueSetsLayout = new ValueSetsLayout();

		i_mainLayout = new VLayout();
		i_mainLayout.setWidth100();
		i_mainLayout.setHeight100();
		i_mainLayout.setMargin(15);

		i_mainLayout.addMember(testButton);
		i_mainLayout.addMember(i_valueSetsLayout);

		// Draw the Layout - main layout
		RootLayoutPanel.get().add(i_mainLayout);
	}

	private void getValueSets() {

		Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);

		// Sample list of oids for testing the call
		final ArrayList<String> oids = new ArrayList<String>();
		oids.add("2.16.840.1.114222.4.11.837");
		oids.add("2.16.840.1.113883.3.221.5");
		oids.add("2.16.840.1.113883.3.464.0003.1021");
		oids.add("2.16.840.1.113883.3.464.0003.1017");
		oids.add("2.16.840.1.113883.3.464.0001.231");

		// Set the busy indicator to show while executing the
		// phenotype.

		// Need to send in the overall layout so the whole
		// screen is greyed out.
		i_busyIndicator = new ModalWindow(i_mainLayout, 40, "#dedede");
		i_busyIndicator.setLoadingIcon("loading_circle.gif");
		i_busyIndicator.show("Retrieving ValueSets...", true);

		service.getValueSets(oids, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String valueSets) {
				/*
				 * NOTE: valueSets is an xml string of
				 * <ValueSetCatalogEntryMsg>s wrapped in the
				 * <ValueSetCatalogEntryMsgList>. The XPath root is
				 * Cts2EditorServiceImpl.XPATH_VALUESETS_BASE
				 */

				System.out.println(valueSets);

				// clear out the existing value sets ListGrid
				i_valueSetsLayout.removeAll();

				// create the new value set ListGrid
				ValueSetsListGrid vsListGrid = new ValueSetsListGrid();
				vsListGrid.populateData(valueSets);

				// put the value set list grid and title into a vlayout.
				ValueSetContainer valueSetContainer = new ValueSetContainer(vsListGrid);

				// put the value set list grid layout (container) into the
				// main list grid container.
				i_valueSetsLayout.addMember(valueSetContainer);

				// hide the progress panel.
				i_busyIndicator.hide();
			}

			@Override
			public void onFailure(Throwable caught) {

				// hide the progress panel.
				i_busyIndicator.hide();

				SC.say("Error retrieving ValueSets.\n" + caught.getMessage());
			}
		});

	}
}
