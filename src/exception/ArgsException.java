package exception;

@SuppressWarnings("serial")
public class ArgsException extends Exception {
	
	private int type;
	
	public ArgsException() {
		
	}
	
	public ArgsException(int type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		switch(type) {
		case 0:
			return "support值设置错误";
		case 1:
			return "confidence值设置错误";
		case 2:
			return "原始文件不存在";
		case 3:
			return "未设置原始文件";
		default:
			return "";
		} 
	}
}
