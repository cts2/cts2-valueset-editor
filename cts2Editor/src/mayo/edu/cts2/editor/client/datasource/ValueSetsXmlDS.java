package mayo.edu.cts2.editor.client.datasource;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.XmlNamespaces;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;

/**
 * DataSource for encapsulating ValueSet data
 */
public class ValueSetsXmlDS extends DataSource {

	private static final Logger logger = Logger.getLogger(ValueSetsXmlDS.class.getName());

	private static final String RECORD_X_PATH = "/ValueSetCatalogEntryMsgList/cts2:ValueSetCatalogEntryMsg/cts2:valueSetCatalogEntry";

	private static final String X_PATH_RESOURCE_ROOT = "/cts2:ValueSetCatalogEntryDirectory/core:heading/core:resourceRoot";
	private static final String X_PATH_RESOURCE_SYNOPSIS = "core:resourceSynopsis/core:value";

	private final XmlNamespaces i_xmlNamespaces;
	private final LinkedHashMap<String, String> i_nsMap;

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

		i_nsMap = getNameSpaceHashMap();

		// Set the namespaces
		i_xmlNamespaces = new XmlNamespaces();
		i_xmlNamespaces.addNamespace("cts2", "http://schema.omg.org/spec/CTS2/1.0/ValueSet");
		i_xmlNamespaces.addNamespace("core", "http://schema.omg.org/spec/CTS2/1.0/Core");
		i_xmlNamespaces.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		setXmlNamespaces(i_xmlNamespaces);

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

	// public DataSource getRelatedDataSource() {
	// return ValueSetItemXmlDS.getInstance();
	// }

	/**
	 * Create a HashMap of the nameSpaces.
	 * 
	 * @return
	 */
	private LinkedHashMap<String, String> getNameSpaceHashMap() {
		LinkedHashMap<String, String> nsMap = new LinkedHashMap<String, String>();
		nsMap.put("cts2", "http://schema.omg.org/spec/CTS2/1.0/ValueSet");
		nsMap.put("core", "http://schema.omg.org/spec/CTS2/1.0/Core");
		nsMap.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		return nsMap;
	}

	public void setData(String xmlData) {
		Object results = XMLTools.selectNodes(xmlData, RECORD_X_PATH, i_nsMap);
		Record[] fetchRecords = recordsFromXML(results);
		setTestData(fetchRecords);
	}
}
