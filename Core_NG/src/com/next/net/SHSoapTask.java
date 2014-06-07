//package com.next.net;
//
//import java.io.StringReader;
//import java.net.InetSocketAddress;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import org.apache.http.NameValuePair;
//import org.ksoap2.SoapEnvelope;
//import org.ksoap2.SoapFault;
//import org.ksoap2.serialization.PropertyInfo;
//import org.ksoap2.serialization.SoapObject;
//import org.ksoap2.serialization.SoapPrimitive;
//import org.ksoap2.serialization.SoapSerializationEnvelope;
//import org.ksoap2.transport.HttpTransportSE;
//import org.kxml2.io.KXmlParser;
//import org.kxml2.kdom.Document;
//import org.kxml2.kdom.Element;
//import org.kxml2.kdom.Node;
//import android.content.Context;
//import android.os.Message;
//
//import com.next.cache.CacheTask;
//import com.next.cache.CacheTask.CacheMethod;
//import com.next.intf.ITaskListener;
//import com.next.util.CacheType;
//import com.next.util.Log;
//import com.next.util.SHEnvironment;
//import com.next.util.SHCacheHelper.CacheData;
//
///**
// * 处理Soap任务
// * 
// * @author chenwang
// * 
// */
//public class SHSoapTask extends SHTask {
//	private static final String TAG = "SoapTask";
//
//	private static final int TIME_OUT = 30000;
//	private static ThreadPoolExecutor executor;
//	static {
//		executor = new ThreadPoolExecutor(3, 6, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
//	}
//
//	private List<NameValuePair> params;
//
//	private String soapNamespace;
//	private String soapMethod;
//
//	private CacheType cacheType;
//
//	public SHSoapTask(Context context) {
//		super(context);
//		this.cacheType = CacheType.DISABLE;
//		params = new ArrayList<NameValuePair>();
//	}
//
//	protected ThreadPoolExecutor getThreadPoolExecutor() {
//		return executor;
//	}
//
//	public List<NameValuePair> getParams() {
//		return params;
//	}
//
//	public void setParams(List<NameValuePair> params) {
//		this.params = params;
//	}
//
//	public String getSoapNamespace() {
//		return soapNamespace;
//	}
//
//	public void setSoapNamespace(String soapNamespace) {
//		this.soapNamespace = soapNamespace;
//	}
//
//	public String getSoapMethod() {
//		return soapMethod;
//	}
//
//	public void setSoapMethod(String soapMethod) {
//		this.soapMethod = soapMethod;
//	}
//
//	public CacheType getCacheType() {
//		return cacheType;
//	}
//
//	public void setCacheType(CacheType cacheType) {
//		this.cacheType = cacheType;
//		if (this.cacheType != CacheType.NORMAL && this.cacheType != CacheType.PERSISTENT && this.cacheType != CacheType.NOTKEYBUSSINESS) {
//			this.cacheType = CacheType.DISABLE;
//		}
//	}
//
//	@Override
//	public boolean cancel(boolean mayInterruptIfRunning) {
//		// TODO Auto-generated method stub
//		return super.cancel(mayInterruptIfRunning);
//	}
//
//	private String generateCacheKey() {
//		StringBuffer sb = new StringBuffer();
//		sb.append(this.mUrl).append("/").append(this.soapNamespace).append("/").append(this.soapMethod);
//		for (NameValuePair param : this.params) {
//			sb.append("/").append(param.getName()).append("=").append(param.getValue());
//		}
//		return sb.toString();
//	}
//	protected void taskResult()
//	{
//		if (this.mListener != null) {
//			if (this.mTaskStatus == TaskStatus.FAILED) {
//				//Task.this.listener.onTaskFailed(Task.this);
//				if(mMaxTryCount <= mCurTryCount){
//					if(this.getCacheType() == CacheType.NOTKEYBUSSINESS){
//						CacheTask cacheTask = new CacheTask(this.mContext, this.cacheType);
//						cacheTask.setUrl(this.generateCacheKey());
//						cacheTask.setListener(new ITaskListener() {
//
//							@Override
//							public void onTaskUpdateProgress(SHTask task, int count, int total) {
//								// TODO Auto-generated method stub
//
//							}
//
//							@Override
//							public void onTaskFinished(SHTask task) {
//								Object cacheData = task.getResult();
//								if (cacheData != null && cacheData instanceof CacheData) {
//									byte[] blob = ((CacheData) cacheData).getBlob();
//									if (blob != null) {								
//											String value  = new String(blob);
//											Object obj = SHSoapTask.this.xmlToSoapObject(value);
//											SHSoapTask.this.mResult = obj;
//											try {
//											SHSoapTask.this.mListener.onTaskFinished(SHSoapTask.this);
//											}catch (Exception e){
//												
//											}
//											return;
//										
//									}
//								}
//								SHSoapTask.this.mListener.onTaskFailed(task);
//							}
//
//							@Override
//							public void onTaskFailed(SHTask task) {
//								// TODO Auto-generated method stub
//								SHSoapTask.this.mListener.onTaskFailed(task);
//							}
//
//							@Override
//							public void onTaskTry(SHTask task) {
//								// TODO Auto-generated method stub
//								
//							}
//						});
//						cacheTask.start();	
//					}else{
//						this.mListener.onTaskFailed(this);
//					}
//				}else{
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					Message msg = new Message();
//					msg.what = MESSAGE_POST_RETRY;
////					handleMessage(msg);
//					msg.setTarget(mHandler);
//
//				}
//			} else {
//				try {
//					this.mListener.onTaskFinished(this);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//	@Override
//	public boolean start() {
//		// TODO Auto-generated method stub
//		if (this.getCacheType() == CacheType.NORMAL || this.getCacheType() == CacheType.PERSISTENT) {
//			CacheTask cacheTask = new CacheTask(this.mContext, this.cacheType);
//			cacheTask.setUrl(this.generateCacheKey());
//			cacheTask.setListener(new ITaskListener() {
//
//				@Override
//				public void onTaskUpdateProgress(SHTask task, int count, int total) {
//					// TODO Auto-generated method stub
//
//				}
//
//				@Override
//				public void onTaskFinished(SHTask task) {
//					Object cacheData = task.getResult();
//					if (cacheData != null && cacheData instanceof CacheData) {
//						long time = ((CacheData) cacheData).getTime();
//						byte[] blob = ((CacheData) cacheData).getBlob();
//						if (blob != null) {
//							double offset = System.currentTimeMillis() - time;
//							if (SHSoapTask.this.getCacheType() == CacheType.NORMAL
//									&& (offset > 5 * 60 * 1000 || offset < 0)) { // Normal缓存已失效
//								// 清除缓存
//								CacheTask deleteCacheTask = new CacheTask(SHSoapTask.this.mContext,
//										SHSoapTask.this.cacheType);
//								deleteCacheTask.setUrl(SHSoapTask.this.generateCacheKey());
//								deleteCacheTask.setCacheMethod(CacheMethod.REMOVE);
//								deleteCacheTask.start(); // 异步处理
//							} else if (offset > 24 * 60 * 60 * 1000 || offset < 0) { // 持久缓存已到期，刷新数据
//								SHSoapTask.this.mResult = new String(blob);
//								try {
//									SHSoapTask.this.mListener.onTaskFinished(SHSoapTask.this);
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								return;
//							} else {
//								SHSoapTask.this.mResult = new String(blob);
//								try {
//									SHSoapTask.this.mListener.onTaskFinished(SHSoapTask.this);
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								return;
//							}
//						}
//					}
//					SHSoapTask.this.innerStart();
//				}
//
//				@Override
//				public void onTaskFailed(SHTask task) {
//					// TODO Auto-generated method stub
//					SHSoapTask.this.innerStart();
//				}
//
//				@Override
//				public void onTaskTry(SHTask task) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//			cacheTask.start();
//			return true;
//		} else {
//			return super.start();
//		}
//	}
//
//	protected Object processDataFormNet() {
//		HttpTransportSE ht = getHttpTransportSE(mUrl);
//		ht.debug = true;
//		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//
//		//envelope.dotNet = true;
//		SoapObject rpc = new SoapObject(soapNamespace, soapMethod);
//		for (NameValuePair param : this.params) {
//			rpc.addProperty(param.getName(), param.getValue());
//		}
//		envelope.bodyOut = rpc;
//		Element[] header = new Element[1];
//		header[0] = new Element().createElement(soapNamespace, "SoapHeader");
//
//		Element version = new Element().createElement(soapNamespace, "version");
//		version.addChild(Node.TEXT, SHEnvironment.getInstance().getVersion());
//		Element token = new Element().createElement(soapNamespace, "token");
//		token.addChild(Node.TEXT, SHEnvironment.getInstance().getSession());
//		header[0].addChild(Node.ELEMENT, version);
//		header[0].addChild(Node.ELEMENT, token);
//		envelope.headerOut = header;
//		Log.i(TAG, rpc.toString());
//
//		try {
//			ht.call(null, envelope);
//			//ht.call(soapNamespace + "/" + soapMethod, envelope);
//		} catch (Exception e) {
//			//this.error = e;
//			this.mTaskStatus = TaskStatus.FAILED;
//			return null;
//		}
//		Object result = null;
//		try {
//		//	SoapObject result1 = (SoapObject) envelope.bodyIn;
//        //    String name = result1.getProperty(0).toString();
//			
//			result = envelope.getResponse();
//			if (result != null) {
//				String temp = result.toString();
//				Log.i(TAG, temp.substring(0, temp.length() > 256 ? 256 : temp.length() - 1));
//			} else {
//				Log.e(TAG, "空返回");
//			}
//		} catch (SoapFault e) {
//			//this.error = e;
//			this.mTaskStatus = TaskStatus.FAILED;
//			Log.e(TAG, e.toString());
//			return null;
//		} finally {
//		}
//		this.mTaskStatus = TaskStatus.FINISHED;
//
//		if (result != null && this.cacheType != CacheType.DISABLE) {
//			CacheTask addCacheTask = new CacheTask(this.mContext, this.cacheType);
//			addCacheTask.setUrl(this.generateCacheKey());
//			addCacheTask.setCacheMethod(CacheMethod.PUT);
//			//SoapFormatter sf = 
//			//SoapObject on = new SoapObject("", "");
//			StringBuilder sb = new StringBuilder();
//			if(result.getClass() == SoapObject.class){
//				
//				soapObjectToXml(null,(SoapObject)result,sb);
//			}
//			addCacheTask.setCacheData(sb.toString().getBytes());
//			addCacheTask.start();
//		}
//
//		return result;
//	}
//
//	private void xmlToSpapObject(Element nd,SoapObject so){
//		for (int i = 0; i < nd.getChildCount(); i++) {
//			Object ob = nd.getChild(i);
//			if(ob.getClass() == String.class){
//				return ;
//			}
//			Element el = (Element)ob;
//			PropertyInfo pi = new PropertyInfo();
//			pi.setName(el.getAttributeValue(0));
//			if(el.getName().equalsIgnoreCase("SoapObject")){
//				SoapObject sojb = new SoapObject("", "");
//				pi.setValue(sojb);
//				xmlToSpapObject(el,sojb);
//			}else{
//				SoapPrimitive sp = new SoapPrimitive("", "anyType", el.getAttributeValue(1));
//				pi.setValue(sp);
//			}
//			so.addProperty(pi);
//		}
//	}
//	private Object xmlToSoapObject(String value) {
//		StringReader sr = new StringReader(value);
//		try {
//
//			// 找到xml，并加载文档
//			KXmlParser parser = new KXmlParser();
//			parser.setInput(new StringReader(value));
//			Document document = new Document();
//			document.parse(parser);
//			// 找到根Element
//			Element root = document.getRootElement();
//			SoapObject so = new SoapObject("", "");
//			xmlToSpapObject(root,so);
//			return so;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//	private void soapPrimitiveToXml(PropertyInfo po,SoapPrimitive sp,StringBuilder sb ){
//	
//		sb.append("<SoapPrimitive type=\""+po.getName()+  "\"  value=\""+sp.toString()+"\">");
//		sb.append("</SoapPrimitive >");
//	}
//	private void soapObjectToXml(PropertyInfo p,SoapObject so,StringBuilder sb ){
//		sb.append("<SoapObject type=\""+((p == null||p.getName()==null) ? "":p.getName())+"\">");
//		for (int i = 0; i < so.getPropertyCount(); i++) {
//			PropertyInfo po = new PropertyInfo();
//			so.getPropertyInfo(i, po);
//			if(po != null ){
//				Object ob = po.getValue();
//				if(ob.getClass() == SoapObject.class){
//					SoapObject sobject= (SoapObject)ob;
//					soapObjectToXml(po,sobject,sb);
//				}else if (ob.getClass() == SoapPrimitive.class){
//					soapPrimitiveToXml(po,(SoapPrimitive) ob, sb);
//				}
//			}
//			
//		}
//		sb.append("</SoapObject>");
//		
//	}
//	public boolean isUsingProxy() {
//		String proxyHost = android.net.Proxy.getDefaultHost();
//		return proxyHost != null;
//	}
//
//	public HttpTransportSE getHttpTransportSE(String url) {
//		HttpTransportSE ht;
//		if (isUsingProxy()) {
//			java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
//					android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
//			ht = new HttpTransportSE(p, url, TIME_OUT);
//		} else {
//			ht = new HttpTransportSE(url, TIME_OUT);
//		}
//		return ht;
//	}
//}
