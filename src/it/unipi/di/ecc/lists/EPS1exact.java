package it.unipi.di.ecc.lists;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;
@Deprecated
public class EPS1exact extends ECC1 {

	
	public EPS1exact(Graph graph) {
		super(graph);
	}
	public EPS1exact(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	@Override
	public void expand(IntOpenHashSet C, IntOpenHashSet P)
	{
		nodes++;
		int cval, scval;
		
		if(P.isEmpty())
		{
			report(C);
			return;
		}
		
		int max=-1, maxClean=-1, maxSClean=-1,
			maxVal=-1,maxCVal=-1,maxSCVal=-1;
		
		for(int i : P)
		{
			cval = scval = 0;
			
			IntSet cleanNeighs = cleanGraph.get(i).keySet();
			if(cleanNeighs  != null && !cleanNeighs.isEmpty())
			{
				for(int cn : cleanNeighs)
				{
					if(C.contains(cn)) //node i has a neighbor in C, so it is clean
					{
						cval++;
					}
					else if(cval == 0 && P.contains(cn)) //semi clean
					{
						scval++;
					}
				}
				
				if(cval > 0 && cval > maxCVal)
				{
					maxCVal = cval;
					maxClean = i;
				} else if(cval == 0 && scval > maxSCVal)
				{
					maxSCVal = scval;
					maxSClean = i;
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
		else //no clean vertices, breaking maximality constraint and stopping.
		{
//			P.clear();
//			P = null;
			report(C);
			return;
		}
	
		//only retrieving 1 clique, iterating on the pivot
		process(C,P,max);
	}
	
}