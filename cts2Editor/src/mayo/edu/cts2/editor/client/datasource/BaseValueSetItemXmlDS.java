package mayo.edu.cts2.editor.client.datasource;

import java.util.LinkedHashMap;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.XmlNamespaces;

/**
 * Base Datasource that holds common data for Value Set Item DataSources
 * 
 */
public class BaseValueSetItemXmlDS extends DataSource {

	protected final XmlNamespaces i_xmlNamespaces;
	protected final LinkedHashMap<String, String> i_nsMap;

	public BaseValueSetItemXmlDS() {
		super();

		i_nsMap = getNameSpaceHashMap();

		// Set the namespaces
		i_xmlNamespaces = new XmlNamespaces();
		i_xmlNamespaces.addNamespace("cts2", "http://schema.omg.org/spec/CTS2/1.0/ValueSetDefinition");
		i_xmlNamespaces.addNamespace("core", "http://schema.omg.org/spec/CTS2/1.0/Core");
		i_xmlNamespaces.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		setXmlNamespaces(i_xmlNamespaces);
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
