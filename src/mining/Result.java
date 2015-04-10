package mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datastructure.DFSCodeStack;

public class Result {
	public static Map<Integer, Integer> vertexRank2Label = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> edgeRank2Label = new HashMap<Integer, Integer>();
	
	public static List<DFSCodeStack> results = new ArrayList<DFSCodeStack>();
	
	public static void add(DFSCodeStack dfsCodeStack) {
		results.add(dfsCodeStack);
	}
	
	public static void print() {
		
	}
}
