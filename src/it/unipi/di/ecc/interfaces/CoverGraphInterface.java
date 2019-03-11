package it.unipi.di.ecc.interfaces;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;


public interface CoverGraphInterface extends IntGraph{

	public int numNodes();

	public void startNodesIteration();
	
	public void startNodesRandomIteration();
	
	public int nextNode();

	public void startNeighborsIteration(int i);

	public int nextNeighbor();

	public IntOpenHashSet neighborsOf(int seli, int selj);

	@Override
	public int cardinality(int i);

	public IntOpenHashSet intersectNeighbors(IntOpenHashSet p, int x);

	public void printStats();
	
	
	
}
