package mayo.edu.cts2.editor.client.datasource;

import com.smartgwt.client.data.fields.DataSourceTextField;
import mayo.edu.cts2.editor.client.utils.RandomString;

public class SelectedEntitiesXmlDS extends BaseValueSetItemSearchXmlDS {


	public SelectedEntitiesXmlDS() {
		this("selectedEntititesXmlDS"+ new RandomString(20).nextString());
	}

	public SelectedEntitiesXmlDS(String id) {
		setID(id);

		DataSourceTextField uri = new DataSourceTextField("uri", "URI");
		uri.setPrimaryKey(true);
		DataSourceTextField codeSystem = new DataSourceTextField("namespace", "Code System");
		DataSourceTextField codeSystemVersion = new DataSourceTextField("codeSystemVersion", "Code System Version");
		DataSourceTextField code = new DataSourceTextField("name", "Code");
		DataSourceTextField description = new DataSourceTextField("designation", "Description");

		setFields(uri, codeSystem, codeSystemVersion, code, description);
		setClientOnly(true);
	}

}
