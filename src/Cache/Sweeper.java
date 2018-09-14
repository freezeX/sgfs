package Cache;
import java.util.*;
import java.util.Map.Entry;
public class Sweeper{
	private boolean stop;
	private long interval;
	private Cache cache;
	private Timer timer;
	public Sweeper(long interval, Cache cache) {
		this.interval = interval;
		this.stop = false;
		this.cache = cache;
	}
	public synchronized void sweep() {
		HashMap<String,CacheNode> hashMap = this.cache.table;
		Iterator<Entry<String,CacheNode>> i = hashMap.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String,CacheNode> entry = i.next();
			String key = entry.getKey();
			CacheNode node = entry.getValue();
			if (node.expired()) {
				hashMap.remove(key);
			}
		}
	}
	public void start(){
		timer = new Timer();
		timer.scheduleAtFixedRate(new sweepTask(), 0, interval);;
	}
	class sweepTask extends TimerTask{
		public void run(){
			if (!stop) {
				System.out.println("Sweeping");
				sweep();
			}else {
				timer.cancel();
				timer.purge();
			}
		}
	}
	public void stopSweeper() {
		stop = true;
	}
}
