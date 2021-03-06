package Cache;


public class cacheTest {
	public Cache cache;
	public cacheTest() {
		cache = new Cache(3000, 5000);
	}
	public void testGet(String s){
		CacheNode node = cache.table.get(s);
		if (node == null) {
			System.out.println("test should return" + s);
		}else {
			System.out.println("Successful");
		}
	}
	public void testSet(String s, String t) {
		cache.set(s, t);
	}
	
	public void testTimeout() {
		testSet("fdsfs","dsfa");
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		testGet("fdsfs");
	}
	public static void main(String[] args) {
		cacheTest test = new cacheTest();
		test.testTimeout();
		test.cache.stop();
	}
	
	
}
