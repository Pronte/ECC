package it.unipi.di.ecc.lists.maxud;

import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;

public class EPS1fast extends it.unipi.di.ecc.lists.EPS1fast {

	
	public EPS1fast(Graph graph)
	{
		super(graph);
	}
	public EPS1fast(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	@Override
	public void selectEdgeToExpand() {
		super.selectEdgeToExpandMaxUncoveredDeg();
	}
		
}