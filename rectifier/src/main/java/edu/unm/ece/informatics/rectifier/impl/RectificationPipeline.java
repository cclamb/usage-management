package edu.unm.ece.informatics.rectifier.impl;

import org.w3c.dom.Document;

import edu.unm.ece.informatics.rectifier.Context;
import edu.unm.ece.informatics.rectifier.DocumentPipeline;

public class RectificationPipeline implements DocumentPipeline {

	private final Context ctx;
	
	public RectificationPipeline(final Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Document execute(final Document original) {
		// TODO Auto-generated method stub
		return original;
	}

}
