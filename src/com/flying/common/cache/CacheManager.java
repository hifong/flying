package com.flying.common.cache;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.flying.common.log.Logger;

/**
 * @author wanghaifeng
 */
@SuppressWarnings("rawtypes")
public final class CacheManager {

    public static final int HASH_CACHE = 0;
    public static final int MEM_CACHE = 1;
    
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Logger logger = Logger.getLogger(CacheManager.class);
	private final static CacheManager instance = new CacheManager();
	
	private final Map<String, Cache> caches = new ConcurrentHashMap<String, Cache> ();
	private final Map<String, CacheTimerTask> tasks = new ConcurrentHashMap<String, CacheTimerTask>();
	
	private Timer timer = new Timer();
	
	private CacheManager() {
		
	}
	
	public static CacheManager getInstance() {
		return instance;
	}
	


    public <T> Cache<T> newCache(int type, String name, String prefix) {
        Cache<T> cache = null;
        switch (type) {
        case HASH_CACHE:
            cache = new HashMapCache<T>(name);
            break;
//        case MEM_CACHE:
//            cache = new MemCache<T>(name, prefix, null);
//            break;
        }

        return cache;
    }
	
	public Map<String, Cache> getCaches() {
		return caches;
	}

	public void register(Cache cache) {
		this.caches.put(cache.getName(), cache);
		if(cache instanceof CacheTask) {
			CacheTask tc = (CacheTask)cache;
			if(tc.peroid()>0)
			this.createTask(tc);
		}
	}
	
	public Map<String, CacheTimerTask> getTasks() {
		return this.tasks;
	}
	
	public void createTask(CacheTask tc) {
		CacheTimerTask task = new CacheTimerTask(tc);
		Cache cache = (Cache)tc;
		this.tasks.put(cache.getName(), task);
		this.timer.schedule(task, new Date(), tc.peroid());
		logger.debug("Task scheduled! -->\n" + task);
	}
	
	public void restartTasks() {
		for(CacheTimerTask task: this.tasks.values()) {
			boolean b = task.cancel();
			logger.debug(task.toString() + " cancel = " + b);
		}
		this.timer.cancel();
		this.timer.purge();
		this.timer = new Timer();
		for(CacheTimerTask task: this.tasks.values()) {
			this.timer.schedule(task, new Date(), task.cache.peroid());
			logger.debug("Task scheduled! -->\n" + task);
		}
	}
	
	public void remove(String cacheName) {
		if(this.caches.containsKey(cacheName)) {
			this.caches.remove(cacheName);
		}
	}
	
	public class CacheTimerTask extends TimerTask {
		public final CacheTask cache;
		private long lastRun;
		private int runTimes;
		
		public CacheTimerTask(CacheTask task) {
			this.cache = task;
			this.lastRun = System.currentTimeMillis();
		}
		
		public void run() {
			try {
				if(System.currentTimeMillis() - this.lastRun < cache.peroid()) 
					return;
				this.cache.execute();
				this.lastRun = System.currentTimeMillis();
				this.runTimes ++;
				logger.debug("task running!\n"+this);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer(cache.toString());
			sb.append("Run times:").append(this.runTimes).append(";\n");
			sb.append("Last Run:").append(sdf.format(this.lastRun)).append(";\n");
			return sb.toString();
		}
	}
}
