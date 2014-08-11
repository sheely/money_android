package com.next.net;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.next.app.StandardApplication;
import com.next.intf.ITaskListener;
import com.next.util.Log;
import com.next.util.SHCacheHelper;
import com.next.util.SHCacheHelper.CacheData;

/**
 * 所有任务的基类
 * 
 * @author sheely
 * 
 */
public abstract class SHTask {
	private static final String TAG = "Task";
	protected int mMaxTryCount = 0;// 最大重试次数;
	protected int mCurTryCount = 0;
	protected String mUrl;
	protected Object mResult;
	protected Object mExtra;
	protected SHRespinfo mRespinfo;
	protected TaskStatus mTaskStatus;
	protected static final int MESSAGE_POST_RESULT = 0x1;
	protected static final int MESSAGE_POST_PROGRESS = 0x2;
	protected static final int MESSAGE_POST_RETRY = 0x3;
	protected FutureTask<Object> mFutureTask;
	protected Context mContext;
	protected Boolean mIsCache = false;
	private Boolean mNeedMainThread = true;
	protected ITaskListener mListener;
	/**
	 * 缓存
	 */
	protected SHCacheType mCacheType = SHCacheType.DISABLE;
	/**
	 * 缓存
	 */
	public void setChacheType(SHCacheType type){
		mCacheType = type;
	}
	/**
	 * 获取缓存
	 * @return
	 */
	protected CacheData getCacheDate(){
		CacheData date = null ;
		if (mCacheType != SHCacheType.DISABLE) {
			date = SHCacheHelper.getInstance().get(this.getUrl());
			if (date != null && date.getBlob() != null) {
				if (mCacheType == SHCacheType.PERSISTENT) {
					long times = System.currentTimeMillis();
					if ((times - date.getTime()) / 1000 / 3600 / 24 > 1) {
						date = null;
					}
				}else if (mCacheType == SHCacheType.NORMAL){
					long times = System.currentTimeMillis();
					if ((times - date.getTime()) / 1000 / 60  > 5) {
						date = null;
					}
				}
			}
		}
		return date;
	}
	/**
	 * 是否缓存
	 * @return
	 */
	public Boolean getIsCache(){
		return mIsCache;
	}
	/**
	 * 回调是否需要使用主线程
	 * @param mainThread
	 */
	public void setIsMainThread(Boolean mainThread){
		mNeedMainThread = mainThread;
	}
	public Boolean getIsMainThread(){
		return mNeedMainThread;
	}
	/**
	 * extra 标示类信息，标示数据体，字段
	 * 
	 * @return
	 */
	public Object getExtra() {
		return mExtra;
	}

	/**
	 * 设置最大重试次数
	 * 
	 * @param max
	 */
	public void setMaxTryCount(int max) {
		mMaxTryCount = max;
	}

	/**
	 * 获取最大重试次数
	 * 
	 * @return
	 */
	public int getMaxTryCount() {
		return mMaxTryCount;
	}

	/**
	 * 当前重试
	 * 
	 * @return
	 */
	public int getCurTryCount() {
		return mCurTryCount;
	}

	/**
	 * Task状态
	 * 
	 */
	public enum TaskStatus {
		FINISHED, FAILED
	}

	/**
	 * Task结果描述
	 * 
	 * @return
	 */
	public SHRespinfo getRespInfo() {
		return mRespinfo;
	}
	/**
	 * handler
	 */
	protected Handler mHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		Object result = msg.obj;
		switch (msg.what) {
		// 结果处理
		case MESSAGE_POST_RESULT:
			// There is only one result
			SHTask.this.mResult = result;
			SHTask.this.taskResult();
			break;
		case MESSAGE_POST_PROGRESS:
			break;
		// 重试
		case MESSAGE_POST_RETRY:
			mCurTryCount++;
			if (SHTask.this.mListener != null) {
				SHTask.this.mListener.onTaskTry(SHTask.this);
				SHTask.this.mFutureTask = null;
				SHTask.this.start();
			}
			break;
		}
		}
	};

	/**
	 * Task 结果处理
	 */
	protected void taskResult() {
		if (SHTask.this.mListener != null) {
			if (SHTask.this.mTaskStatus == TaskStatus.FAILED) {
				// Task.this.listener.onTaskFailed(Task.this);
				if (mMaxTryCount <= mCurTryCount) {
					
					SHTask.this.mListener.onTaskFailed(SHTask.this);
				} else {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message msg = new Message();
					msg.what = MESSAGE_POST_RETRY;
				}
			} else {
				if (this.mRespinfo.getCode() == 0) {
					try {
						SHTask.this.mListener.onTaskFinished(SHTask.this);
					} catch (Exception e) {

					}
				} else {
					SHTask.this.mListener.onTaskFailed(SHTask.this);
				}
			}
		}
	}

	/**
	 * 构造器
	 */
	public SHTask() {
		super();
		this.mContext = StandardApplication.getInstance();
	}

	public SHTask(Context context) {
		this.mContext = context;
	}
	/**
	 * 获取URL
	 * @return
	 */
	public String getUrl() {
		return mUrl;
	}

	/**
	 * Task URL
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.mUrl = url;
	}

	/**
	 * Task 结果
	 * 
	 * @return
	 */
	public Object getResult() {
		return mResult;
	}

	public ITaskListener getListener() {
		return mListener;
	}

	public void setListener(ITaskListener listener) {
		this.mListener = listener;
	}

	/**
	 * 启动Task，异步方式
	 * 
	 * @return 是否成功启动
	 */
	public boolean start() {
		return innerStart();
	}

	protected boolean innerStart() {
		ThreadPoolExecutor executor = this.getThreadPoolExecutor();
		if (executor == null) {
			Log.w(TAG, "Task 异步启动需要依赖线程池");
			return false;
		}
		if (mFutureTask != null) {
			Log.w(TAG, "Task 不能重复启动");
			return false;
		}
		mFutureTask = new FutureTask<Object>(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				// TODO Auto-generated method stub
				return processDataFormNet();
			}
		}) {
			@Override
			protected void done() {
				try {
					final Object result = get();
					//投递消息
					this.sendResult(result);
				} catch (Exception e) {
					this.sendResult(null);
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
			}

			private void sendResult(Object result) {
				Message msg = new Message();
				msg.what = MESSAGE_POST_RESULT;
				msg.obj = result;
//				handleMessage(msg);
//				msg.setTarget(mHandler);
				if (mNeedMainThread) {
					mHandler.sendMessage(msg);
				} else {
					mHandler.handleMessage(msg);
				}
			}
		};

		executor.execute(mFutureTask);
		return true;
	}

	/**
	 * 启动Task,同步方式
	 * 
	 * @return 是否成功启动
	 */
	public Object startSync() {
		return processDataFormNet();
	}

	/**
	 * 取消或停止Task
	 * 
	 * @return 是否成功取消
	 */
	public boolean cancel(boolean mayInterruptIfRunning) {
		this.mListener = null;
		if (mFutureTask == null) {
			Log.w(TAG, "Task 没有启动");
			return false;
		}
		return mFutureTask.cancel(mayInterruptIfRunning);
	}

	/**
	 * 异步处理获取数据过程
	 * 
	 * @return
	 */
	protected abstract Object processDataFormNet();

	protected abstract ThreadPoolExecutor getThreadPoolExecutor();
}
