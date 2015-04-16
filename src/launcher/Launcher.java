package launcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import launcher.Debugger.OnTaskFinishedListener;
import mining.Mining;
import mining.Preprocessor;
import mining.Result;
import datastructure.Graph;
import datastructure.Graph.Edge;
import datastructure.GraphSet;
import exception.ArgsException;
import exception.MiningException;

public class Launcher {

	public static void main(String[] args) {
		try {
			Mining.init(args);

			Debugger.startTask("preprocess", new OnTaskFinishedListener() {
				@Override
				public void onTaskFinished() {
					try {
						Debugger.log("\n处理后的图:\n");
						Set<Graph> graphs = GraphSet.getGraphSet();
						for(Iterator<Graph> it = graphs.iterator(); it.hasNext();){
							Graph g = it.next();
							Set<Edge> edges = g.getEdges();
							Debugger.log("T\n");
							for(Iterator<Edge> eit = edges.iterator(); eit.hasNext();) {
								Edge e = eit.next();
								Debugger.log(e.toString(g.vertex2Rank, Result.vertexRank2Label, Result.edgeRank2Label) + "\n");
							}
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			Debugger.start();

			Preprocessor.loadFile(Mining.getFile());
			Preprocessor.relabel();
			Preprocessor.rebuildGraphSet();
			Debugger.finishTask("preprocess");
			
			Debugger.flush();
			Mining.start(Result.maxVertexRank, Result.maxEdgeRank);
			
			
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
