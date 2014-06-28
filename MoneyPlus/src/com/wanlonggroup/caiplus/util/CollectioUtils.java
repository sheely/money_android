package com.wanlonggroup.caiplus.util;

import java.util.ArrayList;
import java.util.Arrays;

import com.damon.ds.util.Collection2Utils;
import com.wanlonggroup.caiplus.model.CPObject;

public class CollectioUtils extends Collection2Utils {

	public static ArrayList<CPObject> toList(CPObject[] arr) {
		ArrayList<CPObject> list = new ArrayList<CPObject>();
		if (!isEmpty(arr)) {
			list.addAll(Arrays.asList(arr));
		}
		return list;
	}

	public static CPObject[] toArr(ArrayList<CPObject> list) {
		if (!isEmpty(list)) {
			return list.toArray(new CPObject[0]);
		}
		return null;
	}

}
