package com.flying.framework.request;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flying.framework.module.LocalModule;

/**
 * @author wanghaifeng
 *
 */
public interface RequestService {
	/**
	 * @param module
	 * @param pathItems 不包含moduleId的部分
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(LocalModule module, String[] pathItems, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException;
}
