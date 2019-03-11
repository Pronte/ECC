package it.unipi.di.ecc.lists.maxud;

import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;

public class EPS1 extends it.unipi.di.ecc.lists.ECC1 {


	public EPS1(Graph graph) {
		super(graph);
	}
	public EPS1(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	
	@Override
	public void selectEdgeToExpand() {
		super.selectEdgeToExpandMaxUncoveredDeg();
	}
	
}
