package com.flying.framework.service;

import org.junit.Test;

import com.flying.common.util.ServiceHelper;
import com.flying.framework.Tester;
import com.flying.framework.data.Data;

public class ServiceTest extends Tester{
	@Test
	public void invokeLocalMethod() throws Exception {
		logger.debug(ServiceHelper.invoke("mc", "CommentService:findByMessage", new Data("msg_id",4)));
	}
	//@Test
	public void invokeRemoteMethod() throws Exception {
		logger.debug(ServiceHelper.invoke("mc", "CommentService:findByMessage", new Data("msg_id",4)));
	}
}
