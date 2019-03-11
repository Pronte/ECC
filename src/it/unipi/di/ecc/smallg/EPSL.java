package it.unipi.di.ecc.smallg;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.graph.SmallIntGraph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public abstract class EPSL extends it.unipi.di.ecc.interfaces.ECC {

	protected SmallIntGraph graph;
    protected List<IntOpenHashSet> solution;
	
	public EPSL(Graph graph) {
		super(graph);
		this.graph = (SmallIntGraph) graph;
	}

	public EPSL(InputStream is) {
		this(Parser.getSmallIntGraph(is));
	}

	@Override
	public Object solution()
	{
		return this.solution;
	}
	
	public boolean checkSolution()
	{
		if(solution == null){
			System.out.println("No solution");
			return false;
		}
		
//		MapListGraph g = new MapListGraph();
		Map<Integer,Set<Integer>> g= new HashMap<Integer,Set<Integer>>();
		
		for(IntOpenHashSet clq : solution)
		{
			for(int i : clq)
			{
				for(int j : clq)
				{
					if(i != j)
					{
						if(!g.containsKey(i)) g.put(i,new HashSet<Integer>());
						
//						if(i == 259221 || i == 260083) System.out.println("EDGE ("+i+","+j+")");
						
						g.get(i).add(j);
					}
				}
			}
		}
		
		int[] nodes = graph.vertices();
		for(int i :nodes)
		{
			int[] neighs = graph.neighbors(i);
			
			if(neighs != null)
				for (int j : neighs)
				{
					try
					{
						if(!g.get(i).contains(j))
						{
							System.out.println("The edge "+i+","+j+" is not covered in the solution!!");
							return false;
						}
					}
					catch(Exception e)
					{
						System.out.println("The edge "+i+","+j+" raised an exception!!");
						
						System.out.println("g.get(i): "+g.get(i));
						
						return false;
					}
				}
			
		}
		
		if(DEBUG) System.out.println("The solution is correct.");
		return true;
	}
	
	
}
