package mining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import launcher.Debugger;
import launcher.Debugger.OnTaskFinishedListener;
import mining.Mining.MiningData;
import datastructure.Graph;
import datastructure.Graph.Edge;
import datastructure.GraphSet;
import exception.MiningException;

public class Preprocessor {
	private static Map<Integer, Integer> vertexLabel2Freq = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> edgeLabel2Freq = new HashMap<Integer, Integer>();
	private static Map<MiningData, Integer> md2Freq = new HashMap<MiningData, Integer>();
	private static int[] vertexLabel2Rank = new int[5000000];
	private static int[] edgeLabel2Rank = new int[1000];
	
	public static void loadFile(File file) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		Graph graph = null;
		
		Debugger.startTask("loadFile");
		
		int graphCount = -1;
		while((line = br.readLine()) != null) {
			String[] content = line.split("\\s+");
			
			if("t".equals(content[0])) {
				graphCount++;
				graph = new Graph(graphCount);
				GraphSet.add(graph);
			} else if("v".equals(content[0])) {
				int vertex = Integer.valueOf(content[1]);
				int label = Integer.valueOf(content[2]);
				int value = vertexLabel2Freq.containsKey(label) ? vertexLabel2Freq.get(label) + 1 : 1;
				vertexLabel2Freq.put(label, value);
				graph.putVertexRank(vertex, label);
			} else if("e".equals(content[0])) {
				int vertex1 = Integer.valueOf(content[1]);
				int vertex2 = Integer.valueOf(content[2]);
				if(!graph.vertex2Rank.containsKey(vertex1) || !graph.vertex2Rank.containsKey(vertex2)) {
					throw new MiningException();
				}
				
				int vertex2Label = graph.vertex2Rank.get(vertex2);
				int label = Integer.valueOf(content[3]);
				int value = edgeLabel2Freq.containsKey(label) ? edgeLabel2Freq.get(label) + 1 : 1;
				edgeLabel2Freq.put(label, value);
				
				MiningData md = new MiningData(label, vertex2Label);
				int value2 = md2Freq.containsKey(md) ? md2Freq.get(md) + 1 : 1;
				md2Freq.put(md, value2);
				
				graph.addEdge(new Edge(vertex1, label, vertex2));
			}
		}
		
		br.close();
		
		Debugger.finishTask("loadFile");
	}
	
	
	public static void relabel() {
		Debugger.startTask("relabel", new OnTaskFinishedListener() {
			@Override
			public void onTaskFinished() {
				Debugger.log("共有: " + String.valueOf(md2Freq.size()) + "\n");
			}
		});
		
		List<Entry<Integer, Integer>> vList = new ArrayList<Entry<Integer, Integer>>(vertexLabel2Freq.entrySet());
		
		Collections.sort(vList, comparator);
		for(int index = 0; index < vList.size(); index++) {
			int label = vList.get(index).getKey();
			int freq = vList.get(index).getValue();
			
			if(freq >= Mining.MIN_SUPPORT) {
				TempResult.maxVertexRank++;
			}
			TempResult.vertexRank2Label.put(index, label);
			vertexLabel2Rank[label] = index;
		}
		
		List<Entry<Integer, Integer>> eList = new ArrayList<Entry<Integer, Integer>>(edgeLabel2Freq.entrySet());
		Collections.sort(eList, comparator);
		
		for(int index = 0; index < eList.size(); index++) {
			int label = eList.get(index).getKey();
			int freq = eList.get(index).getValue();
			
			if(freq >= Mining.MIN_SUPPORT) {
				TempResult.maxEdgeRank++;
			}
			TempResult.edgeRank2Label.put(index, label);
			edgeLabel2Rank[label] = index;
		}
		
		Mining.startPoint = vList.indexOf(new AbstractMap.SimpleEntry<Integer, Integer>(0,
						vertexLabel2Freq.get(0)));
		vertexLabel2Freq = null;
		edgeLabel2Freq = null;
		
		Debugger.finishTask("relabel");
	}
	
	public static void rebuildGraphSet() {
		Debugger.startTask("rebuildGraphSet");
		
		Set<Graph> graphSet = GraphSet.getGraphSet();
		for(Iterator<Graph> it = graphSet.iterator(); it.hasNext();) {
			Graph g = it.next();
			Set<Edge> edges = g.getEdges();
			boolean hasNoCandidates = true;
			for(Iterator<Edge> eit = edges.iterator(); eit.hasNext();) {
				Edge e = eit.next();
				
				int ver2Label = g.vertex2Rank.get(e.vertex2);
				MiningData md = new MiningData(e.label, ver2Label);
				
				int ver2Rank = vertexLabel2Rank[ver2Label];
				int edgeRank = edgeLabel2Rank[e.label];
				
				g.vertex2Rank.put(e.vertex2, ver2Rank);
				e.label = edgeRank;
				
				if(md2Freq.get(md) >= Mining.MIN_SUPPORT) {
					hasNoCandidates = false;
					Mining.dataSet.add(new MiningData(edgeRank, ver2Rank));
				}
			}
//			for(Iterator<Entry<Integer, Integer>> vit = g.vertex2Rank.entrySet().iterator(); vit.hasNext();) {
//				Entry<Integer, Integer> entry = vit.next();
//				int label = entry.getValue();
//				int rank = vertexLabel2Rank[label];
//				entry.setValue(rank);
//				if(rank < TempResult.maxVertexRank) {
//					hasNoCandidates = false;
//				}
//			}
			
			if(hasNoCandidates) {
				it.remove();
			} else {
				g.init();
			}
		}
		
		Debugger.finishTask("rebuildGraphSet");
	}


	private static Comparator<Entry<Integer, Integer>> comparator = new Comparator<Entry<Integer, Integer>>() {
		public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
			if(o1.getValue().compareTo(o2.getValue()) > 0) {
				return -1;
			} else if(o1.getValue().compareTo(o2.getValue()) < 0) {
				return 1;
			}  else {
				return 0;
			}
		}
	};
}
