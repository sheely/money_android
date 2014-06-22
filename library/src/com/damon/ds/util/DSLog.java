package com.damon.ds.util;

/**
 * 自定义日志，运行时可以通过ENABLE来关闭。<br>
 * 在调试状态下开启，发布下关闭。
 * <p>
 * 日志级别定义
 * <p>
 * v/verbose：用以打印非常详细的日志，例如如果你需要打印网络请求及返回的数据。<br>
 * d/debug：用以打印便于调试的日志，例如网络请求返回的关键结果或者操作是否成功。<br>
 * i/information：用以打印为以后调试或者运行中提供运行信息的日志，例如进入或退出了某个函数、进入了函数的某个分支等。<br>
 * w/warning：用以打印不太正常但是还不是错误的日志。<br>
 * e/error：用以打印出现错误的日志，一般用以表示错误导致功能无法继续运行。<br>
 */
public class DSLog {
	/**
	 * 当前的输出等级，当n>=LEVEL时才会输出<br>
	 * 如果当前为调试模式，默认应该设置为i/info<br>
	 * 如果想完全禁用输出，如发布，则应该设置为Integer.MAX_VALUE<br>
	 * 默认为完全禁用
	 */
	public static int LEVEL = Integer.MAX_VALUE;

	/**
	 * v/verbose：用以打印非常详细的日志，例如如果你需要打印网络请求及返回的数据。
	 */
	public static final int VERBOSE = android.util.Log.VERBOSE;

	/**
	 * d/debug：用以打印便于调试的日志，例如网络请求返回的关键结果或者操作是否成功。
	 */
	public static final int DEBUG = android.util.Log.DEBUG;

	/**
	 * i/information：用以打印为以后调试或者运行中提供运行信息的日志，例如进入或退出了某个函数、进入了函数的某个分支等。
	 */
	public static final int INFO = android.util.Log.INFO;

	/**
	 * w/warning：用以打印不太正常但是还不是错误的日志。
	 */
	public static final int WARN = android.util.Log.WARN;

	/**
	 * e/error：用以打印出现错误的日志，一般用以表示错误导致功能无法继续运行。
	 */
	public static final int ERROR = android.util.Log.ERROR;

	private static String TAG_DEFAULT = "com.damon.ds";

	/**
	 * 检查当前是否需要输出对应的level。
	 * <p>
	 * 如果直接输出字符串，则无需检查。如Log.d("test", "this is a error");
	 */
	public static boolean isLoggable(int level) {
		return level >= LEVEL;
	}

	/**
	 * v/verbose：用以打印非常详细的日志，例如如果你需要打印网络请求及返回的数据。
	 */
	public static void v(String tag, String log) {
		if (VERBOSE >= LEVEL) {
			android.util.Log.v(tag, log);
		}
	}

	/**
	 * v/verbose：用以打印非常详细的日志，例如如果你需要打印网络请求及返回的数据。
	 * <p>
	 * 附带具体的Exception，一般必须带有tag。不允许在默认的tag中输出Exception
	 */
	public static void v(String tag, String log, Throwable e) {
		if (VERBOSE >= LEVEL) {
			android.util.Log.v(tag, log, e);
		}
	}

	/**
	 * v/verbose：用以打印非常详细的日志，例如如果你需要打印网络请求及返回的数据。
	 */
	public static void v(String log) {
		if (VERBOSE >= LEVEL) {
			android.util.Log.v(TAG_DEFAULT, log);
		}
	}

	/**
	 * d/debug：用以打印便于调试的日志，例如网络请求返回的关键结果或者操作是否成功。
	 */
	public static void d(String tag, String log) {
		if (DEBUG >= LEVEL) {
			android.util.Log.d(tag, log);
		}
	}

	/**
	 * d/debug：用以打印便于调试的日志，例如网络请求返回的关键结果或者操作是否成功。
	 * <p>
	 * 附带具体的Exception，一般必须带有tag。不允许在默认的tag中输出Exception
	 */
	public static void d(String tag, String log, Throwable e) {
		if (DEBUG >= LEVEL) {
			android.util.Log.d(tag, log, e);
		}
	}

	/**
	 * d/debug：用以打印便于调试的日志，例如网络请求返回的关键结果或者操作是否成功。
	 */
	public static void d(String log) {
		if (DEBUG >= LEVEL) {
			android.util.Log.d(TAG_DEFAULT, log);
		}
	}

	/**
	 * i/information：用以打印为以后调试或者运行中提供运行信息的日志，例如进入或退出了某个函数、进入了函数的某个分支等。
	 */
	public static void i(String tag, String log) {
		if (INFO >= LEVEL) {
			android.util.Log.i(tag, log);
		}
	}

	/**
	 * i/information：用以打印为以后调试或者运行中提供运行信息的日志，例如进入或退出了某个函数、进入了函数的某个分支等。
	 * <p>
	 * 附带具体的Exception，一般必须带有tag。不允许在默认的tag中输出Exception
	 */
	public static void i(String tag, String log, Throwable e) {
		if (INFO >= LEVEL) {
			android.util.Log.i(tag, log, e);
		}
	}

	/**
	 * i/information：用以打印为以后调试或者运行中提供运行信息的日志，例如进入或退出了某个函数、进入了函数的某个分支等。
	 */
	public static void i(String log) {
		if (INFO >= LEVEL) {
			android.util.Log.i(TAG_DEFAULT, log);
		}
	}

	/**
	 * w/warning：用以打印不太正常但是还不是错误的日志。
	 */
	public static void w(String tag, String log) {
		if (WARN >= LEVEL) {
			android.util.Log.w(tag, log);
		}
	}

	/**
	 * w/warning：用以打印不太正常但是还不是错误的日志。
	 * <p>
	 * 附带具体的Exception，一般必须带有tag。不允许在默认的tag中输出Exception
	 */
	public static void w(String tag, String log, Throwable e) {
		if (WARN >= LEVEL) {
			android.util.Log.w(tag, log, e);
		}
	}

	/**
	 * w/warning：用以打印不太正常但是还不是错误的日志。
	 */
	public static void w(String log) {
		if (WARN >= LEVEL) {
			android.util.Log.w(TAG_DEFAULT, log);
		}
	}

	/**
	 * e/error：用以打印出现错误的日志，一般用以表示错误导致功能无法继续运行。
	 */
	public static void e(String tag, String log) {
		if (ERROR >= LEVEL) {
			android.util.Log.e(tag, log);
		}
	}

	/**
	 * e/error：用以打印出现错误的日志，一般用以表示错误导致功能无法继续运行。
	 * <p>
	 * 附带具体的Exception，一般必须带有tag。不允许在默认的tag中输出Exception
	 */
	public static void e(String tag, String log, Throwable e) {
		if (ERROR >= LEVEL) {
			android.util.Log.e(tag, log, e);
		}
	}

	/**
	 * e/error：用以打印出现错误的日志，一般用以表示错误导致功能无法继续运行。
	 */
	public static void e(String log) {
		if (ERROR >= LEVEL) {
			android.util.Log.e(TAG_DEFAULT, log);
		}
	}
	
}
