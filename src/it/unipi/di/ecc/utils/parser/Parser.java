package it.unipi.di.ecc.utils.parser;

import it.unipi.di.ecc.interfaces.Graph;
import it.unipi.di.ecc.interfaces.IntGraph;
import it.unipi.di.ecc.utils.graph.BSIntGraph;
import it.unipi.di.ecc.utils.graph.ListGraph;
import it.unipi.di.ecc.utils.graph.MatrixGraph;
import it.unipi.di.ecc.utils.graph.SmallIntGraph;
import it.unipi.di.ecc.utils.graph.SparseBKDoubleGraph;
import it.unipi.di.ecc.utils.graph.SparseBKGraph;
import it.unipi.di.ecc.utils.graph.SparseMatrixListGraph;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Parser {
	
		public static boolean DEBUG = (System.getProperty("DEBUG") != null);
	
		private static String parser_nm = "dimacs";

		private static ParserImpl parser = null;

		public static ParserImpl getParser(){
			
			if(parser_nm.equalsIgnoreCase("nde")){
				if(DEBUG) System.out.println("Parsing: nde");
				parser = new NDEParser();
			}  else if(parser_nm.equalsIgnoreCase("ascii") || parser_nm.equalsIgnoreCase("aa")){
				if(DEBUG) System.out.println("Parsing: asciiarcs");
				parser = new ASCIIARCSParser();
			} else {
				System.out.println("Format \""+parser_nm+"\" not recognized, trying aa (edge list)");
				parser = new ASCIIARCSParser();
			}
			return parser;
		}
		
		public static void setParser(String nm){
			if(nm.equalsIgnoreCase("default")) parser_nm = "dimacs";
			parser_nm = nm;
		}
		
		public static IntGraph getIntGraph(InputStream is){
			return getParser().getIntGraph(is);
		}

		public static BSIntGraph getBitSetIntGraph(InputStream is){
			if(DEBUG) System.out.println("Getting the graph..");
			return getParser().getBitSetIntGraph(is);
		}

		public static Map<Integer,List<Integer>> getMapListGraph(InputStream is){
			return getParser().getMapListGraph(is);			
		}

		public static SparseBKGraph getSparseGraph(InputStream is) {
			return getParser().getSparseGraph(is);
		}
		public static SparseBKDoubleGraph getSparseDoubleGraph(InputStream is) {
			return getParser().getSparseDoubleGraph(is);
		}
		
		public static MatrixGraph getMatrixGraph(InputStream is) {
			return getParser().getMatrixGraph(is);
		}

		public static SparseMatrixListGraph getSparseMatrixListGraph(InputStream is) {
			return getParser().getSparseMatrixListGraph(is);
		}

		public static SmallIntGraph getSmallIntGraph(InputStream is)
		{
			return getParser().getSmallIntGraph(is);
		}

		public static Graph getListGraph(InputStream is) { 
			return (Graph) new ListGraph(getParser().getMapListGraph(is));
		}
		

}
