package mayo.edu.cts2.editor.client.datasource;

import java.util.LinkedHashMap;

import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.client.Cts2EditorServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;

/**
 * DataSource for the value set search values
 */
public class ValueSetsSearchXmlDS extends BaseValueSetXmlDS {

	private static final String RECORD_X_PATH = "/cts2:ValueSetCatalogEntryDirectory/cts2:entry";

	private static final String X_PATH_NUMBER_OF_ENTRIES = "/cts2:ValueSetCatalogEntryDirectory/@numEntries";
	private static final String X_PATH_COMPLETE = "/cts2:ValueSetCatalogEntryDirectory/@complete";

	private static final String X_PATH_RESOURCE_ROOT = "/cts2:ValueSetCatalogEntryDirectory/core:heading/core:resourceRoot";
	private static final String X_PATH_RESOURCE_SYNOPSIS = "core:resourceSynopsis/core:value";

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

		// set the XPath
		setRecordXPath(RECORD_X_PATH);

		DataSourceTextField nbrOfEntriesField = new DataSourceTextField("numEntries", "Entries");
		nbrOfEntriesField.setValueXPath(X_PATH_NUMBER_OF_ENTRIES);

		DataSourceTextField completeField = new DataSourceTextField("complete", "Complete");
		completeField.setValueXPath(X_PATH_COMPLETE);

		DataSourceTextField hrefField = new DataSourceTextField("href", "HREF");
		hrefField.setPrimaryKey(true);

		DataSourceTextField resourceTypefField = new DataSourceTextField("resourceRoot", "Resource Type");
		resourceTypefField.setValueXPath(X_PATH_RESOURCE_ROOT);

		// map the value returned to a displayable value
		LinkedHashMap<String, String> resourceTypeMap = new LinkedHashMap<String, String>();
		resourceTypeMap.put("valuesets", "Value Sets");
		resourceTypefField.setValueMap(resourceTypeMap);

		DataSourceTextField valueSetNamefField = new DataSourceTextField("valueSetName", "Value Set Name");
		DataSourceTextField aboutField = new DataSourceTextField("about", "About");
		DataSourceTextField formalNameField = new DataSourceTextField("formalName", "Formal Name");
		DataSourceTextField resourceSynopsisValueField = new DataSourceTextField("value", "Resource Synopsis");
		resourceSynopsisValueField.setValueXPath(X_PATH_RESOURCE_SYNOPSIS);

		setFields(hrefField, resourceTypefField, valueSetNamefField, aboutField, formalNameField,
		        resourceSynopsisValueField, nbrOfEntriesField, completeField);

		setClientOnly(true);
	}

	@Override
	public void fetchData(Criteria criteria, final DSCallback callback) {

		final String searchText = criteria.getAttribute("searchText");

		Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);
		service.getMatchingValueSets(searchText, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				Object results = XMLTools.selectNodes(result, RECORD_X_PATH, i_nsMap);
				Record[] fetchRecords = recordsFromXML(results);
				setTestData(fetchRecords);

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

}
