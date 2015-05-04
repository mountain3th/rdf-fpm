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
		
//		void addToCandidates(List<Edge> edges) {
//			for(Iterator<Edge> it = edges.iterator(); it.hasNext();) {
//				addToCandidates(it.next());
//			}
//		}
//		
//		List<Edge> getCandidateEdges() {
//			if(null == candidates) {
//				return null;
//			}
//			
//			List<Edge> temp = new ArrayList<Edge>();
//			for(Iterator<Node> it = candidates.iterator(); it.hasNext();) {
//				temp.add(it.next().edge);
//			}
//			return temp;
//		}
		
		// 防止后向扩展造成扩展死循环
		boolean checkRepeated(Edge e) {
			Node parent = this.parent;
			// 普通情况
			while(parent != null) {
				if(parent.edge.vertex1 == e.vertex1) {
					return true;
				}
				parent = parent.parent;
			}
			
			// 特殊处理
			if(this.edge.equals(e)) {
				return true;
			}
			
			return false;
		}
		
		List<Node> getNextNodes(int index, List<Node> matchedNodes) {
			List<Node> nodes = new ArrayList<Node>();
			if(candidates != null) {
				for(Iterator<Node> it = candidates.iterator(); it.hasNext();) {
					Node n = it.next();
					Edge e = n.edge;
					n.code = new DFSCode(-1, -1, graph.vertex2Rank.get(e.vertex1), e.label, 
							graph.vertex2Rank.get(e.vertex2));
					if(e.vertex1 == edge.vertex1) {
						n.code.ix = code.ix;
					} else if(e.vertex1 == edge.vertex2) {
						n.code.ix = code.iy;
					}
					n.code.iy = index+1;
					for(int i = 0; i < matchedNodes.size(); i++) {
						Node node = matchedNodes.get(i);
						if(e.vertex2 == node.edge.vertex1) {
							n.code.iy = node.code.ix;
							break;
						}
						if(e.vertex2 == node.edge.vertex2) {
							n.code.iy = node.code.iy;
							break;
						}
					}
				}
				nodes.addAll(this.candidates);
			}
			Node temp = this.parent;
			while(temp != null) {
				for(Iterator<Node> it = temp.candidates.iterator(); it.hasNext();) {
					Node n = it.next();
					if(matchedNodes.contains(n)) {
						continue;
					}
					Edge e = n.edge;
					n.code = new DFSCode(-1, -1, graph.vertex2Rank.get(e.vertex1), e.label, 
							graph.vertex2Rank.get(e.vertex2));
					if(e.vertex1 == temp.edge.vertex1) {
						n.code.ix = temp.code.ix;
					} else if(e.vertex1 == temp.edge.vertex2) {
						n.code.ix = temp.code.iy;
					}
					n.code.iy = index+1;
					for(int i = 0; i < matchedNodes.size(); i++) {
						Node node = matchedNodes.get(i);
						if(e.vertex2 == node.edge.vertex1) {
							n.code.iy = node.code.ix;
							break;
						}
						if(e.vertex2 == node.edge.vertex2) {
							n.code.iy = node.code.iy;
							break;
						}
					}
				}
				nodes.addAll(temp.candidates);
				nodes.removeAll(matchedNodes);
				temp = temp.parent;
			}
			
			return nodes;
		}
	}
	
	public DFSEdgeTree(Graph g) {
		this.graph = g;
	}
	
	public boolean hasCandidates(DFSCode code) {
		Queue<Node> queue = new LinkedList<Node>();

		for(Iterator<Edge> it = graph.getEdges().iterator(); it.hasNext();) {
			Edge e = it.next();
			if(code.a == e.label && graph.vertex2Rank.get(e.vertex1) == code.x && 
					graph.vertex2Rank.get(e.vertex2) == code.y) {
				Node n = new Node(e);
				root = n;
				root.code = new DFSCode(code);
				queue.offer(root);
				expand(queue);
				return true;
			}
		}
		
		return false;
	}
	
	public void expand(Queue<Node> queue) {
		while(!queue.isEmpty()) {
			Node n = queue.poll();
			
			for(Iterator<Edge> it = graph.getEdges().iterator(); it.hasNext();) {
				Edge e = it.next();
				// 特殊处理
				if(n.parent == null && n.edge.vertex1 == e.vertex1 && !n.checkRepeated(e)) {
					n.addToCandidates(e);
				}
				// 前向扩展或者后向扩展
				if(n.edge.vertex2 == e.vertex1 && !n.checkRepeated(e)) {
					n.addToCandidates(e);
				}
			}
			
			if(n.candidates != null) {
				queue.addAll(n.candidates);
			}
		}
	}
	
	public Set<DFSCode> getCandidates(DFSCodeStack dfsCodeStack) throws MiningException {
		if(root == null) {
			return null;
		}
		
		int index;
		Node p = root;
		List<Node> matchedNodes = new ArrayList<Node>();
		
		if(!dfsCodeStack.head().equals(p.code)) {
			throw new MiningException();
		}
		
		matchedNodes.add(root);
		for(index = 1; index < dfsCodeStack.getStack().size(); index++) {
			DFSCode code = dfsCodeStack.getStack().get(index);
			List<Node> nodes = p.getNextNodes(index, matchedNodes);
			int i = 0;
			for(; i < nodes.size(); i++) {
				if(code.equals(nodes.get(i).code)) {
					p = nodes.get(i);
					matchedNodes.add(p);
					break;
				}
			}
			if(i == nodes.size()) {
				dfsCodeStack.print();
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
