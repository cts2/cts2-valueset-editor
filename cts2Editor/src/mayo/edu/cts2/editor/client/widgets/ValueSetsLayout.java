package mayo.edu.cts2.editor.client.widgets;

import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Layout to add all the Value Set ListGrids to.
 */
public class ValueSetsLayout extends VLayout {

	private static final String BACKGROUND_COLOR_BORDER = "#2a3d77";
	private static final int HEIGHT = 800;

	public ValueSetsLayout() {
		super();

		setWidth100();
		setHeight(HEIGHT);
		setMembersMargin(15);
		setMargin(15);
		setBorder("2px solid " + BACKGROUND_COLOR_BORDER);
	}

	public void removeAll() {
		removeMembers(getMembers());
	}

}
