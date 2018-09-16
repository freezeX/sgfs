package Utilities;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterInterface extends Remote {
	public long getID();
	
	public void mkdir(String path);
	
	public void delete(String path);
	
	public String list(String path);
	
	public void create(String path);
	
	public long getFileLength(String path);
	
	public chunkLocation getChunkLocation(String path, long chunkID);
	
	public chunkLocation addChunk(String path,long chunkID) throws ChunkExistException;
	
	public void append(String path, long chunkID, chunkLocation cL, dataID d) throws NoEnoughSpaceException;
	
	public InfoLeaseHolder findLeaseHolder(String key);
}
