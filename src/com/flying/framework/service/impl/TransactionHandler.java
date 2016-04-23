package com.flying.framework.service.impl;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.flying.framework.annotation.Transaction;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

/**
 * 支持事务
 * 
 * @author king
 *
 */
public class TransactionHandler implements ServiceHandler<Transaction> {

	@Override
	public void handle(Transaction annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		PlatformTransactionManager txManager = module.getSpringBeanFactory().getBean("transactionManager");
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(annotation.isolationLevel());
		def.setPropagationBehavior(annotation.propagationBehavior());
		TransactionStatus status = txManager.getTransaction(def);
		try {
			context.doChain(request);
			txManager.commit(status);
		} catch (Exception e) {
			txManager.rollback(status);
			throw e;
		}
	}

}
