package mayo.edu.cts2.editor.client.datasource;

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

public class ValueSetItemSearchXmlDS extends BaseValueSetItemSearchXmlDS {

	private static final String RECORD_X_PATH = "/cts2:EntityDirectory/cts2:entry";

	private static final String X_PATH_NUMBER_OF_ENTRIES = "/cts2:EntityDirectory/@numEntries";
	private static final String X_PATH_COMPLETE = "/cts2:EntityDirectory/@complete";

	private static final String X_PATH_ENTRY_NAME = "core:name/core:name";
	private static final String X_PATH_DESIGNATION = "core:knownEntityDescription/core:designation";
	private static final String
	  X_PATH_CODE_SYSTEM = "core:knownEntityDescription/core:describingCodeSystemVersion/core:codeSystem";
	private static final String X_PATH_CODE_SYSTEM_VERSION = "core:knownEntityDescription/core:describingCodeSystemVersion/core:version";

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

		// set the XPath
		setRecordXPath(RECORD_X_PATH);

		DataSourceTextField nbrOfEntriesField = new DataSourceTextField("numEntries", "Entries");
		nbrOfEntriesField.setValueXPath(X_PATH_NUMBER_OF_ENTRIES);

		DataSourceTextField completeField = new DataSourceTextField("complete", "Complete");
		completeField.setValueXPath(X_PATH_COMPLETE);

		DataSourceTextField uriField = new DataSourceTextField("uri", "URI");
		uriField.setValueXPath("@about");
		uriField.setPrimaryKey(true);

		DataSourceTextField codeSystemField = new DataSourceTextField("namespace", "Code System");
		codeSystemField.setValueXPath(X_PATH_CODE_SYSTEM);

		DataSourceTextField nameField = new DataSourceTextField("name", "Code");
		nameField.setValueXPath(X_PATH_ENTRY_NAME);

		DataSourceTextField designationField = new DataSourceTextField("designation", "Description");
		designationField.setValueXPath(X_PATH_DESIGNATION);

		DataSourceTextField codeSystemVersionField = new DataSourceTextField("codeSystemVersion", "Code System Version");
		codeSystemVersionField.setValueXPath(X_PATH_CODE_SYSTEM_VERSION);

		setFields(nbrOfEntriesField, completeField, uriField, codeSystemField, nameField, designationField, codeSystemVersionField);

		setClientOnly(true);
	}

	@Override
	public void fetchData(Criteria criteria, final DSCallback callback) {

		final String searchText = criteria.getAttribute("searchText");
		final String codeSystem = criteria.getAttribute("codeSystem");
		final String codeSystemVersion = criteria.getAttribute("codeSystemVersion");

		Cts2EditorServiceAsync service = GWT.create(Cts2EditorService.class);
		service.getMatchingEntities(codeSystem, codeSystemVersion, searchText, new AsyncCallback<String>() {

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
