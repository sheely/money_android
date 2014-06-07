package com.next.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.text.TextUtils;

public class CollectionUtils {

	public static String list2Str(final List<String> source, String split) {
		if (source == null || source.size() == 0) {
			return "";
		}
		StringBuffer bfBuffer = new StringBuffer();
		for (String str : source) {
			bfBuffer.append(str).append(split);
		}
		String retValueString = bfBuffer.toString();
		return retValueString.substring(0, retValueString.length() - 1);
	}

	public static String array2Str(final String[] source, String split) {
		if (source == null || source.length == 0) {
			return "";
		}
		return list2Str(Arrays.asList(source), split);
	}

	public static String[] str2Array(String source, String split) {
		if (TextUtils.isEmpty(source)) {
			return null;
		}
		return source.split(split);
	}

	public static ArrayList<String> str2ArrayList(String source, String split) {
		if (TextUtils.isEmpty(source)) {
			return null;
		}
		ArrayList<String> retList = new ArrayList<String>();
		retList.addAll(Arrays.asList(str2Array(source, split)));
		return retList;
	}

	public static boolean isEmpty(Collection<?> collection) {
		if (collection == null || collection.size() == 0) {
			return true;
		}
		return false;
	}
	
	public static boolean isEmpty(Object[] arr) {
		if(arr == null || arr.length == 0){
			return true;
		}
		return false;
	}
}
