package it.unipi.di.ecc.interfaces;

import java.util.List;

//this interface represent a graph containing nodes with simple int labels
public interface IntGraph extends Graph {

	//return a list of all the nodes in the graph
	public List<Integer> vertices ();

	//return a list of all the neighbors of the node with label n
	public List<Integer> neighbors(int n);
	
}
