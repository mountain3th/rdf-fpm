package mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import datastructure.DFSCode;
import datastructure.DFSCodeStack;

public class Result {
	public static Map<Integer, Integer> vertexRank2Label = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> edgeRank2Label = new HashMap<Integer, Integer>();
	
	public static List<DFSCodeStack> results = new ArrayList<DFSCodeStack>();
	
	public static void add(DFSCodeStack dfsCodeStack) {
		results.add(dfsCodeStack);
	}
	
	public static void print() {
		System.out.println(results.size());
		for(Iterator<DFSCodeStack> it = results.iterator(); it.hasNext();) {
			DFSCodeStack stack = it.next();
			for(Iterator<DFSCode> dit = stack.getStack().iterator(); dit.hasNext();) {
				DFSCode code = dit.next();
				System.out.print(code.toString(vertexRank2Label, edgeRank2Label) + " -> ");
			}
			System.out.println();
		}
	}
}
