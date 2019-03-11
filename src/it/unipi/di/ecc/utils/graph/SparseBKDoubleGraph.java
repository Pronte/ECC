package it.unipi.di.ecc.utils.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SparseBKDoubleGraph extends SparseBKGraph {
	
	// neighborsSets.ger(i) -> sets of neighbors of the node labelled "i".
	List<Set<Integer>> neighborSets;
	
	
	/**
	 * Builds a sparse graph from MapListGraph.
	 * @param mlg - map/list graph
	 */
	@SuppressWarnings("deprecation")
	public SparseBKDoubleGraph(Map<Integer, List<Integer>> mlg){
		super();
		
		List<Integer> nodes = new ArrayList<Integer> (mlg.keySet());
		
		int n = nodes.size();
		numVerices = n;
		maxLabel = Collections.max(nodes);
		System.out.println("Nodes: "+n+" MaxLabel: "+maxLabel);
		
		vertices = new int[n];
		position = new int[maxLabel+1]; //the size should be equal to the maximum possible label+1
										//so that all vertices' labels can be used as keys
		neighbors = new int[maxLabel+1][]; //idem
		neighborSets = new ArrayList<Set<Integer>>(maxLabel+1);
		for(int i = 0; i < maxLabel +1; i++) neighborSets.add(null); //init
		
		XPBorder = 0;
		Xstart = 0;
		Pend = n;
		
		btb = new BackTrackBuffer(Xstart,Pend);
		
		toFill = 0;
		Set<Integer> temp;
		for(int v : nodes){
			vertices[toFill] = v;
			position[v] = toFill;
			
			List<Integer> neighs = mlg.get(v);
			int ns = neighs.size();
			
			neighbors[v] = new int[neighs.size()];
			temp = new HashSet<Integer>(neighs.size());
			neighborSets.set(v, temp);
			
			for(int i=0; i<ns; i++){
				neighbors[v][i] = neighs.get(i);
				temp.add(neighs.get(i));
			}
			toFill++;
		}
	}
	/**
	 * Builds a sparse graph.
	 * @param mlg - map/list graph
	 */
//	public SparseBKDoubleGraph(int[][] neighborsVecs){
//		
//		int n = 0;
//
//		for(int i = 0; i<neighborsVecs.length; i++)
//		{
//			if(neighborsVecs[i] != null && neighborsVecs[i].length > 0)
//			{
//				n++;
//			}
//		}
//		for(int i = neighborsVecs.length-1; i >= 0; i--)
//		{
//			if(neighborsVecs[i] != null && neighborsVecs[i].length > 0)
//			{
//				maxLabel = i;
//				break;
//			}
//		}
//		
//		numVertexes = n;
//		System.out.println("Nodes: "+n+" MaxLabel: "+maxLabel);
//		
//		vertices = new int[n];
//		position = new int[maxLabel+1]; //the size should be equal to the maximum possible label+1
//										//so that all vertices' labels can be used as keys
//		neighbors = neighborsVecs;
//		XPBorder = 0;
//		Xstart = 0;
//		Pend = n;
//		
//		btb = new BackTrackBuffer(Xstart,Pend);
//		
//		toFill = 0;
//		for(int i = 0; i<neighborsVecs.length; i++)
//		{
//			if(neighborsVecs[i] != null && neighborsVecs[i].length > 0)
//			{
//				vertices[toFill] = i;
//				position[i] = toFill;
//				
//				toFill++;
//			}
//		}
//	}
	
	/**
	 * Builds a sparse graph.
	 * @param vecs: adjacency lists, sets: adjacency sets (for performance, use arraylist)
	 */
	@SuppressWarnings("deprecation")
	public SparseBKDoubleGraph(int[][] neighborsVecs, List<Set<Integer>> neighborSets){
		super();
		
		int n = 0;

		for(int i = 0; i<neighborsVecs.length; i++)
		{
			if(neighborsVecs[i] != null && neighborsVecs[i].length > 0)
			{
				n++;
			}
		}
		for(int i = neighborsVecs.length-1; i >= 0; i--)
		{
			if(neighborsVecs[i] != null && neighborsVecs[i].length > 0)
			{
				maxLabel = i;
				break;
			}
		}
		
		numVerices = n;
		System.out.println("Nodes: "+n+" MaxLabel: "+maxLabel);
		
		vertices = new int[n];
		position = new int[maxLabel+1]; //the size should be equal to the maximum possible label+1
										//so that all vertices' labels can be used as keys
		this.neighbors = neighborsVecs;
		this.neighborSets = neighborSets;
		XPBorder = 0;
		Xstart = 0;
		Pend = n;
		
		btb = new BackTrackBuffer(Xstart,Pend);
		
		toFill = 0;
		for(int i = 0; i<neighborsVecs.length; i++)
		{
			if(neighborsVecs[i] != null && neighborsVecs[i].length > 0)
			{
				vertices[toFill] = i;
				position[i] = toFill;
				
				toFill++;
			}
		}
	}

	
	public long ttot = 0; //TODO REMOVE!
	private long t0;
	
	/**
	 * takes n in P and adds it to R.
 	 * When the method returns, P and X will only contain neighbors of n.
	 * @param n. precond: n bust be in P (i.e. isInP(n) returns true)
	 * if the precondition is matched, then the backtracking will be successful
	 */
	@Override
	public void addingToR(int n){
		t0 = System.currentTimeMillis(); //TODO remove
		
		//System.out.println("Adding "+n+" to R");
		//System.out.print("vertices: [");
		//for(int i=0; i< vertices.length-1; i++) System.out.print(vertices[i]+",");
		//if(vertices.length > 0) System.out.print(vertices[vertices.length-1]);
		//System.out.println("]");
		//System.out.println("Xstart="+Xstart+", XPBorder="+XPBorder+", Pend="+Pend);
		//System.out.println("Xsize="+X_size()+", Psize="+P_size());
		
		btb.save(Xstart, Pend);
		
		if(neighbors[n].length <= P_size() + X_size())
		{
			int Pfill = XPBorder;
			int Xfill = XPBorder-1;
			
			for(int i : neighbors[n]){
				if(isInP(i)){
					swapTo(i,Pfill); //puts i in the left-most slot of P that has not been filled already 
					Pfill++;
				} else if (isInX(i)){
	//				System.out.println("swapping "+i+" from "+position[i]+" to "+Xfill);
					swapTo(i,Xfill); //puts i in the right-most slot of X that has not been filled already
					Xfill--;
				}
			}

			
			Xstart = Xfill+1; //inclusive
			Pend = Pfill; //exclusive
		}
		else
		{
			int start = P_start();
			int end = Pend;

			for(int i = end-1; i >= start; i--)   //iterate on P
			{
				if(!areNeighbors(n,vertices[i]))  //if the node is not a neighbor of n, take it out of P by putting it at the end of P and decrementing the limit.
				{
					swapPos(i, Pend-1);
					Pend--;
				}
			}
			
			start = Xstart;
			end = P_start();
			
			for(int i = start; i < end; i++)   //iterate on X
			{
				if(!areNeighbors(n,vertices[i]))  //if the node is not a neighbor of n, take it out of X by putting it at the beginning of X and incrementing the limit.
				{
					swapPos(i, Xstart);
					Xstart++;
				}
			}
		}
		ttot += System.currentTimeMillis() - t0;
	}

	@Override
	public boolean areNeighbors(int n1, int n2) {
//		return contains(neighbors[n1],n2);
		return neighborSets.get(n1).contains(n2);
	}
	
	public Set<Integer> neighborSet(int n)
	{
		return neighborSets.get(n);
	}

}
