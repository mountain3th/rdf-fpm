package launcher;

import prediction.Predicate;

import mining.Mining;
import mining.Preprocessor;
import exception.ArgsException;
import exception.MiningException;

public class Launcher {

	public static void main(String[] args) {
		try {
			Mining.init(args);

			Debugger.startTask("main");
			
			Debugger.start();
	
			Debugger.startTask("preprocess");
			
			Preprocessor.loadFile(Mining.file);
			Preprocessor.relabel();
			Preprocessor.rebuildGraphSet();
			Debugger.finishTask("preprocess");
			
			Mining.start();
			
			Predicate.generate();

			Debugger.finishTask("main");
			Debugger.stop();
			
		} catch(ArgsException e) {
			e.printStackTrace();
		} catch(MiningException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
