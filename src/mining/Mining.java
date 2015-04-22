package mining;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import launcher.Debugger;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import datastructure.DFSCode;
import datastructure.DFSCodeStack;
import datastructure.Graph;
import datastructure.GraphSet;
import exception.ArgsException;

/**
 * 褰撳墠鍙敮鎸佷竴骞呭浘鍑虹幇鍚屼竴绫诲彧璁颁竴娆★紝骞朵笖鍙兘浼氬彂鐢熶笉鍙璁＄殑鍚庢灉
 * 
 * @author Three
 *
 */
public class Mining implements Serializable {
	public enum Pattern {
		PATTERN_STRONG,
		PATTERN_WEEK
	}
	
	public transient static int MIN_SUPPORT = 1;
	public transient static int startPoint = -1;
	private transient static Pattern pattern = Pattern.PATTERN_STRONG;
	private transient static File file = null;
	private transient static int fixedThread = 1;

	public static void init(String[] args) throws ArgsException {
		for(int i = 0; i < args.length; i++) {
			String part = args[i];
			// support璁剧疆
			if("-support".equals(part)) {
				i++;
				part = args[i];
				try{
					MIN_SUPPORT = Integer.parseInt(part);
				} catch(NumberFormatException e) {
					throw new ArgsException();
				}
			}
			// pattern璁剧疆
			if("-pattern".equals(part)) {
				i++;
				part = args[i];
				if("strong".equals(part)) {
					pattern = Pattern.PATTERN_STRONG;
				} else if("week".equals(part)) {
					pattern = Pattern.PATTERN_WEEK;
				} else {
					throw new ArgsException();
				}
			}
			// 鏂囦欢妫�煡
			if("-file".equals(part)) {
				i++;
				part = args[i];
				file = new File(part);
				if(!file.exists() || !file.isFile()) {
					throw new ArgsException();
				}
			}
			// 鍒嗗竷寮忔帶鍒�
			if("-spark".equals(part)) {
				
			}
			// 澶氱嚎绋嬫帶鍒�
			if("-thread".equals(part)) {
				i++;
				part = args[i];
				try{
					fixedThread = Integer.parseInt(part);
				} catch(NumberFormatException e) {
					throw new ArgsException();
				}
			}
			// 鏄惁杈撳嚭debug淇℃伅
			if("-debug".equals(part)) {
				Debugger.isDebug = true;
			}
		}
		if(file == null) {
			throw new ArgsException();
		}
	}
	
	public static File getFile() {
		return file;
	}
	
