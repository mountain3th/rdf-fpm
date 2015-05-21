package pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import launcher.Debugger;
import mining.Mining;

public class PatternGenerator {
	
	Map<Integer, Set<TypeGraph>> patternTypes = new HashMap<Integer, Set<TypeGraph>>();
	
	private static class Result {
		static Map<Integer, List<PatternEdgeStack>> results = new HashMap<Integer, List<PatternEdgeStack>>();
		
		static void add(int type, PatternEdgeStack stack) {
			if(!results.containsKey(type)) {
				results.put(type, new ArrayList<PatternEdgeStack>());
			}
			results.get(type).add(new PatternEdgeStack(stack));
		}
	}
	
	private static class PatternEdgeStack {
		Stack<PatternEdge> peStack = new Stack<PatternEdge>();
		
		PatternEdgeStack(PatternEdgeStack stack) {
			for(int i = 0; i < stack.peStack.size(); i++) {
				peStack.push(stack.peStack.get(i));
			}
		}
		
		PatternEdgeStack() {
		}
		
		void push(PatternEdge pe) {
			peStack.push(pe);
		}
		
		PatternEdge peek() {
			return peStack.empty() ? null : peStack.peek();
		}
	}
	
	private static class PatternEdge {
		int predicate;
		int objType;
		
		PatternEdge(int p, int o) {
			this.predicate = p;
			this.objType = o;
		}
		
		@Override
		public int hashCode() {
			return predicate * 10 + objType;
		}
		
		@Override
		public boolean equals(Object o) {
			PatternEdge pe = (PatternEdge) o;
			return pe.predicate == predicate && pe.objType == objType;
		}
	}
	
	private static class TypeEdge {
		int predicate;
		List<Integer> objTypes;
		
		TypeEdge(int predicate, List<Integer> types) {
			this.predicate = predicate;
			this.objTypes = types;
		}
	}
	
	private static class TypeGraph {
		int subType;
		List<TypeEdge> predicates = new ArrayList<TypeEdge>();
		
		int keepIndex = -1;
		List<Integer> indexes = new ArrayList<Integer>();
	
		void addVertex(int vertex, List<Integer> types) {
			predicates.add(new TypeEdge(vertex, types));
		}
		
		void init() {
			Collections.sort(predicates, new Comparator<TypeEdge>() {

				@Override
				public int compare(TypeEdge o1, TypeEdge o2) {
					return o1.predicate < o2.predicate ? -1 : (o1.predicate == o2.predicate ? 0 : 1);
				}
				
			});
		}
		
		Set<PatternEdge> getCandidates(PatternEdge pe) {
			if(pe != null) {
				push(pe);
			}
			
			Set<PatternEdge> pes = new HashSet<PatternEdge>();
			for(int i = keepIndex + 1; i < predicates.size(); i++) {
				TypeEdge te = predicates.get(i);
				for(int j = 0; j < te.objTypes.size(); j++) {
					pes.add(new PatternEdge(te.predicate, te.objTypes.get(j)));
				}
			}
			
			return pes;
		}
		
		void push(PatternEdge pe) {
			out:for(int i = keepIndex + 1; i < predicates.size(); i++) {
				TypeEdge te = predicates.get(i);
				for(int j = 0; j < te.objTypes.size(); j++) {
					if(pe.equals(new PatternEdge(te.predicate, te.objTypes.get(j)))) {
						keepIndex = i;
						break out;
					}
				}
			}
		}
		
		void pop() {
			if(indexes.isEmpty()) {
				return;
			}
			
			int last = indexes.size() - 1;
			indexes.remove(last);
			last = indexes.size() - 1;
			keepIndex = (last >= 0) ? indexes.get(last) : -1;
		}
	}
	
	private void loadFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line = null;
		TypeGraph tGraph = null;
		while((line = br.readLine()) != null) {
			String[] content = line.split("\\s+");
			
			if("t".equals(content[0])) {
				if(tGraph != null) {
					tGraph.init();
				}
				tGraph = new TypeGraph();
			} else if("v".equals(content[0])) {
				int vertex = Integer.valueOf(content[1]);
				String[] labels = content[2].split(",");
				
				if(vertex == 0) {
					for(int i = 0; i < labels.length; i++) {
						int label = Integer.valueOf(labels[i]);
						if(!patternTypes.containsKey(label)) {
							patternTypes.put(label, new HashSet<TypeGraph>());
						}
						patternTypes.get(label).add(tGraph);
					}
					
				} else {
					List<Integer> types = new ArrayList<Integer>();
					for(int i = 0; i < labels.length; i++) {
						int label = Integer.valueOf(labels[i]);
						types.add(label);
					}
					tGraph.addVertex(vertex, types);
				}
			} else if("e".equals(content[0])) {
				int vertex2 = Integer.valueOf(content[2]);
				int label = Integer.valueOf(content[3]);

				for(int i = 0; i < tGraph.predicates.size(); i++) {
					if(tGraph.predicates.get(i).predicate == vertex2) {
						tGraph.predicates.get(i).predicate = label;
						break;
					}
				}
			}
		}

		br.close();
	}
	
	private void pop(Set<TypeGraph> graphItems) {
		for(Iterator<TypeGraph> it = graphItems.iterator(); it.hasNext();) {
			TypeGraph tg = it.next();
			tg.pop();
		}
	}
	
	private void mining(int type, PatternEdgeStack stack, Set<TypeGraph> graphItems) {
		Map<PatternEdge, Set<TypeGraph>> supportChecker = new HashMap<PatternEdge, Set<TypeGraph>>();
		
		Debugger.startTask("mining");
		PatternEdge pe = stack.peek();
		for(Iterator<TypeGraph> it = graphItems.iterator(); it.hasNext();) {
			TypeGraph tg = it.next();
			Set<PatternEdge> edgesCandidates = tg.getCandidates(pe);
			if(edgesCandidates == null || edgesCandidates.isEmpty()) {
				continue;
			} else {
				for(Iterator<PatternEdge> eit = edgesCandidates.iterator(); eit.hasNext();) {
					PatternEdge e = eit.next();
					if(!supportChecker.containsKey(e)) {
						supportChecker.put(e, new HashSet<TypeGraph>());
					}
					supportChecker.get(e).add(tg);
				}
			}
		}
		
		for(Iterator<Entry<PatternEdge, Set<TypeGraph>>> it = supportChecker.entrySet().iterator(); it.hasNext();) {
			Entry<PatternEdge, Set<TypeGraph>> entry = it.next();
			int size = entry.getValue().size();
			if(size >= Mining.MIN_SUPPORT) {
				stack.push(entry.getKey());
				Result.add(type, stack);
				mining(type, stack, entry.getValue());
			}
		}
		
		pop(graphItems);
		
		Debugger.finishTask("mining");
	}
	
	public static void generate() throws IOException {
		PatternGenerator patternG = new PatternGenerator();
		
		Debugger.startTask("loadFile");
		patternG.loadFile(Mining.typeFile);
		Debugger.finishTask("loadFile");
		
		for(Iterator<Entry<Integer, Set<TypeGraph>>> it = patternG.patternTypes.entrySet().iterator(); it.hasNext();) {
			Entry<Integer, Set<TypeGraph>> entry = it.next();
			System.out.println(entry.getKey() + " " + entry.getValue().size());
			if(entry.getValue().size() > Mining.MIN_SUPPORT) {
				patternG.mining(entry.getKey(), new PatternEdgeStack(), entry.getValue());
			}
		}
	}
	
}
