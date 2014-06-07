package com.next.localcontent;
//package com.mobilitychina.localcontent;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.Serializable;
//import java.io.StreamCorruptedException;
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
//import android.content.Context;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//
//import com.mobilitychina.localcontent.ContentManager.Content;
//import com.mobilitychina.net.Task;
//import com.mobilitychina.net.Task.TaskStatus;
//import com.mobilitychina.util.Daemon;
//import com.mobilitychina.util.Log;
//
//public abstract class ContentManager<T extends Content> {
//
//	protected Context mContext;
//	protected File mContentRoot;
//	private int mNextId = 0;
//	protected ConcurrentLinkedQueue<T> mUploadItems = new ConcurrentLinkedQueue<T>();
//	protected ConcurrentHashMap<Integer, T> mUploadItemsMap = new ConcurrentHashMap<Integer, T>();
//
//	protected ContentManager(Context context) {
//		mContext = context;
//		if (isSDCardValid()) {
//			mContentRoot = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
//					+ "Android" + File.separator + "database" + File.separator + context.getPackageName()
//					+ File.separator + "db" + File.separator);
//			if (!mContentRoot.exists()) {
//				mContentRoot.mkdirs();
//			}
//		} else {
//			mContentRoot = mContext.getDir("upload", Context.MODE_PRIVATE);
//		}
//		loadHandler.sendEmptyMessage(LOAD_FROM_FILE);
//	}
//
//	public void destory() {
//		stopUploadThread();
//		loadHandler.removeMessages(SAVE_TO_FILE);
//		saveContentToFile();
//	}
//
//	protected abstract String ContentFileName();
//
//	protected String logTag() {
//		return ContentManager.class.getSimpleName();
//	}
//
//	protected void loadContentFromFile() {
//		File f = new File(mContentRoot, ContentFileName());
//		if (f == null || !f.exists()) {
//			return;
//		}
//		Log.i(logTag(), "enter loadContentFromFile size");
//
//		mUploadItems.clear();
//		mUploadItemsMap.clear();
//
//		FileInputStream fi = null;
//		ObjectInputStream oi = null;
//		try {
//			fi = new FileInputStream(f);
//			oi = new ObjectInputStream(fi);
//			T readObject = (T) oi.readObject();
//			while (readObject != null) {
//				readObject.id = mNextId++;
//				mUploadItems.add(readObject);
//				mUploadItemsMap.put(readObject.id, readObject);
//				addUploadingContent(readObject);
//				readObject = (T) oi.readObject();
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (StreamCorruptedException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (oi != null) {
//					oi.close();
//				}
//				if (fi != null) {
//					fi.close();
//				}
//			} catch (Exception e) {
//
//			}
//		}
//		mMainUIHandler.sendEmptyMessage(PUBLISH_UPLOAD_RELOAD);
//
//		Log.i(logTag(), "loadContentFromFile size  = " + mUploadItemsMap.size());
//	}
//
//	protected void saveContentToFile() {
//		File f = new File(mContentRoot, ContentFileName());
//		if (f.exists()) {
//			f.delete();
//		}
//		FileOutputStream fo = null;
//		ObjectOutputStream oo = null;
//		int count = 0;
//		try {
//			fo = new FileOutputStream(f);
//			oo = new ObjectOutputStream(fo);
//			for (T obj : mUploadItems) {
//				if (obj.status == Content.STATUS_UPLOADED) {
//					continue;
//				} else {
//					oo.writeObject(obj);
//					count++;
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (oo != null) {
//					oo.close();
//				}
//				if (fo != null) {
//					fo.close();
//				}
//			} catch (Exception e2) {
//			}
//		}
//		Log.i(logTag(), "saveContentToFile size = " + count);
//	}
//
//	public void addContent(T content) {
//		if (!(content instanceof Serializable)) {
//			Log.e(logTag(), "content must be implement Serializable");
//			return;
//		}
//		if (content == null) {
//			return;
//		}
//		content.id = mNextId++;
//		if (!mUploadItems.contains(content)) {
//			mUploadItems.add(content);
//			mUploadItemsMap.put(content.id, content);
//		}
//		addUploadingContent(content);
//		loadHandler.removeMessages(SAVE_TO_FILE);
//		loadHandler.sendEmptyMessageDelayed(SAVE_TO_FILE, 3000);
//		Log.i(logTag(), "addContent size = " + mUploadItemsMap.size());
//		mMainUIHandler.sendEmptyMessage(PUBLISH_UPLOAD_RELOAD);
//	}
//
//	private LinkedBlockingQueue<T> mUploadingItems = new LinkedBlockingQueue<T>();
//
//	private void addUploadingContent(T content) {
//		if (content == null) {
//			return;
//		}
//		synchronized (mUploadingItems) {
//			if (!mUploadingItems.contains(content)) {
//				mUploadingItems.add(content);
//			}
//		}
//		if (mUploadingItems.size() > 0) {
//			startUploadThread();
//		}
//	}
//
//	public void uploadAll() {
//		for (T obj : mUploadItems) {
//			if (obj.status == Content.STATUS_UPLOADED) {
//				continue;
//			} else {
//				addUploadingContent(obj);
//			}
//		}
//	}
//
//	private void removeUploadingContent(T content) {
//		synchronized (mUploadingItems) {
//			mUploadingItems.remove(content);
//			mUploadItems.remove(content);
//			mUploadItemsMap.remove(content.id);
//		}
//		loadHandler.removeMessages(SAVE_TO_FILE);
//		loadHandler.sendEmptyMessageDelayed(SAVE_TO_FILE, 3000);
//		mMainUIHandler.sendEmptyMessage(PUBLISH_UPLOAD_RELOAD);
//	}
//
//	private volatile T mUploadingContent;
//	private volatile UploadThread mUploadThread;
//	private volatile boolean mStopUploadThread;
//
//	class UploadThread extends Thread {
//		@Override
//		public void run() {
//			setName("content upload thread");
//			while (true) {
//				try {
//					mUploadingContent = mUploadingItems.poll(30 * 1000, TimeUnit.MILLISECONDS);
//					if (mUploadingContent == null) {
//						break;
//					}
//					if (mUploadingContent.status() == Content.STATUS_UPLOADED) {
//						continue;
//					}
//					Task task = createTask(mUploadingContent);
//					if (task == null) {
//						break;
//					}
//					task.startSync();
//					if (task.Status() == TaskStatus.FINISHED) {
//						mUploadingContent.status = Content.STATUS_UPLOADED;
//						removeUploadingContent(mUploadingContent);
//					} else {
//						mUploadingContent.status = Content.STATUS_FAILED;
//					}
//					mMainUIHandler.sendEmptyMessage(PUBLISH_UPLOAD_RELOAD);
//					if (mStopUploadThread) {
//						break;
//					}
//				} catch (Exception e) {
//					Log.e(logTag(), e.getLocalizedMessage());
//				}
//			}
//			mUploadThread = null;
//			mStopUploadThread = false;
//		}
//	}
//
//	public void startUploadThread() {
//		if (mUploadThread != null) {
//			return;
//		}
//		mStopUploadThread = false;
//		mUploadThread = new UploadThread();
//		mUploadThread.start();
//	}
//
//	private void stopUploadThread() {
//		if (mUploadThread == null) {
//			return;
//		}
//		mStopUploadThread = true;
//		mUploadThread.interrupt();
//		mUploadThread = null;
//	}
//
//	public abstract T[] getContents();
//
//	public abstract Task createTask(T content);
//
//	private static final int LOAD_FROM_FILE = 0x0a42;
//	private static final int SAVE_TO_FILE = 0x1263;
//
//	private Handler loadHandler = new Handler(Daemon.looper()) {
//		public void handleMessage(android.os.Message msg) {
//			if (msg.what == LOAD_FROM_FILE) {
//				loadContentFromFile();
//			} else if (msg.what == SAVE_TO_FILE) {
//				saveContentToFile();
//			}
//		};
//	};
//
//	public static abstract class Content implements Serializable {
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 1L;
//
//		public static final int STATUS_NONE = 0;
//		public static final int STATUS_UPLOADED = STATUS_NONE + 1;
//		public static final int STATUS_FAILED = STATUS_NONE + 2;
//		private transient int id;
//		private transient int status;
//
//		public Content() {
//
//		}
//
//		public int status() {
//			return status;
//		}
//
//		public boolean isUploaded() {
//			return status == STATUS_UPLOADED;
//		}
//	}
//
//	public static interface UploadStatusListener {
//		void onUploadProgress(int itemId, int progress);
//
//		void onUploadReload();
//	}
//
//	private static final int PUBLISH_UPLOAD_RELOAD = 0x004A;
//
//	private ArrayList<WeakReference<UploadStatusListener>> mListeners = new ArrayList<WeakReference<UploadStatusListener>>();
//
//	private MainUIHandler mMainUIHandler = new MainUIHandler();
//
//	class MainUIHandler extends Handler {
//		public MainUIHandler() {
//			super(Looper.getMainLooper());
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			Log.i(logTag(), "MainUIHandler handleMessage " + msg);
//			switch (msg.what) {
//			case PUBLISH_UPLOAD_RELOAD:
//				publishUploadReload();
//				break;
//			}
//		}
//	}
//
//	public void registerListener(UploadStatusListener l) {
//		if (l == null) {
//			return;
//		}
//
//		for (WeakReference<UploadStatusListener> item : mListeners) {
//			if (item.get() == l) {
//				return;
//			}
//		}
//
//		mListeners.add(new WeakReference<UploadStatusListener>(l));
//	}
//
//	public void unregisterListener(UploadStatusListener l) {
//		if (l == null) {
//			return;
//		}
//
//		for (WeakReference<UploadStatusListener> item : mListeners) {
//			if (item.get() == l) {
//				mListeners.remove(item);
//				break;
//			}
//		}
//	}
//
//	private void publishUploadReload() {
//		ArrayList<WeakReference<UploadStatusListener>> toRemove = new ArrayList<WeakReference<UploadStatusListener>>();
//
//		for (WeakReference<UploadStatusListener> w : mListeners) {
//			if (w.get() != null) {
//				w.get().onUploadReload();
//			} else {
//				toRemove.add(w);
//			}
//		}
//
//		for (WeakReference<UploadStatusListener> w : toRemove) {
//			mListeners.remove(w);
//		}
//	}
//
//	private boolean isSDCardValid() {
////		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//		return false;
//	}
//
//}
