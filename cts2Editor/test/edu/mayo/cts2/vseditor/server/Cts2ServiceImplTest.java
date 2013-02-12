package edu.mayo.cts2.vseditor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.server.Cts2EditorServiceImpl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import edu.mayo.cts2.vseditor.server.helpers.ValueSet;

public class Cts2ServiceImplTest {

	private static Cts2EditorService service;
	private static DocumentBuilderFactory factory;
	private static DocumentBuilder documentBuilder;

	@BeforeClass
	public static void initClass() throws ParserConfigurationException {
		service = new Cts2EditorServiceImpl();
		factory = DocumentBuilderFactory.newInstance();
		documentBuilder = factory.newDocumentBuilder();
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
		/*
		 * try { String resultXml = service.getResolvedValueSet(oid); Document
		 * document = documentBuilder.parse(new
		 * ByteArrayInputStream(resultXml.getBytes("UTF-8"))); NodeList nodes =
		 * document.getElementsByTagName("entry"); assertEquals(84,
		 * nodes.getLength()); } catch (Exception e) { fail(e.getMessage()); }
		 */
	}

	@Test
	public void testGetValueSetDefinition() {
		String oid = "2.16.840.1.113883.3.526.03.362";

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
	public void testGetDefinitions() {
		String oid = "2.16.840.1.113883.3.526.03.362";

		try {
			String resultXml = service.getDefinitions(oid);
			System.out.println(resultXml);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUserDefinition() {
		String oid = "2.16.840.1.113883.3.526.03.362";

		try {
			String resultXml = service.getUserDefinitions(oid, "dale");
			System.out.println(resultXml);
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
		String resultXml = service.getMatchingEntities("Acute exacerbation of chronic asthmatic bronchitis");
		System.out.println("resultXml: " + resultXml);
		Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
		NodeList nodes = document.getElementsByTagName("entry");
		assertTrue(nodes.getLength() == 1);
	}

	/*
	 * @Test public void testSaveValueSetAs() throws Exception { String parentId
	 * = "2.16.840.1.113883.1.11.1";
	 * 
	 * List<ValueSetDefinitionEntry> addEntries = new
	 * ArrayList<ValueSetDefinitionEntry>(2); addEntries.add(new
	 * ValueSetDefinitionEntry("", "", "AdministrativeGender", "X"));
	 * addEntries.add(new ValueSetDefinitionEntry("", "",
	 * "AdministrativeGender", "Y")); addEntries.add(new
	 * ValueSetDefinitionEntry("", "", "AdministrativeGender", "Z"));
	 * 
	 * List<ValueSetDefinitionEntry> removeEntries = new
	 * ArrayList<ValueSetDefinitionEntry>(2); removeEntries.add(new
	 * ValueSetDefinitionEntry("", "", "AdministrativeGender", "M"));
	 * 
	 * 
	 * String testUser = "Test User"; String testDesc =
	 * "This is a test description."; String resultXml =
	 * service.saveValueSetAs(parentId, testUser, testDesc, addEntries,
	 * removeEntries); if (resultXml == null || resultXml.equals("")) fail();
	 * ValueSetDefinition resultDefinition =
	 * TestUtils.unmarshallValueSetDefinitionMsg
	 * (resultXml).getValueSetDefinition();
	 * 
	 * assertFalse(resultDefinition.getDocumentURI().equals(parentId));
	 * assertEquals(testUser,
	 * resultDefinition.getSourceAndRole(0).getSource().getContent());
	 * List<edu.mayo
	 * .cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry> entries
	 * = resultDefinition.getEntryAsReference(); List<ValueSetDefinitionEntry>
	 * convertedEntries = getEditorEntries(entries);
	 * 
	 * for (ValueSetDefinitionEntry entry : addEntries) {
	 * assertTrue(convertedEntries.contains(entry)); }
	 * 
	 * for (ValueSetDefinitionEntry entry : removeEntries) {
	 * assertFalse(convertedEntries.contains(entry)); } }
	 * 
	 * @Test public void testGetUsersDefinitions() { String xml =
	 * service.getUserDefinitions("2.16.840.1.113883.1.11.1", "Test User");
	 * System.out.println(xml); }
	 * 
	 * private List<ValueSetDefinitionEntry>
	 * getEditorEntries(List<edu.mayo.cts2.
	 * framework.model.valuesetdefinition.ValueSetDefinitionEntry> entries) {
	 * List<ValueSetDefinitionEntry> editorEntries = new
	 * ArrayList<ValueSetDefinitionEntry>(entries.size()); for
	 * (edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry
	 * entry : entries) { for (URIAndEntityName entity :
	 * entry.getEntityList().getReferencedEntity()) { editorEntries.add(new
	 * ValueSetDefinitionEntry(entity.getUri(), entity.getHref(),
	 * entity.getNamespace(), entity .getName())); } } return editorEntries; }
	 */

}
