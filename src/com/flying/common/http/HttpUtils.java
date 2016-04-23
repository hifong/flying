package com.flying.common.http;


import java.io.FileOutputStream;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.flying.framework.data.Data;


public class HttpUtils {
	
	public static HttpClient newClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		return newClient(null);
	}
	
	public static HttpClient newClient(final Data config) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		final int requestTimeout = config == null?10000:config.getInt("request-timeout", 10000);
		final int timeout = config == null?10000:config.getInt("timeout", 10000);
		final int soTimeout = config == null?10000:config.getInt("so_timeout", 10000);
		
		//SSL
		SSLContextBuilder sslContextbuilder = new SSLContextBuilder();
		sslContextbuilder.useTLS();
		SSLContext sslContext = sslContextbuilder.loadTrustMaterial(null, new TrustStrategy() {

			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}

		}).build();

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
						.register("http", PlainConnectionSocketFactory.INSTANCE)
						.register("https", new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER))
						.build();
		SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
		
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connManager.setDefaultSocketConfig(socketConfig);

		MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200).setMaxLineLength(2000).build();
	    ConnectionConfig connectionConfig = ConnectionConfig.custom()
	    				.setMalformedInputAction(CodingErrorAction.IGNORE)
	    				.setUnmappableInputAction(CodingErrorAction.IGNORE)
	    				.setCharset(Consts.UTF_8)
	    				.setMessageConstraints(messageConstraints).build();
	    connManager.setDefaultConnectionConfig(connectionConfig);
        connManager.setMaxTotal(200);
        connManager.setDefaultMaxPerRoute(20);
        
		RequestConfig defaultRequestConfig = RequestConfig.custom()
		                .setSocketTimeout(soTimeout)
		                .setConnectTimeout(timeout)
		                .setConnectionRequestTimeout(requestTimeout)
						.setCookieSpec(CookieSpecs.BEST_MATCH)
						.setExpectContinueEnabled(true)
						.setStaleConnectionCheckEnabled(true)
						.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
						.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
						.build();

		CloseableHttpClient httpclient = HttpClients.custom().disableRedirectHandling().setConnectionManager(connManager).setDefaultRequestConfig(defaultRequestConfig).build();
		
		return httpclient;
	}

	public static void main(String[] args) throws Exception {
		HttpClient client = newClient();
		for(int i=1; i < 80; i++) {
	        HttpGet httpGet = new HttpGet("http://www.171dl.com/d/file/renti/2014-10-28/18/"+i+".jpg"); 
			CloseableHttpResponse resp = (CloseableHttpResponse)client.execute(httpGet);
			if(resp.getStatusLine().getStatusCode() != 200){
				return;
			}
			FileOutputStream fos = new FileOutputStream("H:\\"+i+".jpg");
			IOUtils.copy(resp.getEntity().getContent(), fos);
			resp.close();
			fos.close();
		}
	}
}
