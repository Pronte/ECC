package it.unipi.di.ecc.lists;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Kellerman extends ECC1 {

//	protected Map<Integer,Map<Integer,Integer>> priorityGraph;
	
	protected int seli, selj, minV;
	
	public Kellerman(Graph graph) {
		super(graph);
	}
	public Kellerman(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	@Override
	public Object solution() {
		return this.solution;
	}
	@Override
	public void expand0() {
		System.out.println("Init..");
		//Priority Queue for Edges
		
		//int size = graph.nodes().size();
		/*
		priorityGraph = new HashMap<Integer,Map<Integer,Integer>>();
		minV=0;
		
		for(int i = graph.nodes().nextSetBit(0); i>=0; i = graph.nodes().nextSetBit(i+1))
		{
			BitSet neighs = graph.neighbors(i);
			HashMap<Integer,Integer> m = new HashMap<Integer,Integer>(); 
			priorityGraph.put(i,m);
			
			for(int j = neighs.nextSetBit(0); j>=0; j=neighs.nextSetBit(j+1))
			{
				m.put(j, 0);
			}
		}
		priorityGraph.put(0, new HashMap<Integer,Integer>());
		*/
		int maxLabel = graph.vertices().size();
		System.out.println("ml = "+maxLabel);
		List<IntOpenHashSet> clqs = new ArrayList<IntOpenHashSet>();
		
		for(int i : graph.vertices())
		{
			List<Integer> neighs = graph.neighbors(i);
			IntOpenHashSet w = new IntOpenHashSet(neighs.size());
			
			for(int ii = 0; ii<neighs.size(); ii++ ) //assuming neighs to be ordered
			{
				if(neighs.get(ii) < i) w.add(neighs.get(ii)); //all edges from i to nodes smaller than i 
				//else break;
				
				if(ii > 0 && neighs.get(ii) <= neighs.get(ii-1)) throw new RuntimeException("Neighbors of "+i+" NOT ORDERED! ("+neighs.get(ii-1)+" precedes "+neighs.get(ii)+")");
			}
//			System.out.println("i: "+i+" , w: "+w.toString());
			if(w.isEmpty()) //if the new node is not connected to any previous node, make it into a new clique of size 1
			{
				IntOpenHashSet newClq = new IntOpenHashSet();
				newClq.add(i);
				clqs.add(newClq);
				newclq();
			}
			else //try to augment existing cliques with i
			{
				IntOpenHashSet u = new IntOpenHashSet();
				
				for(IntOpenHashSet clq : clqs) //check if the clique is all adjacent to i (hence it can be augmented with i)
				{
					boolean included = true;
					
					for(int n : clq)
					{
						if(!w.contains(n))
						{
							included = false;
							break;
						}
					}
					
					if(included) //augment the clique and cover the edges
					{
						for(int x : clq) u.add(x); //set clq vertices as covered
						
						clq.add(i);
						
						if(u.size() >= w.size())
						{
							if(u.equals(w))
							{
								break;
							}
							else
							{
								System.out.println("Error!");
								System.out.println("U:"+u.toString());
								System.out.println("W: "+w.toString());
							}
						}
					}
				}

				w.removeAll(u); //not possible anymore to augment cliques, now we cover the remaining edges
				
				while(!w.isEmpty())
				{
					//TODO optimize
					IntOpenHashSet tmp;
					
					int max = -1, maxval = -1,val=0;
					
					for(int ci = 0; ci < clqs.size(); ci++)
					{
						tmp = clqs.get(ci);
						
						val=0;
						for(int x : tmp )
						{
							if(w.contains(x)) val++;
						}
						
						if(val > maxval){
							max = ci;
							maxval = val;
						}
					}
					
					IntOpenHashSet newclq = new IntOpenHashSet();
					IntOpenHashSet maxclq = clqs.get(max);
					
					for(int x : maxclq)
					{
						if(w.contains(x))
						{
							newclq.add(x); //the new clique is the intersection between the old clique and the neighborhood of i (and it's the one that maximizes this)
							w.remove(x);
						}
					}
					newclq.add(i);
					
					clqs.add(newclq);
					newclq();
					
//					max.set(i);
//					w.andNot(max);
//					clqs.add(max);
				}
			}
		}
		
		
		cliques = clqs.size();
		deaths = 0;
		for(IntOpenHashSet cc : clqs) deaths += cc.size();
		
		solution = clqs;
	}

	
	private void newclq()
	{
		cliques++;
//		System.out.println(cliques+" cliques");
		if(cliques % 10000 == 0) System.out.println(cliques+" cliques");
	}
}
