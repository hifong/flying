package com.flying.common.cache;

/**
 * @author wanghaifeng
 */
public interface CacheTask {
	/**
	 * 处理
	 */
	public void execute();
	
	/**
	 * @return 执行时间间隔的毫秒数
	 */
	public long peroid();
}
