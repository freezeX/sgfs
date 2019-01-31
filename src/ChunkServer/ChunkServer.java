package ChunkServer;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;

import Utilities.RMIInterface.ChunkInterface;
import Utilities.RMIInterface.MasterInterface;


public class ChunkServer extends UnicastRemoteObject implements ChunkInterface {
	public  ChunkServer()  {
	}
}
