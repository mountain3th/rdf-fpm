package datastructure;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import datastructure.Graph.Edge;


public class GraphInRDD implements Serializable {
	private Graph g;
	private int[] vertexLabel2Rank;
	private int[] edgeLabel2Rank;
	
	public GraphInRDD(Graph g, int[] vr, int[] er) {
		this.g = g;
		this.vertexLabel2Rank = vr;
		this.edgeLabel2Rank = er;
	}
	
	public void rebuild() {
		Set<Edge> edges = g.getEdges();
		for(Iterator<Edge> eit = edges.iterator(); eit.hasNext();) {
			Edge e = eit.next();
			e.label = edgeLabel2Rank[e.label];
		}
		for(Iterator<Entry<Integer, Integer>> vit = g.vertex2Rank.entrySet().iterator(); vit.hasNext();) {
			Entry<Integer, Integer> entry = vit.next();
			int label = entry.getValue();
			entry.setValue(vertexLabel2Rank[label]);
		}
		
		g.init();
	}
	
	public Graph getGraph() {
		return g;
	}
	
}
