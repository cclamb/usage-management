package edu.unm.ece.informatics.rectifier.impl;

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
		return original;
	}

}
