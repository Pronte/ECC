package it.unipi.di.ecc.utils.parser;

import it.unipi.di.ecc.interfaces.IntGraph;
import it.unipi.di.ecc.utils.graph.BSIntGraph;
import it.unipi.di.ecc.utils.graph.MatrixGraph;
import it.unipi.di.ecc.utils.graph.SmallIntGraph;
import it.unipi.di.ecc.utils.graph.SparseBKDoubleGraph;
import it.unipi.di.ecc.utils.graph.SparseBKGraph;
import it.unipi.di.ecc.utils.graph.SparseMatrixListGraph;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ParserImpl {
	

		public IntGraph getIntGraph(InputStream is);
		
		public BSIntGraph getBitSetIntGraph(InputStream is);
		
		public Map<Integer, List<Integer>> getMapListGraph(InputStream is);

		public SparseBKGraph getSparseGraph(InputStream is);

		public MatrixGraph getMatrixGraph(InputStream is);

		public SparseBKDoubleGraph getSparseDoubleGraph(InputStream is);

		public SparseMatrixListGraph getSparseMatrixListGraph(InputStream is);
		
		public SmallIntGraph getSmallIntGraph(InputStream is);
}
