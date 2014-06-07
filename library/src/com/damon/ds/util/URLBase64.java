package com.damon.ds.util;

/**
 * 在URL中使用的Base64编码<br>
 * 所有的字符都不需要进行URL Escape。即无需调用URLEncoder.encode(base64)
 * <p>
 * 和普通的Base64编码有如下区别：<br>
 * &nbsp;&nbsp;1. 不换行<br>
 * &nbsp;&nbsp;2. 末尾没有用于padding的"="或"=="结尾<br>
 * &nbsp;&nbsp;3. "+"被替换成了"-", "\"被替换成了"_"
 * <p>
 * 
 * http://en.wikipedia.org/wiki/Base64#URL_applications
 * 
 * @author Yimin
 * 
 */
public class URLBase64 {
	private static final char[] base = new char[64];
	private static final int[] reverse = new int[128];
	static {
		int i = 0;
		for (char c = 'A'; c <= 'Z'; c++) {
			base[i] = c;
			reverse[c] = i++;
		}
		for (char c = 'a'; c <= 'z'; c++) {
			base[i] = c;
			reverse[c] = i++;
		}
		for (char c = '0'; c <= '9'; c++) {
			base[i] = c;
			reverse[c] = i++;
		}
		base[i] = '-';
		reverse['-'] = i++;
		base[i] = '_';
		reverse['_'] = i;
	}

	/**
	 * 编码成Base64的URL变种 <br>
	 * 所有的字符都不需要进行URL Escape。即无需调用URLEncoder.encode(base64)
	 */
	public static String encode(byte[] buf, int start, int length) {
		char[] ar = new char[((length + 2) / 3) * 4];
		int a = 0;
		int i = 0;
		while (i < length) {
			byte b0 = buf[start + (i++)];
			byte b1 = (i < length) ? buf[start + (i++)] : 0;
			byte b2 = (i < length) ? buf[start + (i++)] : 0;

			int mask = 0x3F;
			ar[a++] = base[(b0 >> 2) & mask];
			ar[a++] = base[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
			ar[a++] = base[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
			ar[a++] = base[b2 & mask];
		}
		switch (length % 3) {
		case 1:
			a--;
		case 2:
			a--;
		}
		return new String(ar, 0, a);
	}

	/**
	 * 编码成Base64的URL变种 <br>
	 * 所有的字符都不需要进行URL Escape。即无需调用URLEncoder.encode(base64)
	 */
	public static String encode(byte[] buf) {
		return encode(buf, 0, buf.length);
	}

	/**
	 * 把Base64的URL变种解码为byte[] <br>
	 * 执行过程中可能会抛出异常，建议使用try-catch。
	 */
	public static byte[] decode(String s) {
		int dlen = (s.length() + 3) / 4 * 4, delta = dlen - s.length();
		byte[] buffer = new byte[dlen * 3 / 4 - delta];
		int mask = 0xFF;
		int index = 0;
		for (int i = 0; i < s.length(); i += 4) {
			int c0 = reverse[s.charAt(i)];
			int c1 = reverse[s.charAt(i + 1)];
			buffer[index++] = (byte) (((c0 << 2) | (c1 >> 4)) & mask);
			if (index >= buffer.length) {
				return buffer;
			}
			int c2 = reverse[s.charAt(i + 2)];
			buffer[index++] = (byte) (((c1 << 4) | (c2 >> 2)) & mask);
			if (index >= buffer.length) {
				return buffer;
			}
			int c3 = reverse[s.charAt(i + 3)];
			buffer[index++] = (byte) (((c2 << 6) | c3) & mask);
		}
		return buffer;
	}

	private URLBase64() {
	}
}
