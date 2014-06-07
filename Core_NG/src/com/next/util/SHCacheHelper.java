package com.next.util;

import java.io.File;

import com.next.app.StandardApplication;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * 缓存数据库 提供key/value方式的数据缓存
 * 
 * @author chenwang
 * 
 */

public class SHCacheHelper {
	private static final String TAG = "CacheHelper";

	private SQLiteDatabase db;
	private String cacheName;
	private static SHCacheHelper __instance;
	public SHCacheHelper(String cachePath, String cacheName) {
		super();

		File path = new File(cachePath, cacheName+".db");
		try {

			this.db = SQLiteDatabase.openOrCreateDatabase(path, null);
		} catch (Exception e) {
			Log.e(TAG, "cannot open database " + path.getAbsolutePath(), e);
		}
		if (this.db == null) {
			return;
		}
		this.cacheName = cacheName;
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE IF NOT EXISTS ").append(cacheName).append(" (");
		sb.append("K TEXT PRIMARY KEY, " );
		sb.append("T INT8, ");
		sb.append("V BLOB);");
		try {
		this.db.execSQL(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SHCacheHelper getInstance(){
		if(__instance == null){
			__instance = new SHCacheHelper(StandardApplication.getInstance().getCacheDir().getAbsolutePath(), "catch");
		}
		return __instance;
	}
	
	/**
	 * 获取缓存数据
	 * 
	 * @param key
	 *            缓存key
	 * @return 缓存数据
	 */


	/**
	 * 获取缓存数据
	 * 
	 * @param key
	 *            缓存key
	 * @return 缓存数据
	 */
	public CacheData get(String key) {
		if (this.db == null || !this.db.isOpen() || key == null)
			return null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT T,V FROM ").append(this.cacheName).append(" WHERE K=\"").append(key).append("\"");
			Cursor c = this.db.rawQuery(sql.toString(), null);
			if (c.moveToFirst()) {
				long time = c.getLong(0);
				byte[] blob = c.getBlob(1);
				c.close();
				return new CacheData(blob, time);
			} else {
				c.close();
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 存储缓存数据
	 * 
	 * @param key
	 *            缓存key
	 * @param value
	 *            缓存数据
	 * @return 是否操作成功
	 */
	public boolean put(String key, byte[] value) {
		if (this.db == null || !this.db.isOpen() || key == null || value == null) {
			return false;
		}
		if (this.get(key) == null) { // 插入一条新数据
			DatabaseUtils.InsertHelper insertHelper = null;
			try {
				insertHelper = new DatabaseUtils.InsertHelper(db, cacheName);
				insertHelper.prepareForInsert();
				insertHelper.bind(insertHelper.getColumnIndex("K"), key);
				insertHelper.bind(insertHelper.getColumnIndex("T"), System.currentTimeMillis());
				insertHelper.bind(insertHelper.getColumnIndex("V"), value);
				if (insertHelper.execute() < 0) {
					return false;
				} else {
					return true;
				}
			} catch (Exception e) {
				return false;
			} finally {
				if (insertHelper != null) {
					insertHelper.close();
				}
			}
		} else { // 更新现有记录
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE ").append(this.cacheName).append(" SET T=?,V=? WHERE K=?");
			SQLiteStatement stmt = this.db.compileStatement(sql.toString());
			try {
				stmt.bindLong(1, System.currentTimeMillis());
				stmt.bindBlob(2, (byte[]) value);
				stmt.bindString(3, key);
				int row = stmt.executeUpdateDelete();
				return row > 0;
			} catch (Exception e) {
				return false;
			} finally {
				stmt.close();
			}
		}
	}
	
	/**
	 * 清除某个缓存记录
	 * @param key 缓存key
	 */
	public void remove(String key) {
		if (this.db == null || !this.db.isOpen() || key == null) {
			return;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE FROM").append(this.cacheName).append(" WHERE K=?");
		SQLiteStatement stmt = this.db.compileStatement(sql.toString());
		try {
			stmt.bindString(1, key);
			stmt.executeUpdateDelete();
			return;
		} catch (Exception e) {
			return;
		} finally {
			stmt.close();
		}
	}

	/**
	 * 将缓存记录精简至Count个数
	 * 
	 * @param count
	 *            最多保留的缓存记录数目
	 */
	public void trimToCount(int count) {
		if (db == null || !db.isOpen())
			return;

		// 查询当前总的个数
		int totalCount = 0;
		Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + cacheName, null);
		try {
			if (c.moveToFirst()) {
				totalCount = c.getInt(0);
			}
		} finally {
			c.close();
		}

		final int delCount = totalCount - count;
		if (delCount <= 0)
			return;

		// 查询最后一条需要保留的记录（根据时间顺序）
		long time = 0;
		try {
			String sql = "SELECT T FROM " + cacheName + " ORDER BY T ASC LIMIT 1 OFFSET " + delCount;
			Cursor cursor = db.rawQuery(sql, null);
			if (c.moveToFirst()) {
				time = cursor.getLong(0);
				cursor.close();
			} else {
				cursor.close();
			}
		} catch (Exception e) {
		}
		if (time == 0) {
			return;
		}
		// 删除time时间之前的记录
		try {
			db.delete(cacheName, "T < " + time, null);
			return;
		} catch (Exception e) {
			return;
		}
	}

	/**
	 * 清除数据库
	 */
	public void clear() {
		this.db.delete(this.cacheName, null, null);
		this.close();
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		this.db.close();
	}

	public class CacheData {
		private byte[] blob;
		private long time;

		public CacheData(byte[] blob, long time) {
			this.blob = blob;
			this.time = time;
		}

		public byte[] getBlob() {
			return blob;
		}

		public void setBlob(byte[] blob) {
			this.blob = blob;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

	}
}
