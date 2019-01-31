package MasterServer;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import Utilities.Info;
import Utilities.FileSystemException.ParentNotFounfException;
import javafx.animation.PathTransition;
import javafx.scene.Parent;

public class NameSpaceManager {
	private HashMap<String, Info> paths;
	private ReentrantReadWriteLock lock;
	public NameSpaceManager() {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		HashMap<String, Info> paths = new HashMap<>();
		paths.put("/", new Info(true, 0));
	}
	public void mkdir(String path) {
		lock.writeLock().lock();
		try {
			add(path, true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.writeLock().unlock();
		}
	}
	public void delete(String path) {
		lock.writeLock().lock();
		try {
			if (paths.containsKey(path)) {
				
			}
		}
	}

	public void create(String path) {
		// TODO Auto-generated method stub
		
	}

	public long getFileLength(String path) {
		// TODO Auto-generated method stub
		return 0;
	}
	public void add(String path, boolean isDir) throws Exception {
		String parent = getParent(path);
		if (!exist(parent)) {
			throw new ParentNotFounfException("No parent");
		}
		else if (!paths.get(parent).isDir) {
			throw new Exception("Parent not dir");
		}else if (exist(path)) {
			throw new Exception("Path exists");
		}
		paths.put(path, new Info(isDir, 0));
	}
	public boolean exist(String path) {
		Info info = paths.get(path);
		if (info == null) {
			return false;
		}
		return true;
	}
	public String getParent(String path) {
		int lastIndex = path.lastIndexOf("/");
		return path.substring(0, lastIndex);
	}
}
