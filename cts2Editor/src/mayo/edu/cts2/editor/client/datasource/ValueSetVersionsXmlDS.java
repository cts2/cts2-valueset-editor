package mayo.edu.cts2.editor.client.datasource;

import java.util.LinkedHashMap;

import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.client.Cts2EditorServiceAsync;
import mayo.edu.cts2.editor.client.widgets.ValueSetsListGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.XmlNamespaces;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Datasource for a multiple value set versions.
 * 
 */
public class ValueSetVersionsXmlDS extends DataSource {

	private static final String RECORD_X_PATH = "/cts2:ValueSetDefinitionDirectory/cts2:entry";

	private static final String NOTE_BEGIN = "<note>";
	private static final String NOTE_END = "</note>";
	private static final String CHANGE_SET_URI_BEGIN = "<changeSetUri>";
	private static final String CHANGE_SET_URI_END = "</changeSetUri>";

	private static ValueSetVersionsXmlDS instance = null;

	protected final XmlNamespaces i_xmlNamespaces;
	protected final LinkedHashMap<String, String> i_nsMap;

	public static ValueSetVersionsXmlDS getInstance() {
		if (instance == null) {
			instance = new ValueSetVersionsXmlDS("ValueSetVersionsXmlDS");
		}

		return instance;
	}

	public ValueSetVersionsXmlDS(String id) {

		setID(id);

		i_nsMap = getNameSpaceHashMap();

		// Set the namespaces
		i_xmlNamespaces = new XmlNamespaces();
		i_xmlNamespaces.addNamespace("cts2", "http://schema.omg.org/spec/CTS2/1.0/ValueSetDefinition");
		i_xmlNamespaces.addNamespace("core", "http://schema.omg.org/spec/CTS2/1.0/Core");
		i_xmlNamespaces.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		setXmlNamespaces(i_xmlNamespaces);

		setDataFormat(DSDataFormat.XML);

		// set the XPath
		setRecordXPath(RECORD_X_PATH);

		DataSourceTextField hrefField = new DataSourceTextField("href");
		hrefField.setValueXPath("@href");
		hrefField.setHidden(true);

		DataSourceTextField formalNameField = new DataSourceTextField("formalName");
		formalNameField.setValueXPath("@formalName");

		DataSourceTextField versionIdField = new DataSourceTextField("versionTag");
		versionIdField.setPrimaryKey(true);

		// This field currently holds <note> and <changeSetUri> tags. They are
		// encoded for html. The internal tags will be parsed out later.
		DataSourceTextField valuesField = new DataSourceTextField("values");
		valuesField.setValueXPath("core:resourceSynopsis/core:value");

		DataSourceTextField commentField = new DataSourceTextField("comment");
		commentField.setValueXPath("core:resourceSynopsis/core:value");

		DataSourceTextField changeSetUriField = new DataSourceTextField("changeSetUri");

		setFields(hrefField, versionIdField, commentField, valuesField, changeSetUriField);

		setClientOnly(true);
	}

	public void setData(String xmlData) {
		Object results = XMLTools.selectNodes(xmlData, RECORD_X_PATH, i_nsMap);
		Record[] fetchRecords = recordsFromXML(results);
		setTestData(fetchRecords);
	}

	@Override
	public void fetchData(final Criteria criteria, final DSCallback callback) {

		final String oid = criteria.getAttribute(ValueSetsListGrid.ID_VALUE_SET_NAME);
		final String userName = criteria.getAttribute("userName");

		Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);
		service.getUserDefinitions(oid, userName, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {

				// System.out.println(result);

				// Add a default record for the first/initial version
				ListGridRecord firstVersionRecord = new ListGridRecord();
				firstVersionRecord.setAttribute("formalName", criteria.getAttribute(ValueSetsListGrid.ID_FORMAL_NAME));
				firstVersionRecord.setAttribute("versionTag", "1");
				firstVersionRecord.setAttribute("comment", "Initial Version");

				Object results = XMLTools.selectNodes(result, RECORD_X_PATH, i_nsMap);
				Record[] fetchRecords = recordsFromXML(results);

				// get the value in "valuesField" and parse out the note and
				// changeSetUri
				// It will look like this:
				//
				// &lt;note&gt;User created
				// 1&lt;/note&gt;&lt;changeSetUri&gt;e3aaae65-5d11-4357-93b2-49685e88d222&lt;/changeSetUri&gt;
				for (Record record : fetchRecords) {
					String valuesField = record.getAttribute("values");
					String note = getEmbeddedData(valuesField, NOTE_BEGIN, NOTE_END);
					String changeSetUri = getEmbeddedData(valuesField, CHANGE_SET_URI_BEGIN, CHANGE_SET_URI_END);

					record.setAttribute("comment", note);
					record.setAttribute("changeSetUri", changeSetUri);
				}

				setCacheData(fetchRecords);
				addData(firstVersionRecord);

				// use the callback to let the widget know we got the data...
				callback.execute(null, null, null);
			}

			@Override
			public void onFailure(Throwable caught) {
				setTestData((new Record[0]));
				DSResponse myErrResp = new DSResponse();
				myErrResp.setAttribute("reason", caught.getMessage());

				// use the callback to let the widget know we got the error
				// message.
				callback.execute(myErrResp, null, null);
			}
		});

	}

	/**
	 * Get the data between the two tags.
	 * 
	 * @param valuesField
	 * @param begin
	 * @param end
	 * @return
	 */
	private String getEmbeddedData(String valuesField, String begin, String end) {
		String result = "";

		if (valuesField != null && begin != null && end != null) {
			try {

				int beginIndex = valuesField.indexOf(begin) + begin.length();
				int endIndex = valuesField.indexOf(end);

				result = valuesField.substring(beginIndex, endIndex);
			} catch (Exception e) {

			}
		}
		return result;
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
