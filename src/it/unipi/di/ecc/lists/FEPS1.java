package it.unipi.di.ecc.lists;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FEPS1 extends ECCL {

	//protected Map<Integer,IntOpenHashSet> cleanGraph; 
	protected List<Int2IntOpenHashMap> cleanGraph; //map.get(i).get(j) -> postion of (i,j) in the clean edges list (filled in both directions) 
	protected List<Integer> edgeis, edgejs; //edgeis.get(x) , edgejs.get(x) will contain the edge at the xth position.
	protected int lastCleanEdge; //POSITION of the right-most clean edge in the lists. To be updated after swap removals.
	
	protected int seli, selj;
	private long reptime=0;
	
	
	protected int pMaxVal;
	
	public FEPS1(Graph graph) {
		super(graph);
	}
	public FEPS1(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	@Override
	public final void start() {
		
		//Priority Queue for Edges
		
		//int size = graph.nodes().size();
		
		solution = new ArrayList<IntOpenHashSet>();
		deaths = 0;

		expand0();
		
//		System.out.println("TOTAL REPORTING TIME: "+reptime);
		
		if(checkSolution) checkSolution();
		
	}
	
	@Override
	public void expand0()
	{
		Int2IntOpenHashMap cleans;
		int maxlabel, position;
//		cleanGraph = new HashMap<Integer,IntOpenHashSet>();
		List<Integer> vertices = graph.vertices();
		maxlabel = vertices.get(vertices.size()-1); //list is ordered
		for(int i : vertices) //just to be sure..
		{
			if (i > maxlabel) maxlabel = i;
		}
		
		//stores clean edges and their position in the edge arrays
		// cleangraph[i][j] -> position if i,j , or NULL if i,j is not a clean edge
		cleanGraph = new ArrayList<Int2IntOpenHashMap>(maxlabel+1);

		edgeis = new ArrayList<Integer>(vertices.size()); //initializing to an O(n) size doesn't worsen memory usage and provides a decent starting capacity
		edgejs = new ArrayList<Integer>(vertices.size());
		
		
		for(int i=0; i <= maxlabel; i++)
		{
			cleanGraph.add(new Int2IntOpenHashMap());
		}
		
		for(int i : vertices)
		{
			//cleans = cleanGraph.get(i); //is empty. Fill with edges and fill the edges list at the same time
			for(int j : graph.neighbors(i)) //new edge: (i,j), to consider in both direction for the cgraph, and for the elist only if  i < j
			{
				
				if(i < j && !cleanGraph.get(i).containsKey(j)) //if the edge is in the right direction and is not a duplicate
				{
					edgeis.add(i);
					edgejs.add(j);
					
					position = edgeis.size()-1;
					
					cleanGraph.get(i).put(j, position);
					cleanGraph.get(j).put(i, position);
				}
				
				//calculate position of the edge and insert it in the elist
//				if(cleanGraph.get(j).containsKey(i)) //edge is already there, it was fount the other way around (j,i)
//				{
//					position = cleanGraph.get(j).get(i);
//				}
//				else //need to insert the edge
//				{
//					if(i < j)
//					{
//						edgeis.add(i);
//						edgejs.add(j);
//					}
//					else
//					{
//						edgeis.add(j);
//						edgejs.add(i);
//					}
//						
//					position = edgeis.size()-1;
//				}
//				cleans.put(j,position);
			}
		}
		int ii, jj, pi, pj;
		System.out.println("Edges: "+edgeis.size());
		for(int i = 0; i < edgeis.size(); i++)
		{
			ii = edgeis.get(i);
			jj = edgejs.get(i);
			pi = cleanGraph.get(ii).get(jj);
			pj = cleanGraph.get(jj).get(ii);
			if(i != pi || i != pj)
			{
				System.out.println("In pos "+i+" found edge ("+ii+","+jj+") but it was expected to be in pos ["+pi+"] or ["+pj+"]");
				System.out.println("Pos "+pi+" has edge ("+edgeis.get(pi)+","+edgejs.get(pi)+")");
				System.out.println("Pos "+pj+" has edge ("+edgeis.get(pj)+","+edgejs.get(pj)+")");
			}
		}
		
		
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
			if(graph.neighbors(seli).size() < graph.neighbors(selj).size())
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
						
						if(cleanGraph.get(min).containsKey(x)) val++;
						if(cleanGraph.get(max).containsKey(x)) val++;
						
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
					if(cleanGraph.get(x).containsKey(n)) //n's clean value increases by 1
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
	
	/**
	 * Adds C to the solution and marks all edges in C as dirty.
	 */
	public void report(IntOpenHashSet C)
	{
		if(cliques%10000 == 0) System.out.println(cliques+" cliques");
		cliques++;
		deaths += C.size(); //abused variable for solution sum size
		solution.add(C);
		
//		if(C.size() > maxSize) maxSize = C.size();
		
//		Int2IntOpenHashMap s;
		int pos;
		
		for(int i : C) //for each node in C
		{
//			s = cleanGraph.get(i); 
			for(int j : C)
			{
				if(i < j && cleanGraph.get(i).containsKey(j)) //remove the edge from the clean part of the edge list
				{
					pos = cleanGraph.get(i).get(j);
					
					if(edgeis.get(pos) != i || edgejs.get(pos) != j){
						System.out.println("Also, inverted pos (j->i) was: "+cleanGraph.get(j).get(i)+" instead of "+cleanGraph.get(i).get(j));
						System.out.println("Removing.. expected ("+i+","+j+") in pos "+pos+" to pos "+lastCleanEdge+", found ("+edgeis.get(pos)+","+edgejs.get(pos)+")");
						System.exit(0);
					}
					else
					{
//						System.out.println("Removing ("+i+","+j+") from pos "+pos+" to pos "+lastCleanEdge);
					}

					edgeis.set(pos, edgeis.get(lastCleanEdge));
					edgejs.set(pos, edgejs.get(lastCleanEdge));
					
					edgeis.set(lastCleanEdge, 0);// i); //actually not necessary, this is were the removed edge gets swapped
					edgejs.set(lastCleanEdge, 0);// j); //actually not necessary
					
					lastCleanEdge--;

					cleanGraph.get(edgeis.get(pos)).put((int)edgejs.get(pos), pos); //updating indices of the edge that was swapped down
					cleanGraph.get(edgejs.get(pos)).put((int)edgeis.get(pos), pos);
					
					cleanGraph.get(i).remove(j); //removing the edge from the clean graph
					cleanGraph.get(j).remove(i);
				}
//				s.remove(j); //remove all nodes in C from its clean neighbors (i.e. mark the edges as dirty)
			}
//			if(s.isEmpty()) cleanGraph.remove(i);
		}
		
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
//		System.out.println("[removing]val: "+val+" , pmaxval: "+pMaxVal+", node "+node);
		//updating the index of the rightmost nonempty set in P
		if(P.get(val).isEmpty() && pMaxVal == val)
		{
			while(pMaxVal > 0 && P.get(pMaxVal).isEmpty()) pMaxVal--;
		}
	}
	
	@Override
	/**
	 * Selects a clean edge, and puts its extremes in 'seli' and 'selj'
	 * If there are no clean edges, 'seli' is put to -1
	 * (all valid node IDs are assumed to be >= 0)
	 */
	public void selectEdgeToExpand() //random
	{
//		selectEdgeToExpandFaster();
		if(lastCleanEdge < 0)
		{
			seli = -1;
			selj = -1;
		}
		else
		{
			int pos = (int) ((lastCleanEdge+1)*Math.random()); //from 0 to lastCleanEdge included
		
			seli = edgeis.get(pos);
			selj = edgejs.get(pos);
		}

	}
	/* backup
	public void selectEdgeToExpand() //random
	{
//		selectEdgeToExpandFaster();
		
		
		int size = graph.vertices().size(); //cleanGraph.keySet().size();
		int start = (int)(Math.random()*size);
		int i;
		
		for(int ii = 0; ii < size; ii++)
		{
			i= graph.vertices().get((ii+start)%size);

			Int2IntOpenHashMap cleanNeighs = cleanGraph.get(i);

			if(cleanNeighs != null && !cleanNeighs.isEmpty())
			{
//				int randomPos = (int) (Math.random()*cleanNeighs.size()); 
				
				int random = (int) (cleanNeighs.size()*Math.random()); //TODO check performance and quality impact
				
				int j = -1;
				
				for(int x : cleanNeighs) //TODO check performance and quality impact
				{
					if(random == 0)
					{
						j = x;
						break;
					}
					random--;
				}

				if(j != -1)
				{
					seli = i;
					selj = j;
					return;
				}
			}
		}

		//No clean edges in the graph, triggering halt condition.
		seli = -1;
		selj = -1;
		
	} */
	
	/**
	 * other select edge methods. unused in this class but used in subclasses to generate the different variants. 
	 */
	
	//protected variables for usage by select-edge strategies
	//they store the value found in the last search, so it's possible to cut when an equal value is found.
	protected int maxdval = -1, mindval = Integer.MAX_VALUE, maxudval = -1;
	//---
	
	/**
	 * Selects a clean edge, and puts its extremes in 'seli' and 'selj' (where 'seli' is the node of smaller degree)
	 * If there are no clean edges, 'seli' is put to -1
	 * (all valid node IDs are assumed to be >= 0)
	 * 
	 * MaxDeg: selects the clean edge (i,j) such that Max(deg(i),deg(j)) is the maximum. (the node of max deg is put in selj).
	 */
	public void selectEdgeToExpandMaxDeg()
	{
		int maxV = Integer.MIN_VALUE;

		seli = selj = -1;
		int i,j;
		
		for(int e = 0; e <= lastCleanEdge; e++)
		{
			i = edgeis.get(e);
			j = edgejs.get(e);
			if(graph.cardinality(i) > maxV)
			{
				seli = j;
				selj = i;
				maxV = graph.cardinality(i);
			}

			if(graph.cardinality(j) > maxV)
			{
				seli = i;
				selj = j;
				maxV = graph.cardinality(j);
			}
			if(maxV == maxdval) return; //found an edge that is as good as the one found last time. Finding a better one is not possible so we can stop.
		}
		maxdval = maxV; //value found at this iteration, surely at the next one a better value cannot be found (as the candidate edges will be a subset).
	}	
	
	/**
	 * Selects a clean edge, and puts its extremes in 'seli' and 'selj' (where 'seli' is the node of smaller degree)
	 * If there are no clean edges, 'seli' is put to -1
	 * (all valid node IDs are assumed to be >= 0)
	 * 
	 * MaxUncoveredDeg: selects the clean edge (i,j) such that Max(clean_deg(i),clean_deg(j)) is the maximum. (the node of max deg is put in selj).
	 */
	public void selectEdgeToExpandMaxUncoveredDeg()
	{

		int maxV = Integer.MIN_VALUE;

		seli = selj = -1;
		int i,j;
		
		for(int e = 0; e <= lastCleanEdge; e++)
		{
			i = edgeis.get(e);
			j = edgejs.get(e);
			if(cleanGraph.get(i).keySet().size() > maxV)
			{
				seli = j;
				selj = i;
				maxV = cleanGraph.get(i).keySet().size();
			}

			if(cleanGraph.get(j).keySet().size() > maxV)
			{
				seli = i;
				selj = j;
				maxV = cleanGraph.get(j).keySet().size();
			}
			if(maxV == maxudval) return; //found an edge that is as good as the one found last time. Finding a better one is not possible so we can stop.
		}
		maxudval = maxV; //value found at this iteration, surely at the next one a better value cannot be found (as the candidate edges will be a subset).
	}
	
	/**
	 * Selects a clean edge, and puts its extremes in 'seli' and 'selj' (where 'seli' is the node of smaller degree)
	 * If there are no clean edges, 'seli' is put to -1
	 * (all valid node IDs are assumed to be >= 0)
	 * 
	 * MinDeg: selects the clean edge (i,j) such that Min(deg(i),deg(j)) is the minimum. (the node of min deg is put in seli).
	 */
	public void selectEdgeToExpandMinDeg()
	{
		int minV = Integer.MAX_VALUE;

		seli = selj = -1;

		for(int i :graph.vertices())
		{
			IntSet cleanNeighs = cleanGraph.get(i).keySet();

			if(cleanNeighs != null && !cleanNeighs.isEmpty())// System.out.println("No priority map for "+i);
			{
				for(int j : cleanNeighs)
				{
					if(graph.cardinality(i) < minV)
					{
						seli = j;
						selj = i;
						minV = graph.cardinality(i);
					}

					if(graph.cardinality(j) < minV)
					{
						seli = i;
						selj = j;
						minV = graph.cardinality(j);
					}
					
					if(minV == mindval) return; //found an edge that is as good as the one found last time. Finding a better one is not possible so we can stop.
				}
			}
		}
		mindval = minV; //value found at this iteration, surely at the next one a better value cannot be found (as the candidate edges will be a subset).
	}


}