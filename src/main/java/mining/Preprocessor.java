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

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import datastructure.Graph;
import datastructure.Graph.Edge;
import datastructure.GraphInRDD;
import datastructure.GraphSet;
import exception.MiningException;

public class Preprocessor {
	private static Map<Integer, Integer> vertexLabel2Freq = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> edgeLabel2Freq = new HashMap<Integer, Integer>();
	private static int[] vertexLabel2Rank = new int[5000000];
	private static int[] edgeLabel2Rank = new int[1000];
	
	public static void loadFile(File file) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		Map<Integer, Integer> tmp = null;
		Graph graph = null;
		
		Debugger.startTask("loadFile");
		
		int graphCount = -1;
		while((line = br.readLine()) != null) {
			String[] content = line.split("\\s+");
			
			if("t".equals(content[0])) {
				graphCount++;
				graph = new Graph(graphCount);
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
					throw new MiningException();
				}
//				vertex1 = tmp.get(vertex1);
//				vertex2 = tmp.get(vertex2);
				int label = Integer.valueOf(content[3]);
				int value = edgeLabel2Freq.containsKey(label) ? edgeLabel2Freq.get(label) + 1 : 1;
				edgeLabel2Freq.put(label, value);
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
				Debugger.log("\n顶点Rank");
				for(Iterator<Entry<Integer, Integer>> it = TempResult.vertexRank2Label.entrySet().iterator(); it.hasNext();) {
					Entry<Integer, Integer> entry = it.next();
					Debugger.log(entry.getKey() + " = " + entry.getValue() + ", ");
				}
				Debugger.log("\n边Rank");
				for(Iterator<Entry<Integer, Integer>> it = TempResult.edgeRank2Label.entrySet().iterator(); it.hasNext();) {
					Entry<Integer, Integer> entry = it.next();
					Debugger.log(entry.getKey() + " = " + entry.getValue() + ", ");
				}
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
		
		
		List<Graph> graphSet = GraphSet.getGraphSet();
		List<GraphInRDD> gInRddList = new ArrayList<GraphInRDD>();
		for(Iterator<Graph> it = graphSet.iterator(); it.hasNext();) {
			Graph g = it.next();
			gInRddList.add(new GraphInRDD(g, vertexLabel2Rank, edgeLabel2Rank));
		}
		
		SparkConf conf = new SparkConf().setAppName("MiningInSpark").set("spark.akka.frameSize", "500");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<GraphInRDD> graphRDD = sc.parallelize(gInRddList);
		long a = graphRDD.map(new Function<GraphInRDD, Graph>() {

			@Override
			public Graph call(GraphInRDD gInRdd) throws Exception {
				gInRdd.rebuild();
				
				return gInRdd.getGraph();
			}
			
		}).count();
		
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
