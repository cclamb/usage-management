package edu.unm.ece.drake.rectifier.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.unm.ece.informatics.rectifier.Context;
import edu.unm.ece.informatics.rectifier.impl.DibRectifier;

class TestContext extends HashMap<String, String> implements Context {}

public class DibRectifierTest {
	
	private static final String TEST_CONTEXT_FILENAME 
		= "src/main/ruby/test_context.rb";

	private static final String UMM_FILENAME 
		= "src/main/ruby/usage_management_mechanism.rb";

	private static final String POLICY_EVALUATOR_FILENAME 
		= "src/main/ruby/policy_evaluator.rb";
	
	private static final String RECTIFIER_FILENAME 
		= "src/main/ruby/content_rectifier.rb";
	
	private static final String INITIALIZER_FILENAME 
		= "src/main/ruby/initialize.rb";
	
	private static final String CONTENT_FILENAME 
		= "src/test/xml/new_location_detail.xml";
	
	private static final String POLICY_FILENAME 
		= "src/test/pol/new_location_detail.pol";
	
	private static final String DATA_OBJECT_FILENAME 
		= "src/test/xml/new_location_data_object.xml";

	@Test
	public void testDibRectifier() {
		final DibRectifier rectifier = new DibRectifier(new TestContext());
		assertNotNull(rectifier);
	}

	@Test
	public void testRectify() 
			throws ParserConfigurationException, SAXException, IOException {
		final Document content = loadContent(new File(CONTENT_FILENAME));
		assertNotNull(content);
		final Context ctx = new TestContext();
		final DibRectifier rectifier = new DibRectifier(ctx);
		final Document rectifiedContent = rectifier.rectify(content);
		assertNotNull(rectifiedContent);
		fail("Not yet implemented");
	}
	
	@Test
	public void testPolicyExtraction() 
			throws XPathExpressionException, 
			ParserConfigurationException, 
			SAXException, 
			IOException {
		final Document content = loadContent(new File(CONTENT_FILENAME));
		assertNotNull(content);
		final Context ctx = new TestContext();
		final DibRectifier rectifier = new DibRectifier(ctx);
		final String policy = rectifier.extractPolicy(content);
		assertNotNull(policy);
		final String testPolicy = loadFile(new File(POLICY_FILENAME));
		assertEquals(policy.replaceAll("\\s",""), 
				testPolicy.replaceAll("\\s",""));
	}
	
	@Test
	public void testContentExtraction()
			throws XPathExpressionException, 
			ParserConfigurationException, 
			SAXException, 
			IOException {
		final Document content = loadContent(new File(CONTENT_FILENAME));
		assertNotNull(content);
		final Context ctx = new TestContext();
		final DibRectifier rectifier = new DibRectifier(ctx);
		final NodeList nodes = rectifier.extractDocument(content);
		
		assertNotNull(nodes);
		
		final Document testDoc = loadContent(new File(DATA_OBJECT_FILENAME));
		final NodeList testChildren = testDoc.getChildNodes();
		assert testChildren != null : "unexpected null test children";
		
		assertEquals(traverseNodes(nodes), traverseNodes(testChildren));
	}
	
	@Test
	public void testJRubyPolicyEval() throws ScriptException {
		final String testPolicy = loadFile(new File(POLICY_FILENAME));
		final String evaluator = loadFile(new File(POLICY_EVALUATOR_FILENAME));
		final String umm = loadFile(new File(UMM_FILENAME));
		final String rectifier = loadFile(new File(RECTIFIER_FILENAME));
		final String initializer = loadFile(new File(INITIALIZER_FILENAME));
		final String ctx = loadFile(new File(TEST_CONTEXT_FILENAME));
		final String content = loadFile(new File(CONTENT_FILENAME));
		
		final StringBuilder programBuilder = new StringBuilder(evaluator)
			.append(initializer)
			.append(umm)
			.append(rectifier)
			.append(ctx)
			.append("content = '").append(content).append("'").append("\n")
			.append("policy = '")
			.append(testPolicy).append("'").append("\n")
//			.append("puts policy").append("\n")
			.append("evaluator = PolicyEvaluator.new(:one) do").append("\n")
			.append("\t").append("instance_eval policy").append("\n")
			.append("end").append("\n")
			.append("policy = evaluator.ctx").append("\n")
			.append("umm = UsageManagementMechanism.new").append("\n")
			.append("$result = umm.execute?(policy, Base_Context[:link], :transmit)").append("\n")
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
	
	private String traverseNodes(final NodeList nodes) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < nodes.getLength(); i++) {
	        builder.append(traverseNode(nodes.item(i)));
	    }
		return builder.toString();
	}
	
	private String traverseNode(final Node node) {
		final StringBuilder builder = new StringBuilder();
		builder.append(String.format("Name: %s\n", node.getNodeName()));
		if (node.getNodeValue() != null) {
			builder.append(String.format("Value: %s\n", node.getNodeValue()));
		}
		if (node.hasAttributes() == true) {
			final NamedNodeMap attrs = node.getAttributes();
			builder.append(String.format("Attributes:"));
			for (int i = 0; i < attrs.getLength(); i++) {
				final Node item = attrs.item(i);
				builder.append(String.format("\t%s = %s\n", item.getNodeName(), item.getNodeValue()));
			} 
		}
		builder.append("------\n");
		if (node.hasChildNodes() == false) {
			return builder.toString();
		}
		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			builder.append(traverseNode(children.item(i)));
		}
		return builder.toString();
	}
	
	private Document loadContent(final File file) 
				throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory builderFactory 
			= DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = builderFactory.newDocumentBuilder();
		return builder.parse(file);
	}
	
	private String loadFile(final File file) {
		final StringBuilder builder = new StringBuilder();
		try (final BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				builder.append(sCurrentLine).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();	
	}
	
	
}
