package launcher;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import mining.Result;
import datastructure.Graph;
import datastructure.Graph.Edge;

public class Debugger {
	
	public static boolean isDebug = false;
	
	public static void print(Set<Graph> graphs) {
		if(isDebug) {
			System.out.println("Step1 处理后的图:\n");
			for(Iterator<Graph> it = graphs.iterator(); it.hasNext();){
				Graph g = it.next();
				Set<Edge> edges = g.getEdges();
				System.out.println("T");
				for(Iterator<Edge> eit = edges.iterator(); eit.hasNext();) {
					Edge e = eit.next();
					System.out.println(e.toString(g.vertex2Rank, Result.vertexRank2Label, Result.edgeRank2Label));
				}
			}
		}
	}
	
	public static void print() {
		if(isDebug) {
			System.out.println("\n顶点Rank");
			for(Iterator<Entry<Integer, Integer>> it = Result.vertexRank2Label.entrySet().iterator(); it.hasNext();) {
				Entry<Integer, Integer> entry = it.next();
				System.out.print(entry.getKey() + " = " + entry.getValue() + ", ");
			}
			System.out.println("\n边Rank");
			for(Iterator<Entry<Integer, Integer>> it = Result.edgeRank2Label.entrySet().iterator(); it.hasNext();) {
				Entry<Integer, Integer> entry = it.next();
				System.out.print(entry.getKey() + " = " + entry.getValue() + ", ");
			}
		}
	}
}
