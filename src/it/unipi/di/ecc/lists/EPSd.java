package it.unipi.di.ecc.lists;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;

public class EPSd extends ECC1 {
	
	public EPSd(Graph graph) {
		super(graph);
	}
	public EPSd(InputStream is)
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
		
		int max=-1, maxClean=-1, maxSClean=-1,
			maxVal=-1,maxCVal=-1,maxSCVal=-1;
		
		//int maxC = C.length() -1;
		
		for(int i : P)
		{
			IntSet cleanNeighs = cleanGraph.get(i).keySet();
			if(cleanNeighs  != null && !cleanNeighs.isEmpty())
			{
				
				for(int cn : cleanNeighs)
				{
					if(C.contains(cn)) //node i has a neighbor in C, so it is clean
					{
						if(graph.neighbors(i).size() >= maxCVal)
						{
							maxCVal = graph.neighbors(i).size();
							maxClean = i; //pivot
							break;
						}
					}
				}
				
				if(maxClean ==-1 && graph.neighbors(i).size() >= maxSCVal) //node i is semi-clean (and no clean node has been found yet)
				{
					maxSCVal = graph.neighbors(i).size();
					maxSClean = i; //pivot
				}
			}
			else if(maxSClean ==-1 && maxClean==-1 && graph.neighbors(i).size() >= maxVal) //no clean or semiClean nodes found yet, and current node is dirty
			{
				maxVal = graph.neighbors(i).size();
				max = i; //pivot
			}
		}
		
		if(maxClean != -1)
		{
			max = maxClean;
		}
		else if(maxSClean != -1)
		{
			max = maxSClean;
		}
		else //no clean vertices, but we still continue with the best dirty node.
		{
//			P.clear();
//			P = null;
//			report(C);
//			return;
		}
	
		//only retrieving 1 clique, iterating on the pivot
		process(C,P,max);


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
}