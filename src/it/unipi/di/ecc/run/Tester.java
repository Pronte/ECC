package it.unipi.di.ecc.run;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.ecc.interfaces.SearchAlg;
import it.unipi.di.ecc.utils.AlgStats;
import it.unipi.di.ecc.utils.CoverUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.List;


public class Tester {

	public static boolean DEBUG = (System.getProperty("DEBUG") != null);

	public static String fs = File.separator;
	
	public static void runAndSaveDists(String graph, String alg, String outfolder) throws Exception
	{

			
		AlgStats stats = runAlg(graph, alg);
	
		//names for stats files
		String graphName = (graph.contains(fs) ? graph.substring(graph.lastIndexOf(fs)+1) : (graph.contains("\\")  ? graph.substring(graph.lastIndexOf("\\")+1) : graph.substring(graph.lastIndexOf("/")+1)   ));
		String algName = "ECC-rc";
		
		saveResAndDists(graphName, algName, stats, outfolder);
		
	}
	
	public static void printarray(int[] a)
	{
		System.out.print("{");
		for(int i = 0; i < a.length; i++)
		{
			if(a[i] > 0) System.out.print("["+i+","+a[i]+"]");
		}
		System.out.println("}");
	}
	

	public static void saveResAndDists(String graphName, String algName, AlgStats stats, String outfolder) throws Exception
	{

		List<IntOpenHashSet> solution = (List<IntOpenHashSet>) stats.getSolution();
		
		int removed = CoverUtils.minimalize(solution);

		System.out.println("Removed "+removed+" cliques with postprocessing step by Kou et al.");
		stats.stats.put("removed", ""+removed);
		
		
		String base = null;
				
		if( outfolder != null){
			base = outfolder+fs+graphName+"-"+algName;
			File parent = new File (outfolder);
			if(!parent.exists())parent.mkdir();

			if(solution == null) //graph stats
			{
				File sout = new File (base+".txt");
				
					PrintWriter out = new PrintWriter(sout);
					//graphName #vertices #edges #edgesThatDontParticipateInTriangles
					out.println(graphName+" "+stats.get("vertices")+" "+stats.get("edges")+" "+stats.get("mnotri"));
					out.flush();
					out.close();
					System.out.println("Graph stats saved!");
			}
		}
		
		
		if(solution != null) // alg stats
		{
				saveResult(graphName, algName, stats, base);
				saveDistributions(solution, base);
		}

		
		

	}
	

	public static void saveResult(String graph, String alg, AlgStats stats, String outbasename) throws Exception
	{

		List<IntOpenHashSet> solution = (List<IntOpenHashSet>) stats.getSolution();
		PrintWriter out;
		
		int sum = 0;

		for(IntOpenHashSet s : solution)
		{
			sum += s.size();
		}
	
		File fout = new File(outbasename+".cover");
//		if(fout.exists())
//		{
//			System.out.println("Stats already exist, skipping saving step");
//			return;
//		}
		
		if(outbasename == null){
			System.out.println("---- result stats ----");
			System.out.println("Graph: "+graph);
			System.out.print("Alg: "+stats.get("alg")+"\n"+
					"Time: "+stats.get("time")+" ms\n"+
					"Cliques: "+solution.size()+"\n"+
					"Removed (by Kou et al.'s post-processing): "+stats.get("removed")+"\n");
			System.out.println("----------------------");
			return;
		}
		
		out = new PrintWriter(new BufferedWriter(new FileWriter(fout),10000));
		
		for(IntOpenHashSet x : solution)
		{
			for(int y : x)
			{
				out.print(y+" ");
			}
			out.println();
		}
		
		out.flush();
		out.close();
		
		out = new PrintWriter(new BufferedWriter(new FileWriter(outbasename+"-stats.txt"),10000));

		out.println("Graph: "+graph);
		out.print("Alg: "+stats.get("alg")+"\n"+
				"Time: "+stats.get("time")+"\n"+
				"Cliques: "+solution.size()+"\n"+
				"Removed: "+stats.get("removed")+"\n");
		
		if(solution != null) out.println("Sum: "+sum);
		
		out.flush();
		out.close();
		
		System.out.println("Results saved to file in "+outbasename);

	}
	
	public static void saveDistributions(List<IntOpenHashSet> solution,  String outbasename) throws Exception
	{

		if( outbasename == null ) return;
		
		int[] clqdist = CoverUtils.getClqDistribution(solution);
		
		int[] nindex = CoverUtils.nodesCoveringIndex(solution);
		int[] ndist = CoverUtils.getDistribution(nindex);
		int[] edist = CoverUtils.edgesCoveringIndexDistribution(solution);

		File fout = new File(outbasename+"-clq.csv");
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fout),10000));
		
		for(int i = 0; i < clqdist.length; i++)
		{
			if(clqdist[i]>0) out.println(i+" "+clqdist[i]);
		}
		out.flush();
		out.close();
		
		out = new PrintWriter(new BufferedWriter(new FileWriter(outbasename+"-nci.csv"),10000));
		for(int i = 1; i < ndist.length ; i++) //we don't want to print how many nodes are covered 0 times, so we start from 1
		{
			if(ndist[i] > 0) out.println(i+" "+ndist[i]);
		}
		out.flush();
		out.close();
		
		out = new PrintWriter(new BufferedWriter(new FileWriter(outbasename+"-eci.csv"),10000));
		
		for(int i = 0; i < edist.length ; i++)
		{
			if(edist[i] > 0) out.println(i+" "+edist[i]);
		}
		out.flush();
		out.close();
	
		
		
		if(DEBUG) System.out.println("Distributions saved!");
	}



	public static AlgStats runAlg(String graphfname, String alg, long tlimit) throws Exception {
	
		Class algClass = Class.forName(alg);
		Constructor c;
		SearchAlg sa;
		try{
			c = algClass.getConstructor(InputStream.class);
			InputStream graphfile = new FileInputStream(graphfname);
			sa = (SearchAlg) c.newInstance(graphfile);
		} catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("InputStream constructor for algorithm is not available, or error occurred during parsing");
			c= algClass.getConstructor(String.class);
			sa = (SearchAlg) c.newInstance(graphfname);
		}
		
		
		if (DEBUG)
			System.out.println("Algotithm object created, class="
					+ sa.getClass().getName());
	
		sa.setTimeLimit(tlimit);
	
		sa.search();
		
				
		return sa.getStats();
	}

	public static AlgStats runAlg(String fname, String alg) throws Exception {
		return runAlg(fname, alg, -1);
	}

}
