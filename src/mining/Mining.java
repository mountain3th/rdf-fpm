package mining;


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import launcher.Debugger;
import datastructure.DFSCode;
import datastructure.DFSCode.SimDFSCode;
import datastructure.DFSCodeStack;
import datastructure.Graph;
import datastructure.GraphSet;
import exception.ArgsException;

/**
 * 当前只支持一幅图出现同一类只记一次，并且可能会发生不可预计的后果
 * 
 * @author Three
 *
 */
public class Mining {
	public enum Pattern {
		PATTERN_STRONG,
		PATTERN_WEEK
	}
	
	public static int MIN_SUPPORT = 1;
	public static double CONFIDENCE = 0.75;
	public static int startPoint = -1;
	public static File inputFile = null;
	public static Set<StrongMiningData> smDataSet = new HashSet<StrongMiningData>();
	public static WeekMiningData[] wmDataSet;
//	public static Set<Mining>
	
	public static class StrongMiningData {
		int edgeLabel;
		int vertexLabel;
		
		StrongMiningData(int er, int vr) {
			this.edgeLabel = er;
			this.vertexLabel = vr;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof StrongMiningData) {
				return this.edgeLabel == ((StrongMiningData) o).edgeLabel && 
						this.vertexLabel == ((StrongMiningData) o).vertexLabel;
			}
			
			return false;
		}
		
