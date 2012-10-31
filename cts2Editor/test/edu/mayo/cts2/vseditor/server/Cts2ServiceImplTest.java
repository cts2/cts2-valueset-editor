package edu.mayo.cts2.vseditor.server;

import edu.mayo.cts2.vseditor.server.helpers.ResolvedValueSet;
import edu.mayo.cts2.vseditor.server.helpers.ValueSet;
import mayo.edu.cts2.editor.client.Cts2EditorService;
import mayo.edu.cts2.editor.server.Cts2EditorServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		Map<String, String> resultMap = service.getValueSets(oids);
		Map<String, ValueSet> valueSetMap = new HashMap<String, ValueSet>(resultMap.size());
		for (String oid : resultMap.keySet()) {
			valueSetMap.put(oid, new ValueSet(resultMap.get(oid)));
		}
		assertEquals("2.16.840.1.113883.3.526.03.362", valueSetMap.get(oid1).getName());
		assertEquals("Asthma", valueSetMap.get(oid1).getFormalName());
		assertEquals("National Committee for Quality Assurance", valueSetMap.get(oid1).getDeveloper());

		assertEquals("2.16.840.1.113883.3.526.02.99", valueSetMap.get(oid2).getName());
		assertEquals("Encounter Office & Outpatient Consult", valueSetMap.get(oid2).getFormalName());
		assertEquals("National Committee for Quality Assurance", valueSetMap.get(oid2).getDeveloper());

	}

	@Test
	public void testGetResolvedValueSet() {
		String oid = "2.16.840.1.113883.3.526.03.362";

		try {
			ResolvedValueSet resolvedValueSet = new ResolvedValueSet(service.getResolvedValueSet(oid));
			assertEquals("84", resolvedValueSet.getNumberOfEntries());
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

}
