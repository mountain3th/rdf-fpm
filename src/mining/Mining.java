package mining;


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

/**
 * �ݲ�֧��ͬһ��ͼ�г���һ���ıߵ���������߽���ǲ�ȷ����
 * 
 * @author Three
 *
 */
public class Mining {
	
	private static Set<Graph> graphItems = GraphSet.getGraphSet();
	
	public static void start(int maxVertexRank, int maxEdgeRank) {
		for(int i = 0; i < maxVertexRank; i++) {
			for(int a = 0; a < maxEdgeRank; a++) {
				for(int j = 0; j < maxVertexRank; j++) {
					DFSCode code = new DFSCode(0, 1, i, a, j);
					DFSCodeStack dfsCodeStack = new DFSCodeStack();
					dfsCodeStack.push(code);
					
					subGraphMining(dfsCodeStack, graphItems);
					for(Iterator<Graph> it = graphItems.iterator(); it.hasNext();) {
						Graph g = it.next();
						g.clear();
					}
				}
			}
		}
	}
	
	public static void subGraphMining(DFSCodeStack dfsCodeStack, Set<Graph> graphItems) {
		Map<DFSCode, Set<Graph>> supportChecker = new HashMap<DFSCode, Set<Graph>>();
		
		// 1. �ж��Ƿ���Сdfs
		if(!dfsCodeStack.isMin()) {
			return;
		}
		
		// 2. ƥ��ɹ�������Result
		Result.add(new DFSCodeStack(dfsCodeStack));
		
		// 3. ��������ѡ��
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
		
		// 4. ��֦С��MIN_SUPPORT�ıߣ��ݹ����subGraphMining
		for(Iterator<Entry<DFSCode, Set<Graph>>> it = supportChecker.entrySet().iterator(); it.hasNext();) {
			Entry<DFSCode, Set<Graph>> entry = it.next();
			if(entry.getValue().size() >= StaticData.MIN_SUPPORT) {
				dfsCodeStack.push(entry.getKey());
				subGraphMining(dfsCodeStack, entry.getValue());
			}
		}
		
		dfsCodeStack.pop();
	}
}
