package edu.mayo.cts2.vseditor.server.helpers;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.CharArrayWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ResolvedValueSet implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String X_PATH_CODE_SYSTEM = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolvedUsingCodeSystem/core:codeSystem";
	private static final String X_PATH_CODE_SYSTEM_VERSION = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolvedUsingCodeSystem/core:version";
	private static final String X_PATH_DEFINITION = "/cts2:IteratableResolvedValueSet/cts2:resolutionInfo/cts2:resolutionOf/core:valueSetDefinition";
	private static final String X_PATH_ENTRIES = "/cts2:IteratableResolvedValueSet/cts2:entry";
	private static final String X_PATH_ENTRY_NAMESPACE = "core:namespace";
	private static final String X_PATH_ENTRY_NAME = "core:name";
	private static final String X_PATH_ENTRY_DESIGNATION = "core:designation";
	private static final String X_PATH_NUMBER_OF_ENTRIES = "/cts2:IteratableResolvedValueSet/@numEntries";

	private String xml;
	private String codeSystem;
	private String codeSystemVersion;
	private String definition;
	private String numberOfEntries;
	private List<ValueSetEntry> valueSetEntries;

	public ResolvedValueSet() {
		xml = "";
		valueSetEntries = new ArrayList<ValueSetEntry>();
	}

	public ResolvedValueSet(String xml) throws Exception {
		this.xml = xml;
		valueSetEntries = new ArrayList<ValueSetEntry>();
		parseXml();
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getCodeSystem() {
		return codeSystem;
	}

	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}

	public String getCodeSystemVersion() {
		return codeSystemVersion;
	}

	public void setCodeSystemVersion(String codeSystemVersion) {
		this.codeSystemVersion = codeSystemVersion;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getNumberOfEntries() {
		return numberOfEntries;
	}

	public void setNumberOfEntries(String numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	public List<ValueSetEntry> getValueSetEntries() {
		return valueSetEntries;
	}

	public void setValueSetEntries(List<ValueSetEntry> valueSetEntries) {
		this.valueSetEntries = valueSetEntries;
	}

	private void parseXml() throws SAXException, XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(NamespaceContextHelper.getInstance().getNamespaceContext(NamespaceContextHelper.CTS2_NAMESPACE));
		InputSource inputSource = new InputSource(new StringReader(this.xml));
		this.codeSystem = xpath.evaluate(X_PATH_CODE_SYSTEM, inputSource);
		inputSource = new InputSource(new StringReader(this.xml));
		this.codeSystemVersion = xpath.evaluate(X_PATH_CODE_SYSTEM_VERSION, inputSource);
		inputSource = new InputSource(new StringReader(this.xml));
		this.definition = xpath.evaluate(X_PATH_DEFINITION, inputSource);
		inputSource = new InputSource(new StringReader(this.xml));
		this.numberOfEntries = xpath.evaluate(X_PATH_NUMBER_OF_ENTRIES, inputSource);
	}

	private class ResolvedValueSetHandler extends DefaultHandler {
		private Vector<ValueSetEntry> entries = new Vector<ValueSetEntry>();
		private ValueSetEntry entry;
		private CharArrayWriter contents = new CharArrayWriter();

		public void startElement(String namespaceUri, String localName, String qName, Attributes attributes) throws SAXException {
			contents.reset();
			if (localName.equals("entry")) {
				entry = new ValueSetEntry();
				entries.add(entry);
			}
		}

		public void endElement(String nameSpaceUri, String localName, String qName) throws SAXException {

		}

		public void characters( char[] ch, int start, int length )
		  throws SAXException {
			contents.write( ch, start, length );
		}
	}

}