package mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import launcher.Debugger;
import prediction.Predicate.Concept;
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
	
	private static List<Node> roots = new ArrayList<Node>();
	private static Node currentNode;
	private static int depth = 0;
	
	static class Node {
		int count;
		double confidence;
		DFSCode code;
		Set<Integer> subjects;
		List<Node> childs;
		Node parent;
		
		Node(DFSCode c) {
			code = c;
			confidence = 1.0;
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
			double confidence = (double)n.count / (double)currentNode.count;
			n.confidence = confidence < currentNode.confidence ? confidence : currentNode.confidence;
			currentNode.addChild(n);
		} else {
			roots.add(n);
		}
		currentNode = n;
		depth++;
	}
	
	public static void cutIfHasNoConcept() {
		for(Iterator<Node> it = roots.iterator(); it.hasNext();) {
			Node n = it.next();
			if(!cut(n)) {
				it.remove();
			}
		}
	}
	
	private static boolean cut(Node n) {
		List<Node> childs = n.childs;
		if(childs == null) {
			return hasConcept(n);
		}
		boolean hasConcept = false;
		for(int index = 0; index < childs.size(); index++) {
			boolean temp = cut(childs.get(index));
			if(!temp) {
				childs.remove(index);
			}
			hasConcept |= temp;
		}
		return hasConcept;
	}
	
	private static boolean hasConcept(Node n) {
		return edgeRank2Label.get(n.code.a) == 633;
	}
	
	public static List<Concept> genConcept(int subject) {
		List<Concept> concepts = new ArrayList<Concept>();
		for(Iterator<Node> it = roots.iterator(); it.hasNext();) {
			Node n = it.next();
			if(n.subjects.contains(subject)) {
				genConcept(n, subject, concepts, 1);
			}
		}
		return concepts;
	}
	
	private static void genConcept(Node n, int subject, List<Concept> concepts, int depth) {
		if(hasConcept(n)) {
			if(n.subjects.contains(subject)) {
				concepts.add(new Concept(n.code.y, 1.0, depth));
			} else {
				concepts.add(new Concept(n.code.y, n.confidence, depth));
			}
		}
		
		List<Node> childs = n.childs;
		if(childs == null) {
			return;
		}
		for(int index = 0; index < childs.size(); index++) {
			genConcept(n, subject, concepts, depth + 1);
		}
	}
	
	public static void print() {
		for(Iterator<Node> it = roots.iterator(); it.hasNext();) {
			print(it.next());
		}
	}
	
	private static void print(Node n) {
		Debugger.log(n.code.toString(vertexRank2Label, edgeRank2Label) + "\n");
		List<Node> childs = n.childs;
		if(childs == null) {
			return;
		}
		for(int index = 0; index < childs.size(); index++) {
			print(childs.get(index));
		}
	}
}
