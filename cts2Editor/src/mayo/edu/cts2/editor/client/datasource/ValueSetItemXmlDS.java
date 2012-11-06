package mayo.edu.cts2.editor.client.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.client.Cts2EditorServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.XmlNamespaces;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;

/**
 * Datasource that models the data in a resolved value set (entities).
 */
public class ValueSetItemXmlDS extends DataSource {

	private static final Logger logger = Logger.getLogger(ValueSetItemXmlDS.class.getName());

	private static final String RECORD_X_PATH = "/cts2:IteratableResolvedValueSet/cts2:entry";

	private static final String X_PATH_ENTRY_NAMESPACE = "core:namespace";
	private static final String X_PATH_ENTRY_NAME = "core:name";
	private static final String X_PATH_DESIGNATION = "core:designation";

	private final XmlNamespaces i_xmlNamespaces;
	private final LinkedHashMap<String, String> i_nsMap;

	private static final HashMap<String, ValueSetItemXmlDS> i_instances = new HashMap<String, ValueSetItemXmlDS>();

	private boolean i_getDataCalled = false;

	private final ArrayList<Record> i_recordsToDelete;

	public static ValueSetItemXmlDS getInstance(String id) {

		if (i_instances.containsKey(id)) {
			return i_instances.get(id);
		}
		ValueSetItemXmlDS instance = new ValueSetItemXmlDS(id);
		i_instances.put(id, instance);

		return instance;
	}

	/**
	 * Remove the instance that has this ID.
	 * 
	 * @param id
	 */
	public static void removeInstance(String id) {
		ValueSetItemXmlDS datasource = i_instances.remove(id);
		datasource.destroy();
		datasource = null;
	}

	private ValueSetItemXmlDS(String uniqueId) {
		super();
		setID(uniqueId);
		setDataFormat(DSDataFormat.XML);

		// create new list to hold records to delete for this instance.
		i_recordsToDelete = new ArrayList<Record>();

		i_nsMap = getNameSpaceHashMap();

		// Set the namespaces
		i_xmlNamespaces = new XmlNamespaces();
		i_xmlNamespaces.addNamespace("cts2", "http://schema.omg.org/spec/CTS2/1.0/ValueSetDefinition");
		i_xmlNamespaces.addNamespace("core", "http://schema.omg.org/spec/CTS2/1.0/Core");
		i_xmlNamespaces.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		setXmlNamespaces(i_xmlNamespaces);

		// set the XPath
		setRecordXPath(RECORD_X_PATH);

		DataSourceTextField uriField = new DataSourceTextField("uri", "URI");
		uriField.setPrimaryKey(true);

		DataSourceTextField nameSpaceField = new DataSourceTextField("nameSpace", "Code System Version");
		nameSpaceField.setValueXPath(X_PATH_ENTRY_NAMESPACE);

		DataSourceTextField nameField = new DataSourceTextField("name", "Code");
		nameField.setValueXPath(X_PATH_ENTRY_NAME);

		DataSourceTextField designationField = new DataSourceTextField("designation", "Description");
		designationField.setValueXPath(X_PATH_DESIGNATION);

		setFields(uriField, nameSpaceField, nameField, designationField);

		setClientOnly(true);
	}

	@Override
	public void fetchData(Criteria criteria, final DSCallback callback) {

		if (!i_getDataCalled) {
			Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);
			service.getResolvedValueSet(criteria.getAttribute("oid"), new AsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {
					System.out.print(result);

					// set this to true so we don't retrieve the data again.
					i_getDataCalled = true;

					Object results = XMLTools.selectNodes(result, RECORD_X_PATH, i_nsMap);
					Record[] fetchRecords = recordsFromXML(results);

					// setTestData(fetchRecords);

					if (fetchRecords != null) {
						// add each record
						for (Record record : fetchRecords) {
							addData(record);
						}
					}

					// use the callback to let the widget know we got the
					// data...
					callback.execute(null, null, null);
				}

				@Override
				public void onFailure(Throwable caught) {
					logger.log(Level.SEVERE, "Error retrieving Value Set Definition: " + caught);
				}
			});

		}
	}

	@Override
	public void fetchData(Criteria criteria, DSCallback callback, DSRequest requestProperties) {
		super.fetchData(criteria, callback, requestProperties);
	}

	@Override
	protected void transformResponse(DSResponse response, DSRequest request, Object data) {

		if (request.getOperationType() != null) {
			switch (request.getOperationType()) {

				case ADD : {
					Record[] record = response.getData();
				}
					break;
				case FETCH : {
					System.out.println(request.getDataAsString());

					// executeFetch(request);
				}
					break;
				case REMOVE : {
					executeRemove(response);
				}
					break;
				case UPDATE : {
					// executeUpdate(response);
				}
					break;

				default :
					break;
			}
		}
		super.transformResponse(response, request, data);
	}

	private void executeRemove(DSResponse response) {
		System.out.println("executeRemove row");

		Record[] records = response.getData();

		// Add the records to a "remove" list. The records will not
		// be removed from the server until the user does a save

		for (Record record : records) {
			i_recordsToDelete.add(record);

			System.out.println("Record to remove : " + record.getAttribute("name"));
		}

	}

	private void saveAs() {
		System.out.println("SAVE AS called");
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
