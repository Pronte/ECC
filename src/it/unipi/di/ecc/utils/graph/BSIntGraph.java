package it.unipi.di.ecc.utils.graph;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unipi.di.ecc.interfaces.Graph;

import java.util.BitSet;

public class BSIntGraph implements Graph {
	
	public ObjectArrayList<BitSet> graph;
	public BitSet nodes;

	public BSIntGraph (){
		graph = new ObjectArrayList<BitSet>();
		
		nodes = new BitSet();
	}

	public BSIntGraph (int capacity){
		graph = new ObjectArrayList<BitSet>(capacity);
		
		for(int i = 0; i < capacity; i++)
		{
			graph.add(new BitSet());
		}
		
		nodes = new BitSet();
	}

	
	//return a list of all the nodes in the graph
	public BitSet nodes (){
		return nodes;
	}

	//return a list of all the neighbors of the node with label n
	public BitSet neighbors(int n){
		return graph.get(n);
	}
	
	//true if n1 and n2 are neighbors, false otherwise
	@Override
	public boolean areNeighbors(int n1, int n2){
		return graph.get(n1).get(n2);
	}


	//building methods
	public void setNeighbors(int j, int k){
		
		//avoiding loops.
		if(j == k) return;
		
		int m = Math.max(j, k);
		while(graph.size() <= m)
		{
			graph.add(new BitSet());
		}
		
		graph.get(j).set(k);
		graph.get(k).set(j);
		nodes.set(j);
		nodes.set(k);
	}

	@Override
	public int cardinality(int i) {
		return graph.get(i).cardinality();
	}
}