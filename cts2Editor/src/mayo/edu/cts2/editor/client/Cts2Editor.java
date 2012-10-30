package mayo.edu.cts2.editor.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cts2Editor implements EntryPoint {

	private VLayout i_mainLayout;

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

		i_mainLayout = new VLayout();
		i_mainLayout.setWidth100();
		i_mainLayout.setHeight100();
		i_mainLayout.setMargin(15);

		i_mainLayout.addMember(testButton);

		// Draw the Layout - main layout
		RootLayoutPanel.get().add(i_mainLayout);
	}

	private void getValueSets() {

		Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);

		// Sample list of oids for testing the call
		ArrayList<String> oids = new ArrayList<String>();
		oids.add("2.16.840.1.113883.3.464.0003.1021");
		oids.add("2.16.840.1.113883.3.464.0003.1017");

		service.getValueSets(oids, new AsyncCallback<Map<String, String>>() {

			@Override
			public void onSuccess(Map<String, String> valueSetsMap) {
				for (String oid : valueSetsMap.keySet()) {
					System.out.println("oid: " + oid + "\nXML:\n" + valueSetsMap.get(oid));
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});

	}
}
