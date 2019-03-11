package it.unipi.di.ecc.interfaces;

import it.unipi.di.ecc.utils.AlgStats;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;



public abstract class ECC implements SearchAlg {
	
	
	
	/**
	 * Overview:
	 * 
	 * 1-all edges are uncovered
	 * 2-select an uncovered edge
	 * 3-find a clique that uses that edge
	 * 4-for any found clique: mark all its edges as covered
	 * 6-back to 2 or stop if all edges are covered
	 *
	 */
	
	 	protected int cliques;	// number of maximal cliques
	    protected long nodes;     // number of decisions
	    protected long deaths;
	    protected long timeLimit; // milliseconds
	    protected long startTime;   // milliseconds
	    protected long endTime;	// milliseconds
	    protected int maxSize;    // size of max clique
	    protected boolean storeSolution = true; //false: faster, but only records number of cliques and max size
	    protected boolean checkSolution = true;
		public static boolean DEBUG = (System.getProperty("DEBUG") != null);
	    public boolean aborted = false;
		
	    public ECC (Graph graph) {
		nodes = maxSize = 0;
		startTime = timeLimit = -1;
	    }

	    public ECC (InputStream is) {
	    	throw new RuntimeException("This constructor must be overridden.");
		}
	    
		@Override
		public void search(){
		nodes                = 0;
		deaths = 0;
		
		if(DEBUG) System.out.println("Starting search; Timeout: "+timeLimit+"ms");
		
		startTime              = System.currentTimeMillis();
		
		start();
		
		endTime	= System.currentTimeMillis();

	    }
		
		public abstract void start();
		
		public abstract void selectEdgeToExpand();
		
		public abstract void expand0();
		
		
		@Override
		public void setTimeLimit(long limit){
			this.timeLimit = limit;
		}

		@Override
		public void printStats(){
			System.out.println("---- solution statistics ----");
			System.out.println("Cliques: "+cliques);
			System.out.println("Sum of sizes: "+deaths);
			System.out.println("Max clique size: "+maxSize);
//			System.out.println("Nodes: "+nodes);
			System.out.println("Time (ms): "+(endTime-startTime));
//			System.out.println("Aborted: "+aborted);
			System.out.println("-----------------------------");
		}
		
		@Override
		public AlgStats getStats(){
			
			Map<String,String> stats = new HashMap<String,String>();
			
			stats.put("clq", ""+cliques);
			stats.put("nodes", ""+nodes);
			stats.put("time", ""+(endTime-startTime));
			stats.put("max", ""+maxSize);
			stats.put("limit", ""+timeLimit);
			stats.put("alg",this.getClass().getName());
			
			return new AlgStats(stats, this.solution());
		}
		
		@Override
		public void setAborted(){
			this.endTime = System.currentTimeMillis();
			this.aborted = true;
		}
		


}
