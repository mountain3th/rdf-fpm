package exception;

public class DebuggerException extends RuntimeException {
	
	int type;
	
	public DebuggerException(int type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		switch(type) {
		case 0:
			return "startTask和finishTask顺序不一致";
		default:
			return "";
		}
	}
}
