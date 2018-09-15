package Cache;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.time.*;

public class Cache {
	public HashMap<String,CacheNode> table;
	public Duration duration;
	private Sweeper s;
	public Cache(long ms, long interval) {
		duration = Duration.ofMillis(ms);
		s = new Sweeper(interval, this);
		table = new HashMap<>();
		s.start();
	}
	public synchronized Object get(String str) {
		CacheNode node = table.get(str);
		Object value = node.value;
		if (value == null || node.expired()){
			return null;
		}else {
			return value;
		}
	}
	public synchronized void set(String key, Object value) {
		table.put(key, new CacheNode(value, duration));
	}
	public void stop() {
		s.stopSweeper();
		
	}
	
}


