package it.unipi.di.ecc.utils.graph;

import java.util.ArrayList;
import java.util.List;

public class BackTrackBuffer {
	
	private List<Integer> xs;
	private List<Integer> ps;
		
	public BackTrackBuffer(int Xstart, int Pend){
		xs = new ArrayList<Integer>();
		ps = new ArrayList<Integer>();
		save(Xstart,Pend);
	}
	
	public boolean isEmpty(){
		if(xs.isEmpty() != ps.isEmpty()){
			throw new RuntimeException("Misuse of back track buffer");
		}
		return xs.isEmpty();
	}
	
	public void save(int Xstart, int Pend){
		xs.add(Xstart);
		ps.add(Pend);
	}
	
	public void backtrack(){
		xs.remove(xs.size()-1);
		ps.remove(ps.size()-1);
	}
	
	public int getX(){
		return xs.get(xs.size()-1);
	}
	public int getP(){
		return ps.get(ps.size()-1);
	}
	
}