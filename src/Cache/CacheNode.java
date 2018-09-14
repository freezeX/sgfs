package Cache;
import java.time.*;

public class CacheNode {
	public Object value;
	public LocalDateTime t;
	public CacheNode(Object value, Duration timeout) {
		this.value = value;
		this.t = LocalDateTime.now().plus(timeout);
	}
	public boolean expired() {
		return LocalDateTime.now().isAfter(t);
	}
}

