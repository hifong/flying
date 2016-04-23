package com.flying.framework.event;

import com.flying.framework.data.Data;

public interface EventProcessor {
	void process(String moduleId, String serviceId, Data data);
}
