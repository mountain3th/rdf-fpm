package datastructure;

import java.util.List;
import java.util.Stack;

public class DFSCodeStack {
	
	Stack<DFSCode> dfsCodeStack;
	
	public DFSCodeStack() {
		dfsCodeStack = new Stack<DFSCode>();
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
		return true;
	}
	
	public boolean isEmpty() {
		return dfsCodeStack.isEmpty();
	}
}