	public static void start(int maxVertexRank, int maxEdgeRank) {
//		ExecutorService executorService = Executors.newSingleThreadExecutor();
		
		SparkConf conf = new SparkConf().setAppName("MiningInSpark");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<Graph> graphs = sc.parallelize(GraphSet.getGraphSet());
		
//		for(int i = 0; i < maxVertexRank; i++) {
			for(int a = 0; a < maxEdgeRank; a++) {
				for(int j = 0; j < maxVertexRank; j++) {
					Debugger.log(startPoint, a, j);
					
					DFSCode code = new DFSCode(-1, -1, startPoint, a, j);
					final DFSCodeStack dfsCodeStack = new DFSCodeStack();
					dfsCodeStack.push(code);
					
//					executorService.execute(new Runnable() {

//						@Override
//						public void run() {
					Debugger.startTask("subGraphMining");
							new Mining().subGraphMining(dfsCodeStack, graphs);
//						}
					Debugger.finishTask("subGraphMining");	
//					});
				}
//			}
		}
		
//		try {
//			executorService.awaitTermination(1, TimeUnit.DAYS);
//			executorService.shutdown();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	private void subGraphMining(final DFSCodeStack dfsCodeStack, JavaRDD<Graph> graphRdd) {
		Map<DFSCode, List<Graph>> supportChecker = new HashMap<DFSCode, List<Graph>>();
		
		// 1. 鍒ゆ柇鏄惁鏈�皬dfs
//		if(!dfsCodeStack.isMin()) {
//			return;
//		}
		
		Queue<DFSCodeStack> queue = new LinkedList<DFSCodeStack>();
		
		JavaRDD<Graph> tempRdd = graphRdd;
		// 2. 妫�煡褰撳墠code鏄惁鏈夋墿灞曠殑鍙兘鎬�
		if(dfsCodeStack.getStack().size() == 1) {
//			int count = 0;
//			for(Iterator<Graph> it = graphItems.iterator(); it.hasNext();) {
//				Graph g = it.next();
//				if(g.hasCandidates(dfsCodeStack.head())) {
//					count++;
//				} else {
//					it.remove();
//				}
//			}
//			if(count >= Mining.MIN_SUPPORT) {
//				Result.add(new DFSCodeStack(dfsCodeStack));
//			} else {
//				return;
//			}
			
			
			tempRdd = graphRdd.filter(new Function<Graph, Boolean>() {

				public Boolean call(Graph g) throws Exception {
					return g.hasCandidates(dfsCodeStack.head());
				}
				
			});
			
			if(tempRdd.count() >= Mining.MIN_SUPPORT) {
				Result.add(new DFSCodeStack(dfsCodeStack));
			} else {
				return;
			}
		} else {
			Result.add(new DFSCodeStack(dfsCodeStack));
		}
		
		Debugger.saveResult(dfsCodeStack);
				
		Debugger.watch();

		// 3. 鎵╁睍骞惰幏寰楀�閫夐泦
		
		List<Graph> graphs = tempRdd.collect();
		for(int index = 0; index < graphs.size(); index++) {
			Graph g = graphs.get(index);
			Set<DFSCode> codes = g.getCandidates(dfsCodeStack);
			if(null == codes || codes.isEmpty()) {
				continue;
			}
			for(Iterator<DFSCode> dit = codes.iterator(); dit.hasNext();) {
				DFSCode dfsCode = dit.next();
				DFSCode tempCode = new DFSCode(dfsCode);
				if(pattern == Pattern.PATTERN_WEEK) {
					tempCode.y = -1;
				}
				if(supportChecker.containsKey(tempCode)) {
					List<Graph> temp = supportChecker.get(tempCode);
					temp.add(g);
				} else {
					List<Graph> temp = new ArrayList<Graph>();
					temp.add(g);
					supportChecker.put(tempCode, temp);
				}
			}
		}
		
		// 4. 鍓灊灏忎簬MIN_SUPPORT鐨刢ode锛岄�褰掕皟鐢╯ubGraphMining
		for(Iterator<Entry<DFSCode, List<Graph>>> it = supportChecker.entrySet().iterator(); it.hasNext();) {
			Entry<DFSCode, List<Graph>> entry = it.next();
			if(entry.getValue().size() >= Mining.MIN_SUPPORT) {
				dfsCodeStack.push(entry.getKey());
				JavaRDD<Graph> rdd = tempRdd.filter(new Function<Graph, Boolean>() {

					public Boolean call(Graph g) throws Exception {
						return g.hasCandidates(dfsCodeStack.head());
					}
					
				});
				subGraphMining(dfsCodeStack, rdd);
				dfsCodeStack.pop();
			}
		}
		
//		for(Iterator<Graph> it = graphItems.iterator(); it.hasNext();) {
//			Graph g = it.next();
//			Set<DFSCode> codes = g.getCandidates(dfsCodeStack);
//			if(null == codes || codes.isEmpty()) {
//				continue;
//			}
//			for(Iterator<DFSCode> dit = codes.iterator(); dit.hasNext();) {
//				DFSCode dfsCode = dit.next();
//				DFSCode tempCode = new DFSCode(dfsCode);
//				if(pattern == Pattern.PATTERN_WEEK) {
//					tempCode.y = -1;
//				}
//				if(supportChecker.containsKey(tempCode)) {
//					Set<Graph> temp = supportChecker.get(tempCode);
//					temp.add(g);
//				} else {
//					Set<Graph> temp = new HashSet<Graph>();
//					temp.add(g);
//					supportChecker.put(tempCode, temp);
//				}
//			}
//		}
//		
//		// 4. 鍓灊灏忎簬MIN_SUPPORT鐨刢ode锛岄�褰掕皟鐢╯ubGraphMining
//		for(Iterator<Entry<DFSCode, List<Graph>>> it = supportChecker.entrySet().iterator(); it.hasNext();) {
//			Entry<DFSCode, Set<Graph>> entry = it.next();
//			if(entry.getValue().size() >= Mining.MIN_SUPPORT) {
//				dfsCodeStack.push(entry.getKey());
//				subGraphMining(dfsCodeStack, entry.getValue());
//				dfsCodeStack.pop();
//			}
//		}
		
	}
}
