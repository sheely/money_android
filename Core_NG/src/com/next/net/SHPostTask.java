package com.next.net;


import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

import android.content.Context;


/**
 * 处理Http任务
 * 
 * @author sheely
 * 
 */
public class SHPostTask extends SHGetTask {


	protected  String mPostBody;


	public SHPostTask(){
		super();
	}

	public SHPostTask(Context context) {
		super(context);
	}

	
	public void setPostBody(String body){
		mPostBody = body;
	}
	public int getStatusCode() {
		return mStatusCode;
	}
	protected HttpUriRequest getUriRequest() throws Exception {
		HttpUriRequest request;

		HttpPost post = new HttpPost(this.getUrl());

		request = post;

		if(mPostBody != null){
			post.setEntity(new StringEntity(mPostBody.toString(),"UTF-8"));
		}
		// 暂不支持代理
		return request;
	}

}