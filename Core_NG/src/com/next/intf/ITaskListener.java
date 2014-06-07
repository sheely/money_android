package com.next.intf;

import com.next.net.SHTask;


public interface ITaskListener {
	/**
	 * 任务完成时回调
	 * @param task
	 */
	public void onTaskFinished(SHTask task) throws Exception ;

	public void onTaskFailed(SHTask task);

	public void onTaskUpdateProgress(SHTask task, int count, int total);
	
	public void onTaskTry(SHTask task);
}
