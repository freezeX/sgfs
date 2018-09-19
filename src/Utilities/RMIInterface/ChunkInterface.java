package Utilities.RMIInterface;
import java.rmi.Remote;
import Utilities.NoEnoughSpaceException;
import Utilities.chunkLocation;
import Utilities.DataID;

public interface ChunkInterface extends Remote{
	public void append(String path, long chunkID, chunkLocation cL, DataID d) throws NoEnoughSpaceException;
	
	public void write(String path, long chunkID, chunkLocation cL, DataID dataID, long startIndex);
	
	public void read(String path, long chunkID,byte[] data, long startByteIdx, long endByteIdx);
}
