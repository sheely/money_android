package com.damon.ds.util;

import java.util.ArrayList;
import java.util.Arrays;

import com.damon.ds.app.DSObject;

public class DSObjectUtils extends Collection2Utils {

	public static ArrayList<DSObject> toList(DSObject[] arr) {
		ArrayList<DSObject> list = new ArrayList<DSObject>();
		if (!isEmpty(arr)) {
			list.addAll(Arrays.asList(arr));
		}
		return list;
	}

	public static DSObject[] toArr(ArrayList<DSObject> list) {
		if (!isEmpty(list)) {
			return list.toArray(new DSObject[0]);
		}
		return null;
	}

}
