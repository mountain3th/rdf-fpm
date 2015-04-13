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
		
		@Override
		public boolean equals(Object o) {
			Edge other = (Edge) o;
			if(this.vertex1 == other.vertex1 && this.vertex2 == other.vertex2 && this.label == other.label) {
				return true;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return 100 * vertex1 + 10 * label + vertex2;
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(vertex1);
			sb.append(" ");
			sb.append(label);
			sb.append(" ");
			sb.append(vertex2);
			return sb.toString();
		}
		
		public String toString(Map<Integer, Integer> vertex2Rank, Map<Integer, Integer> vertexRank2Label, 
				Map<Integer, Integer> edgeRank2Label) {
			StringBuffer sb = new StringBuffer();
			sb.append(vertexRank2Label.get(vertex2Rank.get(vertex1)));
			sb.append(" ");
			sb.append(edgeRank2Label.get(label));
			sb.append(" ");
			sb.append(vertexRank2Label.get(vertex2Rank.get(vertex2)));
			return sb.toString();
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
	
	public Set<DFSCode> getCandidates(DFSCodeStack dfsCodeStack) {
		return dfsEdgeTree.getCandidates(dfsCodeStack);
	}
	
	public boolean hasCandidates(DFSCode code) {
		dfsEdgeTree = new DFSEdgeTree(this);
		return dfsEdgeTree.hasCandidates(code);
	}
	
}
