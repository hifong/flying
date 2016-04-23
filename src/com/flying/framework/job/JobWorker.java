package com.flying.framework.job;

import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;

public interface JobWorker {
	int getInterval();
	void work(LocalModule module, Data data);
}
