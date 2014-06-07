package com.next.base;

import java.util.ArrayList;


public class BaseHelper implements EventHandler{
	private ArrayList<EventListener> mListListener = new ArrayList<EventListener>();
	/**
	 * 监听
	 */
	@Override
	public void addListener(EventListener listener) {
		// TODO Auto-generated method stub
		mListListener.add(listener);
	}
	/**
	 * 移除
	 */
	@Override
	public void removeListener(EventListener listener) {
		// TODO Auto-generated method stub
		mListListener.remove(listener);
		}

	@Override
	public void onEventHandler() {
		// TODO Auto-generated method stub
		for (int i= 0;i< mListListener.size();i++){
			EventListener listener  = mListListener.get(i);
			listener.onListenerRespond(this,new Object());
		}
	}
}
