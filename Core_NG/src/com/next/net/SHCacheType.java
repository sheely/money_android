package com.next.net;

/**
 * 缓存类型
 * @author chenwang
 *
 */
public enum SHCacheType {
	/**
	 * 不使用缓存
	 */
	DISABLE,
	/**
	 * 通用缓存, 一般缓存时间为5分钟
	 */
	NORMAL,
	/**
	 * 持久缓存，缓存永不过期，但超过一定时间后（一般为1天），在后台自动更新缓存数据
	 */
	PERSISTENT,
	/**
	 * 图片缓存，区别于缩略图缓存,一般存储较大的图片，缓存永不过期
	 */
	PHOTO,
	/**
	 * 缩略图缓存，缓存永不过期
	 */
	THUMBNAIL,
	/**
	 * 关键业务缓存
	 */
	NOTKEYBUSSINESS
};
