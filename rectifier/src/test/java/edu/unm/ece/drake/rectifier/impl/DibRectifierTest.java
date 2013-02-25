package edu.unm.ece.drake.rectifier.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
	
	private static final String contentFilename = "src/test/xml/new_location_detail.xml";

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
		assert content != null : "null content returned";
		final Context ctx = new TestContext();
		final DibRectifier rectifier = new DibRectifier(ctx);
		final Document rectifiedContent = rectifier.rectify(content);
		assertNotNull(rectifiedContent);
		fail("Not yet implemented");
	}
	
	public Document loadContent(final File file) 
				throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory builderFactory 
			= DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = builderFactory.newDocumentBuilder();
		return builder.parse(file);
	}
}
