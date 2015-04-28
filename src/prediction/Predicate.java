package prediction;

public class Predicate {
	
	public static class Concept {
		int object;
		int confidency;
		int depth;
		
		Concept(int o, int c, int d) {
			this.object = o;
			this.confidency = c;
			this.depth = d;
		}
	}
	
	public static void generate() {
		for(int index = 0; index < 10000; index++) {
			generate(index);
		}
	}
	
	private static void generate(int subject) {
		
	}
}
