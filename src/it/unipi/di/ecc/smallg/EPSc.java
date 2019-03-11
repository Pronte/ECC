package it.unipi.di.ecc.smallg;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.ListUtils;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EPSc extends EPS1 {
	
	public EPSc(Graph graph) {
		super(graph);
	}
	public EPSc(InputStream is)
	{
		this(Parser.getSmallIntGraph(is));
	}
	
	protected int pMaxVal;

	@Override
	public void expand0()
	{
//		Int2IntOpenHashMap cleans;
		int maxlabel, position;
//		cleanGraph = new HashMap<Integer,IntOpenHashSet>();
		int[] vertices = graph.vertices();
		maxlabel = vertices[vertices.length-1]; //list is ordered
		for(int i : vertices) //just to be sure..
		{
			if (i > maxlabel){ maxlabel = i; System.out.println("VERTICES WERE NOT ORDERED!!!!!!-------------------------");}
		}
		
		//stores clean edges and their position in the edge arrays
		// cleangraph[i][j] -> position if i,j , or NULL if i,j is not a clean edge
		cleanGraph = new int[maxlabel+1][];
		cleanPoss  = new int[maxlabel+1][]; 

		edgeis = new ArrayList<Integer>(vertices.length); //initializing to an O(n) size doesn't worsen memory usage and provides a decent starting capacity
		edgejs = new ArrayList<Integer>(vertices.length);
		
		if(DEBUG) System.out.println("Building clean graph... ");

		int i,j;
		for(int iind = 0; iind < vertices.length; iind++) //enforcing crescent order of labels (vertices is ordered)
		{
			i = vertices[iind];
			cleanGraph[i] = graph.neighbors(i).clone();
			cleanPoss[i] = new int[cleanGraph[i].length];

			for(int jind = 0; jind < cleanGraph[i].length; jind++)
			{
				j = cleanGraph[i][jind];
				
				if(i<j) //adding each edge only once
				{
					edgeis.add(i);
					edgejs.add(j);
					
					cleanPoss[i][jind] = edgeis.size()-1;
					
					if(edgeis.size() % 1000000 == 0) System.out.println(edgeis.size()+" Edges done");
				}
				else //i>j: the edge is already added, and i have the association cp[j]-> i -> pos, but i need also cp[i] -> j -> pos
				{	 //hence i need to find the pos of the edge (j,i) in cp[j] -> i, hence the position of i in cp[j], and use it to update cp[i]->j

					cleanPoss[i][jind] = cleanPoss[j][ListUtils.binarySearch(cleanGraph[j], i)];
				}
			}
		}
		

		int ii, jj, pi, pj;
		if(DEBUG) System.out.println("Edges: "+edgeis.size()+". Checking positions..");
		for(i = 0; i < edgeis.size(); i++)
		{
			ii = edgeis.get(i);
			jj = edgejs.get(i);
			if(!graph.areNeighbors(ii,jj))System.out.println(ii+","+jj+" Are not neighbors!!!");
//			System.out.println("trying "+ii+","+jj);
			pi = edgepos(ii, jj);
			pj = edgepos(jj, ii);
			if(i != pi || i != pj)
			{
				System.out.println("In pos "+i+" found edge ("+ii+","+jj+") but it was expected to be in pos ["+pi+"] or ["+pj+"]");
				System.out.println("Pos "+pi+" has edge ("+edgeis.get(pi)+","+edgejs.get(pi)+")");
				System.out.println("Pos "+pj+" has edge ("+edgeis.get(pj)+","+edgejs.get(pj)+")");
			}
		}
		
		if(DEBUG) System.out.println("Done! Starting search");
		
		lastCleanEdge = edgeis.size()-1;
		
		selectEdgeToExpand();
		while(seli != -1)
		{
			
			/**
			 * -select edge
			 * process
			 * -select another edge until halt
			 */
			
			List<IntOpenHashSet> P = new ArrayList<IntOpenHashSet>();
			P.add(new IntOpenHashSet());
			
//			Int2IntOpenHashMap P = new Int2IntOpenHashMap();
			
			int min, max;
			if(graph.neighbors(seli).length < graph.neighbors(selj).length)
			{
				min = seli;
				max = selj;
			}else
			{
				min = selj;
				max = seli;
			}
			
			pMaxVal = 0;
			int val;
			
			for(int x : graph.neighbors(min))
			{
				if(graph.areNeighbors(max, x))
					{
						val = 0;
						
						if(ListUtils.binarySearch(cleanGraph[min], x) >= 0) val++;
						if(ListUtils.binarySearch(cleanGraph[min], x) >= 0) val++;
						
						addToP(P, x, val);
					}
			}
			
			
			IntOpenHashSet C = new IntOpenHashSet();
			
			C.add(seli);
			C.add(selj);
			
			expand(C,P);//TODO: Augment P with clean/semiclean values
			
			selectEdgeToExpand();
		}
		

		
	}


	
	
	public void expand(IntOpenHashSet C, List<IntOpenHashSet> P)
	{
		nodes++;
		//int cval, scval;

		
		if(pMaxVal > 0) //P is not empty and contains at least a clean node
		{

			//an arbitrary node of max value
			int x;
			try{
				x = P.get(pMaxVal).iterator().nextInt();
			}
				catch( Exception e)
			{
				System.out.println(C);
				System.exit(0);
				return;
			}
			
			process(C, P, x);
			
		} else //P does not contain clean nodes (but is not necessarily empty)
		{
			if(true || P.get(0) == null || P.get(0).isEmpty())
			{
				report(C);
				return;
			}
			
			//semiclean stuff in here.
			
			
			//can pay up to \delta for each node of the clique
			//note, all nodes in P are in P[0], which is not empty
			
//			List<IntOpenHashSet> SCP = new ArrayList<IntOpenHashSet>();
//			SCP.add(new IntOpenHashSet());
//			
//			for(int x : P.get(0))
//			{
//				
//				
//				
//			}
//			
//			
//			for (int i: C)
//			{
//				IntSet cleanNeighs = cleanGraph.get(i).keySet();
//				
//				for(int x : cleanNeighs)
//				{
//					if(P.get(0).contains(x)) //x is in P and has a 
//					{
//						
//					}
//				}
//			}
			
			/*
			 
			int max=-1, maxClean=-1, maxSClean=-1,
					maxVal=-1,maxCVal=-1,maxSCVal=-1, cval, scval;
				
				for(int i : P)
				{
					cval = scval = 0;
	
					long ttemp = System.currentTimeMillis();//TODO
					IntSet cleanNeighs = cleanGraph.get(i).keySet();
					reptime += (System.currentTimeMillis()-ttemp);//TODO
					if(cleanNeighs != null && !cleanNeighs.isEmpty())
					{
						for(int cn : cleanNeighs)
						{
							if(C.contains(cn)) //node i has a neighbor in C, so it is clean
							{
								cval++;
							}
							else if(cval == 0 && P.contains(cn)) //semi clean
							{
								scval++;
							}
						}
						
						if(cval > 0 && cval > maxCVal)
						{
							maxCVal = cval;
							maxClean = i;
						} else if(cval == 0 && scval > maxSCVal)
						{
							maxSCVal = scval;
							maxSClean = i;
						}
					}
					else if(maxSClean ==-1 && maxClean==-1 && graph.neighbors(i).size() >= maxVal) //no clean or semiClean nodes found yet, and current node is dirty
					{
						maxVal = graph.neighbors(i).size();
						max = i; //pivot
					}
				}
			*/
		}
	}
	
	public void process(IntOpenHashSet C, List<IntOpenHashSet> P, int x)
	{
//		System.out.println("----------------------Processing "+x);
		C.add(x);
		
		//removing x from P
		//since the strategy is 'max clean value', the val of x is pMaxVal
		remFromP(P, x, pMaxVal);

		IntOpenHashSet toRemove = new IntOpenHashSet();

		for(int vi = pMaxVal; vi >= 0; vi--)
		{
			for(int n : P.get(vi))
			{
				if(graph.areNeighbors(x, n)) //the node n should still be in P
				{
					if(ListUtils.binarySearch(cleanGraph[x], n) >= 0) //n's clean value increases by 1
					{
						toRemove.add(n);
						addToP(P, n, vi+1);
					}
				} else
				{
					toRemove.add(n);
				}
			}
			
			for(int n : toRemove)
			{
				remFromP(P, n, vi);
			}
			toRemove.clear();
		}
		

		expand(C,P);
		
//		for(int n : graph.neighbors(x))
//		{
//			if(P.contains(n)) newP.add(n);
//		}
//		
//		if(newP.isEmpty())
//		{
//			report(C);
//		}
//		else
//		{
//			expand(C, newP);
//		}
//		//C.clear(x);
	}
	

	private void addToP(List<IntOpenHashSet> P, int node, int val)
	{
		while(P.size() <= val) P.add(new IntOpenHashSet());
		
		P.get(val).add(node);
		
		if(val > pMaxVal){
			pMaxVal = val;
//			System.out.println("[adding]new pMaxVal = "+val+" for node "+node);
		}
	}
	
	private void remFromP(List<IntOpenHashSet> P, int node, int val)
	{
		//removing the node
		P.get(val).remove(node);
		//updating the index of the rightmost nonempty set in P
		if(P.get(val).isEmpty() && pMaxVal == val)
		{
			while(pMaxVal > 0 && P.get(pMaxVal).isEmpty()) pMaxVal--;
		}
	}
}