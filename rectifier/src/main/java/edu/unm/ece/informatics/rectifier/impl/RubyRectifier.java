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
package edu.unm.ece.informatics.rectifier.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.script.ScriptContext;
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

public class RubyRectifier implements DocumentRectifier {
	
	private ScriptEngine rubyEngine;
	
	private Context ctx;
	
	private Reader ummReader;
	
	public RubyRectifier() {}
	
	public RubyRectifier(final Context ctx, final Reader ummReader) {
		this.ctx = ctx;
		this.ummReader = ummReader;
	}

	protected ScriptEngine getRubyEngine() throws ScriptException, FileNotFoundException {
		if (rubyEngine == null) {
			rubyEngine = new ScriptEngineManager().getEngineByName("jruby");
			rubyEngine.eval(ummReader);
		}
		return rubyEngine;
	}
	
	@Override
	public Document rectify(final Document original) throws RectificationException {
		final String testPolicy = ctx.get("currentContext");
		
		String content;
		try {
			content = loadStringFromDocument(original);

			final ScriptEngine engine = getRubyEngine();
			
			engine.getContext().setAttribute("content", content, ScriptContext.ENGINE_SCOPE);
			
			final StringBuilder programBuilder = new StringBuilder() //new StringBuilder(ummModule)
				.append("$context = ").append(testPolicy).append("\n")
				//.append("$content = '").append(content).append("'").append("\n")
				//.append("policy = '").append(testPolicy).append("'").append("\n")
				.append("umm = UsageManagementMechanism.new").append("\n")
				.append("rectifier = ContentRectifier.new :umm => umm, :confidentiality_strategy => :encrypt").append("\n")
				.append("$xml = rectifier.process :artifact => $content, :context => $context[:link]").append("\n");

			engine.eval(programBuilder.toString());
			
			final Object xml = engine.getContext().getAttribute("xml");

			return loadDocumentFromString(xml.toString());
			
		} catch (final Exception e) {
			throw new RectificationException(e);
		}
	}
	
	protected String loadStringFromDocument(final Document document) 
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
	
	protected Document loadDocumentFromString(final String xml) 
			throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

}
