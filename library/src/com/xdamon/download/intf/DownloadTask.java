package com.xdamon.download.intf;

import java.io.File;

/**
 * 下载任务
 * 
 */
public interface DownloadTask {

	/**
	 * 
	 * @param url
	 *            下载地址
	 */
	void start(String url);

	/**
	 * 下载链接
	 * 
	 * @return
	 */
	String downloadUrl();

	/**
	 * 保存的文件名
	 * 
	 * @return
	 */
	String fileName();

	/**
	 * 下载完成的文件
	 * 
	 * @return
	 */
	File downloadFile();

	/**
	 * 暂停任务
	 */
	void pause();

	/**
	 * 恢复任务
	 */
	void resume();

	/**
	 * 终止任务
	 */
	void stop(boolean mayInterruptIfRunning);

}
