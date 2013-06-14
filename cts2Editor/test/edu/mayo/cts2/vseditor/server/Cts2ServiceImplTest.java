package edu.mayo.cts2.vseditor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;
import edu.mayo.cts2.vseditor.server.helpers.TestUtils;
import edu.mayo.cts2.vseditor.server.helpers.ValueSet;

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
		/* TODO: Load valuesets into database */
		/*
		 * 2.16.840.1.113883.1.11.1 2.16.840.1.113883.3.526.03.362
		 * 2.16.840.1.113883.3.526.02.99
		 */
	}

	@Before
	public void setUp() {
		valueSetOid = "2.16.840.1.113883.1.11.1";
		user = "dsuesse";
		note = "Note #1";
		fname = "Gender";
		name = valueSetOid;

		entries = new ArrayList<DefinitionEntry>(5);
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "U", "Gender U", ""));
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "M", "Gender M", ""));
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "X", "Gender X", ""));
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "Y", "Gender Y", ""));
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "Z", "Gender Z", ""));
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
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetResolvedValueSet() {
		String oid = "2.16.840.1.113883.3.526.03.362";

		try {
			String resultXml = service.getResolvedValueSet(oid, null, null);
			Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			NodeList nodes = document.getElementsByTagName("entry");
			assertEquals(84, nodes.getLength());
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void testGetValueSetDefinition() {
		String oid = "2.16.840.1.113883.1.11.1";

		try {
			String resultXml = service.getValueSetDefinition(oid);
			Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			NodeList nodes = document.getElementsByTagName("entry");
			assertTrue(nodes.getLength() > 0);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUserDefinition() {
		String oid = "2.16.840.1.113883.1.11.1";

		try {
			String resultXml = service.getUserDefinitions(oid, "Test Runner 97dac6fd-6382-4eb4-81b9-0a7a89cdc7c0");
			Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			Node node = document.getElementsByTagName("ValueSetDefinitionDirectory").item(0);
			assertTrue(Integer.parseInt(node.getAttributes().getNamedItem("numEntries").getTextContent()) > 0);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetDefinitions() {
		String oid = "2.16.840.1.113883.1.11.1";
		try {
			String resultXml = service.getDefinitions(oid);
			Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			Node node = document.getElementsByTagName("ValueSetDefinitionDirectory").item(0);
			assertTrue(Integer.parseInt(node.getAttributes().getNamedItem("numEntries").getTextContent()) > 1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
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
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateChangeSet() throws Exception {
		assertTrue(service.createChangeSet() != null);
	}
	//
	// @Test
	// public void testDeleteChangeSet() throws Exception {
	// String uri = service.createChangeSet();
	// String resultXml = service.deleteChangeSet(uri);
	// /* TODO: test that change set has been deleted */
	// }
	//
	// @Test
	// public void testGetChangeSet() throws Exception {
	// String uri = service.createChangeSet();
	// String resultXml = service.getChangeSet(uri);
	// /* TODO: test that the correct change set has been returned */
	// }

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
//		String resultXml = service.getMatchingEntities("Acute exacerbation of chronic asthmatic bronchitis");
//		Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
//		NodeList nodes = document.getElementsByTagName("entry");
//		assertTrue(nodes.getLength() == 1);
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

	@Test
	public void saveAsThenSave() throws Exception {
		// SAVE_AS
		CTS2Result saveAsResult = testSaveDefinitionAs();
		assertFalse(saveAsResult.isError());
		assertNotNull(saveAsResult.getChangeSetUri());
		assertNotNull(saveAsResult.getDocumentUri());
		assertFalse(saveAsResult.getValueSetVersion().equalsIgnoreCase("1"));
		assertEquals(valueSetOid, saveAsResult.getValueSetOid());

		String resultXML = service.getDefinition(saveAsResult.getValueSetOid(), saveAsResult.getValueSetVersion(),
		        saveAsResult.getChangeSetUri());
		ValueSetDefinition resultDefinition = TestUtils.unmarshallValueSetDefinitionMsg(resultXML)
		        .getValueSetDefinition();
		assertEquals(user, resultDefinition.getSourceAndRole(0).getSource().getContent());
		List<edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry> vsdEntries = resultDefinition
		        .getEntryAsReference();
		List<DefinitionEntry> convertedEntries = getEditorEntries(vsdEntries);
		assertEquals(5, convertedEntries.size());
		for (DefinitionEntry entry : entries) {
			assertTrue(convertedEntries.contains(entry));
		}

		// SAVE
		String newNote = "Save Attempt " + UUID.randomUUID().toString();
		CTS2Result saveResult = testSaveDefinition(saveAsResult, newNote);
		assertEquals(valueSetOid, saveResult.getValueSetOid());
		assertEquals(saveAsResult.getValueSetVersion(), saveResult.getValueSetVersion());
		assertFalse(saveResult.getChangeSetUri().equals(saveAsResult.getChangeSetUri()));

		/* Directly get the definition, it should match the updates made */
		String resultXml = service.getDefinition(valueSetOid, saveResult.getValueSetVersion(),
		        saveResult.getChangeSetUri());
		resultDefinition = TestUtils.unmarshallValueSetDefinitionMsg(resultXml).getValueSetDefinition();
		vsdEntries = resultDefinition.getEntryAsReference();
		convertedEntries = getEditorEntries(vsdEntries);
		assertEquals(1, convertedEntries.size());
		assertEquals(saveResult.getChangeSetUri(), resultDefinition.getChangeableElementGroup().getChangeDescription()
		        .getContainingChangeSet());
		assertEquals(newNote, resultDefinition.getNote(0).getValue().getContent());
	}

	private CTS2Result testSaveDefinitionAs() throws Exception {
		Definition definition = createDefinition();
		CTS2Result cts2Result = service.saveDefinitionAs(definition);

		return cts2Result;
	}

	private CTS2Result testSaveDefinition(CTS2Result def, String newNote) throws Exception {
		String version = def.getValueSetVersion();
		String changeSetUri = def.getChangeSetUri();
		String documentUri = def.getDocumentUri();

		Definition definition = new Definition();
		definition.setValueSetOid(valueSetOid);
		definition.setVersion(version);
		definition.setChangeSetUri(changeSetUri);
		definition.setDocumentUri(documentUri);
		definition.setAbout("urn:oid:" + valueSetOid);
		definition.setFormalName(fname);
		definition.setResourceSynopsis(null);
		definition.setCreator(user);
		definition.setNote(newNote);
		List<DefinitionEntry> entries = new ArrayList<DefinitionEntry>(1);
		entries.add(new DefinitionEntry("", "", "AdministrativeGender", "Y", "Gender Y", ""));
		definition.setEntries(entries);

		/* Save Definition */
		CTS2Result saveResult = service.saveDefinition(definition);
		return saveResult;
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

	private List<DefinitionEntry> getEditorEntries(
	        List<edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry> entries) {
		List<DefinitionEntry> editorEntries = new ArrayList<DefinitionEntry>(entries.size());
		for (edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry entry : entries) {
			for (URIAndEntityName entity : entry.getEntityList().getReferencedEntity()) {
				editorEntries.add(new DefinitionEntry(entity.getUri(), entity.getHref(), entity.getNamespace(), entity
				        .getName(), "", ""));
			}
		}
		return editorEntries;
	}

}
