package it.unipi.di.ecc.lists.mind;

import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;

public class EPSr extends it.unipi.di.ecc.lists.EPSr {
	
	public EPSr(Graph graph) {
		super(graph);
	}
	public EPSr(InputStream is)
	{
		this(Parser.getListGraph(is));
	}

	@Override
	public void selectEdgeToExpand() {
		super.selectEdgeToExpandMinDeg();
	}

}