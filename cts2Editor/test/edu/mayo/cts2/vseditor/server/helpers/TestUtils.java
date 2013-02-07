package edu.mayo.cts2.vseditor.server.helpers;

import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.model.updates.ChangeSet;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionMsg;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Logger;

public class TestUtils {

	private static Logger logger = Logger.getLogger(TestUtils.class.getName());

	public static ValueSetDefinition unmarshallValueSetDefinition(String xml) {
		DelegatingMarshaller marshaller = new DelegatingMarshaller();
		StringReader reader = new StringReader(xml);
		ValueSetDefinition definition = null;
		try {
			definition = (ValueSetDefinition) marshaller.unmarshal(new StreamSource(reader));
		} catch (IOException ioe) {
			logger.warning("Unable to unmarshal the returned value set definition xml. Message: " + ioe.getMessage()) ;
		}
		return definition;
	}

	public static ValueSetDefinitionMsg unmarshallValueSetDefinitionMsg(String xml) {
		DelegatingMarshaller marshaller = new DelegatingMarshaller();
		StringReader reader = new StringReader(xml);
		ValueSetDefinitionMsg definition = null;
		try {
			definition = (ValueSetDefinitionMsg) marshaller.unmarshal(new StreamSource(reader));
		} catch (IOException ioe) {
			logger.warning("Unable to unmarshal the returned value set definition xml. Message: " + ioe.getMessage()) ;
		}
		return definition;
	}

	public static String marshalValueSetDefinition(ValueSetDefinition definition) {
		DelegatingMarshaller marshaller = new DelegatingMarshaller();
		StringWriter writer = new StringWriter();
		try {
			marshaller.marshal(definition, new StreamResult(writer));
		}
		catch (IOException ioe) {
			logger.warning("Unable to marshal the value set definition. Message: " + ioe.getMessage());

		}
		return writer.toString();
	}

	public static ChangeSet unmarshallChangeSet(String xml) {
		DelegatingMarshaller marshaller = new DelegatingMarshaller();
		StringReader reader = new StringReader(xml);
		ChangeSet changeSet = null;
		try {
			changeSet = (ChangeSet) marshaller.unmarshal(new StreamSource(reader));
		} catch (IOException ioe) {
			logger.warning("Unable to unmarshal the returned change set xml. Message: " + ioe.getMessage()) ;
		}
		return changeSet;
	}
}
