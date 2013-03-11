package mayo.edu.cts2.editor.client;

import java.util.ArrayList;
import java.util.List;

import mayo.edu.cts2.editor.client.debug.DebugPanel;
import mayo.edu.cts2.editor.client.utils.ModalWindow;
import mayo.edu.cts2.editor.client.widgets.ValueSetContainer;
import mayo.edu.cts2.editor.client.widgets.ValueSetsLayout;
import mayo.edu.cts2.editor.client.widgets.ValueSetsListGrid;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cts2Editor implements EntryPoint {

	private ModalWindow i_busyIndicator;

	private ValueSetsLayout i_valueSetsLayout;
	private DebugPanel i_debugPanel;

	// Event Bus to capture global events and act upon them.
	public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);

	private static final boolean s_standAlone = true;
	private static final boolean s_readOnly = false;
	private static final boolean s_debug = false;

	private static String i_userName;

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {

		/****************************************************************
		 * NOTE: If you want to run this editor standalone, you need to set the
		 * s_standAlone variable (above) to true.
		 * 
		 * There is another option too for making the editor readonly. You will
		 * need to call setReadOnly(boolean readonly) before creating the main
		 * layout.
		 ****************************************************************/

		// Sample list of oids for testing the call
		final List<String> oids = new ArrayList<String>();
		oids.add("2.16.840.1.113883.1.11.1");
		oids.add("2.16.840.1.114222.4.11.837");
		oids.add("2.16.840.1.113883.3.221.5");
		oids.add("2.16.840.1.113883.3.464.0001.37");
		// oids.add("2.16.840.1.113883.3.464.0003.1021");
		// oids.add("2.16.840.1.113883.3.464.0003.1017");
		// oids.add("2.16.840.1.113883.3.464.0001.231");

		// create and add to the root only if we are in stand alone mode.
		if (s_standAlone) {

			setUser("admin");

			// Draw the Layout - main layout
			RootLayoutPanel.get().add(getMainLayout(oids));
		}
	}

	public static boolean getReadOnly() {
		return s_readOnly;
	}

	/**
	 * Entry method when the CTS2Editor is used as a component.
	 * 
	 * @return
	 */
	public VLayout getMainLayout(List<String> oids) {

		VLayout mainLayout = new VLayout();
		mainLayout.setWidth100();
		mainLayout.setHeight100();

		i_debugPanel = new DebugPanel();

		i_valueSetsLayout = new ValueSetsLayout();
		// get the value sets
		getValueSets(oids);
		// getValueSetVersions(oids, "admin");

		mainLayout.addMember(i_valueSetsLayout);

		// Only add if in debug mode
		if (s_debug) {
			mainLayout.addMember(i_debugPanel);
		}

		return mainLayout;
	}

	public static void setUser(String user) {
		i_userName = user;
	}

	public static String getUserName() {
		return i_userName;
	}

	private void getValueSets(List<String> oids) {

		Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);

		// Set the busy indicator to show while executing the
		// phenotype.

		// TODO: put status window back in. When deployed with another app, the
		// window doesn't go away.

		// Need to send in the overall layout so the whole
		// screen is greyed out.
		i_busyIndicator = new ModalWindow(i_valueSetsLayout, 40, "#dedede");
		i_busyIndicator.setLoadingIcon("loading_circle.gif");
		i_busyIndicator.show("Retrieving Value Sets...", true);

		service.getValueSets(oids, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String valueSets) {

				// hide the progress panel.
				i_busyIndicator.hide();

				/*
				 * NOTE: valueSets is an xml string of
				 * <ValueSetCatalogEntryMsg>s wrapped in the
				 * <ValueSetCatalogEntryMsgList>. The XPath root is
				 * Cts2EditorServiceImpl.XPATH_VALUESETS_BASE
				 */

				displayData(valueSets);
			}

			@Override
			public void onFailure(Throwable caught) {

				DebugPanel.log(DebugPanel.ERROR, "Failed to retrieve value sets. " + caught.getMessage());

				// hide the progress panel.
				i_busyIndicator.hide();
				displayData(null);
				SC.say("Error retrieving Value Sets.\n" + caught.getMessage());
			}

			private void displayData(String valueSets) {
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
			}
		});

	}

	private void getValueSetVersions(List<String> oids, String username) {
		Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);

		// Set the busy indicator to show while executing the
		// phenotype.

		// Need to send in the overall layout so the whole
		// screen is greyed out.
		i_busyIndicator = new ModalWindow(i_valueSetsLayout, 40, "#dedede");
		i_busyIndicator.setLoadingIcon("loading_circle.gif");
		i_busyIndicator.show("Retrieving ValueSet versions...", true);

		service.getUserDefinitions(oids, username, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String valueSets) {
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
