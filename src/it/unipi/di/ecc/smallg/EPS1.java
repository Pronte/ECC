package it.unipi.di.ecc.smallg;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.ListUtils;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EPS1 extends EPSL {

	//protected Map<Integer,IntOpenHashSet> cleanGraph; 
//	protected List<Int2IntArrayMap> cleanGraph; //map.get(i).get(j) -> postion of (i,j) in the clean edges list (filled in both directions) 
	protected int[][] cleanGraph; //cleangraph[i] -> clean neighbors of i
	protected int[][] cleanPoss;  //cleanposs[i][j] -> positions of the edge (i,cleangraph[i][j])
	
	protected List<Integer> edgeis, edgejs; //edgeis.get(x) , edgejs.get(x) will contain the edge at the xth position.
	protected int lastCleanEdge; //POSITION of the right-most clean edge in the lists. To be updated after swap removals.
	
	protected int seli, selj;
	private long reptime=0;
	private int interval = 1;
	
	public EPS1(Graph graph) {
		super(graph);
	}
	public EPS1(InputStream is)
	{
		this(Parser.getSmallIntGraph(is));
	}
	
	@Override
	public final void start() {
		
		//Priority Queue for Edges
		
		//int size = graph.nodes().size();
		
		solution = new ArrayList<IntOpenHashSet>();
		deaths = 0;

		expand0();
		
//		System.out.println("TOTAL REPORTING TIME: "+reptime);
		
		if(DEBUG) checkSolution();
		
	}
	
	@Override
	public void expand0()
	{
		Int2IntOpenHashMap cleans;
		int maxlabel, position;
//		cleanGraph = new HashMap<Integer,IntOpenHashSet>();
		int[] vertices = graph.vertices();
		maxlabel = vertices[vertices.length-1]; //list is ordered
		for(int i : vertices) //just to be sure..
		{
			if (i > maxlabel) maxlabel = i;
		}

//		cleanGraph = new ArrayList<Int2IntOpenHashMap>(maxlabel+1);
		cleanGraph = new int[maxlabel+1][];
		cleanPoss  = new int[maxlabel+1][]; 

		edgeis = new ArrayList<Integer>(vertices.length);//initializing to an O(n) size doesn't worsen memory usage and provides a decent starting capacity
		edgejs = new ArrayList<Integer>(vertices.length);
		
		int i,j;
		for(int iind = 0; iind < vertices.length; iind++) //enforcing crescent order of labels (vertices is ordered)
		{
			i = vertices[iind];
			
			cleanGraph[i] = graph.neighbors(i).clone();
			cleanPoss[i] = new int[cleanGraph[i].length];

			for(int jind : cleanGraph[i])
			{
				j = cleanGraph[i][jind];
				
				if(i<j) //adding each edge only once
				{
					edgeis.add(i);
					edgejs.add(j);
					
					cleanPoss[i][jind] = edgeis.size()-1;
				}
				else //i>j: the edge is already added, and i have the association cp[j]-> i -> pos, but i need also cp[i] -> j -> pos
				{	 //hence i need to find the pos of the edge (j,i) in cp[j] -> i, hence the position of i in cp[j], and use it to update cp[i]->j
					cleanPoss[i][jind] = cleanPoss[j][ListUtils.binarySearch(cleanGraph[j], i)];
				}
			}
		}

		int ii, jj, pi, pj;
		System.out.println("Edges: "+edgeis.size());
		for(i = 0; i < edgeis.size(); i++)
		{
			ii = edgeis.get(i);
			jj = edgejs.get(i);
			pi = cleanPoss[ii][ListUtils.binarySearch(cleanGraph[ii], jj)];
			pj = cleanPoss[jj][ListUtils.binarySearch(cleanGraph[jj], ii)];
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
			
			IntOpenHashSet P = new IntOpenHashSet();
			
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
			
			for(int x : graph.neighbors(min))
			{
				if(graph.areNeighbors(max, x)) P.add(x);
			}
			
			//if(P.nextSetBit(0) >= seli || P.nextSetBit(0) >= selj) return; //cutting: finding only cliques in order
			
			IntOpenHashSet C = new IntOpenHashSet();
			
			C.add(seli);
			C.add(selj);
			

			expand(C,P);//TODO: Augment P with clean/semiclean values
			
			selectEdgeToExpand();
		}
		

		
	}

	public void expand(IntOpenHashSet C, IntOpenHashSet P)
	{
		nodes++;
		int cval, scval;
		
		if(P.isEmpty())
		{
			report(C);
			return;
		}
		
		int max=-1, maxClean=-1, maxSClean=-1,
			maxVal=-1,maxCVal=-1,maxSCVal=-1;
		
		for(int i : P)
		{
			cval = scval = 0;

			long ttemp = System.currentTimeMillis();//TODO
			int[] cleanNeighs = cleanGraph[i];
			reptime += (System.currentTimeMillis()-ttemp);//TODO
			if(cleanNeighs != null && cleanNeighs.length > 0)
			{
				for(int cn : cleanNeighs)
				{
					if(cn != -1)
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
			else if(maxSClean ==-1 && maxClean==-1 && graph.neighbors(i).length  >= maxVal) //no clean or semiClean nodes found yet, and current node is dirty
			{
				maxVal = graph.neighbors(i).length;
				max = i; //pivot
			}
		}

		
		if(maxClean != -1)
		{
			max = maxClean;
		}
		else if(maxSClean != -1)
		{
			max = maxSClean;
		}
		else //no clean vertices, breaking maximality constraint and stopping.
		{
			report(C);
			return;
		}
	
		//only retrieving 1 clique, iterating on the pivot
		process(C,P,max);
	}
	
	public void process(IntOpenHashSet C, IntOpenHashSet P, int x)
	{
		C.add(x);
		
		IntOpenHashSet newP = new IntOpenHashSet(P.size());
		
		for(int n : graph.neighbors(x))
		{
			if(P.contains(n)) newP.add(n);
		}
		
		if(newP.isEmpty())
		{
			report(C);
		}
		else
		{
			expand(C, newP);
		}
		//C.clear(x);
	}
	
	/**
	 * Adds C to the solution and marks all edges in C as dirty.
	 */
	public void report(IntOpenHashSet C)
	{
		if(cliques%interval == 0){ System.out.println(cliques+" cliques found"); if(interval < 50000) interval = interval*2; else interval = 100000;}
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
				if(i < j && ListUtils.binarySearch(cleanGraph[i],j) >= 0) //remove the edge from the clean part of the edge list
				{
					pos = edgepos(i, j);
					
					if(edgeis.get(pos) != i || edgejs.get(pos) != j){
						System.out.println("Also, inverted pos (j->i) was: "+edgepos(j,i)+" instead of "+edgepos(i,j));
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
					
					
					if(lastCleanEdge != pos) //if the edge removed is the last clean, no moving is necessary
					{
						setpos(edgeis.get(pos),edgejs.get(pos),pos); //updating indices of the edge that was swapped down
						setpos(edgejs.get(pos),edgeis.get(pos),pos);
					}

					
					//removing the edge from the clean graph
					removeEdge(i, j);
					removeEdge(j, i);
					

					lastCleanEdge--;
					
				}
//				s.remove(j); //remove all nodes in C from its clean neighbors (i.e. mark the edges as dirty)
			}
//			if(s.isEmpty()) cleanGraph.remove(i);
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
		if(true) throw new RuntimeException("selectEdgeToExpandMaxUncoveredDeg not implemented for small footprint graph");
		int maxV = Integer.MIN_VALUE;

		seli = selj = -1;
		int i,j;
		
		for(int e = 0; e <= lastCleanEdge; e++)
		{
			i = edgeis.get(e);
			j = edgejs.get(e);
			if(false)//cleanGraph.get(i).keySet().size() > maxV)
			{
				seli = j;
				selj = i;
//				maxV = cleanGraph.get(i).keySet().size();
			}

			if(false)//cleanGraph.get(j).keySet().size() > maxV)
			{
				seli = i;
				selj = j;
//				maxV = cleanGraph.get(j).keySet().size();
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
		if(true) throw new RuntimeException("selectEdgeToExpandMaxUncoveredDeg not implemented for small footprint graph");
		int minV = Integer.MAX_VALUE;

		seli = selj = -1;

		for(int i :graph.vertices())
		{
			IntSet cleanNeighs = null;//cleanGraph.get(i).keySet();

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

	protected int edgepos(int i , int j)
	{
		return cleanPoss[i][ListUtils.binarySearch(cleanGraph[i], j)];
	}
	
	protected void setpos(int i , int j, int pos)
	{
		try{
			cleanPoss[i][ListUtils.binarySearch(cleanGraph[i], j)] = pos;
		} catch (Exception e)
		{
			System.out.println("i = "+i+", j= "+j);
			e.printStackTrace();
			System.exit(0);
		}
	}

	protected void removeEdge(int i, int j)
	{
		for(int ix = ListUtils.binarySearch(cleanGraph[i],j) -1; ix >= 0; ix--)
		{
			cleanGraph[i][ix+1] = cleanGraph[i][ix];
			cleanPoss[i][ix+1] = cleanPoss[i][ix];
			if(cleanGraph[i][ix] == -1) break;
		}
		if(cleanGraph[i][0] != -1)
		{
			cleanGraph[i][0] = -1;
			cleanPoss[i][0] = -1;
		}
	}

}