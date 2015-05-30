package prediction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mining.TempResult;
import datastructure.GraphSet;


public class Predicate {
	
	public static class Concept {
		int object;
		double confidence;
		int depth;
		
		public Concept(int o, double c, int d) {
			this.object = o;
			this.confidence = c;
			this.depth = d;
		}
		
		@Override
		public String toString() {
			return "" + object + "," + confidence + "," + depth + "\n";
		}
	}
	
	public static void generate() {
		TempResult.cutIfHasNoConcept();
		
		TempResult.print();
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("result/predicates.log")));
			for(int i = 0; i < GraphSet.getGraphSet().size(); i++) {
				List<Concept> concepts = integrate(TempResult.genConcept(i));
				if(!concepts.isEmpty()) {
					bw.write("s " + String.valueOf(i) + "\n");
					for(int j = 0; j < concepts.size(); j++) {
						bw.write(concepts.get(j).toString());
					}
				}
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static List<Concept> integrate(List<Concept> concepts) {
		List<Concept> integratedConcepts = new ArrayList<Concept>();
		
		Map<Integer, Set<Concept>> conceptIntegrater = new HashMap<Integer, Set<Concept>>();
		for(Iterator<Concept> it = concepts.iterator(); it.hasNext();) {
			Concept concept = it.next();
			if(!conceptIntegrater.containsKey(concept.object)) {
				conceptIntegrater.put(concept.object, new HashSet<Concept>());
			}
			conceptIntegrater.get(concept.object).add(concept);
		}
		for(Iterator<Entry<Integer, Set<Concept>>> it = conceptIntegrater.entrySet().iterator(); it.hasNext();) {
			Entry<Integer, Set<Concept>> entry = it.next();
			int object = entry.getKey();
			Set<Concept> conceptSet = entry.getValue();
			double sum = 0;
			int depths = 0;
			boolean flag = false;
			for(Iterator<Concept> cit = conceptSet.iterator(); cit.hasNext();) {
				Concept concept = cit.next();
				if(concept.confidence == 1.0) {
					integratedConcepts.add(new Concept(object, 1.0, 1));
					flag = true;
					break;
				} else {
					sum += concept.confidence * concept.depth;
					depths += concept.depth;
				}
			}
			if(!flag) {
				integratedConcepts.add(new Concept(object, sum / depths, 0));
			}
		}
		
		return integratedConcepts;
	}
}
