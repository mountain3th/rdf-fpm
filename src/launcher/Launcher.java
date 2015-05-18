package launcher;

import java.io.File;

import mining.Mining;
import mining.Preprocessor;
import mining.TempResult;
import prediction.Predicate;
import exception.ArgsException;
import exception.MiningException;

public class Launcher {

	public static void main(String[] args) {
		try {
			Mining.init(args);

			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
			
			Debugger.startTask("main");
			
			Debugger.start();
	
			Debugger.startTask("preprocess");
			
			Preprocessor.prepare();
			Preprocessor.loadFile(Mining.inputFile);
			Preprocessor.relabel();
			Preprocessor.rebuildGraphSet();
			Debugger.finishTask("preprocess");
			
			System.out.println(TempResult.vertexRank2Label.get(20));
			
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
