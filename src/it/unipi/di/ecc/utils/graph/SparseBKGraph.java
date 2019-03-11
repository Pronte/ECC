package it.unipi.di.ecc.utils.graph;

import it.unipi.di.ecc.interfaces.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SparseBKGraph implements Graph {
	
	protected boolean DEBUG = System.getProperty("DEBUG") != null;

	//To be used as: [X]::[P]
	int[] vertices;
	
	// stores the position of the elements in "vertices" (i.e. lookup[i] = x, such as: vertices[x] = i ))
	int[] position;
	
	// neighbors[i] -> neighbors of the node labelled "i".
	int[][] neighbors;
	
	/**
	 * index of the first element in P.
	 * For every i < XPborder vertices[i] belongs to X,
	 * for every i >= XPborder vertices[i] belongs to P.
	 */
	int XPBorder;
	
	//first element of X
	int Xstart;
	
	//first element out of P (can be out of bounds)
	int Pend;
	
	protected BackTrackBuffer btb; 
	
	
	// to be used during building
	protected int toFill;
	protected int maxLabel;
	protected int numVerices;
/*
	public SparseBKGraph(int n){
		vertices = new int[n];
		position = new int[n];
		neighbors = new int[n][];
		XPBorder = 0;
		toFill = 0;
		
		for(int i = 0; i<n; i++){
			vertices[i] = -1;
			position[i] = -1;
			neighbors[i] = null;
		}
	}
*/
	@Deprecated  // only to be used by extensions that want to completely rewrite the constructor.
	public SparseBKGraph(){}
	
	/**
	 * Builds a sparse graph from MapListGraph.
	 * @param mlg - map/list graph
	 */
	public SparseBKGraph(Map<Integer, List<Integer>> mlg){
		List<Integer> nodes = new ArrayList<Integer> (mlg.keySet());
		
		int n = nodes.size();
		numVerices = n;
		maxLabel = Collections.max(nodes);
		System.out.println("Nodes: "+n+" MaxLabel: "+maxLabel);
		
		vertices = new int[n];
		position = new int[maxLabel+1]; //the size should be equal to the maximum possible label+1
										//so that all vertices' labels can be used as keys
		neighbors = new int[maxLabel+1][]; //idem
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
			}
			Arrays.sort(neighbors[v]);
			toFill++;
		}
	}
	/**
	 * Builds a sparse graph.
	 * @param mlg - map/list graph
	 */
	public SparseBKGraph(int[][] neighborsVecs){
		
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
		neighbors = neighborsVecs;
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

	/**
	 * swaps 2 elements in vertices, and updates the corresponding positions
	 * in positions.
	 * @param n1 and n2 are supposed to be labels
	 */
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
	public void swapTo(int n, int pos){
		swap(n,vertices[pos]);
	}
	

	/**
	 * swaps 2 elements in vertices given the positions, and updates the corresponding positions
	 * in positions.
	 * @param i and j are supposed to be indexes.
	 */
	public void swapPos(int i, int j) {
		swap(vertices[i],vertices[j]);
	}
	
	//swaps the node to the first position in P, then increments X by 1
	public void P2X(int n){
//		System.out.println("P2X : "+n);
		if(!isInP(n)){System.out.println("Misuse of P2X"); System.exit(1);}
		swapTo(n,XPBorder);
		XPBorder++;
	}
	
	//swaps the node to the last position in X, then decrements X by 1
	public void X2P(int n){
//		System.out.println("X2P : "+n);
		if(!isInX(n)){System.out.println("Misuse of X2P"); System.exit(1);}
		swapTo(n, XPBorder-1);
		XPBorder--;
	}
	

	public int firstInP() {
		return vertices[XPBorder];
	}

	public int P_size() {
		return Pend-XPBorder;
	}
	public int X_size() {
		return XPBorder-Xstart;
	}

	public int P_start() {
		return XPBorder;
	}
	
	public boolean isInP(int n){
		return position[n] >= XPBorder && position[n] < Pend;
	}
	public boolean isInX(int n){
		return position[n] < XPBorder && position[n] >= Xstart;
	}
	
	public boolean P_empty(){
		return XPBorder == Pend;
	}
	public boolean X_empty(){
		return Xstart == XPBorder;
	}
	public int getMaxLabel(){
		return maxLabel;
	}
	
	/**
	 * takes n in P and adds it to R.
 	 * When the method returns, P and X will only contain neighbors of n.
	 * @param n. precond: n bust be in P (i.e. isInP(n) returns true)
	 * if the precondition is matched, then the backtracking will be successful
	 */
	public void addingToR(int n){
		int Pfill = XPBorder;
		int Xfill = XPBorder-1;
		
		//System.out.println("Adding "+n+" to R");
		//System.out.print("vertices: [");
		//for(int i=0; i< vertices.length-1; i++) System.out.print(vertices[i]+",");
		//if(vertices.length > 0) System.out.print(vertices[vertices.length-1]);
		//System.out.println("]");
		//System.out.println("Xstart="+Xstart+", XPBorder="+XPBorder+", Pend="+Pend);
		//System.out.println("Xsize="+X_size()+", Psize="+P_size());
		
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
		
		btb.save(Xstart, Pend);
		
		Xstart = Xfill+1; //inclusive
		Pend = Pfill; //exclusive
	}
	
	public int getN(){
		return numVerices;
	}
	
	public int getXPBorder() {
		return XPBorder;
	}
	
	public int getPend(){
		return Pend;
	}

	public int[] getVertices() {
		return vertices;
	}

	public int[] getPositions() {
		return position;
	}

	public int[][] getNeighbors() {
		return neighbors;
	}
	
	public void setXstart(int i) {
		Xstart = i;
	}

	public void setXPBorder(int i) {
		XPBorder = i;
	}

	public void setPend(int i) {
		Pend = i;
	}

	@Override
	public boolean areNeighbors(int n1, int n2) {
		return contains(neighbors[n1],n2);
	}

	protected boolean contains(int[] arr, int x){
		return Arrays.binarySearch(arr, x) >= 0;
		
//		for(int i=0; i<arr.length; i++){
//			if(arr[i] == x) return true;
//		}
//		return false;
	}
	
	public int[] neighbors(int n) {
		return neighbors[n];
	}

	//reverse the last "addingToR(int)" operation performed.
	//the X and P sets will be the same as before, though the order of the nodes might be different
	public void backTrack() {
		Xstart = btb.getX();
		Pend = btb.getP();
		btb.backtrack();
//		if(DEBUG) System.out.println("Backtracking!");
	}

	public void save(int x, int p) {
		btb.save(x, p);
	}
	@Override
	public int cardinality(int node) {
		return neighbors[node].length;
	}



}
