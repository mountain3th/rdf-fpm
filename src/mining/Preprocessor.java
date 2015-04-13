package mining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

import datastructure.Graph;
import datastructure.Graph.Edge;
import datastructure.GraphSet;

public class Preprocessor {
	private static Map<Integer, Integer> vertexLabel2Freq = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> edgeLabel2Freq = new HashMap<Integer, Integer>();
	private static List<Entry<Integer, Integer>> vList;
	private static List<Entry<Integer, Integer>> eList;
	private static int status;
	
	public static void loadFile(File file) throws Exception{
		if(!file.exists() || !file.isFile()) {
			throw new FileNotFoundException("文件不存在");
		}
		
		if(status != 0) {
			throw new IllegalStateException("调用顺序错误");
		}
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		Map<Integer, Integer> tmp = null;
		Graph graph = null;
		while((line = br.readLine()) != null) {
			String[] content = line.split("\\s+");
			
			if("t".equals(content[0])) {
				graph = new Graph();
				GraphSet.add(graph);
				tmp = new HashMap<Integer, Integer>();
			} else if("v".equals(content[0])) {
				int vertex = Integer.valueOf(content[1]);
				int label = Integer.valueOf(content[2]);
				int value = vertexLabel2Freq.containsKey(label) ? vertexLabel2Freq.get(label) + 1 : 1;
				vertexLabel2Freq.put(label, value);
				graph.putVertexRank(vertex, label);
				tmp.put(vertex, label);
			} else if("e".equals(content[0])) {
				int vertex1 = Integer.valueOf(content[1]);
				int vertex2 = Integer.valueOf(content[2]);
				if(!tmp.containsKey(vertex1) || !tmp.containsKey(vertex2)) {
					throw new Exception("找不到对应的顶点");
				}
//				vertex1 = tmp.get(vertex1);
//				vertex2 = tmp.get(vertex2);
				int label = Integer.valueOf(content[3]);
				int value = edgeLabel2Freq.containsKey(label) ? edgeLabel2Freq.get(label) + 1 : 1;
				edgeLabel2Freq.put(label, value);
				graph.addEdge(new Edge(vertex1, label, vertex2));
			}
		}
		
		status++;
	}
	
	
	public static void relabel() {
		if(status != 1) {
			throw new IllegalStateException("调用顺序出错");
		}
		
		vList = new ArrayList<Entry<Integer, Integer>>(vertexLabel2Freq.entrySet());
		for(int i = 0; i < vList.size(); i++) {
			for(int j = i; j < vList.size(); j++) {
				Entry<Integer, Integer> entry1 = vList.get(i);
				Entry<Integer, Integer> entry2 = vList.get(j);
				if(entry1.getValue() < entry2.getValue()) {
					vList.set(j, entry1);
					vList.set(i, entry2);
				}
			}
		}
		
//		Collections.sort(vList, comparator);
		for(int index = 0; index < vList.size(); index++) {
			if(vList.get(index).getValue() < StaticData.MIN_SUPPORT) {
				break;
			}
			Result.vertexRank2Label.put(index, vList.get(index).getKey());
			Result.maxVertexRank++;
		}
		
		eList = new ArrayList<Entry<Integer, Integer>>(edgeLabel2Freq.entrySet());
//		Collections.sort(eList, comparator);
		for(int i = 0; i < eList.size(); i++) {
			for(int j = i; j < eList.size(); j++) {
				Entry<Integer, Integer> entry1 = eList.get(i);
				Entry<Integer, Integer> entry2 = eList.get(j);
				if(entry1.getValue() < entry2.getValue()) {
					eList.set(j, entry1);
					eList.set(i, entry2);
				}
			}
		}
		
		for(int index = 0; index < eList.size(); index++) {
			if(eList.get(index).getValue() < StaticData.MIN_SUPPORT) {
				break;
			}
			Result.edgeRank2Label.put(index, eList.get(index).getKey());
			Result.maxEdgeRank++;
		}
		
		status++;
	}
	
	public static void rebuildGraphSet() {
		if(status != 2) {
			throw new IllegalStateException("调用顺序出错");
		}
		
		Set<Graph> graphSet = GraphSet.getGraphSet();
		for(Iterator<Graph> it = graphSet.iterator(); it.hasNext();) {
			Graph g = it.next();
			Set<Edge> edges = g.getEdges();
			for(Iterator<Edge> eit = edges.iterator(); eit.hasNext();) {
				boolean flag = false;
				Edge e = eit.next();
				int vertex1Label = g.vertex2Rank.get(e.vertex1);
				int vertex2Label = g.vertex2Rank.get(e.vertex2);
				if(vertexLabel2Freq.get(vertex1Label) < StaticData.MIN_SUPPORT) {
					g.vertex2Rank.remove(e.vertex1);
					flag = true;		
				}
				if(vertexLabel2Freq.get(vertex2Label) < StaticData.MIN_SUPPORT) {
					g.vertex2Rank.remove(e.vertex2);
					flag = true;
				}
				if(edgeLabel2Freq.get(e.label) < StaticData.MIN_SUPPORT) {
					flag = true;
				}
				if(flag) {
					eit.remove();
					continue;
				}
//				g.vertex2Rank.put(vertex1, vList.indexOf(new AbstractMap.SimpleEntry<Integer, Integer>(vertex1Label,
//						vertexLabel2Freq.get(vertex1Label))));
//				g.vertex2Rank.put(vertex2, vList.indexOf(new AbstractMap.SimpleEntry<Integer, Integer>(vertex2Label,
//						vertexLabel2Freq.get(vertex2Label))));
				e.label = eList.indexOf(new AbstractMap.SimpleEntry<Integer, Integer>(e.label,
						edgeLabel2Freq.get(e.label)));
			}
			for(Iterator<Entry<Integer, Integer>> vit = g.vertex2Rank.entrySet().iterator(); vit.hasNext();) {
				Entry<Integer, Integer> entry = vit.next();
				int value = entry.getValue();
				entry.setValue(vList.indexOf(new AbstractMap.SimpleEntry<Integer, Integer>(value,
						vertexLabel2Freq.get(value))));
			}
		}
		
		vList = null;
		eList = null;
		vertexLabel2Freq = null;
		edgeLabel2Freq = null;
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
