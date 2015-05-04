package exception;


public class MiningException extends RuntimeException {

	private int type;
	
	public MiningException() {
		
	}
	
	public MiningException(int type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		switch(type) {
		case 0:
			return "无法找到与此边匹配的顶点";
		default:
			return "";
		}
	}
}
