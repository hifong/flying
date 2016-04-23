package com.flying.framework.service;

import org.junit.Test;

import com.flying.common.util.ServiceHelper;
import com.flying.framework.Tester;
import com.flying.framework.data.Data;

public class ServiceTest2 extends Tester{
	//@Test
	public void test3() throws Exception {
		logger.debug(ServiceHelper.invoke("mc", "CommentService:test3", new Data("msg_id",4)));
	}
	
	@Test
	public void test4() throws Exception {
		logger.debug(ServiceHelper.invoke("mc", "CommentService:test4", new Data("msg","hello world")));
	}
}
