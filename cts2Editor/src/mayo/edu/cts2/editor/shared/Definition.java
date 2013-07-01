package mayo.edu.cts2.editor.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Definition implements Serializable {

	/* Value Set */
	private String valueSetOid;
	private String valueSetUri;
	private String valueSetFormalName;

	/* Value Set Definition */
	private String name;
	private String version;
	private String changeSetUri;
	private String documentUri;
	private String about;
	private String formalName;
	private String resourceSynopsis;
	private String creator;
	private String note;
	private List<DefinitionEntry> entries = new ArrayList<DefinitionEntry>();

	public String getValueSetOid() {
		return valueSetOid;
	}

	public void setValueSetOid(String valueSetOid) {
		this.valueSetOid = valueSetOid;
	}

	public String getValueSetUri() {
		return valueSetUri;
	}

	public void setValueSetUri(String valueSetUri) {
		this.valueSetUri = valueSetUri;
	}

	public String getValueSetFormalName() {
		return valueSetFormalName;
	}

	public void setValueSetFormalName(String valueSetFormalName) {
		this.valueSetFormalName = valueSetFormalName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFormalName() {
		return formalName;
	}

	public void setFormalName(String formalName) {
		this.formalName = formalName;
	}

	public String getResourceSynopsis() {
		return resourceSynopsis;
	}

	public void setResourceSynopsis(String resourceSynopsis) {
		this.resourceSynopsis = resourceSynopsis;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getChangeSetUri() {
		return changeSetUri;
	}

	public void setChangeSetUri(String changeSetUri) {
		this.changeSetUri = changeSetUri;
	}

	public String getDocumentUri() {
		return documentUri;
	}

	public void setDocumentUri(String documentUri) {
		this.documentUri = documentUri;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<DefinitionEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<DefinitionEntry> entries) {
		this.entries = entries;
	}

	public void addEntry(DefinitionEntry entry) {
		this.entries.add(entry);
	}

	public void addEntry(int index, DefinitionEntry entry) {
		this.entries.add(index, entry);
	}

	public boolean removeEntry(DefinitionEntry entry) {
		return this.entries.remove(entry);
	}

	public DefinitionEntry removeEntry(int index) {
		return this.entries.remove(index);
	}

}
