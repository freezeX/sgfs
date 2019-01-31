
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Logger;

import com.oracle.webservices.internal.api.databinding.Databinding;

import java.rmi.*;
import Cache.*;
import Utilities.*;
import Utilities.FileSystemException.AppendLimitException;
import Utilities.FileSystemException.ChunkExistException;
import Utilities.FileSystemException.NoEnoughSpaceException;
import Utilities.RMIInterface.ChunkInterface;
import Utilities.RMIInterface.MasterInterface;
import javafx.scene.chart.PieChart.Data;
import sun.net.www.http.ChunkedInputStream;

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
	
	public void delete(String path) {
		master.delete(path);
	}
	public void append(String path, byte[] data) throws AppendLimitException, MalformedURLException, RemoteException, NotBoundException {
		if (data.length > AppendSize) {
			throw new AppendLimitException("Reach Append Limit");
		}
		long fileLength = getFileLength(path);
		long chunkID = fileLength / ChunkSize;
		chunkLocation t = getChunkLocation(path, chunkID);
		DataID d = new DataID(clientID);
		pushData(t.chunkAddress, data, d);
		String primaryAddr = findLeaseHolder(t.chunkHandle);
		ChunkInterface primary = (ChunkInterface) Naming.lookup(primaryAddr);
		try {
			primary.append(path, chunkID, t, d);
		} catch (NoEnoughSpaceException e) {
			append(path, data);
		}
		 
	}
	public void write(String path, byte[] data, long offset) throws MalformedURLException, RemoteException, NotBoundException {
		long length = data.length;
		long startChunkID = offset/ChunkSize;
		long endChunkID = (offset + length - 1)/ChunkSize;
		long offsetData = 0; // Offset to split the data
		for (long i = startChunkID; i<= endChunkID; i++) {
			chunkLocation t = getChunkLocation(path, i);
			long startIndex = (i==startChunkID)? offset%ChunkSize : 0; // Offset in chunk
			long lengthOfPushed = (i==startChunkID)? ChunkSize - startIndex  : ChunkSize;
			long nextOffset = offsetData + lengthOfPushed ;
			DataID dataID = new DataID(clientID);
			pushData(t.chunkAddress, Arrays.copyOfRange(data, (int)offsetData, (int)nextOffset), dataID);
			String primaryAddr = findLeaseHolder(t.chunkHandle);
			ChunkInterface primary = (ChunkInterface) Naming.lookup(primaryAddr);
			primary.write(path, i, t, dataID , startIndex);
			
			
			offsetData = nextOffset;
			}
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
		
	public void read(String path, long offset, byte[] data) throws MalformedURLException, RemoteException, NotBoundException {
		long length = getFileLength(path);
		long limit = Math.min(length, offset + data.length);
		long startChunkIdx = offset/ ChunkSize;
		long endChunkIdx = (limit-1)/ ChunkSize;
		long writeIdx = 0;
		for (long i = startChunkIdx; i <=endChunkIdx; i++) {
			long startByteIdx = (i == startChunkIdx)? offset % ChunkSize : 0 ;
			long endByteIdx = (i == endChunkIdx)? (limit-1)% ChunkSize : ChunkSize-1;
			chunkLocation Chunklocation = getChunkLocation(path, i);
			String primaryAddr = findLeaseHolder(Chunklocation.chunkHandle);
			ChunkInterface primary = (ChunkInterface) Naming.lookup(primaryAddr);
			byte[] dataFeed = new byte[(int) (endByteIdx - startByteIdx +1)];
			primary.read(path,i, dataFeed,startByteIdx, endByteIdx);
			System.arraycopy(dataFeed, 0, data, (int) writeIdx, dataFeed.length);
			writeIdx = writeIdx + dataFeed.length;
			
		}
		
	}
	public void addChunk(String path, long chunkID) throws ChunkExistException {
		master.addChunk(path,chunkID);
	}
	
	public void pushData(String[] locations, byte[] data, DataID d) {
		
	}
	public String findLeaseHolder(long chunkHandle) {
		String key = Long.toString(chunkHandle);
		String primary = (String) leaseHolderCache.get(key);
		if (primary == null) {
			InfoLeaseHolder reply = master.findLeaseHolder(key);
			leaseHolderCache.setWithTimeOut(key, primary, reply.expiration);
			primary = reply.primary;
		}
		return primary;
	}
	
}
