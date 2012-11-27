package edu.mayo.cts2.vseditor.server;

import static org.junit.Assert.assertEquals;
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

		try {
			String resultXml = service.getResolvedValueSet(oid);
			Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			NodeList nodes = document.getElementsByTagName("entry");
			assertEquals(84, nodes.getLength());
		} catch (Exception e) {
			fail(e.getMessage());
		}

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
			String resultXml = service.getDefinitons(oid);
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

	@Test
	public void testDeleteChangeSet() throws Exception {
		String uri = service.createChangeSet();
		String resultXml = service.deleteChangeSet(uri);
		/* TODO: test that change set has been deleted */
	}

	@Test
	public void testGetChangeSet() throws Exception {
		String uri = service.createChangeSet();
		String resultXml = service.getChangeSet(uri);
		/* TODO: test that the correct change set has been returned */
	}

	@Test
	public void testUpdateChangeSet() throws Exception {
		String uri = service.createChangeSet();
		String resultXml = service.updateChangeSet(uri);
		/* TODO: test that the change set has been updated */
	}

	@Test
	public void testGetMatchingEntities() throws Exception {
		String resultXml = service.getMatchingEntities("Acute exacerbation of chronic asthmatic bronchitis");
		System.out.println("resultXml: " + resultXml);
		Document document = documentBuilder.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
		NodeList nodes = document.getElementsByTagName("entry");
		assertTrue(nodes.getLength() == 1);
	}

}
