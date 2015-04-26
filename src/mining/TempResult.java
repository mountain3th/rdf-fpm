package mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import datastructure.DFSCode;
import datastructure.DFSCodeStack;
import datastructure.Graph;

public class TempResult {
	public static Map<Integer, Integer> vertexRank2Label = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> edgeRank2Label = new HashMap<Integer, Integer>();
	public static int maxVertexRank = 0;
	public static int maxEdgeRank = 0;
	
//	public static List<DFSCodeStack> results = new ArrayList<DFSCodeStack>();
//	
//	public static void add(DFSCodeStack dfsCodeStack) {
//		results.add(dfsCodeStack);
//	}
	
	private static List<Node> roots;
	private static Node currentNode;
	private static int depth = 0;
	
	static class Node {
		int count;
		double confidency;
		DFSCode code;
		Set<Integer> subjects;
		List<Node> childs;
		Node parent;
		
		Node(DFSCode c) {
			code = c;
			confidency = 1.0;
			subjects = new HashSet<Integer>();
		}
		
		void addChild(Node n) {
			if(childs == null) {
				childs = new ArrayList<Node>();
			}
			childs.add(n);
			n.parent = this;
		}
	}
	
	public static void add(DFSCodeStack dfsCodeStack, Set<Graph> graphs) {
		int size = dfsCodeStack.size();
		while(size <= depth) {
			currentNode = currentNode.parent;
			depth--;
		}
		
		Node n = new Node(dfsCodeStack.peek());
		n.count = graphs.size();
		for(Iterator<Graph> it = graphs.iterator(); it.hasNext();) {
			n.subjects.add(it.next().subject);
		}
		if(currentNode != null) {
			n.confidency = n.count / currentNode.count;
			currentNode.addChild(n);
		} else {
			roots.add(n);
			currentNode = n;
			depth++;
		}
	}
	
	public static void print() {
		for(Iterator<Node> it = roots.iterator(); it.hasNext();) {
			print(it.next());
		}
	}
	
	private static void print(Node n) {
		List<Node> childs = n.childs;
		if(childs == null) {
			return;
		}
		for(int index = 0; index < childs.size(); index++) {
			print(childs.get(index));
		}
	}
}
