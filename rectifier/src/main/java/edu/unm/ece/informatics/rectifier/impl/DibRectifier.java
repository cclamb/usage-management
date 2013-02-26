package edu.unm.ece.informatics.rectifier.impl;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

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
		} catch (XPathExpressionException e) {
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
	
	public Document extractDocument(final Document original) {
		return null;
	}

}
