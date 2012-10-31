package mayo.edu.cts2.editor.client.datasource;

import java.util.LinkedHashMap;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.XmlNamespaces;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;

/**
 * Datasource that models the data in a resolved value set (entities).
 */
public class ValueSetItemXmlDS extends DataSource {

	private static final String RECORD_X_PATH = "/cts2:IteratableResolvedValueSet/cts2:entry";

	private static final String X_PATH_NUMBER_OF_ENTRIES = "/cts2:IteratableResolvedValueSet/@numEntries";

	private static final String X_PATH_VS_DEFINITION = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolutionOf/core:valueSetDefinition";
	private static final String X_PATH_VS_DEFINITION_URI = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolutionOf/core:valueSet/@uri";
	private static final String X_PATH_VS_DEFINITION_HREF = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolutionOf/core:valueSet/@href";

	private static final String X_PATH_CODE_SYSTEM_VERSION = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolvedUsingCodeSystem/core:version";
	private static final String X_PATH_CODE_SYSTEM_VERSION_HREF = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolvedUsingCodeSystem/core:version/@href";

	private static final String X_PATH_CODE_SYSTEM = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolvedUsingCodeSystem/core:codeSystem";
	private static final String X_PATH_CODE_SYSTEM_URI = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolvedUsingCodeSystem/core:codeSystem/@uri";
	private static final String X_PATH_CODE_SYSTEM_HREF = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolvedUsingCodeSystem/core:codeSystem/@href";

	private static final String X_PATH_ENTRY_NAMESPACE = "core:namespace";
	private static final String X_PATH_ENTRY_NAME = "core:name";
	private static final String X_PATH_DESIGNATION = "core:designation";

	private final XmlNamespaces i_xmlNamespaces;
	private final LinkedHashMap<String, String> i_nsMap;

	public ValueSetItemXmlDS(String id) {
		super();
		setID(id);
		setDataFormat(DSDataFormat.XML);

		i_nsMap = getNameSpaceHashMap();

		// Set the namespaces
		i_xmlNamespaces = new XmlNamespaces();
		i_xmlNamespaces.addNamespace("cts2", "http://schema.omg.org/spec/CTS2/1.0/ValueSetDefinition");
		i_xmlNamespaces.addNamespace("core", "http://schema.omg.org/spec/CTS2/1.0/Core");
		i_xmlNamespaces.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		setXmlNamespaces(i_xmlNamespaces);

		// set the XPath
		setRecordXPath(RECORD_X_PATH);

		DataSourceTextField vsDefinitionField = new DataSourceTextField("valueSetDefinition", "Value Set Definition");
		vsDefinitionField.setValueXPath(X_PATH_VS_DEFINITION);

		DataSourceTextField vsDefinitionUriField = new DataSourceTextField("valueSetDefinitionUri",
		        "Value Set Definition URI");
		vsDefinitionUriField.setValueXPath(X_PATH_VS_DEFINITION_URI);

		DataSourceTextField vsDefinitionHrefField = new DataSourceTextField("valueSetDefinitionHref",
		        "Value Set Definition HREF");
		vsDefinitionHrefField.setValueXPath(X_PATH_VS_DEFINITION_HREF);
		vsDefinitionHrefField.setPrimaryKey(true);

		DataSourceTextField codeSystemVersionField = new DataSourceTextField("codeSystemVersion", "Code System Version");
		codeSystemVersionField.setValueXPath(X_PATH_CODE_SYSTEM_VERSION);

		DataSourceTextField codeSystemVersionHrefField = new DataSourceTextField("codeSystemVersionHref",
		        "Code System Version HREF");
		codeSystemVersionHrefField.setValueXPath(X_PATH_CODE_SYSTEM_VERSION_HREF);

		DataSourceTextField codeSystemField = new DataSourceTextField("codeSystem", "Code System");
		codeSystemField.setValueXPath(X_PATH_CODE_SYSTEM);

		DataSourceTextField codeSystemUriField = new DataSourceTextField("codeSystemUri", "Code System URI");
		codeSystemUriField.setValueXPath(X_PATH_CODE_SYSTEM_URI);

		DataSourceTextField codeSystemHrefField = new DataSourceTextField("codeSystemHref", "Code System HREF");
		codeSystemHrefField.setValueXPath(X_PATH_CODE_SYSTEM_HREF);

		DataSourceTextField uriField = new DataSourceTextField("uri", "URI");
		DataSourceTextField hrefField = new DataSourceTextField("href", "HREF");

		DataSourceTextField nameSpaceField = new DataSourceTextField("nameSpace", "Code System Version");
		nameSpaceField.setValueXPath(X_PATH_ENTRY_NAMESPACE);

		DataSourceTextField nameField = new DataSourceTextField("name", "Code");
		nameField.setValueXPath(X_PATH_ENTRY_NAME);

		DataSourceTextField designationField = new DataSourceTextField("designation", "Description");
		designationField.setValueXPath(X_PATH_DESIGNATION);

		setFields(vsDefinitionField, vsDefinitionUriField, vsDefinitionHrefField, codeSystemVersionField,
		        codeSystemVersionHrefField, codeSystemField, codeSystemUriField, codeSystemHrefField, uriField,
		        hrefField, nameSpaceField, nameField, designationField);

		setClientOnly(true);
	}

	/**
	 * Create a HashMap of the nameSpaces.
	 * 
	 * @return
	 */
	private LinkedHashMap<String, String> getNameSpaceHashMap() {
		LinkedHashMap<String, String> nsMap = new LinkedHashMap<String, String>();
		nsMap.put("cts2", "http://schema.omg.org/spec/CTS2/1.0/ValueSetDefinition");
		nsMap.put("core", "http://schema.omg.org/spec/CTS2/1.0/Core");
		nsMap.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		return nsMap;
	}

}
