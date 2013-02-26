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
import org.xml.sax.SAXException;

import edu.unm.ece.informatics.rectifier.Context;
import edu.unm.ece.informatics.rectifier.impl.DibRectifier;

class TestContext extends HashMap<String, String> implements Context {}

public class DibRectifierTest {
	
	private static final String contentFilename 
		= "src/test/xml/new_location_detail.xml";
	private static final String policyFilename 
		= "src/test/pol/new_location_detail.pol";

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
