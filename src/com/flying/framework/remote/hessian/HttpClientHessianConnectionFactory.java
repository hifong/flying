package com.flying.framework.remote.hessian;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.apache.http.client.HttpClient;

import com.caucho.hessian.client.AbstractHessianConnectionFactory;
import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianConnectionFactory;
import com.flying.common.http.HttpUtils;
import com.flying.common.util.Utils;
import com.flying.framework.application.Application;

public class HttpClientHessianConnectionFactory extends AbstractHessianConnectionFactory implements HessianConnectionFactory {

	private HttpClient httpClient;
	private Map<String, HessianConnection> connections = Utils.newHashMap();

	private static HttpClientHessianConnectionFactory factory;

	private HttpClientHessianConnectionFactory(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public static HttpClientHessianConnectionFactory getInstance() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		if(factory == null) {
			factory =new HttpClientHessianConnectionFactory(HttpUtils.newClient(Application.getInstance().getConfigs("hessian"))) ;
		}
		return factory;
	}

	@Override
	public HessianConnection open(URL url) throws IOException {
		URI uri = null;
		try {
			uri = url.toURI();
		} catch (URISyntaxException e) {
			String msg = String.format("Unable to convert URL '%s' to URI.", url.toString());
			throw new IllegalStateException(msg, e);
		}
		String key = uri.toString();
		if (!connections.containsKey(key)) {
			connections.put(key, new HttpClientHessianConnection(httpClient, uri));
		}
		return connections.get(key);
	}

}
