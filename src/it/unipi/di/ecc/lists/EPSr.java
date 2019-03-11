package it.unipi.di.ecc.lists;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;

public class EPSr extends ECC1 {
	
	public EPSr(Graph graph) {
		super(graph);
	}
	public EPSr(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	@Override
	public void expand(IntOpenHashSet C, IntOpenHashSet P)
	{
		nodes++;
		
		if(P.isEmpty())
		{
			report(C);
			return;
		}
		
		int pos = (int)(P.size()*Math.random());
		
		int random = -1;
		
		for(int x : P)
		{
			if(pos == 0)
			{
				random = x;
				break;
			}
			pos--;
		}
	
		//only retrieving 1 clique, iterating on the pivot
		process(C,P,random);

	}
}