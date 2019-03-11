package it.unipi.di.ecc.utils;

import it.unipi.di.ecc.interfaces.ECC;
import it.unipi.di.ecc.interfaces.IntGraph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphStats extends ECC {
	
	IntGraph g;

	public GraphStats(InputStream is) {
		this((IntGraph) Parser.getListGraph(is));
	}


	public GraphStats(IntGraph graph) {
		super(graph);
		
		g = graph;
	}


	@Override
	public Object solution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		System.out.println(getStats());
	}
	
	
	@Override
	public AlgStats getStats(){
		
		List<Integer> v = this.g.vertices();
		List<Integer> intersection;
		long m = 0, mNoTri = 0; //mNoTri = edges that don't participate in any triangle (aka: intersection of the neighborhoods of their extremes is empty)
		for(int x : v)
		{
			for(int y : g.neighbors(x))
			{
				if(x < y) //only scanning edges one time each
				{
					//check if the edge (x,y) closes any triangle
					intersection = ListUtils.intersectOrdered(g.neighbors(x),g.neighbors(y));
					
					if(intersection.size() == 0) mNoTri++;
				}
			}
			
			m += g.neighbors(x).size();
		}
		
		m = m/2;
		
		Map<String,String> stats = new HashMap<String,String>();
		
		stats.put("time", ""+0);
		stats.put("alg",this.getClass().getName());
		stats.put("vertices", ""+v.size());
		stats.put("edges", ""+m);
		stats.put("mnotri", ""+mNoTri);
		
		return new AlgStats(stats){
			@Override
			public String toString()
			{
				return 	get("vertices")+" "+get("edges")+"\n";
			}
		};
	}


	@Override
	public void selectEdgeToExpand() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void expand0() {
		// TODO Auto-generated method stub
		
	}


//	@Override
//	public void setFilters(List<Integer> toVisit, List<Integer> excluded,
//			Set<Integer> required, int requiredNum, int minSize) {
//		// TODO Auto-generated method stub
//		
//	}

}
