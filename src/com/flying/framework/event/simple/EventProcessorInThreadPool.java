package com.flying.framework.event.simple;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.flying.common.log.Logger;
import com.flying.common.util.ServiceHelper;
import com.flying.framework.data.Data;
import com.flying.framework.event.EventProcessor;

public class EventProcessorInThreadPool implements EventProcessor{
	private static Logger logger = Logger.getLogger(EventProcessorInThreadPool.class);
	private ExecutorService executor = Executors.newCachedThreadPool();

	@Override
	public void process(String moduleId, String serviceId, Data data) {
		executor.execute(new EventHandler(moduleId, serviceId, data));
	}
	
	class EventHandler implements Runnable {
		private String moduleId;
		private String serviceId;
		private Data data;
		
		EventHandler(String moduleId, String serviceId, Data data) {
			this.moduleId = moduleId;
			this.serviceId = serviceId;
			this.data = data;
		}
		@Override
		public void run() {
			try {
				ServiceHelper.invoke(moduleId, serviceId, data);
			} catch (Exception e) {
				logger.error("EventHandler fail, Module[" + moduleId +"], Service[" + serviceId +"], Req[" + data + "]", e);
			}
		}
		
	}
}
