package datastructure;

public class DFSCode {
	public int ix;
	public int iy;
	public int x;
	public int a;
	public int y;
	
	public DFSCode(int ix, int iy, int x, int a, int y) {
		this.ix = ix;
		this.iy = iy;
		this.x = x;
		this.a = a;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		DFSCode code = (DFSCode) o;
		return code.a == a && code.ix == ix && code.iy == iy && code.x == x && code.y == y;
	}
}
