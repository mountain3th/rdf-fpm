package launcher;

import mining.Mining;
import mining.Preprocessor;
import pattern.PatternGenerator;
import prediction.Predicate;
import exception.ArgsException;
import exception.DebuggerException;
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
			
			Mining.start();
			
			Predicate.generate();
//
			
//			Debugger.startTask("patternGenerate");
			
//			PatternGenerator.generate();
			
//			Debugger.finishTask("patternGenerate");
	
			Debugger.finishTask("main");
			Debugger.stop();
			
		} catch(ArgsException e) {
			e.printStackTrace();
		} catch(MiningException e) {
			e.printStackTrace();
		} catch (DebuggerException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
