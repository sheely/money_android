package com.wanlonggroup.caiplus.util;

import com.next.util.Log;


public class Environment {
	public static boolean isDebug() {
		return Log.LEVEL < Integer.MAX_VALUE;
	}
}
