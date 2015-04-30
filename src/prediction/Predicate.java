package prediction;

import java.util.List;

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
			return "" + object + " : " + confidence;
		}
	}
	
	public static void generate() {
		for(int i = 0; i < 10000; i++) {
			List<Concept> concepts = TempResult.genConcept(i);
			for(int j = 0; j < concepts.size(); j++) {
				System.out.print(concepts.get(j));
			}
		}
	}
}
