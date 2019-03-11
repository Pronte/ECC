package it.unipi.di.ecc.lists.mind;

import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;
@Deprecated
public class EPS1exact extends it.unipi.di.ecc.lists.EPS1exact {

	
	public EPS1exact(Graph graph) {
		super(graph);
	}
	public EPS1exact(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	@Override
	public void selectEdgeToExpand() {
		super.selectEdgeToExpandMinDeg();
	}
}