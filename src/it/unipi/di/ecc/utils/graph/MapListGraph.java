package it.unipi.di.ecc.utils.graph;

import it.unipi.di.ecc.interfaces.IntGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapListGraph implements IntGraph {

	//key: node label, value: list of neighbor's labels
	public Map<Integer,List<Integer>> g;
	
	private boolean ordered = false;
	
	private List<Integer> vertices;
	
	public MapListGraph (Map<Integer,List<Integer>> g){
		this.g = g;
		vertices = null;
	}
	
	@Override
	//O(n)
	public List<Integer> vertices() {
		
		if (vertices != null) return vertices;
		
		vertices = new ArrayList<Integer>(g.keySet().size());
		
		for(int i : g.keySet()){
			vertices.add(i);
		}
		
		return vertices;
	}

	@Override
	//O(1)
	public List<Integer> neighbors(int n) {
		if(!g.containsKey(n) || g.get(n) == null){
			return new ArrayList<Integer>();
		} else {
			return g.get(n);
		}
	}

	@Override
	//O( d(n1) ), worst case: O(n)
	public boolean areNeighbors(int n1, int n2) {
		if(ordered)
		{
			return binarySearch(g.get(n1),n2) >= 0;
		}
		else
		{
			return g.get(n1).contains(n2);
		}
	}
	
	public void orderGraph(){
		
		Collections.sort(vertices());
		
    	for(int n : vertices()){
    		Collections.sort(this.neighbors(n));
    	}
    	
    	ordered = true;
		
	}
	
	/**
	 * returns the index of the element "key" if present,
	 * if not, returns the index in which the element "key" should be inserted (i.e. the index of the first element greater than "key"). 
	 */
	public static int binarySearch(List<Integer> l , int key){
		return bs(l,key,0,l.size());
	}
	
	private static int bs(List<Integer> l, int key, int min, int max){
		if(min == max) return 0-min-1; //not found
		else {
			int m = mid(min,max);
			
			if(key > l.get(m)) return bs(l,key,m+1,max);
			else if(key < l.get(m)) return bs(l,key,min,m);
			else return m; // key = l.get(m)
		}
	}

	private static int mid(int min, int max){
		return (min+max)/2;
	}

	public static List<Integer> emptyList() {
		return new ArrayList<Integer>();
	}

	@Override
	public int cardinality(int n) {
		return g.get(n).size();
	}


}
