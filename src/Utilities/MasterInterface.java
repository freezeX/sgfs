package Utilities;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterInterface {
	public long getID();
	
	public void mkdir(String path);
	
	public void delete(String path);
	
	public String list(String path);
	
	public void create(String path);
	
	public long getFileLength(String path);
	
}
