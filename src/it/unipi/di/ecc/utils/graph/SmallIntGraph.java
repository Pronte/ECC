package it.unipi.di.ecc.utils.graph;

import java.util.Arrays;

import it.unipi.di.ecc.interfaces.*;

public class SmallIntGraph implements Graph{

	protected int[][] g;
	
	protected int[] vertices;
	
	
	public SmallIntGraph(int n)
	{
		g = new int[n][];
		vertices = new int[n];
		Arrays.fill(vertices, -1);
	}


	public void setVertices(int[] vertices) {
		this.vertices = vertices;
	}
	
	@Override
	public boolean areNeighbors(int n1, int n2) {
		return(contains(g[n1], n2));
	}

	@Override
	public int cardinality(int n) {
		return g[n].length;
	}
	
	public int[] vertices(){
		return vertices;
	}

	public int[] neighbors(int n){
		return g[n];
	}
	
	public void setNeighbors(int[] l, int n)
	{
		g[n] = null;
		g[n] = l;
		vertices[n] = n;
	}
	
	private boolean contains(int[] l, int x)
	{
		return bs(l, x, 0, l.length) >= 0;
	}
	
	public int bs(int[] l, int x, int low, int hi)
	{	
		if(low >= hi){
			if(low > hi || low >= l.length || l[low] != x) return -1;
			else return low;
		}
		
		int mid = (low+hi)/2;
		
		if(l[mid] < x) return bs(l,x,mid+1,hi);
		
		else if(l[mid] > x) return bs(l,x,low,mid);

		else return mid;
	}

	
}
