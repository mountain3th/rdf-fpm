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
			return "tmp文件不存在";
		case 3:
			return "tmp文件格式错误";
		case 4:
			return "input文件不存在";
		case 5:
			return "未设置input文件";
		default:
			return "";
		} 
	}
}
