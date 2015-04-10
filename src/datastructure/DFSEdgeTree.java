package datastructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import datastructure.Graph.Edge;
import exception.MiningException;

public class DFSEdgeTree {
	
	private Graph graph;
	private Node root;
	
	private class Node {
		Edge edge;
		List<Node> candidates;
		Node parent;
		DFSCode code;
		
		Node(Edge e) {
			edge = e;
			parent = null;
		}
		
		void addToCandidates(Edge e) {
			if(null == candidates) {
				candidates = new ArrayList<Node>();
			}
			
			Node newNode = new Node(e);
			newNode.parent = this;
			candidates.add(newNode);
		}
		
		void addToCandidates(List<Edge> edges) {
			for(Iterator<Edge> it = edges.iterator(); it.hasNext();) {
				addToCandidates(it.next());
			}
		}
		
		List<Edge> getCandidateEdges() {
			if(null == candidates) {
				return null;
			}
			
			List<Edge> temp = new ArrayList<Edge>();
			for(Iterator<Node> it = candidates.iterator(); it.hasNext();) {
				temp.add(it.next().edge);
			}
			return temp;
		}
		
		boolean checkRepeated(Edge e) {
			Node parent = this.parent;
			while(parent != null) {
				if(parent.edge.equals(e)) {
					return true;
				}
				parent = parent.parent;
			}
			if(this.edge.equals(e)) {
				return true;
			}
			
			return false;
		}
		
		List<Node> getNextNodes(int index, List<Node> matchedNodes) {
			List<Node> nodes = new ArrayList<Node>();
			for(Iterator<Node> it = candidates.iterator(); it.hasNext();) {
				Node n = it.next();
				Edge e = n.edge;
				n.code = new DFSCode(index, index+1, graph.vertex2Rank.get(e.vertex1), e.label, 
						graph.vertex2Rank.get(e.vertex2));
			}
			nodes.addAll(this.candidates);
			if(this.parent != null) {
				for(Iterator<Node> it = parent.candidates.iterator(); it.hasNext();) {
					Node n = it.next();
					if(matchedNodes.contains(n)) {
						continue;
					}
					Edge e = n.edge;
					n.code = new DFSCode(index-1, index+1, graph.vertex2Rank.get(e.vertex1), e.label, 
							graph.vertex2Rank.get(e.vertex2));
				}
				nodes.addAll(this.parent.candidates);
				nodes.removeAll(matchedNodes);
			}
			
			return nodes;
		}
	}
	
	public DFSEdgeTree(Graph g) {
		this.graph = g;
	}
	
	public void expand(DFSCode code) {
//		Node p = root;
//		List<Node> temp = new ArrayList<Node>();
		
//		List<Edge> candidateEdges = new ArrayList<Edge>();
//		Set<Edge> repeatedEdges = new HashSet<Edge>();
		
		Queue<Node> queue = new LinkedList<Node>();
		for(Iterator<Edge> it = graph.getEdges().iterator(); it.hasNext();) {
			Edge e = it.next();
			if(graph.vertex2Rank.get(e.vertex1) == code.x && graph.vertex2Rank.get(e.vertex2) == code.y) {
				Node n = new Node(e);
				root = n;
				queue.offer(root);
				break;
			}
		}
		
		while(!queue.isEmpty()) {
			Node n = queue.poll();
			
			for(Iterator<Edge> it = graph.getEdges().iterator(); it.hasNext();) {
				Edge e = it.next();
				// 填补部分
				if(n.parent == null && e.vertex1 == n.edge.vertex1 && !n.checkRepeated(e)) {
					n.addToCandidates(e);
				}
				// 前向或后向扩展
				if(e.vertex1 == n.edge.vertex2 && !n.checkRepeated(e)) {
					n.addToCandidates(e);
				}
			}
			
			if(n.candidates != null) {
				queue.addAll(n.candidates);
			}
		}
				
	}
	
	
	public Set<DFSCode> getCandidates(DFSCodeStack dfsCodeStack) throws MiningException {
		if(null == root) {
			return null;
		}
		
		int index;
		Node p = root;
		List<Node> matchedNodes = new ArrayList<Node>();
		
		if(!p.code.equals(dfsCodeStack.head())) {
			throw new MiningException();
		}
		for(index = 1; index < dfsCodeStack.getStack().size(); index++) {
			DFSCode code = dfsCodeStack.getStack().get(index);
			matchedNodes.add(p);
			List<Node> nodes = p.getNextNodes(index, matchedNodes);
			int i = 0;
			for(; i < nodes.size(); i++) {
				if(code.equals(nodes.get(i).code)) {
					p = nodes.get(i);
					break;
				}
			}
			if(i == nodes.size()) {
				throw new MiningException();
			}
		}
		
		Set<DFSCode> codes = new HashSet<DFSCode>();
		for(Iterator<Node> it = p.getNextNodes(index, matchedNodes).iterator(); it.hasNext();) {
			codes.add(it.next().code);
		}
		
		return codes;
	}
	
}
