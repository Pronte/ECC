package it.unipi.di.ecc.utils.parser;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.io.FastBufferedReader;
import it.unipi.di.ecc.interfaces.IntGraph;
import it.unipi.di.ecc.utils.ListUtils;
import it.unipi.di.ecc.utils.graph.BSIntGraph;
import it.unipi.di.ecc.utils.graph.MapListGraph;
import it.unipi.di.ecc.utils.graph.MatrixGraph;
import it.unipi.di.ecc.utils.graph.SmallIntGraph;
import it.unipi.di.ecc.utils.graph.SparseBKDoubleGraph;
import it.unipi.di.ecc.utils.graph.SparseBKGraph;
import it.unipi.di.ecc.utils.graph.SparseMatrixListGraph;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ASCIIARCSParser implements ParserImpl {
	
	boolean DEBUG = System.getProperty("DEBUG") != null;
	
	
	public static void save (IntGraph g, PrintWriter out)
	{
		try
		{
			@SuppressWarnings("unused")
			MapListGraph mlg = (MapListGraph) g;
		}
			catch (Exception e)
		{
			System.out.println("Preferred class: MapListGraph. "+g.getClass().getName()+" found instead.");
		}
		
		List<Integer> vertices = g.vertices();
		
		for(int i : vertices)
		{
			List<Integer> neighs = g.neighbors(i);
		
			for(int j : neighs)
			{
				if(j>i) out.println(i+" "+j);
			}
		}
		
		out.close();
	}
	
	
	@Override
	public  IntGraph getIntGraph(InputStream is){
		
		MapListGraph graph = new MapListGraph(this.getMapListGraph(is));
		graph.orderGraph();
		
		return graph;
	}

	@Override
	public  BSIntGraph getBitSetIntGraph(InputStream is){
		System.out.println("getting bsintgraph");
		Scanner s = new Scanner(is);
		
		BSIntGraph graph = new BSIntGraph();
		
		//String header = s.nextLine();
		String[] line;
		
		int a, b;
		
		//System.out.println("Parsing: "+header);
		
		while(s.hasNextLine()){
			System.out.println("ciao");
			try{
				line = s.nextLine().split(" ");
				
				//line[0] and line[1] are the nodes connected by an edge
	
				a = Integer.parseInt(line[0]);
				b = Integer.parseInt(line[1]);
	
				System.out.println(a+"-"+b);
				
				if(a != b) graph.setNeighbors(a,b);
			} catch (Exception e){
				
			}
		}
		s.close();
		System.out.println("Done, n ="+graph.nodes().cardinality());
		return graph;
	}
	
	@Override
	public  Map<Integer,List<Integer>> getMapListGraph(InputStream is){
		
		Scanner s = new Scanner(is);
		
		
		Map<Integer,List<Integer>> graph = new HashMap<Integer,List<Integer>>();
		
//		String header = s.nextLine();
		String[] line;
		
		int a, b;
		
//		System.out.println("Parsing: "+header);
		
		while(s.hasNextLine()){
			try{
				line = s.nextLine().split(" ");
				
				
				//line[0] is the letter e (for edge)
				//there's an edge between line[1], and line[2]
	
				a = Integer.parseInt(line[0]);
				b = Integer.parseInt(line[1]);
	
				if(graph.get(a) == null){
					graph.put(a, new ArrayList<Integer>());
				}
				if(graph.get(b) == null){
					graph.put(b, new ArrayList<Integer>());
				}
	
				//adding the edge in both directions, avoiding duplicates and loops.
				if(a!=b){
					ListUtils.addOrdered(graph.get(a), b); //method avoids duplicates
					ListUtils.addOrdered(graph.get(b), a);
//					graph.get(a).add(b);
//					graph.get(b).add(a);
				}	
			} catch (Exception e){
				
			}	
		}
		s.close();
		return graph;
	}
	
	@Override
	public SparseBKGraph getSparseGraph(InputStream is) {
		return new SparseBKGraph(getMapListGraph(is));
	}

	@Override
	public MatrixGraph getMatrixGraph(InputStream is) {
		//Scanner s = new Scanner(is);

//		String header = s.nextLine();
		
//		System.out.println("Header: "+header);
		
//		String ns = header.split(" ")[2]; // header: "p #n #m"
		
//		int n = Integer.parseInt(ns);
		
		//calculating max label for matrix initialization:
		
		return new MatrixGraph(this.getBitSetIntGraph(is));
		
		/*
		MatrixGraph g = new MatrixGraph(n+1);
		
		
		String[] line;
		
		int a = -1, b = -1;
		
		while(s.hasNextLine()){
			try{
				line = s.nextLine().split(" ");
				
				//line[0] is the letter e (for edge)
				//there's an edge between line[1], and line[2]
	
				a = Integer.parseInt(line[1]);
				b = Integer.parseInt(line[2]);
	
				g.setNeighbors(a, b);
				
			} catch (Exception e){
				System.out.println("woops! could not add the edge ("+a+","+b+",)");
			}
		}
		
		return g;
		
		*/
	}

	@Override
	public SparseBKDoubleGraph getSparseDoubleGraph(InputStream is) {
		return new SparseBKDoubleGraph(this.getMapListGraph(is));
	}

	@Override
	public SparseMatrixListGraph getSparseMatrixListGraph(InputStream is) {
		return new SparseMatrixListGraph(this.getMapListGraph(is));
	}



	@Override
	public SmallIntGraph getSmallIntGraph(InputStream is) {

		long t0 = System.currentTimeMillis();
		
		FastBufferedReader fr = new FastBufferedReader(new InputStreamReader(is));

		
		int a = -1, b = -1;

		if(DEBUG) System.out.println("Reading graph from file..");
		
		int i = 0, maxl = 0;
		
		Map<Integer, int[]> n2neighs = new HashMap<Integer,int[]>();
		IntOpenHashSet allnodes = new IntOpenHashSet();
		
		
		int li = 0;
		while(true){
			try{
				
				//line[0] is the letter e (for edge)
				//there's an edge between line[1], and line[2]
	
				a = nextInt(fr);
				b = nextInt(fr);

				
				if(a == -1 || b == -1)
				{
					if(a != b){if(DEBUG) System.out.println("Error! read line: ("+a+","+b+")");}
					break;
				}

				int[] nA = null, nB = null;

				if(allnodes.add(a)){
					nA = emptyadjacency();
					n2neighs.put(a, nA);
					maxl = Math.max(maxl, a);
				} else { nA = n2neighs.get(a); }

				if(allnodes.add(b)){
					nB = emptyadjacency();
					n2neighs.put(b, nB);
					maxl = Math.max(maxl, b);
				} else { nB = n2neighs.get(b); }

				if(nA[0] >= nA.length){ nA = enlarge(nA);  n2neighs.put(a, nA);};
				if(nB[0] >= nB.length){ nB = enlarge(nB);  n2neighs.put(b, nB);};
				
				insert(nA,b);
				insert(nB,a);
				
			}
			catch (Exception e){
				e.printStackTrace();
				System.exit(0);
			}	
			
			li++;
			if(DEBUG && li%1000000 == 0) System.out.println(li+" lines done");
		}
		
		try {
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		allnodes = null;
		
		int nodes = maxl;
		
		SmallIntGraph g = new SmallIntGraph(nodes+1);
		
		for(int n = 0; n <= maxl; n++){
			if( n2neighs.containsKey(n)){
				g.setNeighbors(n2neighs.get(n), n);
			}
		}
		
		
		if(DEBUG) System.out.println("Uniquifying and sorting.................");
		
		li = 0;
		int voids = 0, x;
		for(i = 0; i < g.vertices().length; i++)
		{
			x = g.vertices()[i];
			
			if(x != -1){
				if(g.neighbors(x) == null || g.neighbors(x).length == 0)
				{
					g.vertices()[i] = -1;
					voids++;
				}else
				{
					g.setNeighbors(fixneighbors(g.neighbors(x)), x);
				}
			} else
			{voids++;}
			
			li++;
			if(li%1000000 == 0) System.out.println(li+" nodes done");
		}
	
		if(voids > 0)
		{
			if(DEBUG) System.out.println("there were "+voids+" missing nodes.");
			int[] newverts = new int[g.vertices().length -voids];
			int el = 0;
			for(i = 0; i < newverts.length; i++)
			{
				while(g.vertices()[el] == -1) el++;
				newverts[i] = g.vertices()[el];
				el++;
			}
			g.setVertices(newverts);
		}
		
		Arrays.sort(g.vertices());
		
		if(DEBUG) System.out.println("Collecting garbage..");
		
		System.gc();
		
		if(DEBUG) System.out.println("Done.");
		
		return g;
	}

	//vec[0] : first empty index of the list. empty indices contain -1 
	private static int[] emptyadjacency(){

		int[] vec = new int[5];
		Arrays.fill(vec, -1);
		vec[0] = 1;
		return vec;
	}
	
	//l: [1st unfilled index][filled elements][-1s]
	private static void insert (int[] l, int x)
	{
		if(l[0] >= l.length){
			System.out.println("Error! no elements left in g["+x+"] : "+Arrays.toString(l));
			return;
		}
		
		l[l[0]] = x;
		l[0]++;
	}
	

	private boolean duplicates = false;
	//l: [1st unfilled index][filled elements][-1s]
	private int[] fixneighbors(int[] l) { //returning a smaller array containing only unique non -1 elements and not containing the first element (the counter)
		
		boolean myduplicates = false;
		
		Arrays.sort(l,1,l[0]); //sorting the good elements
		
		int lg = l[0]-1, i;
		
		for(i = lg-1 ;i >= 1; i--) //removing duplicates from good elements
		{
			if(l[i] == l[i+1] )
			{
				if(!duplicates){myduplicates = true; duplicates = true; if(DEBUG) System.out.println("The graph contains duplicated edges.");}
				l[i+1] = l[lg];
				lg--;
			}
		}
		
		if(myduplicates) Arrays.sort(l,1,lg+1); //sorting the good elements again if necessary
		
		return Arrays.copyOfRange(l, 1, lg+1);
	}
	
	private int[] enlarge(int[] s)
	{
		int[] l = Arrays.copyOf(s, s.length*2); 
		
		for(int i = s.length; i < l.length; i++) l[i] = -1;
		
		return l;
	}
	
	
	private static int nextInt(FastBufferedReader fr) throws IOException {
	    int ret = 0;
	    boolean dig = false;

	    for (int c = fr.read(); c != -1; c = fr.read()) {
	        if (c >= '0' && c <= '9') {
	            dig = true;
	            ret = ret * 10 + c - '0';
	        } else if (dig) return ret;
	    }
	    
	    if(dig) return ret;
	    else return -1;
	}

}