		@Override
		public int hashCode() {
			return edgeLabel + vertexLabel;
		}
	}
	
	public static class WeekMiningData {
		Set<Graph> graphs;
		
		WeekMiningData(Graph g) {
			graphs = new HashSet<Graph>();
			graphs.add(g);
		}
		
		public void addGraph(Graph g) {
			graphs.add(g);
		}
	}

	public static void init(String[] args) throws ArgsException {
		for(int i = 0; i < args.length; i++) {
			String part = args[i];
			// support设置
			if("-support".equals(part)) {
				i++;
				part = args[i];
				try{
					MIN_SUPPORT = Integer.parseInt(part);
				} catch(NumberFormatException e) {
					throw new ArgsException(0);
				}
			}
			// Confidence
			if("-confidence".equals(part)) {
				i++;
				part = args[i];
				try{
					CONFIDENCE = Double.parseDouble(part);
				} catch(NumberFormatException e) {
					throw new ArgsException(1);
				}
			}
			// 原始文件
			if("-file".equals(part)) {
				i++;
				part = args[i];
				inputFile = new File(part);
				if(!inputFile.exists() || !inputFile.isFile()) {
					throw new ArgsException(5);
				}
			}
			// 是否输出debug信息
			if("-debug".equals(part)) {
				Debugger.isDebug = true;
			}
		}
		if(inputFile == null) {
			throw new ArgsException(4);
		}
	}
	
	
	public static void start() {
//		for(int i = 0; i < maxVertexRank; i++) {
//			for(int a = 0; a < maxEdgeRank; a++) {
//				for(int j = 0; j < maxVertexRank; j++) {
		int index = 0;
		
		Debugger.startTask("strongMining");
//		for(Iterator<StrongMiningData> it = smDataSet.iterator(); it.hasNext();) {	
//			StrongMiningData md = it.next();
			StrongMiningData md = new StrongMiningData(20, 6);
		
			Debugger.log(String.valueOf(index) + "\n");
			index++;
			
			DFSCode code = new DFSCode(-1, -1, startPoint, md.edgeLabel, md.vertexLabel);
			final DFSCodeStack dfsCodeStack = new DFSCodeStack();
			dfsCodeStack.push(code);
			Set<Graph> graphItems = new HashSet<Graph>(GraphSet.getGraphSet());
			
			Debugger.startTask("subGraphMining");
			new Mining().subGraphMining(Pattern.PATTERN_STRONG, dfsCodeStack, graphItems);
			Debugger.finishTask("subGraphMining");	
//		}
		Debugger.finishTask("strongMining");
		
		Debugger.startTask("weekMining");
//		for(index = 0; index < wmDataSet.length; index++) {
//			WeekMiningData wmd = wmDataSet[index];
//			DFSCode code = new DFSCode(-1, -1, startPoint, index, -1);
//			final DFSCodeStack dfsCodeStack = new DFSCodeStack();
//			dfsCodeStack.push(code);
//			
//			Set<Graph> graphItems = new HashSet<Graph>(wmd.graphs);
//			new Mining().subGraphMining(Pattern.PATTERN_WEEK, dfsCodeStack, graphItems);
//		}
		Debugger.finishTask("weekMining");
	}
	
	private void subGraphMining(Pattern pattern, DFSCodeStack dfsCodeStack, Set<Graph> graphItems) {
//		Map<DFSCode, Set<Graph>> supportChecker = new HashMap<DFSCode, Set<Graph>>();
		
		// 1. 判断是否最小dfs
//		if(!dfsCodeStack.isMin()) {
//			return;
//		}

		SupportChecker supportChecker = new SupportChecker(pattern);
		
		Debugger.startTask("checkHasCandidates " + dfsCodeStack.peek());
		// 2. 检查当前code是否有扩展的可能性
		if(dfsCodeStack.getStack().size() == 1) {
			if(TempResult.hasConcept(dfsCodeStack.peek().a)) {
				Debugger.finishTask("checkHasCandidates " + dfsCodeStack.peek());
				return;
			}
			
			int count = 0;
			for(Iterator<Graph> it = graphItems.iterator(); it.hasNext();) {
				Graph g = it.next();
				if(g.hasCandidates(pattern, dfsCodeStack.head())) {
					count++;
				} else {
					it.remove();
				}
			}
			if(count >= Mining.MIN_SUPPORT) {
				TempResult.add(new DFSCodeStack(dfsCodeStack), graphItems);
			} else {
				Debugger.finishTask("checkHasCandidates " + dfsCodeStack.peek());
				return;
			}
		} else {
			TempResult.add(new DFSCodeStack(dfsCodeStack), graphItems);
		}
		Debugger.finishTask("checkHasCandidates " + dfsCodeStack.peek());
		
		Debugger.saveResult(dfsCodeStack);
				
		Debugger.startTask("getCandidates " + dfsCodeStack.peek());
		// 3. 扩展并获得候选集
		for(Iterator<Graph> it = graphItems.iterator(); it.hasNext();) {
			Graph g = it.next();
			Set<DFSCode> codes = g.getCandidates(pattern, dfsCodeStack);
			if(null == codes || codes.isEmpty()) {
				continue;
			}
			for(Iterator<DFSCode> dit = codes.iterator(); dit.hasNext();) {
				DFSCode dfsCode = dit.next();
				Debugger.log(dfsCode + "\n");
				supportChecker.add(dfsCode, g);
			}
		}
		Debugger.finishTask("getCandidates " + dfsCodeStack.peek());
		return;
		
		// 4. 继续扩展，递归调用
//		if(pattern == Pattern.PATTERN_STRONG) {
//			for(Iterator<Entry<DFSCode, Set<Graph>>> it = supportChecker.getStrongIterator(); it.hasNext();) {
//				Entry<DFSCode, Set<Graph>> entry = it.next();
//				
//				int size = entry.getValue().size();
//				double confidence = (double) size / (double) graphItems.size();
//				if(confidence >= Mining.CONFIDENCE) {
//					dfsCodeStack.push(entry.getKey());
//					subGraphMining(pattern, dfsCodeStack, entry.getValue());
//					dfsCodeStack.pop();
//					pop(entry.getValue());
//				}
//			}
//		} else {
//			for(Iterator<Entry<Integer, Set<Graph>>> it = supportChecker.getWeekIterator(); it.hasNext();) {
//				Entry<Integer, Set<Graph>> entry = it.next();
//				int size = entry.getValue().size();
//				double confidence = (double) size / (double) graphItems.size();
//				if(confidence >= Mining.CONFIDENCE) {
//					int a = entry.getKey();
//					Set<Graph> graphs = entry.getValue();
//					
//					dfsCodeStack.push(new DFSCode(-1, -1, startPoint, a, -1));
//					subGraphMining(pattern, dfsCodeStack, graphs);
//					dfsCodeStack.pop();
//					pop(graphs);
//				}
//			}
//		}
//		
//		// 5. 检查是否已经有type生成
//		for(Iterator<Entry<SimDFSCode, Set<Graph>>> it = supportChecker.getConceptIterator(); it.hasNext();) {
//			Entry<SimDFSCode, Set<Graph>> entry = it.next();
//			
//			int size = entry.getValue().size();
//			double confidence = (double) size / (double) graphItems.size();
//			if(confidence >= Mining.CONFIDENCE) {
//				SimDFSCode sdc = entry.getKey();
//				int a = sdc.a;
//				int y = sdc.y;
//				dfsCodeStack.push(new DFSCode(-1, -1, startPoint, a, y));
//
//				TempResult.add(dfsCodeStack, entry.getValue());
//				
//				dfsCodeStack.pop();
//			}
//		}
	}
	
	private void pop(Set<Graph> graphs) {
		for(Iterator<Graph> it = graphs.iterator(); it.hasNext();) {
			Graph g = it.next();
			g.pop();
		}
	}
	
	private static class SupportChecker {
		Map<DFSCode, Set<Graph>> strongChecker;
		Map<Integer, Set<Graph>> weekChecker;
		Map<SimDFSCode, Set<Graph>> conceptChecker;
		
		Pattern pattern;
		
		SupportChecker(Pattern pattern) {
			strongChecker = new HashMap<DFSCode, Set<Graph>>();
			weekChecker = new HashMap<Integer, Set<Graph>>();
			conceptChecker = new HashMap<SimDFSCode, Set<Graph>>();
			this.pattern = pattern;
		}
		
		Iterator<Entry<DFSCode, Set<Graph>>> getStrongIterator() {
			return strongChecker.entrySet().iterator();
		}
		
		Iterator<Entry<Integer, Set<Graph>>> getWeekIterator() {
			return weekChecker.entrySet().iterator();
		}
		
		Iterator<Entry<SimDFSCode, Set<Graph>>> getConceptIterator() {
			return conceptChecker.entrySet().iterator();
		}
		
		void add(DFSCode code, Graph g) {
			if(pattern == Pattern.PATTERN_STRONG) {
				check(code, g);
			} else if(TempResult.hasConcept(code.a)){
				check(code.toSimDFSCode(), g);
			} else {
				check(code.a, g);
			}
		}
		
		void check(DFSCode code, Graph g) {
			if(strongChecker.containsKey(code)) {
				Set<Graph> temp = strongChecker.get(code);
				temp.add(g);
			} else {
				Set<Graph> temp = new HashSet<Graph>();
				temp.add(g);
				strongChecker.put(code, temp);
			}
		}

		void check(int a, Graph g) {
			if(weekChecker.containsKey(a)) {
				Set<Graph> temp = weekChecker.get(a);
				temp.add(g);
			} else {
				Set<Graph> temp = new HashSet<Graph>();
				temp.add(g);
				weekChecker.put(a, temp);
			}
		}
		
		void check(SimDFSCode sdc, Graph g) {
			if(conceptChecker.containsKey(sdc)) {
				Set<Graph> temp = conceptChecker.get(sdc);
				temp.add(g);
			} else {
				Set<Graph> temp = new HashSet<Graph>();
				temp.add(g);
				conceptChecker.put(sdc, temp);
			}
		}
	}
	
}
