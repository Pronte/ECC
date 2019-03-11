package it.unipi.di.ecc.utils.graph;

import it.unipi.di.ecc.interfaces.IntGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatrixGraph implements IntGraph {

	private boolean[][] matrix;
	private Set<Integer> vertices;
	private List<Integer> verticesList;
	private int[] cardinalities = null;
	
	/**
	 * @deprecated
	 */
	@Deprecated
	public MatrixGraph(boolean[][] matrix){
		this.matrix = matrix;
		vertices = null;
	}
	
	/**
	 * Creates an empty matrix graph of cardinality n
	 * @param n - cardinality of the graph (note that the available labels go from 0 to n-1).
	 */
	public MatrixGraph(int n)
	{
		this.matrix = new boolean[n][n];
		for(int i = 0; i < n; i++)
		{
			Arrays.fill(matrix[i], false);
		}
		vertices = new HashSet<Integer>(n);
	}
	
	public MatrixGraph(BSIntGraph graph) {
		
		
		int length = graph.nodes().length();
		
		matrix = new boolean[length][length];
		vertices = new HashSet<Integer>();
		
		for(int i = 0; i < length; i++)
		{
			for(int j = 0; j < length; j++)
			{
				if(i!=j && graph.areNeighbors(i, j))
					{
						matrix[i][j] = true;
						vertices.add(i);
						vertices.add(j);
					}
			}
		}
		
	}
	@Override
	public boolean areNeighbors(int n1, int n2) {
		return matrix[n1][n2];
	}

	/**
	 * @deprecated as this is an adjacency matrix implementation,
	 * this method runs in O(n). It is suggested to use instead "boolean[] neighborsM(int)", which runs in O(1).
	 */
	@Deprecated
	@Override
	public List<Integer> neighbors(int n){
		List<Integer> neighs = new ArrayList<Integer>();
		for(int i = 0; i<matrix.length; i++){
			if(matrix[n][i]) neighs.add(i);
		}
		return neighs;
	}
	
	public boolean[] neighborsM(int n){
		return matrix[n];
	}

	@Override
	public List<Integer> vertices() {
		if(vertices != null)
		{
			if( verticesList == null )verticesList = new ArrayList<Integer>(vertices);
		}
		else if(verticesList == null)
		{
			verticesList = new ArrayList<Integer>(matrix.length);
			for(int i = 0; i<matrix.length; i++){
				verticesList.add(i);
			}
		}
		return verticesList;
	}
	
	public void setNeighbors(int a, int b)
	{
		matrix[a][b] = true;
		matrix[b][a] = true;

		vertices.add(a);
		vertices.add(b);
	}
	
	@Override
	public int cardinality(int node) {
		if(cardinalities == null)
		{
			cardinalities = new int[matrix.length];
			
			Arrays.fill(cardinalities, 0);
			
			for(int i : vertices())
			{
				for(int j = 0; j < matrix[i].length; j++)
				{
					if(matrix[i][j])
						cardinalities[i]++;
				}
			}
		}
		return cardinalities[node];
	}

	
	public boolean[][] getMatrix(){
		return this.matrix;
	}
	
}
