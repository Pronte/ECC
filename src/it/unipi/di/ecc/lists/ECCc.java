package it.unipi.di.ecc.lists;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ECCc extends ECC1 {
	
	public ECCc(Graph graph) {
		super(graph);
	}
	public ECCc(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	protected int pMaxVal;

	@Override
	public void start() {
		
		solution = new ArrayList<IntOpenHashSet>();

		expand0();
		
		if(checkSolution) checkSolution();
	}
	
	@Override
	public void expand0()
	{
		int maxlabel, position;
//		cleanGraph = new HashMap<Integer,IntOpenHashSet>();
		List<Integer> vertices = graph.vertices();
		maxlabel = 0;
		for(int i : vertices) maxlabel = Math.max(i, maxlabel);
		
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
//			if(i != pi || i != pj) //debug
//			{
//				System.out.println("In pos "+i+" found edge ("+ii+","+jj+") but it was expected to be in pos ["+pi+"] or ["+pj+"]");
//				System.out.println("Pos "+pi+" has edge ("+edgeis.get(pi)+","+edgejs.get(pi)+")");
//				System.out.println("Pos "+pj+" has edge ("+edgeis.get(pj)+","+edgejs.get(pj)+")");
//			}
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
}