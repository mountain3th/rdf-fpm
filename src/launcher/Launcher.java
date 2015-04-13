package launcher;

import java.io.File;
import mining.Mining;
import mining.Preprocessor;
import mining.Result;
import exception.MiningException;

public class Launcher {

	public static void main(String[] args) {
		try {
			Preprocessor.loadFile(new File("test1"));
			Preprocessor.relabel();
			Preprocessor.rebuildGraphSet();
			
//			Set<Graph> graphs = GraphSet.getGraphSet();
//			Debugger.print(graphs);
			
			Debugger.print();
			
			Mining.start(Result.maxVertexRank, Result.maxEdgeRank);
			
			Result.print();
			
		} catch(MiningException e) {
			System.out.println("\ndebug: ");
			
			e.print();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
