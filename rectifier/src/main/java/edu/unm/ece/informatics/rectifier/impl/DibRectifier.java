package edu.unm.ece.informatics.rectifier.impl;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.unm.ece.informatics.rectifier.Context;
import edu.unm.ece.informatics.rectifier.DocumentRectifier;

public final class DibRectifier implements DocumentRectifier {
	
	private final Context ctx;
	
	public DibRectifier(final Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public Document rectify(final Document original) {
		// TODO Auto-generated method stub
		String policy;
		Document doc;
		try {
			policy = extractPolicy(original);
			doc = extractDocument(original);
		} catch (XPathExpressionException | SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public String extractPolicy(final Document original) 
			throws XPathExpressionException {
		final XPathExpression expr = XPathFactory.newInstance()
				.newXPath()
				.compile("/artifact/policy-set");
		return (String) expr.evaluate(original, XPathConstants.STRING);
	}
	
	public Document extractDocument(final Document original)
			throws XPathExpressionException, 
			SAXException, 
			IOException, 
			ParserConfigurationException {
		final XPathExpression expr = XPathFactory.newInstance()
				.newXPath()
				.compile("/artifact/data-object");
		final String strContent 
			= expr.evaluate(original); //, XPathConstants.STRING);
		final DocumentBuilderFactory builderFactory 
			= DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = builderFactory.newDocumentBuilder();
		System.out.println(strContent);
		return builder.parse(new InputSource(new StringReader(strContent)));
	}

}
