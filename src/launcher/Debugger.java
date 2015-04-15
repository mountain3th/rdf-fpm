package launcher;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import mining.Result;
import datastructure.Graph;
import datastructure.Graph.Edge;
import datastructure.GraphSet;

public class Debugger implements Runnable {
	
	public static boolean isDebug = false;
	private static boolean isOk = false;
	
	interface onTaskFinishedListener {
		void onTaskFinished();
	}
	
	private static Queue<Task> queue = new LinkedList<Task>();
	
	private static abstract class Task implements onTaskFinishedListener {
		String theme;
		
		Task(String theme) {
			this.theme = theme;
		}
		
		@Override
		public String toString() {
			return this.theme + "进行中...";
		}
	}
	
	public static boolean setOk(String theme) {
		synchronized(queue) {
			if(queue.peek().theme.equals(theme)) {
				queue.notify();
				return true;
			}
			
			return false;
		}
	}
	
	public static void start() {
		if(!isDebug) {
			return;
		}
		
		queue.add(new Task("relabel") {

			@Override
			public void onTaskFinished() {
				System.out.println("\n顶点Rank");
				for(Iterator<Entry<Integer, Integer>> it = Result.vertexRank2Label.entrySet().iterator(); it.hasNext();) {
					Entry<Integer, Integer> entry = it.next();
					System.out.print(entry.getKey() + " = " + entry.getValue() + ", ");
				}
				System.out.println("\n边Rank");
				for(Iterator<Entry<Integer, Integer>> it = Result.edgeRank2Label.entrySet().iterator(); it.hasNext();) {
					Entry<Integer, Integer> entry = it.next();
					System.out.print(entry.getKey() + " = " + entry.getValue() + ", ");
				}
				System.out.println();
			}
		});
		
		queue.add(new Task("preprocess") {

			@Override
			public void onTaskFinished() {
				System.out.println("\n处理后的图:");
				Set<Graph> graphs = GraphSet.getGraphSet();
				for(Iterator<Graph> it = graphs.iterator(); it.hasNext();){
					Graph g = it.next();
					Set<Edge> edges = g.getEdges();
					System.out.println("T");
					for(Iterator<Edge> eit = edges.iterator(); eit.hasNext();) {
						Edge e = eit.next();
						System.out.println(e.toString(g.vertex2Rank, Result.vertexRank2Label, Result.edgeRank2Label));
					}
				}
			}
			
		});
		
		new Thread(new Debugger()).start();
	}
	
	@Override
	public void run() {
		synchronized(queue) {
			while(!queue.isEmpty()) {
				System.out.println();
				Task task = queue.peek();
				System.out.print(task);
				
				try {
					queue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("OK");
				queue.poll();
			}
		}
	}
}
