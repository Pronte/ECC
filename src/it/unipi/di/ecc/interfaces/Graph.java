package it.unipi.di.ecc.interfaces;

//this interface represent a generic graph
public interface Graph {

	
	//true if n1 and n2 are neighbors, false otherwise
	public boolean areNeighbors(int n1, int n2);

	public int cardinality(int n);
}
