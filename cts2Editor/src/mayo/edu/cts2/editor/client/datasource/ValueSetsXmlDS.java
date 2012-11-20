package mayo.edu.cts2.editor.client.datasource;

import java.util.logging.Logger;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;

/**
 * DataSource for encapsulating ValueSet data
 */
public class ValueSetsXmlDS extends BaseValueSetXmlDS {

	private static final Logger logger = Logger.getLogger(ValueSetsXmlDS.class.getName());

	private static final String RECORD_X_PATH = "/ValueSetCatalogEntryMsgList/cts2:ValueSetCatalogEntryMsg/cts2:valueSetCatalogEntry";

	private static final String X_PATH_RESOURCE_ROOT = "/cts2:ValueSetCatalogEntryDirectory/core:heading/core:resourceRoot";
	private static final String X_PATH_RESOURCE_SYNOPSIS = "core:resourceSynopsis/core:value";

	private static ValueSetsXmlDS instance = null;

	public static ValueSetsXmlDS getInstance() {
		if (instance == null) {
			instance = new ValueSetsXmlDS("ValueSetsXmlDS");
		}

		return instance;
	}

	private ValueSetsXmlDS(String id) {

		setID(id);
		setDataFormat(DSDataFormat.XML);

		// set the XPath
		setRecordXPath(RECORD_X_PATH);

		DataSourceTextField valueSetNameField = new DataSourceTextField("valueSetName", "Value Set Name");
		// valueSetNameField.setForeignKey("ValueSetsXmlDS.valueSetName");
		valueSetNameField.setPrimaryKey(true);

		// Set this as the primary key
		DataSourceTextField aboutField = new DataSourceTextField("about", "About");
		aboutField.setHidden(true);
		// aboutField.setPrimaryKey(true);
		aboutField.setForeignKey("ValueSetsXmlDS.valueSetName");
		aboutField.setRequired(true);

		DataSourceTextField formalNameField = new DataSourceTextField("formalName", "Formal Name");
		DataSourceTextField resourceSynopsisValueField = new DataSourceTextField("value", "Resource Synopsis");
		resourceSynopsisValueField.setValueXPath(X_PATH_RESOURCE_SYNOPSIS);

		setFields(valueSetNameField, aboutField, formalNameField, resourceSynopsisValueField);

		setClientOnly(true);
	}

	public void setData(String xmlData) {
		Object results = XMLTools.selectNodes(xmlData, RECORD_X_PATH, i_nsMap);
		Record[] fetchRecords = recordsFromXML(results);
		setTestData(fetchRecords);
	}
}
