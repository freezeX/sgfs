package Clients;
import java.io.*;
import java.util.*;

import com.oracle.webservices.internal.api.databinding.Databinding;

import java.rmi.*;
import Cache.*;
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
	public void append(String path, byte[] data) throws AppendLimitException {
		if (data.length > AppendSize) {
			throw new AppendLimitException("Reach Append Limit");
		}
		long fileLength = getFileLength(path);
		long chunkID = fileLength / ChunkSize;
		chunkLocation t = getChunkLocation(path, chunkID);
		dataID d = new dataID(clientID);
		pushData(t, data, d);
		try {
			master.append(path, chunkID, t, new dataID(clientID));
		} catch (NoEnoughSpaceException e) {
			append(path, data);
		}
		 
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
	public chunkLocation getChunkLocation(String path, long chunkID){
		String key = path + ","+ Long.toString(chunkID);
		chunkLocation n = (chunkLocation)locationCache.get(key);
		if (n == null) {
			try {
				n = master.getChunkLocation(path, chunkID);
				if (n == null) {
					n = master.addChunk(path, chunkID);
				}
			} catch (ChunkExistException e) {
				n = getChunkLocation(path, chunkID);
			}
			locationCache.set(key, n);
			}
		return n;
	}
		
	public void addChunk(String path, long chunkID) throws ChunkExistException {
		master.addChunk(path,chunkID);
	}
	
	public void pushData(chunkLocation c, byte[] data, dataID d) {
		
	}
	public String findLeaseHolder(long chunkHandle) {
		String key = Long.toString(chunkHandle);
		String primary = (String) leaseHolderCache.get(key);
		if (primary == null) {
			InfoLeaseHolder reply = master.findLeaseHolder(key);
			leaseHolderCache.setWithTimeOut(key, primary, reply.duration);
			primary = reply.primary;
		}
		return primary;
	}
	
	
}
