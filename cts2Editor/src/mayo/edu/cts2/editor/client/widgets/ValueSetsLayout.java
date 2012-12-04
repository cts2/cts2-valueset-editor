package mayo.edu.cts2.editor.client.widgets;

import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Layout to add all the Value Set ListGrids to.
 */
public class ValueSetsLayout extends VLayout {

	public ValueSetsLayout() {
		super();

		setWidth100();
		setHeight100();
	}

	public void removeAll() {
		removeMembers(getMembers());
	}

}
