package com.flying.framework.remote.hessian;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import com.caucho.hessian.client.AbstractHessianConnection;

public class HttpClientHessianConnection extends AbstractHessianConnection {
	public static final Log LOG = LogFactory.getLog(HttpClientHessianConnection.class);

	private boolean isReady = false;
	private HttpClient httpClient;
	private ByteArrayOutputStream requestContentOutputStream;
	private HttpPost request;
	private HttpResponse response;
	private InputStream responseContentInputStream;

	public HttpClientHessianConnection(HttpClient httpClient, URI uri) {
		this.httpClient = httpClient;
		requestContentOutputStream = new ByteArrayOutputStream();
		request = new HttpPost(uri);
		isReady = true;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return requestContentOutputStream;
	}

	@Override
	public void addHeader(String key, String value) {
		request.addHeader(key, value);
	}

	@Override
	public void sendRequest() throws IOException {
		if (!isReady) {
			throw new IllegalStateException("HttpClientHessianConnection is not ready to send request.");
		}

		HttpEntity entity = new ByteArrayEntity(requestContentOutputStream.toByteArray());
		request.setEntity(entity);

		response = httpClient.execute(request);
	}

	@Override
	public int getStatusCode() {
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		return statusCode;
	}

	@Override
	public String getStatusMessage() {
		StatusLine statusLine = response.getStatusLine();
		String statusMessage = statusLine.getReasonPhrase();
		return statusMessage;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		HttpEntity entity = response.getEntity();
		responseContentInputStream = entity.getContent();
		return responseContentInputStream;
	}

	@Override
	public void destroy() throws IOException {
		closeSafely(requestContentOutputStream);
		requestContentOutputStream = null;
		closeSafely(responseContentInputStream);
		responseContentInputStream = null;
		isReady = false;
	}

	public void closeSafely(Closeable closeable) {
		if (null != closeable) {
			try {
				closeable.close();
			} catch (IOException e) {
				LOG.warn("Unable to close resource", e);
			}
		}
	}
}
