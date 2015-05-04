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
	public static Set<MiningData> dataSet = new HashSet<MiningData>();
	public static File file = null;
	
//	private static int fixedThread = 1;
	
	public static class MiningData {
		int edgeLabel;
		int vertexLabel;
		
		MiningData(int er, int vr) {
			this.edgeLabel = er;
			this.vertexLabel = vr;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof MiningData) {
				return this.edgeLabel == ((MiningData) o).edgeLabel && 
						this.vertexLabel == ((MiningData) o).vertexLabel;
			}
			
			return false;
		}
		
		@Override
		public int hashCode() {
			return edgeLabel + vertexLabel;
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
			// 文件检查
			if("-file".equals(part)) {
				i++;
				part = args[i];
				file = new File(part);
				if(!file.exists() || !file.isFile()) {
					throw new ArgsException(2);
				}
			}
			// 多线程控制
//			if("-thread".equals(part)) {
//				i++;
//				part = args[i];
//				try{
//					fixedThread = Integer.parseInt(part);
//				} catch(NumberFormatException e) {
//					throw new ArgsException();
//				}
//			}
			// 是否输出debug信息
			if("-debug".equals(part)) {
				Debugger.isDebug = true;
			}
		}
		if(file == null) {
			throw new ArgsException(3);
		}
	}
	
	
	public static void start() {
//		for(int i = 0; i < maxVertexRank; i++) {
//			for(int a = 0; a < maxEdgeRank; a++) {
//				for(int j = 0; j < maxVertexRank; j++) {
		int index = 0;
		for(Iterator<MiningData> it = dataSet.iterator(); it.hasNext();) {	
			MiningData md = it.next();
		
			Debugger.log(String.valueOf(index) + "\n");
			index++;
			
			DFSCode code = new DFSCode(-1, -1, startPoint, md.edgeLabel, md.vertexLabel);
			final DFSCodeStack dfsCodeStack = new DFSCodeStack();
			dfsCodeStack.push(code);
			Set<Graph> graphItems = new HashSet<Graph>(GraphSet.getGraphSet());
			
			Debugger.startTask("subGraphMining");
			new Mining().subGraphMining(dfsCodeStack, graphItems);
			Debugger.finishTask("subGraphMining");	
		}
	}
	
	private void subGraphMining(DFSCodeStack dfsCodeStack, Set<Graph> graphItems) {
		Map<DFSCode, Set<Graph>> supportChecker = new HashMap<DFSCode, Set<Graph>>();
		
		// 1. 判断是否最小dfs
//		if(!dfsCodeStack.isMin()) {
//			return;
//		}
		
		Debugger.startTask("checkHasCandidates " + dfsCodeStack.peek());
		// 2. 检查当前code是否有扩展的可能性
		if(dfsCodeStack.getStack().size() == 1) {
			int count = 0;
			for(Iterator<Graph> it = graphItems.iterator(); it.hasNext();) {
				Graph g = it.next();
				if(g.hasCandidates(dfsCodeStack.head())) {
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
			Set<DFSCode> codes = g.getCandidates(dfsCodeStack);
			if(null == codes || codes.isEmpty()) {
				continue;
			}
			for(Iterator<DFSCode> dit = codes.iterator(); dit.hasNext();) {
				DFSCode dfsCode = dit.next();
				if(supportChecker.containsKey(dfsCode)) {
					Set<Graph> temp = supportChecker.get(dfsCode);
					temp.add(g);
				} else {
					Set<Graph> temp = new HashSet<Graph>();
					temp.add(g);
					supportChecker.put(dfsCode, temp);
				}
			}
		}
		Debugger.finishTask("getCandidates " + dfsCodeStack.peek());
		
		// 4. 剪枝小于MIN_SUPPORT的code，递归调用subGraphMining
		for(Iterator<Entry<DFSCode, Set<Graph>>> it = supportChecker.entrySet().iterator(); it.hasNext();) {
			Entry<DFSCode, Set<Graph>> entry = it.next();
			int size = entry.getValue().size();
			double confidence = (double) size / (double) graphItems.size();
			if(size >= Mining.MIN_SUPPORT) {
				continue;
			} else if(confidence >= Mining.CONFIDENCE) {
				dfsCodeStack.push(entry.getKey());
				subGraphMining(dfsCodeStack, entry.getValue());
				dfsCodeStack.pop();
			}
		}
		
	}
}
