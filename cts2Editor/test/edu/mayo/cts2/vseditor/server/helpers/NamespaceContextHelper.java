package edu.mayo.cts2.vseditor.server.helpers;

import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Map;

public class NamespaceContextHelper {
	
	public static final String CTS2_NAMESPACE = "cts2namespace";

	private static NamespaceContextHelper instance = null;
	private static Map<String, NamespaceContext> contexts;

	public static NamespaceContextHelper getInstance() {
		if (instance == null) {
			instance = new NamespaceContextHelper();
		}
		return instance;
	}

	public NamespaceContext getNamespaceContext(String namespace) {
		return contexts.get(namespace);
	}
	
	private NamespaceContextHelper(){
		contexts = new HashMap<String, NamespaceContext>();
		initContexts();
	};

	private void initContexts() {
		/* CTS2 Namespace */
		NamespaceContext cts2Context = new NamespaceContextMap(
		  "cts2", "http://schema.omg.org/spec/CTS2/1.0/ValueSet",
		  "core", "http://schema.omg.org/spec/CTS2/1.0/Core",
		  "xsi", "http://www.w3.org/2001/XMLSchema-instance"
		);
		contexts.put(CTS2_NAMESPACE, cts2Context);
	}

}
