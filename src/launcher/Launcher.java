package launcher;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import mining.Mining;
import mining.Preprocessor;
import mining.Result;
import datastructure.Graph;
import datastructure.Graph.Edge;
import datastructure.GraphSet;

public class Launcher {

	public static void main(String[] args) {
		try {
			Preprocessor.loadFile(new File("test1"));
			Preprocessor.relabel();
			Preprocessor.rebuildGraphSet();
			
			Set<Graph> graphs = GraphSet.getGraphSet();
			for(Iterator<Graph> it = graphs.iterator(); it.hasNext();){
				Graph g = it.next();
				Set<Edge> edges = g.getEdges();
				System.out.println("T");
				for(Iterator<Edge> eit = edges.iterator(); eit.hasNext();) {
					Edge e = eit.next();
					System.out.println(e.getVertex1() + " " + e.getLabel() + " " + e.getVertex2());
				}
			}
			
			Mining.start(4, 2);
			
			Result.print();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
