package com.xdamon.download.intf;

/**
 * DownloadTask 事件监听
 * 
 * 
 */
public interface DownloadListener {

	/**
	 * 任务开始
	 * 
	 * @param task
	 */
	void onStart(DownloadTask task);

	/**
	 * 任务进行中
	 * 
	 * @param task
	 * @param progress
	 *            进度：0~100
	 */
	void onDoing(DownloadTask task, int progress);
	
	/**
	 * 任务暂停
	 * @param task
	 */
	void onPause(DownloadTask task,String message);
	
	/** 
	 * 任务恢复
	 * @param task
	 */
	void onResume(DownloadTask task);

	/**
	 * 任务成功
	 * 
	 * @param task
	 */
	void onSuccess(DownloadTask task);

	/**
	 * 任务失败
	 * 
	 * @param task
	 * @param message
	 */
	void onFailed(DownloadTask task, String message);

	/**
	 * 任务取消
	 * 
	 * @param task
	 */
	void onCancel(DownloadTask task);

}
