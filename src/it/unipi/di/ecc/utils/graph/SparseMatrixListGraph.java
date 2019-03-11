package it.unipi.di.ecc.utils.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SparseMatrixListGraph extends SparseBKGraph {
	
	// neighbors[i] -> matrix of neighbors of the node labelled "i".
	boolean[][] neighborMat;
	
	
	/**
	 * Builds a sparse graph from MapListGraph.
	 * @param mlg - map/list graph
	 */
	@SuppressWarnings("deprecation")
	public SparseMatrixListGraph(Map<Integer, List<Integer>> mlg){
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
		neighborMat = new boolean[maxLabel+1][maxLabel+1]; //idem
		
		for(int i = 0; i < neighborMat.length; i++)
		{
			Arrays.fill(neighborMat[i], false);
		}
		XPBorder = 0;
		Xstart = 0;
		Pend = n;
		
		btb = new BackTrackBuffer(Xstart,Pend);
		
		toFill = 0;

		for(int v : nodes){
			vertices[toFill] = v;
			position[v] = toFill;
			
			List<Integer> neighs = mlg.get(v);
			int ns = neighs.size();
			
			neighbors[v] = new int[neighs.size()];
			
			for(int i=0; i<ns; i++){
				neighbors[v][i] = neighs.get(i);
				neighborMat[v][neighs.get(i)] = true;
				neighborMat[neighs.get(i)][v] = true;
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
	public SparseMatrixListGraph(int[][] neighborsVecs, List<Set<Integer>> neighborSets){
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
		this.neighborMat = new boolean[maxLabel+1][maxLabel+1];
		
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
				
				for(int j = 0; j < neighborsVecs[i].length; j++)
				{
					neighborMat[i][neighborsVecs[i][j]] = true;
					neighborMat[neighborsVecs[i][j]][i] = true;
				}
				toFill++;
			}
		}
	}

	/**
	 * swaps 2 elements in vertices, and updates the corresponding positions
	 * in positions.
	 * @param n1 and n2 are supposed to be labels
	 */
	@Override
	public void swap(int n1, int n2){
		int p1 = position[n1];
		int p2 = position[n2];
		
		vertices[p1] = n2;
		vertices[p2] = n1;
		
		position[n1] = p2;
		position[n2] = p1; 
	}
	/**
	 * swaps an element in vertices with the one in the specified position,
	 * and updates the corresponding positions in positions.
	 * @param n is a label and pos the desired position of n.
	 */
	@Override
	public void swapTo(int n, int pos){
		swap(n,vertices[pos]);
	}
	

	/**
	 * swaps 2 elements in vertices given the positions, and updates the corresponding positions
	 * in positions.
	 * @param i and j are supposed to be indexes.
	 */
	@Override
	public void swapPos(int i, int j) {
		swap(vertices[i],vertices[j]);
	}
	
	//swaps the node to the first position in P, then increments X by 1
	@Override
	public void P2X(int n){
//		System.out.println("P2X : "+n);
		if(!isInP(n)){System.out.println("Misuse of P2X"); System.exit(1);}
		swapTo(n,XPBorder);
		XPBorder++;
	}
	
	//swaps the node to the last position in X, then decrements X by 1
	@Override
	public void X2P(int n){
//		System.out.println("X2P : "+n);
		if(!isInX(n)){System.out.println("Misuse of X2P"); System.exit(1);}
		swapTo(n, XPBorder-1);
		XPBorder--;
	}
	

	@Override
	public int firstInP() {
		return vertices[XPBorder];
	}

	@Override
	public int P_size() {
		return Pend-XPBorder;
	}
	@Override
	public int X_size() {
		return XPBorder-Xstart;
	}

	@Override
	public int P_start() {
		return XPBorder;
	}
	
	@Override
	public boolean isInP(int n){
		return position[n] >= XPBorder && position[n] < Pend;
	}
	@Override
	public boolean isInX(int n){
		return position[n] < XPBorder && position[n] >= Xstart;
	}
	
	@Override
	public boolean P_empty(){
		return XPBorder == Pend;
	}
	@Override
	public boolean X_empty(){
		return Xstart == XPBorder;
	}
	@Override
	public int getMaxLabel(){
		return maxLabel;
	}
	
	/**
	 * takes n in P and adds it to R.
 	 * When the method returns, P and X will only contain neighbors of n.
	 * @param n. precond: n bust be in P (i.e. isInP(n) returns true)
	 * if the precondition is matched, then the backtracking will be successful
	 */
	@Override
	public void addingToR(int n){

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

	}
	
	@Override
	public int getN(){
		return numVerices;
	}
	
	@Override
	public int getXPBorder() {
		return XPBorder;
	}
	
	@Override
	public int getPend(){
		return Pend;
	}

	@Override
	public int[] getVertices() {
		return vertices;
	}

	@Override
	public int[] getPositions() {
		return position;
	}

	@Override
	public int[][] getNeighbors() {
		return neighbors;
	}
	
	@Override
	public void setXstart(int i) {
		Xstart = i;
	}

	@Override
	public void setXPBorder(int i) {
		XPBorder = i;
	}

	@Override
	public void setPend(int i) {
		Pend = i;
	}

	@Override
	public boolean areNeighbors(int n1, int n2) {
//		return contains(neighbors[n1],n2);
		return neighborMat[n1][n2];
	}

	@Override
	public int[] neighbors(int n)
	{
		return neighbors[n];
	}
	
	public boolean[] neighborsM(int n)
	{
		return neighborMat[n];
	}

	//reverse the last "addingToR(int)" operation performed.
	//the X and P sets will be the same as before, though the order of the nodes might be different
	@Override
	public void backTrack() {
		Xstart = btb.getX();
		Pend = btb.getP();
		btb.backtrack();
//		if(DEBUG) System.out.println("Backtracking!");
	}

	@Override
	public void save(int x, int p) {
		btb.save(x, p);
	}



}
