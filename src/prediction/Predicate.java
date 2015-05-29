package prediction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import datastructure.GraphSet;

import mining.TempResult;


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
			return "" + object + "," + confidence + "\n";
		}
	}
	
	public static void generate() {
		TempResult.cutIfHasNoConcept();
		
		TempResult.print();
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("result/predicates.log")));
			for(int i = 0; i < GraphSet.getGraphSet().size(); i++) {
				List<Concept> concepts = TempResult.genConcept(i);
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
}
