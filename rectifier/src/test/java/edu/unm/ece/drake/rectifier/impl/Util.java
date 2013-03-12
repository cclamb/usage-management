/*
 * Copyright (c) 2012 Christopher C. Lamb
 *
 * SBIR DATA RIGHTS
 * Contract No. FA8750-11-C-0195
 * Contractor: AHS Engineering Services (under subcontract to Modus Operandi, Inc.)
 * Address: 5909 Canyon Creek Drive NE, Albuquerque, NM 87111
 * Expiration Date: 05/03/2018
 * 
 * The Government’s rights to use, modify, reproduce, release, perform, display, 
 * or disclose technical data or computer software marked with this legend are 
 * restricted during the period shown as provided in paragraph (b) (4) 
 * of the Rights in Noncommercial Technical Data and Computer Software-Small 
 * Business Innovative Research (SBIR) Program clause contained in the above 
 * identified contract. No restrictions apply after the expiration date shown 
 * above. Any reproduction of technical data, computer software, or portions 
 * thereof marked with this legend must also reproduce the markings.
 */
package edu.unm.ece.drake.rectifier.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class Util {
	
	public static String loadXMLStringFromDocument(final Document document) 
			throws TransformerException {
		final Transformer trans = TransformerFactory
				.newInstance()
				.newTransformer();
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(document.getDocumentElement());

		trans.transform(source, result);
		return sw.toString();
	}
	
	public static String traverseNodes(final NodeList nodes) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < nodes.getLength(); i++) {
	        builder.append(traverseNode(nodes.item(i)));
	    }
		return builder.toString();
	}
	
	private static String traverseNode(final Node node) {
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
	
	public static Document loadContent(final File file) 
				throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory builderFactory 
			= DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = builderFactory.newDocumentBuilder();
		return builder.parse(file);
	}
	
	public static String loadFile(final File file) {
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
	
	public static String extractPolicy(final Document original) 
		throws XPathExpressionException {
		final XPathExpression expr = XPathFactory.newInstance()
				.newXPath()
				.compile("/artifact/policy-set");
		return (String) expr.evaluate(original, XPathConstants.STRING);
	}
	
	public static NodeList extractDocumentContent(final Document original)
		throws XPathExpressionException, 
		SAXException, 
		IOException, 
		ParserConfigurationException {
		final XPathExpression expr = XPathFactory.newInstance()
				.newXPath()
				.compile("/artifact/data-object/content/*");
		final NodeList content 
			= (NodeList) expr.evaluate(original, XPathConstants.NODESET);
		return content;
	}
	
//	@Test
//	public void testPolicyExtraction() 
//			throws XPathExpressionException, 
//			ParserConfigurationException, 
//			SAXException, 
//			IOException {
//		final Document content = loadContent(new File(CONTENT_FILENAME));
//		assertNotNull(content);
//		final Context ctx = new TestContext();
//		final DibRectifier rectifier = new DibRectifier(ctx);
//		final String policy = rectifier.extractPolicy(content);
//		assertNotNull(policy);
//		final String testPolicy = loadFile(new File(POLICY_FILENAME));
//		assertEquals(policy.replaceAll("\\s",""), 
//				testPolicy.replaceAll("\\s",""));
//	}
	
//	@Test
//	public void testContentExtraction()
//			throws XPathExpressionException, 
//			ParserConfigurationException, 
//			SAXException, 
//			IOException {
//		final Document content = loadContent(new File(CONTENT_FILENAME));
//		assertNotNull(content);
//		final Context ctx = new TestContext();
//		final DibRectifier rectifier = new DibRectifier(ctx);
//		final NodeList nodes = rectifier.extractDocument(content);
//		
//		assertNotNull(nodes);
//		
//		final Document testDoc = loadContent(new File(DATA_OBJECT_FILENAME));
//		final NodeList testChildren = testDoc.getChildNodes();
//		assert testChildren != null : "unexpected null test children";
//		
//		assertEquals(traverseNodes(nodes), traverseNodes(testChildren));
//	}
}
