package it.unipi.di.ecc.utils;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoverUtils {

	public static int[] nodesCoveringIndex(List<IntOpenHashSet> clqs)
	{
		int maxlabel = 0;
		
		for(IntOpenHashSet s : clqs)
		{
			for(int i : s)
			{
				if(i > maxlabel) maxlabel = i;
			}
		}
		int[] index = new int[maxlabel+1];
		
		Arrays.fill(index, 0);
		
		for(IntOpenHashSet s : clqs)
		{
			for(int i : s)
			{
				index[i]++;
			}
		}
		
		return index;
	}
	public static int[] edgesCoveringIndexDistribution(List<IntOpenHashSet> clqs)
	{
		int maxlabel = 0;
		
		//edge2weight:   e2w.get(x).get(y) -> weight of edge (x,y) (with x<y!)
		Int2ObjectOpenHashMap<Int2IntOpenHashMap> e2w = new Int2ObjectOpenHashMap<Int2IntOpenHashMap>();
		
		Int2IntOpenHashMap tmp = null;
		int w, maxweight = 0,i,j;
		
		IntIterator out,in;
		
		for(IntOpenHashSet s : clqs)
		{
			out = s.iterator();
			while(out.hasNext())
			{
				i = out.nextInt();
				if(i > maxlabel) maxlabel = i;
				
				in = s.iterator();
				while(in.hasNext())
				{
					j = in.nextInt();
					if(i<j)
					{
						if(!e2w.containsKey(i)) e2w.put(i, new Int2IntOpenHashMap());
						
						tmp = e2w.get(i);
						
						if((!tmp.containsKey(j)) || tmp.get(j) <= 0) w = 1;
						else w = tmp.get(j)+1;
						
						tmp.put(j, w);
						if(w>maxweight) maxweight = w;
					}
				}
			}
		}
		
		
		
		int[] index = new int[maxweight+1];
		Arrays.fill(index, 0);
		Int2IntOpenHashMap m;
		
		for(int x : e2w.keySet())
		{
			m = e2w.get(x); 
			for(int y : m.keySet())
			{
				index[m.get(y)]++;
			}
		}

		return index;
	}
	
	public static int[] getDistribution(int[] values)
	{
		Map<Integer,Integer> dist = new HashMap<Integer,Integer>(); //value2frequency
		int freq, mv = 0;
		
		for(int i : values)
		{
			if(!dist.containsKey(i)) freq = 1;
			else freq = dist.get(i)+1;
				
			if(i>mv) mv = i;
			
			dist.put(i, freq);
		}
		
		int[] distArr = new int[mv+1];
		
		for(int i : dist.keySet())
		{
			distArr[i] = dist.get(i);
		}
		return distArr;
	}
	
	public static int[] getClqDistribution(List<IntOpenHashSet> clqs)
	{

		int msize = 0;
		
		for(IntOpenHashSet b : clqs)
		{
			if (b.size() > msize) msize = b.size();
		}
		
		int[] dist = new int[msize+1];
				
		Arrays.fill(dist, 0);
		
		for(IntOpenHashSet b : clqs)
		{
			dist[b.size()] ++;
		}

		return dist;
	}	
	
	
	public static int[] getClqDistributionbs(List<BitSet> clqs)
	{

		int msize = 0;
		
		for(BitSet b : clqs)
		{
			if (b.cardinality() > msize) msize = b.cardinality();
		}
		
		int[] dist = new int[msize+1];
				
		Arrays.fill(dist, 0);
		
		for(BitSet b : clqs)
		{
			dist[b.cardinality()] ++;
		}

		return dist;
	}
	
	
	/**
	 * @return number of cliques removed
	 */
	public static int minimalize(List<IntOpenHashSet> clqs)
	{
		int maxlabel = 0;
		
		for(IntOpenHashSet s : clqs)
		{
			for(int i : s)
			{
				if(i > maxlabel) maxlabel = i;
			}
		}

		//weights.get(i).get(j) -> weight of the edge (i,j)   only if: i < j
		List<Int2IntOpenHashMap> weights = new ArrayList<Int2IntOpenHashMap>();
		
		for(int i = 0; i <= maxlabel; i++) weights.add(new Int2IntOpenHashMap());
		
		for(IntOpenHashSet s : clqs)
		{
			for(int x : s)
			{
				for(int y : s) //checks all edges x,y such that x<y
				{
					if(x<y)
					{
						if(!weights.get(x).containsKey(y))
						{
							weights.get(x).put(y,1);
						}
						else
						{
							weights.get(x).put(y, weights.get(x).get(y)+1);
						}
					}
				}
			}
		}
			
			//if it is redundant then remove it
			//(this is more efficient than first checking and then adding if it is not redundant as the redundant case is less frequent)   
		boolean redundant = true;
		IntOpenHashSet s;
		List<Integer> del = new ArrayList<Integer>();
		
		for(int i = 0; i < clqs.size(); i++)
		{
			s = clqs.get(i);
			
			redundant = true;
			for(int x : s)
			{
				if(!redundant) break;
				for(int y : s) //checks all edges x,y such that x<y
				{
					if(x<y)
					{
						if(weights.get(x).get(y) <= 1)
						{
							redundant = false;
							break;
						}
					}
				}
			}
			
			if(redundant)
			{
				for(int x : s)
				{
					for(int y : s) //checks all edges x,y such that x<y
					{
						if(x<y)
						{
							weights.get(x).put(y,weights.get(x).get(y)-1);
						}
					}
				}
				del.add(i);
			}
		}
		
		
		//delete redundant cliques
		for(int i = del.size()-1; i>=0; i--)
		{
			clqs.remove((int)del.get(i));
		}
		
		
		return del.size();
	}
	
	
}

