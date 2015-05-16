package datastructure;

import java.util.Map;

import mining.Mining.Pattern;

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
	
	public boolean isLessThan(DFSCode code) {
//		// ����������ͬ
//		if(ix == code.ix) {
//			// ����ǰ����չ
//			if(iy > ix && code.iy > code.ix) {
//				return a < code.a || (a == code.a && y < code.y);
//			} 
//			// һ��ǰ��һ�����򣬻�������������չ
//			else {
//				return iy < code.iy;
//			}
//		} else if(ix > code.ix) {
//			
//		} else {
//			return ix < code.ix && iy == code.ix;
//		}
		
		// 后向扩展
		if(ix > iy) {
			if(code.ix < code.iy) {
				return true;
			}
			if(iy < code.iy || (iy == code.iy && a < code.a)) {
				return true;
			}
		}
		// 前向扩展
		else if(code.ix < code.iy) {
			if(iy == code.ix) {
				return true;
			}
			if(ix > code.ix) {
				return true;
			}
			if(ix == code.ix) {
				if(x < code.x) {
					return true;
				}
				if(x == code.x && (a < code.a || (a == code.a && y < code.y))) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		DFSCode code = (DFSCode) o;
		return code.a == a && code.ix == ix && code.iy == iy && code.x == x && code.y == y;
	}
	
	public boolean equalsTo(Object o) {
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
	
	public SimDFSCode toSimDFSCode() {
		return new SimDFSCode(this.a, this.y);
	}
	
	public static class SimDFSCode {
		public int a;
		public int y;

		public SimDFSCode(int a, int y) {
			this.a = a;
			this.y = y;
		}
		
		@Override
		public int hashCode() {
			return 100 * a + 10 * y;
		}
		
		@Override
		public boolean equals(Object o) {
			SimDFSCode sdc = (SimDFSCode) o;
			return sdc.a == a && sdc.y == y;
		}
	}
}
