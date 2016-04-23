package com.flying.framework.request.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.flying.common.cache.Cache;
import com.flying.common.cache.HashMapCache;
import com.flying.common.util.Constants;
import com.flying.common.util.DateUtils;
import com.flying.framework.application.Application;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestService;
import com.flying.framework.request.RequestService;

/**
 * @author wanghaifeng
 * 
 */
@SuppressWarnings("rawtypes")
public class ResourceRequestService extends AbstractRequestService implements RequestService {
	private final static Logger logger = Logger.getLogger(ResourceRequestService.class);
	private final static Cache<ResourceEntity> cache = new HashMapCache<ResourceEntity>("ResourceRequestService");

	public void service(LocalModule module, String[] uris, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ServletContext servletContext = (ServletContext) req.getAttribute(Constants.SERVLET_CONTEXT);

		String requestURI = req.getRequestURI();
		String uri = requestURI.substring(requestURI.indexOf(module.getId()) + module.getId().length() + 1);
		uri = module.getPath() + uri;
		if (servletContext != null)
			resp.setContentType(servletContext.getMimeType(uri));

		OutputStream os = resp.getOutputStream();
		File file = new File(uri);
		long lastModified = file.lastModified();
		Date lastDate = new Date(lastModified);
		resp.setHeader("Last-Modified", DateUtils.toGMT(lastDate));
		resp.setHeader("Cache-Control", "max-age=86400, public, must-revalidate");
		// resp.setHeader("Expires", DateUtils.toGMT(new Date(lastModified +
		// expireTime)));

		if (cache.containsKey(uri) && Application.getInstance().isProductMode()) {
			byte[] buff;
			ResourceEntity<byte[]> re = cache.get(uri);
			if (lastModified > re.getLastModified()) {
				try {
					buff = getBytes(file);
					re.setLastModified(lastModified);
					re.setValue(buff);
					cache.put(uri, re);
				} catch (Exception e) {
					logger.error("request resource(" + uri + ") fail!", e);
					if (e instanceof IOException) {
						throw (IOException) e;
					} else {
						// new IOException(e);jdk1.5不支持该方法
						throw new IOException(e.toString());
					}

				}
			} else {
				buff = re.getValue();
			}
			os.write(buff);
			logger.debug("Resource["+ uri +"] read from cache!");
		} else {
			try {
				byte[] buff = getBytes(file);
				ResourceEntity<byte[]> re = new ResourceEntity<byte[]>(lastModified, buff);
				cache.put(uri, re);
				os.write(buff);
			} catch (Exception e) {
				logger.error("request resource(" + uri + ") fail!", e);
				if (e instanceof IOException) {
					throw (IOException) e;
				} else {
					// new IOException(e);jdk1.5不支持该方法
					throw new IOException(e.toString());
				}

			}
			logger.debug("Resource["+ uri +"] read from file!");
		}
		os.flush();
	}

	private byte[] getBytes(File file) throws Exception {
		InputStream is = new FileInputStream(file);
		byte[] buff = new byte[is.available()];
		try {
			int pos = 0;
			byte[] tmp = new byte[1024];
			int len = -1;
			while ((len = is.read(tmp)) > 0) {
				System.arraycopy(tmp, 0, buff, pos, len);
				pos += len;
			}
		} finally {
			is.close();
		}
		return buff;
	}

	class ResourceEntity<T> {
		private long lastModified;
		private T value;

		public ResourceEntity(long lastModified, T value) {
			super();
			this.lastModified = lastModified;
			this.value = value;
		}

		public long getLastModified() {
			return lastModified;
		}

		public void setLastModified(long lastModified) {
			this.lastModified = lastModified;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}
	}

}
