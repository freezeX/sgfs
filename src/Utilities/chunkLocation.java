package Utilities;

public class chunkLocation{
	public long chunkHandle;
	public String[] chunkAddress;
	public chunkLocation(long chunkHandle, String[] addr) {
		this.chunkAddress = addr;
		this.chunkHandle = chunkHandle;
	}
}
