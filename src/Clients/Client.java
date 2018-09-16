package Clients;
import java.io.*;
import java.util.*;
import java.rmi.*;
import Cache.Cache;
import Utilities.*;

public class Client {
	private MasterInterface master;
	private Cache locationCache;
	private Cache leaseHolderCache;
	private long clientID;
	private long AppendSize;
	private long ChunkSize;
	public Client(String s)  {
		try {
			master = (MasterInterface) Naming.lookup("//localhost/master"); 
		} catch (Exception e) {
			//System.out.println("Connection Fail");
		}
		
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("src/config/client.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		long timeout = Long.parseLong(p.getProperty("Timeout"));
		long interval = Long.parseLong(p.getProperty("Interval"));
		ChunkSize = Long.parseLong(p.getProperty("ChunkSize"));
		AppendSize = Long.parseLong(p.getProperty("AppendSize"));
		locationCache = new Cache(timeout, interval);
		leaseHolderCache = new Cache(timeout, interval);
		clientID = master.getID();
	}
	
	
	public void create(String path) {
		master.create(path);
	}
	public void mkdir(String path) {
		master.mkdir(path);
	}
	
	public String list(String path) {
		return master.list(path);
	}
	public void delete(String path) {
		master.delete(path);
	}
	public void append(String path, byte[] data) throws Exception {
		if (data.length > AppendSize) {
			throw new AppendLimitException("Reach Append Limit");
		}
		long fileLength = getFileLength(path);
		long chunkID = 
		
	}
	public void write(String path, byte[] data) {
		
	}
	public long getFileLength(String path) {
		return master.getFileLength(path);
	}
	public void stop() {
		locationCache.stop();
		leaseHolderCache.stop();
	}
}
