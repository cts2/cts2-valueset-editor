package mayo.edu.cts2.editor.client.datasource;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.DSDataFormat;

public class ValueSetItemSearchXmlDS extends DataSource {

	private static ValueSetItemSearchXmlDS instance = null;

	public static ValueSetItemSearchXmlDS getInstance() {
		if (instance == null) {
			instance = new ValueSetItemSearchXmlDS("ValueSetItemSearchXmlDS");
		}

		return instance;
	}

	public ValueSetItemSearchXmlDS(String id) {

		setID(id);
		setDataFormat(DSDataFormat.XML);

		// i_nsMap = getNameSpaceHashMap();

	}
}
