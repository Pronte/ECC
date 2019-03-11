package it.unipi.di.ecc.run;

import it.unipi.di.ecc.utils.parser.Parser;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;

/**
 * 
 * @author Alessio Conte, Roberto Grossi, Andrea Marino
 *
 * Runner
 * Example execution (from code):
 * ECCRun.run( "-g /path/to/graph.nde -f nde -o /output/folder/".split( " +" ) );
 */
public class ECCRun {
	
	public static void main( String[] args ) throws JSAPException{
		
		
		System.out.println("--------------------ECC-rc----------------------");
		System.out.println("Heuristic Algorithm by Alessio Conte, Roberto Grossi and Andrea Marino. University of Pisa.");
		System.out.println("Reference paper: Clique covering of large real-world networks ( https://dl.acm.org/citation.cfm?id=2851816 )");
		System.out.println("Written for Java 1.8");
		System.out.println("------------------------------------------------");
		
		if(args.length == 0 || args[0] == null || args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("-help"))
		{
			System.out.println("parameters: -g graphFile [-o outFolder] [-f format]");
			System.out.println("Out folder: to save result and statistics. If not specified, stats will be printed on the standard output.");
			System.out.println("Input graph format: Use -f { nde | aa }  (default = aa: AsciiArcs, a simple list of edges in a text file. Index nodes in [0,n-1], or use .nde format, for better performance and memory usage.)");
			System.exit(0);
		}


		ECCRun.run( args );
		
	}

	
	private static void run( String[] args ) throws JSAPException{
		
		
		SimpleJSAP jsap = new SimpleJSAP( ECCRun.class.getName(),
				"Run covering algorithms",
				new Parameter[] {
						new FlaggedOption( "graph", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'g', "graph", "The graph (it will be symmetrized)" ),
						new FlaggedOption( "out", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'o', "out", "outputbasename" ),
						new FlaggedOption( "format", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'f', "format", "format of the graph" )
				} );

		JSAPResult jsapResult = jsap.parse( args );
		if ( jsap.messagePrinted() ) System.exit( 1 );
		

		final String graph = jsapResult.getString( "graph" );
		final String es = "r";
		final String ns = "epsc";
		String out = jsapResult.getString( "out" ); //File name for the output: basename-stats.txt -clqd.csv -nd.csv -ed.csv
		if(out == null || out.isEmpty()) out = null;
		
		String format = jsapResult.getString( "format" );
		if(format == null || format.isEmpty()) format = "aa";
		
		Parser.setParser(format);
		
		String algClass = getAlgClass(es, ns);

		try {
			Tester.runAndSaveDists(graph, algClass, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
	private static String getAlgClass(String edgeSel, String nodeSel)
	{
		return "it.unipi.di.ecc.smallg.EPSc";
	}
}
