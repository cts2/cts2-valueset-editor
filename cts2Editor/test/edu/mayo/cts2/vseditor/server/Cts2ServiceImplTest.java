package edu.mayo.cts2.vseditor.server;

import edu.mayo.cts2.vseditor.server.helpers.ValueSet;
import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.server.Cts2EditorServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Cts2ServiceImplTest {

	private static Cts2EditorService service;

	@BeforeClass
	public static void initClass() {
		service = new Cts2EditorServiceImpl();
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
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = factory.newDocumentBuilder();
			Document document = db.parse(new ByteArrayInputStream(resultXml.getBytes("UTF-8")));
			NodeList nodes = document.getElementsByTagName("ValueSetCatalogEntryMsg");
			assertEquals(2, nodes.getLength());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetResolvedValueSet() {
//		String oid = "2.16.840.1.113883.3.526.03.362";
//
//		try {
//			ResolvedValueSet resolvedValueSet = new ResolvedValueSet(service.getResolvedValueSet(oid));
//			assertEquals("84", resolvedValueSet.getNumberOfEntries());
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}

	}

}
