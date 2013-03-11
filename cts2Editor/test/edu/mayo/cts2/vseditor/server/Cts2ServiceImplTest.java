package edu.mayo.cts2.vseditor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.vseditor.server.helpers.TestUtils;
import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.server.Cts2EditorServiceImpl;
import mayo.edu.cts2.editor.shared.CTS2Result;
import mayo.edu.cts2.editor.shared.Definition;
import mayo.edu.cts2.editor.shared.DefinitionEntry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.mayo.cts2.vseditor.server.helpers.ValueSet;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;

public class Cts2ServiceImplTest {

	private static Cts2EditorService service;
	private static DocumentBuilderFactory factory;
	private static DocumentBuilder documentBuilder;

	private String valueSetOid;
	private String user;
	private String note;
	private String fname;
	private String name;
	private List<DefinitionEntry> entries;

	@BeforeClass
	public static void initClass() throws ParserConfigurationException {
		service = new Cts2EditorServiceImpl();
		factory = DocumentBuilderFactory.newInstance();
		documentBuilder = factory.newDocumentBuilder();
	}

	@Before
	public void setUp() {
		valueSetOid = "2.16.840.1.113883.1.11.1";
//		user = "Test Runner " + UUID.randomUUID().toString();
//		note = "Test Note " + UUID.randomUUID().toString();
		user = "dsuesse";
		note = "Note #1";
		fname = "Gender";
		name = valueSetOid;

		entries = new ArrayList<DefinitionEntry>(5);
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "U"));
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "M"));
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "X"));
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "Y"));
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "Z"));
	}

	@Test
	public void testGetValueSet() {
		String oid = "2.16.840.1.113883.1.11.1";
		String formalName = "Gender";
		String developer = "National Committee for Quality Assurance";
		String currentDefinition = "1";
		ValueSet vs = new ValueSet(service.getValueSet(oid));
		assertEquals(oid, vs.getName());
		assertEquals(formalName, vs.getFormalName());
		assertEquals(developer, vs.getDeveloper());
		assertEquals(currentDefinition, vs.getCurrentDefinition());
	}

	@Test
	public void testGetValueSets() {
		List<String> oids = new ArrayList<String>();
		String oid1 = "2.16.840.1.113883.3.526.03.362";
		String oid2 = "2.16.840.1.113883.3.526.02.99";
		oids.add(oid1);
		oids.add(oid2);

		String resultXml = service.getValueSets(oids);
		try {
			Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			NodeList nodes = document.getElementsByTagName("ValueSetCatalogEntryMsg");
			assertEquals(2, nodes.getLength());
		} catch (Exception e) { fail(e.getMessage()); }
	}

	@Test
	public void testGetResolvedValueSet() {
		String oid = "2.16.840.1.113883.3.526.03.362";

		try {
			String resultXml = service.getResolvedValueSet(oid, null, null);
			Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			NodeList nodes = document.getElementsByTagName("entry");
			assertEquals(84, nodes.getLength());
		} catch (Exception e) { fail(e.getMessage()); }

	}

	@Test
	public void testGetValueSetDefinition() {
		String oid = "2.16.840.1.113883.1.11.1";

		try {
			String resultXml = service.getValueSetDefinition(oid);
			Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			NodeList nodes = document.getElementsByTagName("entry");
			assertTrue(nodes.getLength() > 0);
		} catch (Exception e) { fail(e.getMessage()); }
	}

	@Test
	public void testGetUserDefinition() {
		String oid = "2.16.840.1.113883.1.11.1";

		try {
			String resultXml = service.getUserDefinitions(oid, "Test Runner 97dac6fd-6382-4eb4-81b9-0a7a89cdc7c0");
			Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			Node node = document.getElementsByTagName("ValueSetDefinitionDirectory").item(0);
			assertTrue(Integer.parseInt(node.getAttributes().getNamedItem("numEntries").getTextContent()) > 0);
		} catch (Exception e) { fail(e.getMessage()); }
	}

	@Test
	public void testGetDefinitions() {
		String oid = "2.16.840.1.113883.1.11.1";
		try {
		String resultXml = service.getDefinitions(oid);
		Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			Node node = document.getElementsByTagName("ValueSetDefinitionDirectory").item(0);
			assertTrue(Integer.parseInt(node.getAttributes().getNamedItem("numEntries").getTextContent()) > 1);
		} catch (Exception e) { fail(e.getMessage()); }
	}

	@Test
	public void testGetMatchingValueSets() {
		String term = "asthma";
		try {
			String resultXml = service.getMatchingValueSets(term);
			Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			NodeList nodes = document.getElementsByTagName("entry");
			assertTrue(nodes.getLength() > 0);
			System.out.println("testGetMatchingValueSets:\n" + resultXml);
		} catch (Exception e) { fail(e.getMessage()); }
	}

	@Test
	public void testCreateChangeSet() throws Exception {
		assertTrue(service.createChangeSet() != null);
	}
