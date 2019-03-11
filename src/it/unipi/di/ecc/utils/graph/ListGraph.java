package it.unipi.di.ecc.utils.graph;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import it.unipi.di.ecc.interfaces.IntGraph;

public class ListGraph implements IntGraph {

	//key: node label, value: list of neighbor's labels
	private List<List<Integer>> g;
	
	private List<Integer> vertices;
	private int maxlabel;
	
	public ListGraph (Map<Integer,List<Integer>> graph){
		
		vertices = new ArrayList<Integer>(graph.size());
		
		maxlabel=0;
		
		for(int i : graph.keySet())
		{
			vertices.add(i);
			if(i > maxlabel) maxlabel = i;
		}
		
		this.g = new ArrayList<List<Integer>>(maxlabel+1);
		
		for(int i = 0; i<=maxlabel; i++)
		{
			if(graph.get(i) != null) this.g.add(graph.get(i));
			else g.add(new ArrayList<Integer>());
		}
		
		orderGraph();
	}
	
	@Override
	//O(1)
	public List<Integer> vertices() {
		if (vertices != null) return vertices;
		
		vertices = new ArrayList<Integer>(g.size());
		
		for(int i= 0; i < g.size(); i++){
			if(g.get(i) != null)
				vertices.add(i);
		}
		
		return vertices;
	}

	@Override
	//O(1)
	public List<Integer> neighbors(int n) {
		if(g.get(n) == null)
		{
			return new ArrayList<Integer>();
		} else {
			return g.get(n);
		}
	}

	@Override
	//O( log(d(n1)) ), worst case: O(log(n))
	public boolean areNeighbors(int n1, int n2) {
			return binarySearch(g.get(n1),n2) >= 0;
	}
	
	public void orderGraph(){
		
		Collections.sort(vertices());
		
    	for(List<Integer> l : g){
    		Collections.sort(l);
    	}
	}
	
	/**
	 * returns the index of the element "key" if present,
	 * if not, returns the index in which the element "key" should be inserted (i.e. the index of the first element greater than "key") multiplied by -1. 
	 */
	public static int binarySearch(List<Integer> l , int key){
		return bs(l,key,0,l.size());
	}
	
	private static int bs(List<Integer> l, int key, int min, int max){
		if(min == max) return 0-min-1; //not found
		else {
			int m = (min+max)/2;
			
			if(key > l.get(m)) return bs(l,key,m+1,max);
			else if(key < l.get(m)) return bs(l,key,min,m);
			else return m; // key = l.get(m)
		}
	}

	@Override
	public int cardinality(int n) {
		return (g.get(n) == null ? 0 : g.get(n).size());
	}

}
