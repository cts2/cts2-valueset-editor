package edu.mayo.cts2.vseditor.server.helpers;

import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.Serializable;
import java.io.StringReader;

public class ValueSet implements Serializable {

	private String name;
	private String formalName;
	private String developer;
	private String currentDefinition;
	private String xml;

	private static final String XPATH_BASE = "/cts2:ValueSetCatalogEntryMsg/cts2:valueSetCatalogEntry";
	private static final String XPATH_NAME = XPATH_BASE + "/@valueSetName";
	private static final String XPATH_FORMAL_NAME = XPATH_BASE + "/@formalName";
	private static final String XPATH_DEVELOPER = XPATH_BASE + "/core:sourceAndRole/core:source";
	private static final String XPATH_CURRENT_DEFINITION = XPATH_BASE + "/cts2:currentDefinition/core:valueSetDefinition";

	public ValueSet() {
		name = "";
		formalName = "";
		developer = "";
		currentDefinition = "";
		xml = "";
	}

	/**
	 * Creates a new value set by parsing the xml representation.
	 *
	 * @param xml the xml representation of the value set
	 */
	public ValueSet(String xml) {
		this.xml = xml;
		parseXml();
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormalName() {
		return formalName;
	}

	public void setFormalName(String formalName) {
		this.formalName = formalName;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public String getCurrentDefinition() {
		return currentDefinition;
	}

	public void setCurrentDefinition(String currentDefinition) {
		this.currentDefinition = currentDefinition;
	}

	private void parseXml() {
		if (xml != null) {
			try {
				XPath xpath = XPathFactory.newInstance().newXPath();
				xpath.setNamespaceContext(NamespaceContextHelper.getInstance().getNamespaceContext(NamespaceContextHelper.CTS2_NAMESPACE));
				InputSource inputSource = new InputSource(new StringReader(this.xml));
				this.name = xpath.evaluate(XPATH_NAME, inputSource);
				inputSource = new InputSource(new StringReader(this.xml));
				this.formalName = xpath.evaluate(XPATH_FORMAL_NAME, inputSource);
				inputSource = new InputSource(new StringReader(this.xml));
				this.developer = xpath.evaluate(XPATH_DEVELOPER, inputSource);
				inputSource = new InputSource(new StringReader(this.xml));
				this.currentDefinition = xpath.evaluate(XPATH_CURRENT_DEFINITION, inputSource);
			}
			catch (XPathExpressionException e) {

			}

		}
	}

}

