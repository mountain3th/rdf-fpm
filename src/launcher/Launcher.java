package launcher;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import prediction.Predicate;

import launcher.Debugger.OnTaskFinishedListener;
import mining.Mining;
import mining.Preprocessor;
import mining.TempResult;
import datastructure.Graph;
import datastructure.Graph.Edge;
import datastructure.GraphSet;
import exception.ArgsException;
import exception.MiningException;

public class Launcher {

	public static void main(String[] args) {
		try {
			Mining.init(args);

			Debugger.startTask("main");
			
			Debugger.start();
	
			Debugger.startTask("preprocess", new OnTaskFinishedListener() {
				@Override
				public void onTaskFinished() {
//					Debugger.log("\n处理后的图:\n");
//					Set<Graph> graphs = GraphSet.getGraphSet();
//					int index = 0;
//					for(Iterator<Graph> it = graphs.iterator(); it.hasNext();){
//						Graph g = it.next();
//						Set<Edge> edges = g.getEdges();
//						Debugger.log("T" + index + "\n");
//						index++;
//						for(Iterator<Edge> eit = edges.iterator(); eit.hasNext();) {
//							Edge e = eit.next();
//							Debugger.log(e.toString(g.vertex2Rank, TempResult.vertexRank2Label, TempResult.edgeRank2Label) + "\n");
//						}
//					}
				}
			});
			

			Preprocessor.loadFile(Mining.getFile());
			Preprocessor.relabel();
			Preprocessor.rebuildGraphSet();
			Debugger.finishTask("preprocess");
			
			Mining.start(TempResult.maxVertexRank, TempResult.maxEdgeRank);
			
			Debugger.finishTask("main");
			Debugger.stop();
			
			Predicate.generate();
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
