package mayo.edu.cts2.editor.client.datasource;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.DSDataFormat;

public class ValueSetsSearchXmlDS extends DataSource {

	private static ValueSetsSearchXmlDS instance = null;

	public static ValueSetsSearchXmlDS getInstance() {
		if (instance == null) {
			instance = new ValueSetsSearchXmlDS("ValueSetsSearchXmlDS");
		}

		return instance;
	}

	public ValueSetsSearchXmlDS(String id) {

		setID(id);
		setDataFormat(DSDataFormat.XML);

		// i_nsMap = getNameSpaceHashMap();

	}
}
