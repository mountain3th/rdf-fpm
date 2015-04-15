package launcher;

import launcher.ClockerManager.Clocker;
import mining.Mining;
import mining.Preprocessor;
import mining.Result;
import exception.ArgsException;
import exception.MiningException;

public class Launcher {

	public static void main(String[] args) {
		try {
			Clocker projectClocker = ClockerManager.getClocker("project");
			projectClocker.start();
			Mining.init(args);
	
			Preprocessor.loadFile(Mining.getFile());
			Preprocessor.relabel();
			Preprocessor.rebuildGraphSet();
			
//			Set<Graph> graphs = GraphSet.getGraphSet();
//			Debugger.print(graphs);
			
			Debugger.print();
			
			Mining.start(Result.maxVertexRank, Result.maxEdgeRank);
			projectClocker.stop();
			projectClocker.show();
			
			Result.print();
			
		} catch(ArgsException e) {
			
		} catch(MiningException e) {
			System.out.println("\ndebug: ");
			
			e.print();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
