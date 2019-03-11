package it.unipi.di.ecc.utils.parser;

import it.unimi.dsi.fastutil.ints.IntArrayList;
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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class NDEParser implements ParserImpl {

	boolean DEBUG = System.getProperty("DEBUG") != null;
	
	@Override
	public  IntGraph getIntGraph(InputStream is){
		
		MapListGraph graph = new MapListGraph(this.getMapListGraph(is));
		graph.orderGraph();
		
		return graph;
	}

	@Override
	public  BSIntGraph getBitSetIntGraph(InputStream is){
		if(DEBUG) System.out.println("getting bsintgraph");
		
		Scanner s = new Scanner(is);
		
		
		String header = s.nextLine();
		String[] line;
		
		int a, b;
		
		if(DEBUG) System.out.println("Parsing: "+header);
		
		int nodes = Integer.parseInt(header);
		BSIntGraph graph = new BSIntGraph(nodes);
		
		int i = 0;
		
		while(i++ < nodes) s.nextLine(); //skipping the degree distribution
		
		while(s.hasNextLine()){
			try{
				line = s.nextLine().split(" ");
				
				//line[0] and line[1] are the nodes connected by an edge
	
				a = Integer.parseInt(line[0]);
				b = Integer.parseInt(line[1]);
	
				if(a != b) graph.setNeighbors(a,b);
			} catch (Exception e){
				
			}
		}
		s.close();
		if(DEBUG) System.out.println("Done");
		return graph;
	}
	
	@Override
	public  Map<Integer,List<Integer>> getMapListGraph(InputStream is){
		
		Scanner s = new Scanner(new BufferedInputStream(is, 10000));
		
		Map<Integer,List<Integer>> graph = new HashMap<Integer,List<Integer>>();
		
		int header = s.nextInt();
		String[] line;
		
		int a, b;

		if(DEBUG) System.out.println("Parsing: "+header);
		
		int nodes = header;
				
		int i = 0;
		
		while(i++ < nodes) {s.nextInt(); s.nextInt();} //skipping the degree distribution
		
		
//		Map<Integer,IntOpenHashSet> sgraph = new HashMap<Integer,IntOpenHashSet>();
		
		if(DEBUG) System.out.println("Reading graph from file..");
		int li = 0;
		while(s.hasNextInt()){
			try{
//				line = s.nextLine().split(" ");
				
				
				//line[0] is the letter e (for edge)
				//there's an edge between line[1], and line[2]
	
				a = s.nextInt();
				b = s.nextInt();

//				if(graph.get(a) == null){
//					graph.put(a, new ArrayList<Integer>());
//				}
//				if(graph.get(b) == null){
//					graph.put(b, new ArrayList<Integer>());
//				}
//	
//				//adding the edge in both directions, avoiding duplicates and loops.
//				if(a!=b){
//					ListUtils.addOrdered(graph.get(a), b); //method avoids duplicates
//					ListUtils.addOrdered(graph.get(b), a);
////					graph.get(a).add(b);
////					graph.get(b).add(a);
//				}
//			}
//			catch (Exception e){
//			e.printStackTrace();
//		}

				
				if(graph.get(a) == null){
//					sgraph.put(a, new IntOpenHashSet());
					graph.put(a, new IntArrayList());
				}
				if(graph.get(b) == null){
//					sgraph.put(b, new IntOpenHashSet());
					graph.put(b, new IntArrayList());
				}

				//adding the edge in both directions, avoiding duplicates and loops.
				if(a!=b){
					graph.get(a).add(b);
					graph.get(b).add(a);
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}	
			
			li++;
			if(li%1000000 == 0) if(DEBUG) System.out.println(li+" lines done");
		}
		s.close();
		
		if(DEBUG) System.out.println("Removing duplicates.................");
		
//		li=0;
//		for(int x : sgraph.keySet())
//		{
//			graph.get(x).addAll(sgraph.get(x));
//			
//			sgraph.put(x, null);
//			

//		}
		
		li = 0;
		for(int x : graph.keySet())
		{
			ListUtils.unique((IntArrayList)graph.get(x));
			
			li++;
			if(li%1000000 == 0) if(DEBUG) System.out.println(li+" nodes done");
		}
	
		
		
		if(DEBUG) System.out.println("Done.");
		
		
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
		MatrixGraph g = new MatrixGraph(n);
		
		
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

		FastBufferedReader fr = new FastBufferedReader(new InputStreamReader(is));
		
		
		int header = -1;
		try {
			header = nextInt(fr);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		
		int a = -1, b = -1;
		int[] vec;
		
		if(DEBUG) System.out.println("Parsing: "+header);
		
		int nodes = header;
				
		int i = 0;
		
		SmallIntGraph g = new SmallIntGraph(nodes+1);

		if(DEBUG) System.out.println("Reading graph from file..");
		
		while(i++ < nodes) { //deg dist
			try {
				a = nextInt(fr);
				b = nextInt(fr);
			} catch (IOException e) {

				e.printStackTrace();
			} 
			
			vec = new int[1+2*b];
			Arrays.fill(vec, -1);
			vec[0] = 1;
			
			g.setNeighbors(vec, a);
			}
		
		
		
		int li = 0;
		while(true){
			try{
//				line = s.nextLine().split(" ");
				
				
				//line[0] is the letter e (for edge)
				//there's an edge between line[1], and line[2]
	
				a = nextInt(fr);
				b = nextInt(fr);

				
				if(a == -1 || b == -1)
				{
					if(a != b){System.out.println("Error! read line: ("+a+","+b+")");}
					break;
				}

//				if(g.neighbors(a)[0] >= g.neighbors(a).length) g.setNeighbors(enlarge(g.neighbors(a)), a);
//				if(g.neighbors(b)[0] >= g.neighbors(b).length) g.setNeighbors(enlarge(g.neighbors(b)), b);
				
				insert(g.neighbors(a),b);
				insert(g.neighbors(b),a);
				
			}
			catch (Exception e){
				e.printStackTrace();
				System.exit(0);
			}	
			
			li++;
			if(DEBUG) if(li%1000000 == 0) System.out.println(li+" lines done");
		}
		try {
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
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
			if(DEBUG) if(li%1000000 == 0) System.out.println(li+" nodes done");
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

//	private boolean duplicates = false;
//	//l: [1st unfilled index][filled elements][-1s]
//	private int[] fixneighbors(int[] l) { //returning a smaller array containing only unique non -1 elements and not containing the first element (the counter)
//		
//		
//		Arrays.sort(l,1,l[0]); //sorting the good elements
//		
//		int lg = l[0]-1, i;
//		
//		for(i = lg-1 ;i >= 1; i--) //removing duplicates from good elements
//		{
//			if(l[i] == l[i+1] )
//			{
//				if(!duplicates){duplicates = true; System.out.println("The graph contains duplicated edges.");}
//				l[i+1] = l[lg];
//				lg--;
//			}
//		}
//		
//		Arrays.sort(l,1,lg+1); //sorting the good elements again
//		
//		return Arrays.copyOfRange(l, 1, lg+1);
//	}
	
	

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
