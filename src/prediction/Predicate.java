package prediction;

import java.util.List;

import launcher.Debugger;
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
//		TempResult.print();
		
		TempResult.cutIfHasNoConcept();
		
//		Debugger.log("\nAfter cut\n");
//		
//		TempResult.print();
		
		for(int i = 0; i < 5000000; i++) {
			List<Concept> concepts = TempResult.genConcept(i);
			if(!concepts.isEmpty()) {
				Debugger.log("s " + String.valueOf(i) + "\n");
				for(int j = 0; j < concepts.size(); j++) {
					Debugger.log(concepts.get(j).toString());
				}
			}
		}
	}
}
