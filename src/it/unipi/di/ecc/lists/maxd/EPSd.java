package it.unipi.di.ecc.lists.maxd;

import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;

public class EPSd extends it.unipi.di.ecc.lists.EPSd {
	
	public EPSd(Graph graph) {
		super(graph);
	}
	public EPSd(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	@Override
	public void selectEdgeToExpand() {
		super.selectEdgeToExpandMaxDeg();
	}
	
}