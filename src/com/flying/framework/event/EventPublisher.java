package com.flying.framework.event;

import com.flying.common.log.Logger;
import com.flying.framework.application.Application;
import com.flying.framework.data.Data;
import com.flying.framework.event.simple.EventProcessorInThreadPool;

public class EventPublisher {
	private static Logger logger = Logger.getLogger(EventPublisher.class);
	private static String producer = Application.getInstance().getEventProducer();

	private static EventProcessor ep;
	
	static {
		try {
			ep = (EventProcessor)Class.forName(producer).newInstance();
			logger.info("EventProducer init by " + producer);
		} catch (Exception e) {
			logger.warn("EventProducer fail to init by " + producer + " using default " + EventProcessorInThreadPool.class.getName());
			ep = new EventProcessorInThreadPool();
		}
	}

	public static void publish(String moduleId, String serviceId, Data data) {
		ep.process(moduleId, serviceId, data);
	}
	
	public static EventProcessor getEventProducer() {
		return ep;
	}
}
