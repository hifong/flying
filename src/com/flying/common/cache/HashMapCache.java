package com.flying.common.cache;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wanghaifeng
 * @param <T>
 */
public class HashMapCache<T> implements Cache<T> ,CacheTask{
    private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected final Map<String, HashMapCacheEntry> cache = new ConcurrentHashMap<String, HashMapCacheEntry>();
    protected final String name;
    protected final AtomicLong totalCount = new AtomicLong(0); //璁块棶娆℃暟
    protected final AtomicLong totalHits = new AtomicLong(0); //鍛戒腑娆℃暟
    
    private long overdue;

    public HashMapCache(String name) {
        super();
        this.name = name;
        CacheManager.getInstance().register(this);
    }
    
    public HashMapCache(String name,long overdue) {
        super();
        this.name = name;
        this.overdue = overdue;
        CacheManager.getInstance().register(this);
    }

    public Map<String, T> getCache() {
        Map<String, T> res = new LinkedHashMap<String, T>();
        for (String k : cache.keySet()) {
            HashMapCacheEntry e = cache.get(k);
            res.put(k, e == null ? null : e.getValue());
        }
        return res;
    }

    
    public String getName() {
        return name;
    }

    
    public void clear() {
        cache.clear();
    }

    
    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    
    public boolean containsValue(T value) {
        return cache.containsValue(value);
    }

    
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    
    public T get(String key) {
        this.totalCount.incrementAndGet();
        HashMapCacheEntry e = cache.get(key);
        if (e != null) {
            this.totalHits.incrementAndGet();
        }
        return e == null ? null : e.getValue();
    }
    
    /**
     * 设置超时时间，如果保存的数据已经超过了timeout指定的时间，则返回null
     * timeout为毫秒
     */
    public T get(String key,long timeout) {
        this.totalCount.incrementAndGet();
        HashMapCacheEntry e = cache.get(key);
        if(e==null){
        	return null;
        }
        long curTime = System.currentTimeMillis();
        long deadTime = e.creationTime + timeout;
        if(deadTime<curTime){
        	this.remove(key);
        	return null;
        }
        this.totalHits.incrementAndGet();
        return e == null ? null : e.getValue();
    }

    
    public T put(String key, T value) {
        cache.put(key, new HashMapCacheEntry(key, value));
        return value;
    }

    
    public void putAll(Map<String, T> map) {
        Map<String, HashMapCacheEntry> entries = new LinkedHashMap<String, HashMapCacheEntry>(map.size());
        for (String key : map.keySet()) {
            entries.put(key, new HashMapCacheEntry(key, map.get(key)));
        }
        cache.putAll(entries);
    }

    
    public T remove(Object key) {
        HashMapCacheEntry e = cache.remove(key);
        return e == null ? null : e.getValue();
    }

    
    public int size() {
        return cache.size();
    }

    
    public Collection<String> keys() {
        return cache.keySet();
    }

    
    public Collection<T> values() {
        Collection<T> list = new LinkedList<T>();
        for (HashMapCacheEntry e : cache.values()) {
            if ((e != null) && (e.getValue() != null)) {
                list.add(e.getValue());
            }
        }
        return list;
    }

    
    public void flush() {
        this.cache.clear();
    }

    
    public void execute() {
		this.clear();
	}


	public long peroid() {
		return overdue;
	}


	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ClassName: ").append(this.getClass().getName()).append("\n");
        sb.append("Name: ").append(name).append(";\n");
        sb.append("Cache size: ").append(this.cache.size()).append(";\n");
        sb.append("Total Count: ").append(this.totalCount).append(";\n");
        sb.append("Total Hits: ").append(this.totalHits).append(";\n");
        return sb.toString();
    }

    public long getTotalCount() {
        return this.totalCount.longValue();
    }

    public long getTotalHits() {
        return this.totalHits.longValue();
    }

    public Map<String, Long> getEntryHits() {
        Map<String, Long> res = new LinkedHashMap<String, Long>();
        for (String key : cache.keySet()) {
            HashMapCacheEntry e = cache.get(key);
            res.put(key, e == null ? 0 : e.getHits());
        }
        return res;
    }

    public class HashMapCacheEntry{
        private String key;
        private final AtomicLong hits = new AtomicLong(0);
        private T value;
        private long creationTime = System.currentTimeMillis(); //鍒涘缓鏃堕棿
        private long lastHit = 0; //鏈�繎鍛戒腑鏃堕棿

        HashMapCacheEntry(String key, T value) {
            this.key = key;
            this.value = value;
        }

        public long getHits() {
            return this.hits.longValue();
        }

        public T getValue() {
            hits.incrementAndGet();
            this.lastHit = System.currentTimeMillis();
            return value;
        }

        public long getCreationTime() {
            return this.creationTime;
        }

        public long getLastHit() {
            return this.lastHit;
        }

        public String getKey() {
            return this.key;
        }

        
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("Key:").append(key).append("\n");
            sb.append("Value:").append(value).append("\n");
            sb.append("Hits:").append(hits).append("\n");
            sb.append("Creation:").append(df.format(new Date(creationTime))).append("\n");
            sb.append("Hit Time:").append(df.format(new Date(lastHit))).append("\n");
            return sb.toString();
        }
    }
}
