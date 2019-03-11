package it.unipi.di.ecc.utils;

import java.util.HashMap;
import java.util.Map;

public class AlgStats implements Comparable<AlgStats> {
	public Map<String,String> stats = new HashMap<String,String>();
	Object solution = null;
	//	List<int[]> cliques = new ArrayList<int[]>();

	public AlgStats(Map<String, String> map, Object solution){
		this.stats = map;
		this.solution = solution;
	}
	public AlgStats(Map<String, String> stats){
		this.stats = stats;
	}
	
	public Map<String, String> getStats() {
		return stats;
	}

	public String get(String prop)
	{
		return stats.get(prop);
	}

	public Long getLong(String prop)
	{
		return Long.parseLong(stats.get(prop));
	}

	public Long getTime()
	{
		return Long.parseLong(stats.get("time"));
	}
	public Long getNodes()
	{
		return Long.parseLong(stats.get("nodes"));
	}

	public void setStats(Map<String, String> stats) {
		this.stats = stats;
	}

	public Object getSolution() {
		return solution;
	}

	public void setSolution(Object solution) {
		this.solution = solution;
	}
	@Override
	public int compareTo(AlgStats o) {
		return (int) (this.getTime() - o.getTime());
	}

	@Override
	public String toString()
	{
		return 	"Alg: "+get("alg")+"\n"+
				"Time (ms) : "+get("time")+"\n"+
				"#Cliques: "+get("clq")+"\n";
	}

	
}
