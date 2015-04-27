package datastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GraphSet {

	private static List<Graph> graphSet = new ArrayList<Graph>();
	
	public static List<Graph> getGraphSet() {
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
