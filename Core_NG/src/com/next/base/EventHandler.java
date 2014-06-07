package com.next.base;
/**
 * 事件处理器
 * @author zywang
 *
 */
public interface EventHandler {
	//添加监听
	public void addListener( EventListener listener);
	//移除
	public void removeListener (EventListener listener);
	//处理事件
	public void onEventHandler();
}
