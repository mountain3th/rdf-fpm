package launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;

import mining.TempResult;
import datastructure.DFSCode;
import datastructure.DFSCodeStack;

public class Debugger implements Runnable {
	
	public static boolean isDebug = false;
	
	public interface OnTaskFinishedListener {
		void onTaskFinished();
	}
	
	private static String logFile = "log/mining.log";
	private static String resultFile = "log/result.log";
	private static Stack<Task> taskStack = new Stack<Task>();
	private static BufferedWriter bw;
	private static BufferedWriter bs;
	
	private static class Clocker {
		private long startTime;
		private long stopTime;
		
		void start() {
			startTime = System.currentTimeMillis();
		}
		
		void stop() {
			stopTime = System.currentTimeMillis();
		}
		
		long cost() {
			return stopTime - startTime;
		}
			
	}
	
	private static class Task {
		String theme;
		int priority;
		OnTaskFinishedListener listener;
		Clocker clocker;
		
		Task(String theme, int priority, OnTaskFinishedListener listener) {
			this.theme = theme;
			this.priority = priority;
			this.listener = listener;
			clocker = new Clocker();
		}
		
		void start() {
			clocker.start();
		}
		
		void finish() {
			clocker.stop();
			System.out.println(toString(true));
			if(listener != null) {
				listener.onTaskFinished();
			}
		}
		
		public String toString(boolean isFinished) {
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < priority; i++) {
				sb.append("\t");
			}
			sb.append(this.theme);
			if(isFinished) {
				sb.append("完成 ");
				sb.append(clocker.cost());
				sb.append(" ms");
			} else {
				sb.append("进行中...");
			}
			
			return sb.toString();
		}
	}
	
	public static void startTask(String theme, OnTaskFinishedListener listener) {
		if(isDebug) {
			Task task = new Task(theme, 0, listener);
			if(taskStack.isEmpty()) {
				taskStack.push(task);
			} else {
				Task tempTask = taskStack.peek();
				task.priority = tempTask.priority + 1;
				taskStack.push(task);
			}
			System.out.println(task.toString(false));
			task.start();
		}
	}
	
	public static void startTask(String theme) {
		startTask(theme, null);
	}
	
	public static void finishTask(String theme) {
		if(isDebug) {
			synchronized(taskStack) {
				if(taskStack.peek().theme.equals(theme)) {
					taskStack.notify();
					try {
						taskStack.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.exit(1);
				}
			}
		}
	}
	
	public static void start() {
		if(!isDebug) {
			return;
		}
		
		try {
			bw = new BufferedWriter(new FileWriter(new File(logFile)));
			bs = new BufferedWriter(new FileWriter(new File(resultFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(new Debugger()).start();
	}
	
	@Override
	public void run() {
		synchronized(taskStack) {
			while(!taskStack.isEmpty()) {
				try {
					taskStack.wait();
					Task task = taskStack.pop();
					task.finish();
					taskStack.notify();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void log(String str) {
		try {
			bw.write(str);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveResult(DFSCodeStack dfsCodeStack) {
		try{
			for(Iterator<DFSCode> it = dfsCodeStack.getStack().iterator(); it.hasNext();) {
				DFSCode code = it.next();
				bs.write(code.toString(TempResult.vertexRank2Label, TempResult.edgeRank2Label) + " -> ");
			}
			bs.newLine();
			bs.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void stop() {
		try {
			bw.close();
			bs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
