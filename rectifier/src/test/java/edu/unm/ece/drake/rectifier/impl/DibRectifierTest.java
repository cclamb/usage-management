package edu.unm.ece.drake.rectifier.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
	
	private static final String contentFilename 
		= "src/test/xml/new_location_detail.xml";
	private static final String policyFilename 
		= "src/test/pol/new_location_detail.pol";
	private static final String dataObjectFileName 
		= "src/test/xml/new_location_data_object.xml";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDibRectifier() {
		final DibRectifier rectifier = new DibRectifier(new TestContext());
		assertNotNull(rectifier);
	}

	@Test
	public void testRectify() 
			throws ParserConfigurationException, SAXException, IOException {
		final Document content = loadContent(new File(contentFilename));
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
		final Document content = loadContent(new File(contentFilename));
		assertNotNull(content);
		final Context ctx = new TestContext();
		final DibRectifier rectifier = new DibRectifier(ctx);
		final String policy = rectifier.extractPolicy(content);
		assertNotNull(policy);
		final String testPolicy = loadPolicy(new File(policyFilename));
		assertEquals(policy.replaceAll("\\s",""), 
				testPolicy.replaceAll("\\s",""));
	}
	
	@Test
	public void testContentExtraction()
			throws XPathExpressionException, 
			ParserConfigurationException, 
			SAXException, 
			IOException {
		final Document content = loadContent(new File(contentFilename));
		assertNotNull(content);
		final Context ctx = new TestContext();
		final DibRectifier rectifier = new DibRectifier(ctx);
		final NodeList nodes = rectifier.extractDocument(content);
		
		assertNotNull(nodes);
		
		final Document testDoc = loadContent(new File(dataObjectFileName));
		final NodeList testChildren = testDoc.getChildNodes();
		assert testChildren != null : "unexpected null test children";
		
		assertEquals(traverseNodes(nodes), traverseNodes(testChildren));
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
	
	private String loadPolicy(final File file) {
		final StringBuilder builder = new StringBuilder();
		try (final BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				builder.append(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();	
	}
	
	
}
