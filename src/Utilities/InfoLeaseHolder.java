package Utilities;
import java.time.*;
public class InfoLeaseHolder {
	public String primary;
	public LocalDateTime expiration;
	public InfoLeaseHolder(String p, LocalDateTime d) {
		primary = p;
		expiration = d;
	}
}
