package datastructure;

import java.util.HashSet;
import java.util.Set;

public class GraphSet {

	private static Set<Graph> graphSet = new HashSet<Graph>();
	
	public static Set<Graph> getGraphSet() {
		return graphSet;
	}
	
	public static void add(Graph g) {
		graphSet.add(g);
	}
		
	public Set<DFSCode> getExtender() {
		return null;
	}
	
	public int getMatchedCount(DFSCodeStack dt) {
		return 0;
	}
}
