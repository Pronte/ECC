package it.unipi.di.ecc.lists.maxud;

import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;

public class EPSc extends it.unipi.di.ecc.lists.ECCc {
	
	public EPSc(Graph graph) {
		super(graph);
	}
	public EPSc(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	@Override
	public void selectEdgeToExpand() {
		super.selectEdgeToExpandMaxUncoveredDeg();
	}
	
}