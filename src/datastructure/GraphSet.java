package datastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GraphSet {

	private static List<Graph> graphList = new ArrayList<Graph>();
	
	public static List<Graph> getGraphSet() {
		return graphList;
	}
	
	public static void add(Graph g) {
		graphList.add(g);
	}
}
