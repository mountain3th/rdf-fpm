package launcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClockerManager {
	
	private static List<Clocker> clockers;
	private static ClockerManager manager = new ClockerManager();
	
	private ClockerManager() {
		clockers = new ArrayList<Clocker>();
	}
	
	public static Clocker getClocker(String theme) {
		if(!clockers.isEmpty()) {
			for(Iterator<Clocker> it = clockers.iterator(); it.hasNext();) {
				Clocker clocker = it.next();
				if(clocker.theme != null && clocker.theme.equals(theme)) {
					return clocker;
				}
			}
		}
		Clocker clocker = manager.new Clocker(theme);
		clockers.add(clocker);
		return clocker;
	}
	
	class Clocker {
	
		private long startTime;
		private long stopTime;
		private String theme;
		
		private Clocker(String theme) {
			this.theme = theme;
		}
		
		public void start() {
			startTime = System.currentTimeMillis();
		}
		
		public void stop() {
			stopTime = System.currentTimeMillis();
		}
		
		public void show() {
			System.out.println(theme + " 用时 " + String.valueOf(stopTime - startTime) + " ms");
		}
	}
}
