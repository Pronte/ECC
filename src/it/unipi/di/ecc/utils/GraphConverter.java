package it.unipi.di.ecc.utils;

import it.unipi.di.ecc.utils.graph.MapListGraph;
import it.unipi.di.ecc.utils.parser.ASCIIARCSParser;
import it.unipi.di.ecc.utils.parser.Parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GraphConverter {
	
	public static void main(String[] args) {
		
		String fname = "graphs/lasagne/";
//		String[] files = {"hprd_pp.nde","advogato.nde","Gnp_5e3.nde","GoogleNw.nde","p2p-Gnutella31.nde"};
		String[] files = {"forest1e4_2.nde"};
		File f = new File(fname);
		
		System.out.println(f.getAbsolutePath()+" ex: "+f.exists());
		
		for(String s : files)
			toAsciiArcs(fname+s, "graphs/lasagne/aa/"+s.substring(0, s.lastIndexOf("."))+".aa");
//		asciiArcsToWebgraph("graphs/er/1000-20-00.aa","milleventi");
		
		
		
	}

	public static void toAsciiArcs(String graphFile, String outFile)
	{
		

		File gf = new File(graphFile);
		String format="dimacs";
		
		if(gf.getName().contains("."))
		{
			format = graphFile.substring(graphFile.lastIndexOf(".")+1);
		}
		
		System.out.println("Converting format "+format+" to asciiarcs..");
		
		toAsciiArcs(graphFile, outFile, format);
		
	}
	
	
	public static void toAsciiArcs(String graphFile, String outName, String format)
	{
		
		
		File gf = new File(graphFile);
//		String outName;
//		
//		if(gf.getName().contains("."))
//		{
//			outName = graphFile.substring(0, graphFile.lastIndexOf("."))+".aa";
//		}
//		else
//		{
//			outName = graphFile+".aa";
//		}
		
		PrintWriter out = null;
		
		File outFile = new File (outName);
		
		if(outFile.exists()){
			System.out.println("File "+outFile+" already exists! skipping..");
			return;
		}
		
		try {
			out = new PrintWriter(outFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Parser.setParser(format);
		
		MapListGraph g = null;
		
		try {
			g = new MapListGraph(Parser.getMapListGraph(new FileInputStream(gf)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		ASCIIARCSParser.save(g, out);
		
		System.out.println("done!");
		
	}
	
}
