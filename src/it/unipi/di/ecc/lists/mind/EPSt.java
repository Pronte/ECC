package it.unipi.di.ecc.lists.mind;

import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;

/**
 * EPS - tomita
 * uses Tomita's pivoting strategy as a heuristic for finding a large clique (find clique step)
 *
 */
public class EPSt extends it.unipi.di.ecc.lists.EPSt {

	public EPSt(Graph graph) {
		super(graph);
	}
	public EPSt(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	@Override
	public void selectEdgeToExpand() {
		super.selectEdgeToExpandMinDeg();
	}
	
}