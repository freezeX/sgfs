package Utilities;

import java.time.LocalDateTime;

public class dataID {
	public long clientID;
	public LocalDateTime t;
	public dataID(long c) {
		clientID = c;
		t = LocalDateTime.now();
	}
	
}
