package com.next.net;

import java.security.KeyStore;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.next.util.SHCacheHelper;
import com.next.util.SHCacheHelper.CacheData;

/**
 * 处理Http任务
 * 
 * @author sheely
 * 
 */
public class SHPostTaskM extends SHPostTask {

	@Override
	protected Object processDataFormNet() {
		CacheData date = getCacheDate();
		try {
			if (date != null && date.getBlob() != null) {
				this.mIsCache = true;// 确认调用缓存
				String result = new String(date.getBlob());
				SHAnalyzeFactory.analyze(this, result);
			} else {

				HttpParams httpParams = new BasicHttpParams();
				HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
				HttpProtocolParams.setUseExpectContinue(httpParams, true);
				// 设置连接管理器的超时
				ConnManagerParams.setTimeout(httpParams, SHPostTask.TIME_OUT);
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

				String httpResult = EntityUtils.toString(response.getEntity(),
						"utf-8");
				response.getEntity().consumeContent();
				SHAnalyzeFactory.analyze(this, httpResult);
				this.mTaskStatus = TaskStatus.FINISHED;
				SHCacheHelper.getInstance().put(this.getUrl(),
						httpResult.getBytes());
			}
		} catch (Exception e) {
			this.mRespinfo = new SHRespinfo();
			this.mRespinfo.mResultCode = -1;
			this.mRespinfo.mMessage = e.getMessage();
			this.mTaskStatus = TaskStatus.FAILED;
		}
		return this.mResult;
	}

	protected HttpUriRequest getUriRequest() throws Exception {
		HttpUriRequest request;

		HttpPost post = new HttpPost(this.getUrl());
		if (mPostBody != null) {
			post.setEntity(new StringEntity(mPostBody));
		} else {
			JSONObject jsonPostBody = new JSONObject();
			jsonPostBody.put("identication", SHIdentication.getIdentication());
			JSONObject jsData = new JSONObject();
			for (String arg : args.keySet()) {
				jsData.put(arg, args.get(arg));
			}
			jsonPostBody.put("data", jsData);
			post.setEntity(new StringEntity(jsonPostBody.toString(), "UTF-8"));
		}
		request = post;

		// 暂不支持代理
		return request;
	}

	/**
	 * js数组
	 */
	public Object getResult() {
		return (Object) mResult;
	}

}
