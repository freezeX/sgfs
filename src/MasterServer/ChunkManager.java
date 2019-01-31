package MasterServer;

import Utilities.*;
import Utilities.FileSystemException.ChunkExistException;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.*;
import java.util.stream.IntStream;

import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.server.ServerSchemaValidationTube;

import java.util.List;
import java.util.Properties;
import java.util.Random;


public class ChunkManager {
	final ReadWriteLock lock;
	private long chunkHandleIdx;
	private HashMap<Long, InfoLeaseHolder> leaseInfo;
	private HashMap<String , HashMap<Long, Long>> handleInfo;
	private HashMap<Long, PathID> pathInfo;
	private HashMap<Long, chunkLocation> locationInfo;
	private List<String> chunkServers;
	private long LeaseTimeout;
	public ChunkManager(String[] servers) {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("src/config/master.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		LeaseTimeout =  Long.parseLong(p.getProperty("LeaseTimeOut")); 
		lock = new ReentrantReadWriteLock();
		chunkHandleIdx = 0;
		leaseInfo = new HashMap<>();
		handleInfo = new HashMap<>();
		pathInfo = new HashMap<>();
		locationInfo = new HashMap<>();
		chunkServers = Arrays.asList(servers);
		
	}

	public chunkLocation getChunkLocation(String path, long chunkID) {
		lock.readLock().lock();
		try {
			long chunkHandle = handleInfo.get(path).get(chunkID);
			return locationInfo.get(chunkID);
		}finally {
			lock.readLock().unlock();
		}
		
	}
	public void addChunkLocation(long chunkHandle, String addr){
		lock.writeLock().lock();
		try {
			chunkLocation location = locationInfo.get(chunkHandle);
			if (location == null) {
				String[] s = new String[1];
				s[1] = addr;
				location = new chunkLocation(chunkHandle, s);
				locationInfo.put(chunkHandle, location);
			}else {
				List<String> temp = Arrays.asList(location.chunkAddress);
				if (!temp.contains(addr)) {
					temp.add(addr);
					location.chunkAddress = (String[]) temp.toArray();
					}
			}
		}finally {
			lock.writeLock().unlock();
		}
		
		
		
	}
	public chunkLocation addChunk(String path, long chunkID) throws ChunkExistException {
		lock.writeLock().lock();
		try {
			HashMap<Long, Long> tempHandleInfo = handleInfo.get(path);
			if (tempHandleInfo == null) {
				handleInfo.put(path, new HashMap<>());
				tempHandleInfo = handleInfo.get(path);
			}
			if (tempHandleInfo.containsKey(chunkID)) {
				throw new ChunkExistException("Chunk Exist");
			}
			chunkHandleIdx ++;
			tempHandleInfo.put(chunkID, chunkHandleIdx);
			String[] locations = (String[]) random(chunkServers, 3).toArray();
			chunkLocation chunkLocatio = new chunkLocation(chunkHandleIdx, locations);
			pathInfo.put(chunkHandleIdx, new PathID(path, chunkID));
			locationInfo.put(chunkHandleIdx, chunkLocatio);
			return chunkLocatio;
			
		}finally {
			lock.writeLock().unlock();
		}
	}
	public void addLease(long chunkHandle) throws Exception {
		lock.writeLock().lock();
		try {
			chunkLocation location = locationInfo.get(chunkHandle);
			if (location == null) {
				throw new Exception("Chunk does't exist");
			}
			if (location.chunkAddress.length ==0) {
				throw new Exception("No alive chunk server");
			}
			InfoLeaseHolder lease = leaseInfo.get(chunkHandle);
			if (lease == null) {
				leaseInfo.put(chunkHandle, new InfoLeaseHolder(null, null));
			}
			lease.primary = location.chunkAddress[0];
			lease.expiration = LocalDateTime.now().plus(Duration.ofMillis(LeaseTimeout));
		}finally {
			lock.writeLock().unlock();
		}
		
		
	}
	public boolean checkLease(long chunkHandle) {
		InfoLeaseHolder lease = leaseInfo.get(chunkHandle);
		if (lease == null) {
			return false;
		}
		return lease.expiration.isAfter(LocalDateTime.now());
	}
	
	public List<Object> random(List<String> chunkServers2, int n){
		List<Integer> ret = new ArrayList();
		List<Object> output = new ArrayList<>();
		Random rand = new Random();
		int i = 0;
		while (ret.size()< n) {
			int next = rand.nextInt(chunkServers2.size());
			if (!ret.contains(next)) {
				ret.add(next);
			}
		}
		for (int num:ret) {
			output.add(chunkServers2.indexOf(num));
		}
		
		
		return null;
	}
	public InfoLeaseHolder findLeaseHolder(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public void extendLease(String addr, Long[] extendRequest) {
		
	}

}
