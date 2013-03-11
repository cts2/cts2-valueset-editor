package mayo.edu.cts2.editor.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class UpdateValueSetVersionEvent extends GwtEvent<UpdateValueSetVersionEventHandler> {

	public static Type<UpdateValueSetVersionEventHandler> TYPE = new Type<UpdateValueSetVersionEventHandler>();

	private final String i_valueSetId;
	private final String i_changeSetUri;
	private final String i_versionId;
	private final String i_comment;
	private final String i_docuemntUri;

	public UpdateValueSetVersionEvent(String valueSetId, String changeSetUri, String versionId, String comment,
	        String docuemntUri) {
		super();

		i_valueSetId = valueSetId;
		i_changeSetUri = changeSetUri;
		i_versionId = versionId;
		i_comment = comment;
		i_docuemntUri = docuemntUri;
	}

	@Override
	public Type<UpdateValueSetVersionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateValueSetVersionEventHandler handler) {
		handler.onValueSetVersionUpdated(this);
	}

	public String getValueSetId() {
		return i_valueSetId;
	}

	public String getChangeSetUri() {
		return i_changeSetUri;
	}

	public String getVersionId() {
		return i_versionId;
	}

	public String getComment() {
		return i_comment;
	}

	public String getDocumentUri() {
		return i_docuemntUri;
	}

}
