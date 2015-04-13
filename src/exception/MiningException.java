package exception;

import datastructure.DFSCodeStack;

public class MiningException extends RuntimeException {
	private DFSCodeStack dfsCodeStack;
	
	public MiningException(DFSCodeStack dfsCS) {
		dfsCodeStack = dfsCS;
	}
	
	public void print() {
		dfsCodeStack.print();
	}
}
