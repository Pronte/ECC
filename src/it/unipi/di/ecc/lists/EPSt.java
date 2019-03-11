package it.unipi.di.ecc.lists;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;

/**
 * EPS - tomita
 * uses Tomita's pivoting strategy as a heuristic for finding a large clique (find clique step)
 *
 */
public class EPSt extends ECC1 {

	public EPSt(Graph graph) {
		super(graph);
	}
	public EPSt(InputStream is)
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
		
	
		//only retrieving 1 clique, iterating on the pivot
		process(C,P,getTomitaPivot(P));


		/*
		for(int i = P.nextSetBit(0); i >=0; i = P.nextSetBit(i+1))
		{
			if(i == max || !graph.areNeighbors(i, max))
			{
				if(i>maxC) //selective: only finding cliques in crescent order!
				{
					process(C,P,i);
				}
			}
		} 
		*/
	}


    /**
     *Tomita's Pivoting strategy.
     *The pivot is chosen as the node in P
     *with the highest number of neighbours in P.
     */
    protected int getTomitaPivot(IntOpenHashSet P){
    	
    	int best = -1;
    	int bestVal = -1;

    	int val;

    	 for (int node : P) {//iterating over P
    		val = neighborsIn(P,node);

    		if(val>bestVal){
    			best = node;
    			bestVal = val;
    		}
    	}

    	return best;
    }

    protected int neighborsIn(IntOpenHashSet P, int node){
    	
    	int nip = 0;
    	
    	for(int x : graph.neighbors(node)) if(P.contains(x)) nip++;
    	
    	return nip;
    }

	
}