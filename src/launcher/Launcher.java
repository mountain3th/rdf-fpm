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
			Mining.init(args);

			Debugger.start();
			
			Thread.sleep(3000);
			Clocker projectClocker = ClockerManager.getClocker("project");
			projectClocker.start();
	
			Preprocessor.loadFile(Mining.getFile());
			Preprocessor.relabel();
			Preprocessor.rebuildGraphSet();
			Debugger.setOk("preprocess");
			
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
