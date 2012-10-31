package edu.mayo.cts2.vseditor.server.helpers;

import java.util.List;

public class ValueSetList {

	private String xml;
	private List<ValueSet> valueSetList;

	public ValueSetList(String xml) {
		this.xml = xml;
		parseXml();
	}

	private void parseXml() {

	}

}
