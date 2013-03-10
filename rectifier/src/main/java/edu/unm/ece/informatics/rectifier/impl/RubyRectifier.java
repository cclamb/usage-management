package edu.unm.ece.informatics.rectifier.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
	
	private static final String UMM_MODULE_FILENAME 
		= "src/main/ruby/umm_system.rb";
	
	private ScriptEngine rubyEngine;
	
	private final Context ctx;
	
	public RubyRectifier(final Context ctx) {
		this.ctx = ctx;
	}

	protected ScriptEngine getRubyEngine() throws ScriptException {
		if (rubyEngine == null) {
			final String ummModule = loadStringFromFile(new File(UMM_MODULE_FILENAME));
			rubyEngine = new ScriptEngineManager().getEngineByName("jruby");
			rubyEngine.eval(ummModule);
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
			
		} catch (final ScriptException | TransformerException | ParserConfigurationException | SAXException | IOException e) {
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
	
	protected String loadStringFromFile(final File file) {
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
