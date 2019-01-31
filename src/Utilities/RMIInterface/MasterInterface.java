package Utilities.RMIInterface;
import java.rmi.Remote;

import Utilities.InfoLeaseHolder;
import Utilities.chunkLocation;
import Utilities.FileSystemException.ChunkExistException;

public interface MasterInterface extends Remote {
	public long getID();
	
	public void mkdir(String path);
	
	public void delete(String path);
	
	public void create(String path);
	
	public long getFileLength(String path);
	
	public chunkLocation getChunkLocation(String path, long chunkID);
	
	public chunkLocation addChunk(String path,long chunkID) throws ChunkExistException;
	
	public InfoLeaseHolder findLeaseHolder(String key);
	
	public void heartbeat();
}
