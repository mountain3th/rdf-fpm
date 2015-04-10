package datastructure;

import java.util.Map;

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
	
	public DFSCode(DFSCode code) {
		this(code.ix, code.iy, code.x, code.a, code.y);
	}
	
	@Override
	public boolean equals(Object o) {
		DFSCode code = (DFSCode) o;
		return code.a == a && code.ix == ix && code.iy == iy && code.x == x && code.y == y;
	}
	
	@Override
	public int hashCode() {
		return ix * 10000 + iy * 1000 + x * 100 + a * 10 + y;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(ix);
		sb.append(" ");
		sb.append(iy);
		sb.append(" ");
		sb.append(x);
		sb.append(" ");
		sb.append(a);
		sb.append(" ");
		sb.append(y);
		return sb.toString();
	}
	
	public String toString(Map<Integer, Integer> vertexRank2Label, Map<Integer, Integer> edgeRank2Label) {
		StringBuffer sb = new StringBuffer();
		sb.append(ix);
		sb.append(" ");
		sb.append(iy);
		sb.append(" ");
		sb.append(vertexRank2Label.get(x));
		sb.append(" ");
		sb.append(edgeRank2Label.get(a));
		sb.append(" ");
		sb.append(vertexRank2Label.get(y));
		return sb.toString();
	}
}
