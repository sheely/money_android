package com.next.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.next.net.SHTask.TaskStatus;
import com.next.util.SHCacheHelper;
import com.next.util.SHCacheHelper.CacheData;
import com.next.util.SHTools;

import android.content.Context;

/**
 * 处理Http任务
 * 
 * @author sheely
 * 
 */
public class SHGetTask extends SHTask {

	protected static ThreadPoolExecutor executor;
	static {
		executor = new ThreadPoolExecutor(2, 10, 60, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	protected HashMap<String, Object> args = new HashMap<String, Object>();

	public HashMap<String, Object> getTaskArgs() {
		return args;
	}

	protected int mStatusCode;
	protected static int TIME_OUT = 30000;
	protected HttpUriRequest mHttpRequest;

	public SHGetTask() {
		super();
	}

	public SHGetTask(Context context) {
		super(context);
	}

	public int getStatusCode() {
		return mStatusCode;
	}

	@Override
	protected Object processDataFormNet() {
		try {
			CacheData date = getCacheDate();
			if (date != null && date.getBlob() != null) {
				this.mIsCache = true;//确认调用缓存
				this.mResult = date.getBlob();
				this.mRespinfo = new SHRespinfo();
				this.mTaskStatus = TaskStatus.FINISHED;
			} else {
				HttpParams httpParams = new BasicHttpParams();
				HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
				HttpProtocolParams.setUseExpectContinue(httpParams, true);
				// 设置连接管理器的超时
				ConnManagerParams.setTimeout(httpParams, TIME_OUT);
				// 设置http https支持
				SchemeRegistry schReg = new SchemeRegistry();
				if (this.mUrl != null && this.mUrl.startsWith("https://", 0)) {
					SSLSocketFactory sf;
					KeyStore trustStore = KeyStore.getInstance(KeyStore
							.getDefaultType());
					trustStore.load(null, null);
					sf = new SSLSocketFactoryEx(trustStore);

					sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); // 允许所有主机的验证
					schReg.register(new Scheme("https", sf, 443));
				} else {
					schReg.register(new Scheme("http", PlainSocketFactory
							.getSocketFactory(), 80));
				}
				ClientConnectionManager conManager = new ThreadSafeClientConnManager(
						httpParams, schReg);
				HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
				HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);
				HttpClient httpClient = new DefaultHttpClient(conManager,
						httpParams);
				this.mHttpRequest = this.getUriRequest();
				HttpResponse response = httpClient.execute(this.mHttpRequest);
				this.mStatusCode = response.getStatusLine().getStatusCode();
				byte[] byte2 = SHTools
						.inputStreamToByte((InputStream) response.getEntity().getContent());
				if (byte2 != null) {
					SHCacheHelper.getInstance().put(this.getUrl(), byte2);
					this.mResult = byte2;
					this.mRespinfo = new SHRespinfo();
					this.mTaskStatus = TaskStatus.FINISHED;
				} else {
					this.mRespinfo = new SHRespinfo();
					this.mRespinfo.mResultCode = -1;
					this.mRespinfo.mMessage = "服务器没有返回任何数据!";
					this.mTaskStatus = TaskStatus.FAILED;
				}
			}
		} catch (Exception e) {
			this.mRespinfo = new SHRespinfo();
			this.mRespinfo.mResultCode = -1;
			this.mRespinfo.mMessage = e.getMessage();
			this.mTaskStatus = TaskStatus.FAILED;
		}

		return this.mResult;
	}

	protected class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {

				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	protected HttpUriRequest getUriRequest() throws Exception {
		HttpUriRequest request;
		HttpGet post = new HttpGet(this.getUrl());
		request = post;
		// 暂不支持代理
		return request;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (super.cancel(mayInterruptIfRunning)) {
			this.mHttpRequest.abort();
			return true;
		}
		return false;
	}

	@Override
	protected ThreadPoolExecutor getThreadPoolExecutor() {
		return executor;
	}
}