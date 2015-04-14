package mining;


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	enum Pattern {
		PATTERN_STRONG,
		PATTERN_WEEK
	}
	
	private static Pattern pattern = Pattern.PATTERN_STRONG;
	private static Set<Graph> graphItems = GraphSet.getGraphSet();
	private static File file = null;

	public static void init(String[] args) throws ArgsException {
		for(int i = 0; i < args.length; i++) {
			String part = args[i];
			// pattern设置
			if("-p".equals(part)) {
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
			// 文件检查
			if("-f".equals(part)) {
				i++;
				part = args[i];
				file = new File(part);
				if(!file.exists() || !file.isFile()) {
					throw new ArgsException();
				}
			}
			// 分布式控制
			if("-spark".equals(part)) {
				
			}
			// 多线程控制
			if("-j".equals(part)) {
				
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
		for(int i = 0; i < maxVertexRank; i++) {
			for(int a = 0; a < maxEdgeRank; a++) {
				for(int j = 0; j < maxVertexRank; j++) {
					DFSCode code = new DFSCode(0, 1, i, a, j);
					DFSCodeStack dfsCodeStack = new DFSCodeStack();
					dfsCodeStack.push(code);
					
					subGraphMining(dfsCodeStack, graphItems);
				}
			}
		}
	}
	
	private static void subGraphMining(DFSCodeStack dfsCodeStack, Set<Graph> graphItems) {
		Map<DFSCode, Set<Graph>> supportChecker = new HashMap<DFSCode, Set<Graph>>();
		
		// 1. 判断是否最小dfs
		if(!dfsCodeStack.isMin()) {
			return;
		}
		
		// 2. 检查当前code是否有扩展的可能性
		if(dfsCodeStack.getStack().size() == 1) {
			int count = 0;
			for(Iterator<Graph> it = graphItems.iterator(); it.hasNext();) {
				Graph g = it.next();
				if(g.hasCandidates(dfsCodeStack.head())) {
					count++;
				}
			}
			if(count >= StaticData.MIN_SUPPORT) {
				Result.add(new DFSCodeStack(dfsCodeStack));
			}
		} else {
			Result.add(new DFSCodeStack(dfsCodeStack));
		}
				
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
		
		// 4. 剪枝小于MIN_SUPPORT的code，递归调用subGraphMining
		for(Iterator<Entry<DFSCode, Set<Graph>>> it = supportChecker.entrySet().iterator(); it.hasNext();) {
			Entry<DFSCode, Set<Graph>> entry = it.next();
			if(entry.getValue().size() >= StaticData.MIN_SUPPORT) {
				dfsCodeStack.push(entry.getKey());
				subGraphMining(dfsCodeStack, entry.getValue());
				dfsCodeStack.pop();
			}
		}
		
	}
}
