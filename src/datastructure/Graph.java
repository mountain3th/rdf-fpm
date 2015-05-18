package datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import launcher.Debugger;
import mining.TempResult;
import mining.Mining.Pattern;
import exception.MiningException;

public class Graph {
	
	public int subject;
	public Map<Integer, Integer> vertex2Rank;
	
	private Set<Edge> edges;
	private DFSCandidates dfsCandidates;
	
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
//			sb.append(edgeRank2Label.get(label));
			sb.append(label);
			sb.append(" ");
			sb.append(vertexRank2Label.get(vertex2Rank.get(vertex2)));
			return sb.toString();
		}
	}
	
	private class DFSCandidates {

		List<Integer> indexes;
		List<DFSCode> dfsCodes;
//		DFSCode[] fastDfsCodes;
		int keepIndex = -1;
		
		DFSCandidates() {
			indexes = new ArrayList<Integer>();
			dfsCodes = new ArrayList<DFSCode>();
		}
		
		void init() {
			Debugger.log("\n#\n");
			for(Iterator<Edge> it = edges.iterator(); it.hasNext();) {
				Edge e = it.next();
				DFSCode code = new DFSCode(-1, -1, vertex2Rank.get(e.vertex1), e.label, vertex2Rank.get(e.vertex2));
				dfsCodes.add(code);
				if(e.label == 20) {
					Debugger.log("Edge: " + e + " -> " + code + "\n");
				}
			}
			
			Collections.sort(dfsCodes, new Comparator<DFSCode>() {

				@Override
				public int compare(DFSCode e1, DFSCode e2) {
					if(TempResult.hasConcept(e1.a)) {
						return 1;
					}
					
					return e1.a < e2.a ? -1 : (e1.a == e2.a ? (e1.y < e2.y ? -1 : (e1.y == e2.y ? 0 : 1)) : 1);
				}
				
			});
			
			
//			fastDfsCodes = dfsCodes.toArray(new DFSCode[dfsCodes.size()]);
		}
		
		int indexOf(DFSCode code) {
			return dfsCodes.indexOf(code);
		}
		
		
		boolean hasCandidates(Pattern pattern, DFSCode code) {
			if(pattern == Pattern.PATTERN_STRONG) {
				return indexOf(code) != -1;
			} else {
				int i;
//				for(i = keepIndex + 1; i < fastDfsCodes.length; i++) {
//					if(code.equalsTo(fastDfsCodes[i])) {
//						break;
//					}
//				}
				for(i = keepIndex + 1; i < dfsCodes.size(); i++) {
					if(code.equalsTo(dfsCodes.get(i))) {
						break;
					}
				}
				
				return i != dfsCodes.size();
			}
		}
		
		
		void push(Pattern pattern, DFSCode code) throws MiningException {
			boolean flag = false;
			if(pattern == Pattern.PATTERN_STRONG) {
//				keepIndex = indexOf(code);
//				for(int i = keepIndex + 1; i < fastDfsCodes.length; i++) {
//					if(code.equals(fastDfsCodes[i])) {
//						keepIndex = i; 
//						flag = true;
//						break;
//					}
//				}
				for(int i = keepIndex + 1; i < dfsCodes.size(); i++) {
					if(code.equals(dfsCodes.get(i))) {
						keepIndex = i;
						flag = true;
						break;
					}
				}
			} else {
//				for(int i = keepIndex + 1; i < fastDfsCodes.length; i++) {
//					if(code.equalsTo(fastDfsCodes[i])) {
//						keepIndex = i;
//						flag = true;
//						break;
//					}
//				}
				for(int i = keepIndex + 1; i < dfsCodes.size(); i++) {
					if(code.equalsTo(dfsCodes.get(i))) {
						keepIndex = i;
						flag = true;
						break;
					}
				}
			}
			
			if(!flag) {
				System.out.println(keepIndex + "\n");
				throw new MiningException();
			}
			
			indexes.add(keepIndex);
		}
		
		void pop() {
			int last = indexes.size() - 1;
			indexes.remove(last);
			last = indexes.size() - 1;
			keepIndex = (last >= 0) ? indexes.get(last) : -1;
		}
		
		Set<DFSCode> getCandidates(Pattern pattern, DFSCodeStack dfsCodeStack) {
			DFSCode code = dfsCodeStack.peek();
			push(pattern, code);
			
			Set<DFSCode> codes = new HashSet<DFSCode>();
//			for(int i = keepIndex + 1; i < fastDfsCodes.length; i++) {
//				codes.add(fastDfsCodes[i]);
//			}
			for(int i = keepIndex + 1; i < dfsCodes.size(); i++) {
				codes.add(dfsCodes.get(i));
			}
			
			return codes;
		}
		
		void log() {
			Debugger.log("\n");
			Debugger.log("" + dfsCodes.size() + "\n");
			for(int i = 0; i < dfsCodes.size(); i++) {
				Debugger.log(dfsCodes.get(i).toString());
				Debugger.log("\n");
			}
		}
	}
	
	public Graph(int subject) {
		this.subject = subject;
		vertex2Rank = new HashMap<Integer, Integer>();
		edges = new HashSet<Edge>();
		dfsCandidates = new DFSCandidates();
	}
	
	public Set<Edge> getEdges() {
		return edges;
	}

	public boolean addEdge(Edge e) {
		return edges.add(e);
	}
	
	
	public Integer putVertexRank(int vertex, int rank) {
		return vertex2Rank.put(vertex, rank);
	}
	
	public void init() {
		dfsCandidates.init();
	}
	
	public boolean hasCandidates(Pattern pattern, DFSCode code) {
		// way 1
//		dfsEdgeTree = new DFSEdgeTree(this);
//		return dfsEdgeTree.hasCandidates(code);
	
		// way 2
		return dfsCandidates.hasCandidates(pattern, code);
	}
	
	public Set<DFSCode> getCandidates(Pattern pattern, DFSCodeStack dfsCodeStack) {
		// way 1
//		return dfsEdgeTree.getCandidates(dfsCodeStack);
	
		// way 2
		return dfsCandidates.getCandidates(pattern, dfsCodeStack);
	}
	
	public void pop() {
		dfsCandidates.pop();
	}
	
//	private DFSEdgeTree dfsEdgeTree;
}
