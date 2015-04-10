package datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {
	public Map<Integer, Integer> vertex2Rank;
	private Set<Edge> edges;
	private DFSEdgeTree dfsEdgeTree;
	
	
	public static class Edge {

		public int vertex1;
		public int label;
		public int vertex2;
		
		public Edge(int vertex1, int label, int vertex2) {
			this.vertex1 = vertex1;
			this.vertex2 = vertex2;
			this.label = label;
		}
		
		public int getLabel() {
			return label;
		}
		
		public int getVertex1() {
			return vertex1;
		}
		
		public int getVertex2() {
			return vertex2;
		}
		
		public void setLabel(int l) {
			label = l;
		}
		
		public void setVertex1(int v) {
			vertex1 = v;
		}
		
		public void setVertex2(int v) {
			vertex2 = v;
		}
		
		@Override
		public boolean equals(Object o) {
			Edge other = (Edge) o;
			if(this.vertex1 == other.vertex1 && this.vertex2 == other.vertex2 && this.label == other.label) {
				return true;
			}
			return false;
		}
	}
	
	public Graph() {
		vertex2Rank = new HashMap<Integer, Integer>();
		edges = new HashSet<Edge>();
	}
	
	public boolean addEdge(Edge e) {
		return edges.add(e);
	}
	
	public boolean removeEdge(Edge e) {
		if(edges.contains(e)) {
			return edges.remove(e);
		}
		return false;
	}
	
	public Set<Edge> getEdges() {
		return edges;
	}
	
	public Integer putVertexRank(int vertex, int rank) {
		return vertex2Rank.put(vertex, rank);
	}
	
	public void clear() {
		dfsEdgeTree = null;
	}
	
	public Set<DFSCode> getCandidates(DFSCodeStack dfsCodeStack) {
		if(null == dfsEdgeTree) {
			dfsEdgeTree = new DFSEdgeTree(this);
			dfsEdgeTree.expand(dfsCodeStack.head());
		}
		
		return dfsEdgeTree.getCandidates(dfsCodeStack);
	}
	
}
