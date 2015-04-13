package datastructure;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import mining.Result;

public class DFSCodeStack {
	
	Stack<DFSCode> dfsCodeStack;
	
	public DFSCodeStack() {
		dfsCodeStack = new Stack<DFSCode>();
	}
	
	public DFSCodeStack(DFSCodeStack stack) {
		dfsCodeStack = new Stack<DFSCode>();
		for(Iterator<DFSCode> it = stack.dfsCodeStack.iterator(); it.hasNext();) {
			dfsCodeStack.push(it.next());
		}
	}
	
	public List<DFSCode> getStack() {
		return dfsCodeStack;
	}
	
	public DFSCode peek() {
		return dfsCodeStack.peek();
	}
	
	public DFSCode head() {
		return dfsCodeStack.firstElement();
	}
	
	public DFSCode pop() {
		return dfsCodeStack.pop();
	}
	
	public void push(DFSCode code) {
		dfsCodeStack.push(code);
	}
	
	public boolean isMin() {
//		for(int index = 0; index < dfsCodeStack.size() - 1; index++) {
//			if(!dfsCodeStack.get(index).isLessThan(dfsCodeStack.get(index+1))) {
//				return false;
//			}
//		}
		return true;
	}
	
	public boolean isEmpty() {
		return dfsCodeStack.isEmpty();
	}
	
	public void print() {
		for(Iterator<DFSCode> it = dfsCodeStack.iterator(); it.hasNext();) {
			DFSCode code = it.next();
			System.out.print(code.toString(Result.vertexRank2Label, Result.edgeRank2Label) + " -> ");
		}
	}

}
