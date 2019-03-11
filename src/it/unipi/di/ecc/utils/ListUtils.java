package it.unipi.di.ecc.utils;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ListUtils {

	
	public static void main(String[] args){
		int[] a = {1,2,3,4,5,6,7,8,9,10};
		int[] b = {1,1,5,3,6,2,7,3,1,3,5,76,8,2,1,2,5,88};
//		int[] c = {3,8,15};
//		int[] d = {13,20};
		
		List<Integer> la = list(a);
		List<Integer> lb = list(b);
//		List<Integer> lc = list(c);
//		List<Integer> ld = list(d);
//		List<Integer> le = new ArrayList<Integer>();
		
		System.out.println("Ordered test");
		System.out.println(la+" - "+lb+" = "+removeOrdered(la, lb));
		

//		System.out.println("Unique "+la+" -> "+unique(la)+" ( "+la+" )");
//		System.out.println("Unique "+lb+" -> "+unique(lb)+" ( "+lb+" )");
		
	}
	
	/**
	 * removes all duplicates from the list. Returns the list but does not create a copy, changes are done on the input list.
	 */
	public static List<Integer> unique(IntArrayList a)
	{
		
		Collections.sort(a);
		
		int lg = a.size()-1;
//		int el = Integer.MAX_VALUE;
		
		for(int i=a.size()-2; i>=0; i--)
		{
			if(a.get(i) == a.get(i+1)) //remove i+1
			{
				a.set(i+1, a.get(lg)); //replaces i+1 with the last 'good' element, and shrinks the list by 1, effectively removing i+1 from the list
				lg--;
			}
		}
		
		while(a.size() > lg+1)
			a.remove(a.size()-1);
		
		a.trim();
		
		return a;
		
	}
	
	private static List<Integer> list(int[] a){
		List<Integer> l = new ArrayList<Integer>(a.length);
		
		for(int i : a){
			l.add(i);
		}
		return l;
	}
	

    /**
     * removes all elements in b from a (if they are in it). 
     * 
     */
	public static void removeFrom(List<Integer> a, List<Integer> b) {

		for(int x : b){
			a.remove((Integer)x);
		}
	}
    
    /**
     * returns a new list containing all elements in a which are not in b. 
     * assuming that the 2 lists are naturally ordered. O(|a| + |b|)
     */
    public static List<Integer> removeOrdered(List<Integer> a, List<Integer> b) {
		
    	
    	if(a.isEmpty()) return new ArrayList<Integer>();
    	if (b.isEmpty()) return new ArrayList<Integer>(a);
    	
    	List<Integer> r = new ArrayList<Integer>();
    	
    	
    	Iterator<Integer> ib = b.iterator();
    	int y = ib.next();
    	boolean yend = false;
    	
    	for(int i : a){
    		if( yend || i < y){
    			r.add(i);
    		} else if ( i > y){
    			try {while(y<i) y = ib.next();}
    			catch (NoSuchElementException e){ r.add(i); yend = true;}
    			
    			if(i<y) r.add(i);
    		}
    		ib.hashCode();
    		// if i == y nothing happens (the element is not added)
    	}
		return r;
	}
    
    public static List<Integer> intersect (List<Integer> a, List<Integer> b){
    	
    	List<Integer> r = new ArrayList<Integer>(a.size()/2);
    	
    	for(int i : a){
    		if (b.contains(i)) r.add(i);
    	}
    	
    	return r;
    }

    /**
     * Returns a new list containing the intersection between a and b.
     * Since a and b are ordered the complexity is O(|a|+|b|).
     * 
     * The method is not guaranteed to work with unordered lists.
     * 
     * @param a,b 2 lists supposed to be ordered
     * @return a new list that contains all the elements in both a and b.
     */
    public static List<Integer> intersectOrdered(List<Integer> a, List<Integer> b){ 
    	
		
    	List<Integer> r = new ArrayList<Integer>(a.size()/2);

		if(a.isEmpty() || b.isEmpty()) { return r;}
    	
    	int i = 0, j = 0;
    	int x = a.get(i), y = b.get(j);
    	
    	while(true){
    		if(i == a.size() || j == b.size()){
    			break;
    		}

			x = a.get(i);
			y = b.get(j);
    		
    		if(x == y){
    			r.add(x);
    			i++;
    			j++;
    		} else if (x > y){
    			j++;
    		} else {
    			i++;
    		}
    	}
    	
    	return r;
    }

    /**
     * assuming a ordered, inserts e maintaining the order. excludes duplicates.
     * O(log(a))
     */
	public static void addOrdered(List<Integer> a, Integer e) {
		if(a.isEmpty()){
			a.add(e);
		}
		
		int ind = binarySearch(a, e);
		
		if(ind >= a.size() || a.get(ind) != e) a.add(ind,e);
	}

	/**
	 * assuming both list are ordered, adds all the element from a to l 
	 * O( l + a )
	 */
	public static void addOrdered(List<Integer> l, List<Integer> a) {

		if(a.isEmpty()) return;
		
		
		int i = 0;
		int j = 0;
		
		if(l.isEmpty()) {
			l.add(a.get(0));
			i = 1;
		}
		
		while(i < a.size()){
			int x = a.get(i);
			
			while(j < l.size() && x>l.get(j)){
				j++;
			}
			l.add(j, x);
			i++;
		}
	}
	/**
	 * assuming l list is ordered, adds all the element from a to l. excludes duplicates
	 * O(a * log(l) )
	 */
	public static void addSemiOrdered(List<Integer> l, List<Integer> a) {

		if(a.isEmpty()) return;
		
		if(l.isEmpty()) {
			l.add(a.get(0));
		}
		
		for(int x : a){
			int ind = binarySearch(l,x);
			if(l.get(ind) != x) l.add(ind,x);
		}
	}

	/**
	 * assuming a and o ordered, returns a new list containing the union. excludes duplicates.
	 * O(a + o)
	 */
	public static List<Integer> uniteOrdered(List<Integer> a, List<Integer> o) {
		int i = 0;
		int j = 0;
		
		if(a == null || a.isEmpty()){
			if(o == null || o.isEmpty()){
				return new ArrayList<Integer>();
			} else {
				return new ArrayList<Integer>(o);
			}
		} else if(o == null || o.isEmpty()){
				return new ArrayList<Integer>(a);
		} 
		
		int asize = a.size();
		int osize = o.size();
		
		List<Integer> u = new ArrayList<Integer>(asize + osize);
		
		for(i = 0; i < asize ; i++){
			while(j<osize && a.get(i) > o.get(j)){
				u.add(o.get(j));
				j++;
			}
			if(j>=osize || a.get(i) != o.get(j)) u.add(a.get(i));
		}
		for(   ; j<osize;j++){
			u.add(o.get(j));
		}
		
		return u;
	}

//	public static void addOrdered(List<Integer> a, Integer e) {   0 1 2 6 7 8 9 10 11      ...4
//		if(a.isEmpty()){
//			a.add(e);
//		}
//		
//		int x;
//		
//		for(int i=0; i<a.size();i++){
//			x = a.get(i);
//			if(e<x){
//				a.add(i,e);
//				return;
//			}
//		}
//		//if we're here there's no element bigger than e in the list, so we add e in tail.
//		a.add(e);
//		
//	}
	
	/**
	 * returns the index of the element "key" if present,
	 * if not, returns the index in which the element "key" should be inserted (i.e. the index of the first element greater than "key"). 
	 */
	public static int binarySearch(List<Integer> l , int key){
		return bs(l,key,0,l.size());
	}
	
	private static int bs(List<Integer> l, int key, int min, int max){
		if(min == max) return min; //not found
		else {
			int m = mid(min,max);
			
			if(key > l.get(m)) return bs(l,key,m+1,max);
			else if(key < l.get(m)) return bs(l,key,min,m);
			else return m; // key = l.get(m)
		}
	}

	private static int mid(int min, int max){
		return (min+max)/2;
	}

	public static List<Integer> emptyList() {
		return new ArrayList<Integer>();
	}
	
	
	/**
	 * Returns the index where x is contained in l, or -1 if l does not contain x
	 */
	public static int binarySearch(int[] l, int x)
	{
		return bs(l, x, 0, l.length);
	}
	
	private static int bs(int[] l, int x, int low, int hi)
	{	
		if(low >= hi){
			if(low > hi || low >= l.length || l[low] != x) return -1;
			else return low;
		}
		
		int mid = (low+hi)/2;
		
		if(l[mid] < x) return bs(l,x,mid+1,hi);
		
		else if(l[mid] > x) return bs(l,x,low,mid);

		else return mid;
	}

}
