package it.unipi.di.ecc.utils;

import it.unipi.di.ecc.interfaces.IntGraph;
import it.unipi.di.ecc.utils.graph.SmallIntGraph;
import it.unipi.di.ecc.utils.graph.SparseBKGraph;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DegeneracyUtils {
	
	static int[] degenOrd;
	static int degeneracy;
	
	static int[] position;
	static int[] degree;
	static int maxdegree;
	static int maxlabel;
	
	
	public static void main(String[] args){
		
		args= new String[]{"C:/graphs/lasagne/email-Enron.nde"};
		
		if(args.length != 1) {
			System.out.println("USAGE: java -jar xxx.jar graphfile.nde");
			System.exit(0);
		}
		
		Parser.setParser("nde");
		
		SmallIntGraph g = null;
		
		try{
			Parser.getSmallIntGraph(new FileInputStream(args[0]));
		} catch (Exception e){
			return;
		}
		
		degeneracyOrdering(g);
		
		System.out.println("Graph "+args[0]+" has degeneracy "+degeneracy+"\n");
		
		return;
	}
		
	public static int[] degeneracyOrdering(SmallIntGraph g) {
		
		degenOrd = null;
		degeneracy = -1;
		
		int[] nodes = g.vertices();

		//n° of neighbors -> nodes with such number
		Map<Integer,List<Integer>> deg2nodes = new HashMap<Integer, List<Integer>>();

		maxdegree = 0;		
		maxlabel = 0;

		for(int i : nodes){
			//cheching how many neighbors the node has
			int num = g.cardinality(i);

			if(num>maxdegree){maxdegree = num;}
			if(i > maxlabel) maxlabel = i;

			if(deg2nodes.get(num) == null){
				deg2nodes.put(num, new ArrayList<Integer>());
			}
			deg2nodes.get(num).add(i);
		}
		if(deg2nodes.isEmpty()){
			System.out.println("error during degeneracy ordering computation or empty graph");
			System.exit(1);
		}


		degenOrd = new int[nodes.length];
		position = new int[maxlabel+1];
		degree = new int[maxlabel+1];
		
		int i = 0;
		
		for(int d = 0; d<=maxdegree; d++){
			
			List<Integer> bucket = deg2nodes.get(d);
			if(bucket != null && !bucket.isEmpty()){
				for(int n : bucket){
					degenOrd[i] = n; //here the ordering is initiated as a simple crescent degree ordering
					position[n] = i;
					degree[n] = d;
					i++;
				}
			}
		}
		
		
		for(i = 0; i<degenOrd.length; i++){ //this will tweak the list to transform the degree ordering in a degeneracy ordering
			int node = degenOrd[i];
			
			if (degeneracy < degree[node]) degeneracy = degree[node];
			degree[node] = -1; //this node is now fixed in the degeneracy ordering.
			
			for(int n : g.neighbors(node)){
				degree[n]--;
				
				int p1 = position[n];
				int p2 = p1-1;
				
				while(p2 > i && degree[degenOrd[p2]] > degree[degenOrd[p1]]){
					swapPos(p1,p2);
					p1--;
					p2--;
				}
			}
		}
		
		return degenOrd;
	}

	
	public static int[] degeneracyOrdering(IntGraph g) {
		
		degenOrd = null;
		degeneracy = -1;
		
		List<Integer> nodes = g.vertices();

		//n° of neighbors -> nodes with such number
		Map<Integer,List<Integer>> deg2nodes = new HashMap<Integer, List<Integer>>();

		maxdegree = 0;		
		maxlabel = 0;

		for(int i : nodes){
			//cheching how many neighbors the node has
			int num = g.cardinality(i);

			if(num>maxdegree){maxdegree = num;}
			if(i > maxlabel) maxlabel = i;

			if(deg2nodes.get(num) == null){
				deg2nodes.put(num, new ArrayList<Integer>());
			}
			deg2nodes.get(num).add(i);
		}
		if(deg2nodes.isEmpty()){
			System.out.println("error during degeneracy ordering computation or empty graph");
			System.exit(1);
		}


		degenOrd = new int[nodes.size()];
		position = new int[maxlabel+1];
		degree = new int[maxlabel+1];
		
		int i = 0;
		
		for(int d = 0; d<=maxdegree; d++){
			
			List<Integer> bucket = deg2nodes.get(d);
			if(bucket != null && !bucket.isEmpty()){
				for(int n : bucket){
					degenOrd[i] = n; //here the ordering is initiated as a simple crescent degree ordering
					position[n] = i;
					degree[n] = d;
					i++;
				}
			}
		}
		
		
		for(i = 0; i<degenOrd.length; i++){ //this will tweak the list to transform the degree ordering in a degeneracy ordering
			int node = degenOrd[i];
			
			if (degeneracy < degree[node]) degeneracy = degree[node];
			degree[node] = -1; //this node is now fixed in the degeneracy ordering.
			
			for(int n : g.neighbors(node)){
				degree[n]--;
				
				int p1 = position[n];
				int p2 = p1-1;
				
				while(p2 > i && degree[degenOrd[p2]] > degree[degenOrd[p1]]){
					swapPos(p1,p2);
					p1--;
					p2--;
				}
			}
		}
		
		return degenOrd;
	}

	
	private static void swap(int a, int b){
		int p1 = position[a];
		int p2 = position[b];
		
		degenOrd[p2] = a;
		degenOrd[p1] = b;
		
		position[a] = p2;
		position[b] = p1;
	}
	
	private static void swapPos(int p1, int p2){
		swap(degenOrd[p1], degenOrd[p2]);
	}
	

	public static int[] degeneracyOrdering(SparseBKGraph g) {
		
//		sg = g;
		sparseGraphDegeneracyOrdering(g);
		
		return degenOrd;
	}
	

	public static void sparseGraphDegeneracyOrdering(SparseBKGraph sg) {
		
		degenOrd = null;
		degeneracy = -1;
		
		int[] nodes = sg.getVertices();
				
		//n° of neighbors -> nodes with such number
		Map<Integer,List<Integer>> neighbors = new HashMap<Integer, List<Integer>>();
	
		maxdegree = 0;		
		maxlabel = 0;
		
		for(int i : nodes){
			//checking how many neighbors the node has
			int num = sg.neighbors(i).length;
			
			if(num>maxdegree){maxdegree = num;}
			if(i > maxlabel) maxlabel = i;
			
			if(neighbors.get(num) == null){
				neighbors.put(num, new ArrayList<Integer>());
			}
			neighbors.get(num).add(i);
		}
		if(neighbors.isEmpty()){
			System.out.println("error during degeneracy ordering computation or empty graph");
			System.exit(1);
		}
		

		degenOrd = new int[nodes.length];
		position = new int[maxlabel+1];
		degree = new int[maxlabel+1];
		
		int i = 0;
		
		for(int d = 0; d<=maxdegree; d++){
			
			List<Integer> bucket = neighbors.get(d);
			if(bucket != null && !bucket.isEmpty()){
				for(int n : bucket){
					degenOrd[i] = n;
					position[n] = i;
					degree[n] = d;
					i++;
				}
			}
		}
		
		
		for(i = 0; i<degenOrd.length; i++){
			int node = degenOrd[i];
			
			if(degeneracy < degree[node]) degeneracy = degree[node];
			degree[node] = -1; //this node is now fixed in the ordering.
			
			for(int n : sg.neighbors(node)){
				degree[n]--;
				
				int p1 = position[n];
				int p2 = p1-1;
				
				while(p2 > i && degree[degenOrd[p2]] > degree[degenOrd[p1]]){
					swapPos(p1,p2);
					p1--;
					p2--;
				}
			}
		}
		
	}

	
}
