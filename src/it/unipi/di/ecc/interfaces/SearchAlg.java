package it.unipi.di.ecc.interfaces;

import it.unipi.di.ecc.utils.AlgStats;


public interface SearchAlg {

    public void search();

    public void setTimeLimit(long limit);
    
    public AlgStats getStats();

	public void setAborted();

	public void printStats();
	
	public Object solution();
}
