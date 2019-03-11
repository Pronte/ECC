package it.unipi.di.ecc.lists;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Gramm extends ECC1 {

//	protected Map<Integer,Map<Integer,Integer>> priorityGraph;
	
	protected List<IntOpenHashSet> clqs;
	protected int k;
	
	protected List<IntOpenHashSet> S;
	protected List<IntList> I, Ivals;
	public Gramm(Graph graph) {
		super(graph);
	}
	public Gramm(InputStream is)
	{
		this(Parser.getListGraph(is));
	}
	
	@Override
	public Object solution() {
		return this.solution;
	}
	@Override
	public void expand0() {
		
		boolean printclqs = false;
		DEBUG = false;
		
//		printclqs = true;
		
		if(DEBUG) System.out.println("[Gramm] Init..");

		List<Integer> vertices = graph.vertices();
		Collections.sort(vertices);

		int maxLabel = vertices.get(vertices.size()-1);
		
		for(int i = 0; i < vertices.size()-1; i++)
		{
			if(vertices.get(i) > maxLabel)
			{
				maxLabel = vertices.get(i);
				if(DEBUG) System.out.println("Maxlabel improved.. is this an error?");
			}
			if(vertices.get(i) >= vertices.get(i+1)) throw new RuntimeException(i+"th vertex ("+vertices.get(i)+") greater than "+(i+1)+"th ("+vertices.get(i+1)+")!");
		}

		S = new ArrayList<IntOpenHashSet>(maxLabel); //S[i] -> tutte le cliques che possono essere estese con i ( 1 <= i <= n)
		I = new ArrayList<IntList>(maxLabel); //I[i] -> tutte le cliques che hanno intersezione non zero con N<i (i vicini di i minori di i stesso)
		//I[i] è ordinato "descending according to |Cl \cap N<i|" (i.e. il primo elemento l di I[i] (I[i][0]) massimizza l'intersezione tra N<i e clqs.getInt(l))
		
		Ivals = new ArrayList<IntList>(maxLabel); //Ivals[i][j] -> il valore dell'intersezione tra la clique l = I[i][j] e N<i  
		
		for(int i = 0; i <= maxLabel; i++){
			S.add(new IntOpenHashSet());
			I.add(new IntArrayList());
			Ivals.add(new IntArrayList());
		}
		//TODO fill S and I
		
		if(DEBUG) System.out.println("[Gramm] maxlabel = "+maxLabel);
		if(DEBUG) System.out.println("[Gramm] init done.");
		
		clqs = new ArrayList<IntOpenHashSet>();
		k = -1;
		
		for(int ind = 0; ind < graph.vertices().size(); ind++)
		{
			int i = graph.vertices().get(ind); //enforcing i to be the > than all visited and < than all non visited.
			
//			if(i == 10) DEBUG = true;
//			else DEBUG = false;
			
			if(DEBUG) System.out.println("----------Visiting "+i);
			//if(i > 4 ) System.exit(0);
			List<Integer> neighs = graph.neighbors(i);
			IntOpenHashSet w = new IntOpenHashSet(neighs.size());
			
			//W <- {j | j < i and {i,j} \in E}
			for(int ii = 0; ii<neighs.size(); ii++ ) //assuming neighs to be ordered
			{
				if(neighs.get(ii) < i) w.add(neighs.get(ii)); //all edges from i to nodes smaller than i 
				//else break;
				
				if(ii > 0 && neighs.get(ii) <= neighs.get(ii-1)) throw new RuntimeException("Neighbors of "+i+" NOT ORDERED! ("+neighs.get(ii-1)+" precedes "+neighs.get(ii)+")");
			}
			if(DEBUG) System.out.println("i: "+i+" , w: "+w.toString());
			if(w.isEmpty()) //if the new node is not connected to any previous node, make it into a new clique of size 1
			{
				k++;
				add_to_clique(k,i);
				//TODO new_clq() instead;
				
//				IntOpenHashSet newClq = new IntOpenHashSet();
//				newClq.add(i);
//				clqs.add(newClq);
//				newclq();
			}
			else //try to augment existing cliques with i
			{
				IntOpenHashSet u = new IntOpenHashSet();
				
				IntOpenHashSet currentS = S.get(i);
				IntOpenHashSet toremove = new IntOpenHashSet();
				for(int l : currentS) //for all cliques that can be extended with i (TODO you can remove iteratively instead of iterating (see add_to_clique pseudocode)
				{
					IntOpenHashSet cl = clqs.get(l);
					if(!u.containsAll(cl))
					{
						if(DEBUG) System.out.println("Augmenting "+cl.toString()+" with "+i+" and adding it to u.");
						u.addAll(cl);
						//toremove.add(l); //TODO correct? to prevent add_to_clique() from modifying currentS while i am iterating over it!
						add_to_clique(l,i);
					

						
						if(u.size() >= w.size())
						{
							if(u.equals(w))
							{
								if(DEBUG) System.out.println("U == W ! breaking (U: "+u.toString()+" W: "+w.toString()+")");
								break;
							}
							else throw new RuntimeException("Error! |U|>=|W| but they are not the same.\nU: "+u.toString()+"\nW: "+w.toString()+"\n");
						}
					}
				}
				if(DEBUG) System.out.println("Removing "+toremove+" from S["+i+"].");
				
				updateI(i,w);
				
				//currentS.removeAll(toremove); //TODO correct? to prevent add_to_clique() from modifying currentS while i am iterating over it!
				
				w.removeAll(u); //not possible anymore to augment cliques completely
								//now we cover the remaining edges by duplicating parts of cliques and adding i to them

				toremove = new IntOpenHashSet();
				while(!w.isEmpty()) //TODO does I getInt updated? or do i always chose the same I.getInt(i).getInt(0)?
				{
//					int l = I.getInt(i).getInt(0); //TODO correct??   //l <- min{I[i]}
					int l = I.get(i).remove(0); //TODO correct??   //l <- min{I[i]}
							Ivals.get(i).remove(0);
					k++;
					
					IntOpenHashSet cl = clqs.get(l);
					if(DEBUG) System.out.println("Duplicating a part of "+cl+" to cover more of W "+w);
					//for(q : (Cl \cap w)\cup{i}) add_to_clique(k,q);
					

					//TODO new_clq() instead
					add_to_clique(k,i);
					
					
					for(int q : cl)
					{
						if(w.contains(q)){
							toremove.add(q);
							add_to_clique(k,q);
							if(DEBUG) System.out.println("Augmented clique "+k+" with "+q);
						}
					}
//					toremove.add(i);
					
//					S.getInt(i).removeAll(toremove);
					
					
					//int pos = I.getInt(i).indexOf(l);
					
//					if(pos >= 0)
//					{
//						IntList Ii = I.getInt(i);
//						IntList Ivi = Ivals.getInt(i);
//						int lval = Ivi.getInt(pos);
//						while(pos < Ii.size()-1 && Ivi.getInt(pos) < Ivi.getInt(pos+1))
//						{
//							Ii;
//						}
//						System.out.print("Removing I["+i+"]["+pos+"]: "+I.getInt(i).remove(pos)+", val="); //TODO error here? (without it it doesn't work).
//						System.out.println(Ivals.getInt(i).remove(pos));
//						System.out.println("I["+i+"]  "+I.getInt(i));
//						System.out.println("Iv["+i+"] "+Ivals.getInt(i));
//					}
					//removing the clique just 
//					I.getInt(i).remove(0);
//					Ivals.getInt(i).remove(0);
					
					//W <- W \ Cl
					for(int q : cl)
					{
						w.remove(q);
					}
					

					updateI(i,w);
				}
			}
			
			if(DEBUG || printclqs) System.out.print("Cliques so far: ");
			if(DEBUG || printclqs) for(IntOpenHashSet xxx : clqs) System.out.print(xxx.toString()+" ");
			if(DEBUG || printclqs) System.out.println(".");
		}
		
		
		cliques = clqs.size();
		deaths = 0;
		for(IntOpenHashSet cc : clqs) deaths += cc.size();
		
		solution = clqs;
	}
	
	
	private void updateI(int i, IntOpenHashSet w)
	{
		
		IntList Ii = I.get(i);
		IntList Ivi = Ivals.get(i);

		if(DEBUG) System.out.print("Cliques so far: ");
		if(DEBUG) for(IntOpenHashSet xxx : clqs) System.out.print(xxx.toString()+" ");
		if(DEBUG) System.out.println(".");

		if(DEBUG) System.out.println("Updating vals for I["+i+"] (W="+w+"):"); //TODO error here? (without it it doesn't work).
		if(DEBUG) System.out.println("I["+i+"]  "+Ii);
		if(DEBUG) System.out.println("Iv["+i+"] "+Ivi);
		
		int val;
		IntOpenHashSet clq;
		
		for(int ii = 0; ii < Ii.size(); ii++)
		{
//			System.out.print("Clique "+ii+" from val "+Ivi.getInt(ii));
			val = 0;
			clq = clqs.get(Ii.getInt(ii));
			
			for(int x : clq)
			{
				if(w.contains(x)) val++;
			}
//			System.out.println(" to val "+val);
			Ivi.set(ii, val);
		}
		
		
//		int c;
//		for(int rem : clqs.getInt(k)) //updating I: removing the nodes covered with l from their intersection value (to keep (Cl \cap N<i \cap W) instead of (Cl \cap N<i)
//		{
//			for(int ii=0; ii < Ii.size(); ii++)
//			{
//				c = Ii.getInt(ii);
//				if(clqs.getInt(c).contains(rem))
//				{
//					Ivi.set(ii,Ivi.getInt(ii)-1);
//				}
//			}
//		}
		int pos, tmp, ci, cv;
		
		for(pos = Ivi.size()-1; pos >= 0; pos--)
		{
			if(Ivi.getInt(pos) <= 0)
			{
				Ii.remove(pos);
				Ivi.remove(pos);
			}
		}
		if(Ii.size() != Ivi.size()) throw new IllegalStateException("I and Ivals of node "+i+" differ in size! ("+Ii.size()+","+Ivi.size()+")");
//		System.out.println("Updated2 vals for I["+i+"]:"); //TODO error here? (without it it doesn't work).
//		System.out.println("I["+i+"]  "+Ii);
//		System.out.println("Iv["+i+"] "+Ivi);
		for(pos=0; pos < Ii.size()-1; pos++)
		{
			if(Ivi.size() > 0 && Ivi.getInt(pos) < Ivi.getInt(pos+1))
			{
				for(tmp = pos+1; tmp > 0; tmp--)
				{
					if(Ivi.getInt(tmp-1) < Ivi.getInt(tmp))
					{//swap
						ci = Ii.getInt(tmp);
						cv = Ivi.getInt(tmp);

						Ii.set(tmp, Ii.getInt(tmp-1));
						Ivi.set(tmp, Ivi.getInt(tmp-1));

						Ii.set(tmp-1, ci);
						Ivi.set(tmp-1, cv);
					}
					else break;
				}
			}
		}
		if(DEBUG) System.out.println("Updated I["+i+"]:"); //TODO error here? (without it it doesn't work).
		if(DEBUG) System.out.println("I["+i+"]  "+Ii);
		if(DEBUG) System.out.println("Iv["+i+"] "+Ivi);
		
	}
	
	private void add_to_clique(int cli, int v)
	{
		if(DEBUG){
			try {Thread.sleep(50);}
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		
		if(cli == clqs.size() /*clqs.getInt(cli) == null*/){
			clqs.add(new IntOpenHashSet());
			newclq(cli, v);
		} else if( cli > clqs.size())
		{
			throw new RuntimeException("Error! trying to add a new clique ("+v+") in pos "+cli+" but its being added in "+(clqs.size()-1)+" instead.");
		}

		IntOpenHashSet cl = clqs.get(cli);
		
		if(DEBUG) if(!cl.isEmpty()) System.out.println("Adding "+v+" to clique "+cli+"-"+cl);
		
		if(cl == null) throw new RuntimeException("Error! trying to add a new clique ("+v+") in pos "+cli+" but its being added in "+(clqs.size()-1)+" instead.");
		
		if(!cl.isEmpty()) //Updating S[]
		{
			for(int j : graph.neighbors(cl.iterator().nextInt())) //TODO iterating over first element, maybe iterating over random is better.
			{	
				if(S.get(j).contains(cli) && v!=j && (v > j || !graph.areNeighbors(v, j) )) //the case v==j getInts dealt with in the parent.
				{
					S.get(j).remove(cli);
				}
			}
		}
		
		//updating I[]
		for(int j : graph.neighbors(v))
		{
			if(j>v) //j in N>(v)
			{
				int pos, clval;
				IntList Ij = I.get(j), Ivj = Ivals.get(j);
				
//				for(pos=0; pos < Ij.size(); pos++)
//				{
//					if(Ij.getInt(pos) == cli) break;
//				}
				pos = Ij.indexOf(cli);

				//if(DEBUG) System.out.print("Updating "+cli+" in I["+j+"], from pos="+pos+" ");
				//if(pos < 0 || pos >= Ij.size()) throw new RuntimeException("Error! I.getInt("+j+") (v="+v+") didn't contain cli "+cli+"."); //actually possible when adding new clique, correct
				
				
				if(pos < 0)
				{
					Ij.add(cli); //the clique was not there, so it can now be added with value 1 as the intersection is {v}
					Ivj.add(1);
//					if(DEBUG) System.out.println("to pos="+(Ij.size()-1)+" (insertion)");
					if(Ij.size() != Ivj.size()) throw new IllegalStateException("I and Ivals of node "+j+" differ in size! ("+Ij.size()+","+Ivj.size()+")");
				}
				else 
				{
					clval = Ivj.getInt(pos)+1;
					Ivj.set(pos,clval);
					
					for(/*pos = pos*/; pos > 0; pos--)
					{
						if(Ivj.getInt(pos) > Ivj.getInt(pos-1)) //bubble-sort-style advancing the updated element (could be improved with a single swap, since the value is only increased by 1 but would be still O(|Ij|) and it would not guarantee a stable ordering)
						{
							Ij.set(pos, Ij.getInt(pos-1));
							Ivj.set(pos, Ivj.getInt(pos-1));
							
							Ij.set(pos-1, cli);
							Ivj.set(pos-1, clval);
						}
						else break;
					}
//					if(DEBUG) System.out.println("to pos="+pos);
//					if(DEBUG) System.out.println("I[j]:  "+Ij.toString());
//					if(DEBUG) System.out.println("Iv[j]: "+Ivj.toString());
				}
			}
		}
		
		cl.add(v);
		
//		throw new RuntimeException("Operation add_to_clique() not implemented");
	}

	
	private void newclq(int cli, int v)
	{
		cliques++;
//		System.out.println(cliques+" cliques");
		if(cliques % 10000 == 0) System.out.println("New clique: #"+cli+" = {"+v+"} ("+cliques+" cliques so far)");
		
		if(DEBUG) System.out.println("New clique: #"+cli+" = {"+v+"} ("+cliques+" cliques)");
		
		//S[i] - all cliques that can be extended with i
		//for x : N>(v), S[x] must getInt cli 

		//I[i] - all cliques with nonzero intersection with N<(i)
		//for x : N>(v), I[x] must getInt cli (nonzero intersection is the same as containing all of the clique when the clique size is 1)
		
		for(int x : graph.neighbors(v))
		{
			if(x>v)
			{
//				if(DEBUG) System.out.println("Adding clique "+cli+" (v="+v+") to S and I["+x+"].");
				S.get(x).add(cli);
				
				//clearly cli can be inserted in the tail of I and Ivals since it has value 1, which is the minimum possible value.
				//TODO redundant?
//				I.getInt(x).add(cli);
//				Ivals.getInt(x).add(1);
			}
		}
		
		
	}
}