//
//	@Test
//	public void testDeleteChangeSet() throws Exception {
//		String uri = service.createChangeSet();
//		String resultXml = service.deleteChangeSet(uri);
//		/* TODO: test that change set has been deleted */
//	}
//
//	@Test
//	public void testGetChangeSet() throws Exception {
//		String uri = service.createChangeSet();
//		String resultXml = service.getChangeSet(uri);
//		/* TODO: test that the correct change set has been returned */
//	}

	@Test
	public void testUpdateChangeSet() throws Exception {
		String user = "Test User";
		String uri = service.createChangeSet();
		String initialXml = service.getChangeSet(uri);
		assertFalse(initialXml.contains(user));

		service.updateChangeSet(uri, user, "Test Description");
		String resultXml = service.getChangeSet(uri);
		assertTrue(resultXml.contains(user));
	}

	@Test
	public void testGetMatchingEntities() throws Exception {
		String resultXml = service.getMatchingEntities("Acute exacerbation of chronic asthmatic bronchitis");
		Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
		NodeList nodes = document.getElementsByTagName("entry");
		assertTrue(nodes.getLength() == 1);
	}

	@Test
	public void testSaveDefinition() throws Exception {
		String version = "3a00b51b-6a85-407a-918e-4902e2bc2154";
		String changeSetUri = "4bc5e70e-91b8-4db8-8a08-587e0f996e49";
		String documentUri = "3bab8884-f6d3-4da4-ac32-d40c07601963";

		Definition definition = new Definition();
		definition.setValueSetOid(valueSetOid);
		definition.setVersion(version);
		definition.setChangeSetUri(changeSetUri);
		definition.setDocumentUri(documentUri);
		definition.setAbout("urn:oid:" + valueSetOid);
		definition.setFormalName(fname);
		definition.setResourceSynopsis(null);
		definition.setCreator(user);
		definition.setNote("Save Definition Attempt");
		List<DefinitionEntry> entries = new ArrayList<DefinitionEntry>(1);
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "Y"));
		definition.setEntries(entries);

		/* Save Definition */
		CTS2Result saveResult = service.saveDefinition(definition);
		assertEquals(valueSetOid, saveResult.getValueSetOid());
		assertEquals(version, saveResult.getValueSetVersion());
		assertFalse(saveResult.getChangeSetUri().equals(changeSetUri));

		/* Directly get the definition, it should match the updates made */
		String resultXml = service.getDefinition(valueSetOid, version, saveResult.getChangeSetUri());
		ValueSetDefinition resultDefinition = TestUtils.unmarshallValueSetDefinitionMsg(resultXml).getValueSetDefinition();
		List<edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry> vsdEntries = resultDefinition.getEntryAsReference();
		List<DefinitionEntry> convertedEntries = getEditorEntries(vsdEntries);
		assertEquals(1, convertedEntries.size());

		validateUserDefinitions(version, saveResult.getChangeSetUri());

	}

	private void validateUserDefinitions(String version, String changeSetUri) throws Exception {
		/* Get the list of definitions for the user, the version "74e400f6-5655-4add-868c-73ead8d4351d" should have the latest changeset "a6e85ef1-fcef-4b58-81f5-93fc90dab606" applied. */
		String userDefsXml = service.getUserDefinitions(valueSetOid, user);
		Document userDefsDoc = documentBuilder.parse(new ByteArrayInputStream(userDefsXml.getBytes("UTF-8")));
		NodeList defEntries = userDefsDoc.getElementsByTagName("entry");
		assertEquals(1, defEntries.getLength());

		Node defEntry = defEntries.item(0);
		assertEquals(version, defEntry.getAttributes().getNamedItem("resourceName"));
		NodeList defEntryElements = defEntry.getChildNodes();
		for (int i = 0; i < defEntryElements.getLength(); i++) {
			Node element = defEntryElements.item(i);
			// check for correct version
			if (element.getNodeName().equals("versionTag")) {
				assertEquals(version, element.getNodeValue());
			}
			// check for correct changeSetUri
			else if (element.getNodeName().equals("resourceSynopsis")) {
				String value = element.getFirstChild().getNodeValue();
				String beginStr = "&lt;changeSetUri&gt;";
				int begin = value.indexOf(beginStr) + beginStr.length();
				String endStr = "&lt;/changeSetUri&gt;";
				int end = value.indexOf(endStr) + endStr.length();
				assertEquals(changeSetUri, value.substring(begin, end));
			}
		}
	}

	@Test
	public void testSaveDefinitionAs() throws Exception {
		Definition definition = createDefinition();
		CTS2Result cts2Result = service.saveDefinitionAs(definition);

		assertFalse(cts2Result.isError());
		assertNotNull(cts2Result.getChangeSetUri());
		assertNotNull(cts2Result.getDocumentUri());
		assertFalse(cts2Result.getValueSetVersion().equalsIgnoreCase("1"));
		assertEquals(valueSetOid, cts2Result.getValueSetOid());

		String resultXML = service.getDefinition(cts2Result.getValueSetOid(), cts2Result.getValueSetVersion(), cts2Result.getChangeSetUri());

		ValueSetDefinition resultDefinition = TestUtils.unmarshallValueSetDefinitionMsg(resultXML).getValueSetDefinition();

		assertEquals(user, resultDefinition.getSourceAndRole(0).getSource().getContent());
		List<edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry> vsdEntries = resultDefinition.getEntryAsReference();
		List<DefinitionEntry> convertedEntries = getEditorEntries(vsdEntries);

		assertEquals(5, convertedEntries.size());

		for (DefinitionEntry entry : entries) {
			assertTrue(convertedEntries.contains(entry));
		}

	}

	@Test
	public void testIsFinal() throws Exception {
		String oid = "2.16.840.1.113883.1.11.1";
		String version = "1";

		Definition openDef = new Definition();
		openDef.setValueSetOid(oid);
		openDef.setVersion("7130dd27-a224-4712-92a4-b1f3c07e580f");
		openDef.setChangeSetUri("216a5e4a-9e43-4318-9696-8aa4a2bf0e73");

		Definition finalDef = new Definition();
		finalDef.setValueSetOid(oid);
		finalDef.setVersion(version);

		assertTrue(service.isFinal(finalDef));
		assertFalse(service.isFinal(openDef));

	}

	private Definition createDefinition() {
		Definition definition = new Definition();
		definition.setValueSetOid(valueSetOid);
		definition.setAbout(valueSetOid);
		definition.setCreator(user);
		definition.setNote(note);
		definition.setFormalName(fname);
		definition.setResourceSynopsis(name);
		definition.setEntries(entries);
		return definition;
	}

	private List<DefinitionEntry> getEditorEntries(List<edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry> entries) {
		List<DefinitionEntry> editorEntries = new ArrayList<DefinitionEntry>(entries.size());
		for (edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry entry : entries) {
			for (URIAndEntityName entity : entry.getEntityList().getReferencedEntity()) {
				editorEntries.add(new DefinitionEntry(entity.getUri(), entity.getHref(), entity.getNamespace(), entity
				  .getName()));
			}
		}
		return editorEntries;
	}

}
