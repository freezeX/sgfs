

import Utilities.InfoLeaseHolder;
import Utilities.chunkLocation;
import Utilities.FileSystemException.ChunkExistException;
import Utilities.RMIInterface.MasterInterface;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;

import MasterServer.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Master extends UnicastRemoteObject implements MasterInterface {
	final Lock lock;
	private long clientID;
	private boolean dead;
	private long chunkHandleID;
	private String myAddr;
	private String metaData;
	private Hashtable<String, LocalDateTime> chunkServer;
	private ChunkManager chunkManager;
	private NameSpaceManager nameSpaceManager;
	

	public Master() throws RemoteException, UnknownHostException {
		super();
		lock = new ReentrantLock();
		clientID = 0;
		chunkHandleID = 0;
		myAddr = InetAddress.getLocalHost().toString();
		dead = false;
		chunkManager = new ChunkManager(null);
		nameSpaceManager = new NameSpaceManager();
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("src/config/master.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public long getID() {
		lock.lock();
		try {
			clientID ++;
			return clientID; 
		}finally {
			lock.unlock();
		}
	}

	@Override
	public void mkdir(String path) {
		nameSpaceManager.mkdir(path);
	}

	@Override
	public void delete(String path) {
		nameSpaceManager.delete(path);
	}


	@Override
	public void create(String path) {
		nameSpaceManager.create(path);
	}

	@Override
	public long getFileLength(String path) {
		return nameSpaceManager.getFileLength(path);
	}

	@Override
	public chunkLocation getChunkLocation(String path, long chunkID) {
		return chunkManager.getChunkLocation(path, chunkID);
	}

	@Override
	public chunkLocation addChunk(String path, long chunkID) throws ChunkExistException {
		return chunkManager.addChunk(path, chunkID);
	}

	@Override
	public InfoLeaseHolder findLeaseHolder(String key) {
		return chunkManager.findLeaseHolder(key);
	}
	
	public void heartbeat(String addr, Long[] extendRequest) {
		chunkServer.put(addr, LocalDateTime.now());
		if (extendRequest.length > 0 ) {
			extendLease(addr,extendRequest);
		}
	}
	
	private void extendLease(String addr, Long[] extendRequest) {
		chunkManager.extendLease(addr, extendRequest);
	}

	public static void main(String[] args) {
		try {
			Naming.rebind("//localhost/master", new Master());
			System.out.println("Master Server Ready");
		}catch (Exception e) {
			System.err.println("Server Exception: " + e.toString());
            e.printStackTrace();
		}
	}

	// @Override
	// public void heartbeat() {
	// 	// TODO Auto-generated method stub
		
	// }
	
}
