package edu.unm.ece.drake.rectifier.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.unm.ece.informatics.rectifier.Context;
import edu.unm.ece.informatics.rectifier.RectificationException;
import edu.unm.ece.informatics.rectifier.impl.RubyRectifier;

class TestContext extends HashMap<String, String> implements Context {}

public class RubyRectifierTest {
	
	private static final String UMM_MODULE_FILENAME 
		= "src/main/ruby/umm_system.rb";
	
	private static final String TEST_CONTEXT_FILENAME 
		= "src/main/ruby/test_context.rb";
	
	private static final String CONTENT_FILENAME 
		= "src/test/xml/new_location_detail.xml";
	
	private static final String POLICY_FILENAME 
		= "src/test/pol/new_location_detail.pol";
	
	protected Reader getUmmReader() throws FileNotFoundException {
		final File ummModule = new File(UMM_MODULE_FILENAME);
		return new BufferedReader(new FileReader(ummModule));
	}

	@Test
	public void testDibRectifier() throws FileNotFoundException {
		
		final RubyRectifier rectifier = new RubyRectifier(new TestContext(), getUmmReader());
		assertNotNull(rectifier);
	}

	@Test
	public void testRectify() 
			throws ParserConfigurationException, 
			SAXException, 
			IOException, 
			RectificationException, 
			TransformerException, XPathExpressionException {
		final Document content = Util.loadContent(new File(CONTENT_FILENAME));
		assertNotNull(content);
		final String env = Util.loadFile(new File(TEST_CONTEXT_FILENAME));
		final Context ctx = new TestContext();
		ctx.put("currentContext", env);
		final RubyRectifier rectifier = new RubyRectifier(ctx, getUmmReader());
		final Document rectifiedContent = rectifier.rectify(content);
		
		//System.out.println(loadXMLStringFromDocument(rectifiedContent));
		
		final NodeList nodes = Util.extractDocumentContent(rectifiedContent);
		for ( int i = 0; i < nodes.getLength(); i++) {
			final Node node = nodes.item(i);
			final NamedNodeMap attrs = node.getAttributes();
			final String policyName = attrs.getNamedItem("policy").getNodeValue();
			final Node encryptionStatus = attrs.getNamedItem("status");
			if (policyName.equals("history") || policyName.equals("location")) {
				assertNotNull(encryptionStatus);
			} else {
				assertNull(encryptionStatus);
			}
		}
	}
	
	@Test
	public void testRectificationPerformance() 
			throws XPathExpressionException, 
			ParserConfigurationException, 
			SAXException, 
			IOException, 
			RectificationException, 
			TransformerException {
		final long startTime = System.currentTimeMillis();
		for (int i = 0; i < 5; i++) testRectify();
		final long stopTime = System.currentTimeMillis();
		System.out.format("Elapsed time: %d ms\n", stopTime - startTime);
	}
	
	@Test
	public void testJRubyPolicyEval() throws ScriptException {
		final String testPolicy = Util.loadFile(new File(POLICY_FILENAME));
		final String ctx = Util.loadFile(new File(TEST_CONTEXT_FILENAME));
		final String content = Util.loadFile(new File(CONTENT_FILENAME));
		final String ummModule = Util.loadFile(new File(UMM_MODULE_FILENAME));
		
		final StringBuilder programBuilder = new StringBuilder(ummModule)
			.append(ctx)
			.append("content = '").append(content).append("'").append("\n")
			.append("policy = '").append(testPolicy).append("'").append("\n")
			.append("umm = UsageManagementMechanism.new").append("\n")
			.append("rectifier = ContentRectifier.new :umm => umm, :confidentiality_strategy => :encrypt").append("\n")
			.append("$xml = rectifier.process :artifact => content, :context => Base_Context[:link]").append("\n");
			//.append("puts xml");
		
		System.out.println(programBuilder.toString());
		
		final ScriptEngine engine = new ScriptEngineManager()
			.getEngineByName("jruby");
		assert engine != null : "engine should be valid";
		
		engine.eval(programBuilder.toString());
		
		final Object result = engine.getContext().getAttribute("result");
		System.out.format("RESULT: %s\n", result);
		
		final Object xml = engine.getContext().getAttribute("xml");
		System.out.format("New XML: %s\n", xml);
		
		//engine.eval(initializer);
	}
	
	
}
