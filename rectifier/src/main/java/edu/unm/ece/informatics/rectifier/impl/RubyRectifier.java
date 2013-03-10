package edu.unm.ece.informatics.rectifier.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.unm.ece.informatics.rectifier.Context;
import edu.unm.ece.informatics.rectifier.DocumentRectifier;
import edu.unm.ece.informatics.rectifier.RectificationException;

public final class RubyRectifier implements DocumentRectifier {
	
	private static final String UMM_MODULE_FILENAME 
		= "src/main/ruby/umm_system.rb";
	
	private final Context ctx;
	
	public RubyRectifier(final Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public Document rectify(final Document original) throws RectificationException {
		final String ummModule = loadStringFromFile(new File(UMM_MODULE_FILENAME));
		final String testPolicy = ctx.get("currentContext");
		
//		System.out.println(testPolicy);
		
		String content;
		try {
			content = loadXMLStringFromDocument(original);
		} catch (final TransformerException e) {
			throw new RectificationException(e);
		}
		
		final ScriptEngine engine = new ScriptEngineManager()
			.getEngineByName("jruby");
		
		final StringBuilder programBuilder = new StringBuilder(ummModule)
			.append(testPolicy).append("\n")
			.append("content = '").append(content).append("'").append("\n")
			.append("policy = '").append(testPolicy).append("'").append("\n")
			.append("umm = UsageManagementMechanism.new").append("\n")
			.append("rectifier = ContentRectifier.new :umm => umm, :confidentiality_strategy => :encrypt").append("\n")
			.append("$xml = rectifier.process :artifact => content, :context => Base_Context[:link]").append("\n");

		//System.out.println(programBuilder);
		

		try {
			engine.eval(programBuilder.toString());
		} catch (final ScriptException e) {
			throw new RectificationException(e);
		}
		
		final Object xml = engine.getContext().getAttribute("xml");
		
		System.out.println(xml.toString());
		
		try {
			return loadXMLFromString(xml.toString());
		} catch (final ParserConfigurationException | SAXException | IOException e) {
			throw new RectificationException(e);
		}
		
//		String policy;
//		NodeList doc;
//		try {
//			policy = extractPolicy(original);
//			doc = extractDocument(original);
//		} catch (XPathExpressionException | SAXException | IOException | ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		//return null;
	}
	
	private String loadXMLStringFromDocument(final Document document) 
			throws TransformerException {
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(document.getDocumentElement());

		trans.transform(source, result);
		return sw.toString();
	}
	
	private Document loadXMLFromString(final String xml) 
			throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
	
	private String loadStringFromFile(final File file) {
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
	
//	public String extractPolicy(final Document original) 
//			throws XPathExpressionException {
//		final XPathExpression expr = XPathFactory.newInstance()
//				.newXPath()
//				.compile("/artifact/policy-set");
//		return (String) expr.evaluate(original, XPathConstants.STRING);
//	}
//	
//	public NodeList extractDocument(final Document original)
//			throws XPathExpressionException, 
//			SAXException, 
//			IOException, 
//			ParserConfigurationException {
//		final XPathExpression expr = XPathFactory.newInstance()
//				.newXPath()
//				.compile("/artifact/data-object");
//		final NodeList content 
//			= (NodeList) expr.evaluate(original, XPathConstants.NODESET);
//		return content;
//	}

}
