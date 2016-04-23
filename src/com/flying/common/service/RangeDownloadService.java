package com.flying.common.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.flying.common.log.Logger;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.RequestService;

/**
 * @author wanghaifeng
 *
 */
public class RangeDownloadService implements RequestService {
	private static final long serialVersionUID = -636507664460534803L;
	private static final Logger logger = Logger.getLogger(RangeDownloadService.class);

	public void service(LocalModule module, String[] urlParams,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//TODO 用户是否可以访问检查
		//String videoId = urlParams[1];
		try {
			//Map<String, String> fm = SimpleDAO.findOneBySQL(module.getModuleConfig().getConfig("SQL.Video.FileName"), new String[]{videoId});
			//TODO 如果文件找不到，需要向外报异常
			//TODO 检查用户是否有查看此视频的权限
			//String filename = fm.get("FILENAME");
			String filename = "E:\\tmp\\1.ppt";
			writeReposeStream(request, response, new File(filename));
		} catch (Exception e) {
			//TODO 如何处理异常
			throw new ServletException(e);
		}
		
	}

	public void writeReposeStream(HttpServletRequest request,
			HttpServletResponse response, File file) throws Exception {
		response.setHeader("Accept-Ranges", "bytes");
		// 从请求头中获取Range字段
		String range = request.getHeader("Range");
		if (logger.isDebugEnabled()) {
			logger.debug("Range=" + range);
		}
		logger.info("Range=" + range);
		int length = (int) file.length();
		int[] ra = parseRange(range, length);
		// Range为空，没有分段请求，返回全部数据
		if (ra == null) {
			response.setContentLength(length); // 设置下载内容大小
			copy(file, response.getOutputStream(), 0, length - 1);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("start=" + ra[0] + " end=" + ra[1]);
			}
			String contentRange;
			if (ra[0] > length - 1) {
				response.setContentLength(0);
				contentRange = "bytes */" + length;
				response.setHeader("Content-Range", contentRange);
				if (logger.isDebugEnabled()) {
					logger.debug("Content-Range=" + contentRange);
				}
				response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);// 错误码416
			} else {
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);// 返回状态码206
				int len = ra[1] - ra[0] + 1;
				response.setContentLength(len);
				contentRange = "bytes " + ra[0] + "-" + ra[1] + "/" + length;
				response.setHeader("Content-Range", contentRange);
				if (logger.isDebugEnabled()) {
					logger.debug("Content-Range=" + contentRange
							+ " ContentLength = " + len);

				}
				copy(file, response.getOutputStream(), ra[0], ra[1]);
				response.flushBuffer();
			}
		}

	}
	
	private int[] parseRange(String range, int length ) {
		if(range == null) return null;
		range = range.substring(range.indexOf("=") + 1);
		String[] tmps = StringUtils.split(range, "-");
		int[] res = new int[2];
		try {
			res[0] = Integer.parseInt(tmps[0]);
		} catch (Exception e) {
			res[0] = 1;
		}
		try {
			res[1] = Integer.parseInt(tmps[1]);
		} catch (Exception e) {
			res[1] = length;
		}
		
		return res;
	}

	protected void copy(File contentFile, OutputStream ostream, long start,
			long end) throws IOException {
		IOException exception = null;
		// fixme: add cache
		final int inputSize = (int)(end - start + 1);
		
		InputStream resourceInputStream = new FileInputStream(contentFile);
		InputStream istream = new BufferedInputStream(resourceInputStream, inputSize);
		exception = copyRange(istream, ostream, start, end);
		istream.close();
		if (exception != null)
			throw exception;
	}

	protected IOException copyRange(InputStream istream, OutputStream ostream,
			long start, long end) {
		logger.debug("copyRange(InputStream istream,OutputStream ostream,long start, long end) is starting ...");
		try {
			istream.skip(start);
		} catch (IOException e) {
			return e;
		}
		IOException exception = null;
		long bytesToRead = end - start + 1;
		final int inputSize = 1024;
		byte buffer[] = new byte[inputSize];
		int len = buffer.length;
		while ((bytesToRead > 0) && (len >= buffer.length)) {
			try {
				len = istream.read(buffer);
				if (bytesToRead >= len) {
					ostream.write(buffer, 0, len);
					bytesToRead -= len;
				} else {
					ostream.write(buffer, 0, (int) bytesToRead);
					bytesToRead = 0;
				}
			} catch (IOException e) {
				exception = e;
				len = -1;
			}
			if (len < buffer.length)
				break;
		}
		return exception;
	}
}
