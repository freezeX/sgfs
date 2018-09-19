package Utilities;

import java.time.LocalDateTime;

public class DataID {
	public long clientID;
	public LocalDateTime t;
	public DataID(long c) {
		clientID = c;
		t = LocalDateTime.now();
	}
	
}
