package com.xdamon.util;

/*
 * Copyright 2013 Stephen Asbury
 * Released under the MIT license
 * http://opensource.org/licenses/MIT
 */
import java.math.*;

/**
 * Static methods for working with numbers.
 */
public class NumberUtils {
	/**
	 * Convert a short to its byte components.
	 * 
	 * @param s
	 *            The short
	 * @return The equivalent bytes
	 */
	public static byte[] convertToBinary(short s) {
		byte[] retVal = new byte[2];

		retVal[0] = (byte) ((s >>> 0) & 0xFF);
		retVal[1] = (byte) ((s >>> 8) & 0xFF);

		return retVal;
	}

	/**
	 * Convert bytes to a short.
	 * 
	 * @param bytes
	 *            The bytes to convert
	 * @return The short created from the bytes.
	 */
	public static short convertToShort(byte[] bytes) {
		short retVal = 0;
		int i, j;

		if (bytes.length > 1) {
			i = (int) bytes[0];
			j = (int) bytes[1];

			if (j < 0)
				j = -(j ^ 0xff) - 1;
			if (i < 0)
				i = -(i ^ 0xff) - 1;

			retVal = (short) ((i << 0) + (j << 8));
		} else if (bytes.length == 1) {
			i = (int) bytes[0];
			retVal = (short) (i << 0);
		}

		return retVal;
	}

	/**
	 * Convert a int to its byte components.
	 * 
	 * @param i
	 *            The int
	 * @return The equivalent bytes
	 */
	public static byte[] convertToBinary(int i) {
		byte[] retVal = new byte[4];
		int index, shift;

		for (index = 0, shift = 0; index < 4; index++) {
			retVal[index] = (byte) ((i >>> shift) & 0xFF);
			shift += 8;
		}

		return retVal;
	}

	/**
	 * Convert bytes to an int. Works with 4,2 or 1 byte.
	 * 
	 * @param bytes
	 *            The bytes to convert
	 * @return The int created from the bytes.
	 */
	public static int convertToInt(byte[] bytes) {
		int retVal = 0;
		int i;
		int index, shift;

		if (bytes.length > 3) {
			for (index = 0, shift = 0; index < 4; index++) {
				i = (int) bytes[index];
				if (i < 0)
					i = -(i ^ 0xff) - 1;

				retVal += (i << shift);
				shift += 8;
			}
		} else if (bytes.length > 1) {
			retVal = (int) convertToShort(bytes);
		} else if (bytes.length == 1) {
			i = (int) bytes[0];
			retVal = (i << 0);
		}

		return retVal;
	}

	/**
	 * Convert a long to its byte components.
	 * 
	 * @param i
	 *            The long
	 * @return The equivalent bytes
	 */
	public static byte[] convertToBinary(long i) {
		byte[] retVal = new byte[8];
		int index, shift;

		for (index = 0, shift = 0; index < 8; index++) {
			retVal[index] = (byte) ((i >>> shift) & 0xFF);
			shift += 8;
		}

		return retVal;
	}

	/**
	 * Convert bytes to an long. Works with 8,4,2 or 1 byte.
	 * 
	 * @param bytes
	 *            The bytes to convert
	 * @return The long created from the bytes.
	 */
	public static long convertToLong(byte[] bytes) {
		long retVal = 0;
		long i;
		int shift;
		int index;

		if (bytes.length > 7) {
			for (index = 0, shift = 0; index < 8; index++) {
				i = (long) bytes[index];
				if (i < 0)
					i = -(i ^ 0xff) - 1;

				retVal += (i << shift);
				shift += 8;
			}
		} else if (bytes.length > 3) {
			retVal = (long) convertToInt(bytes);
		} else if (bytes.length > 1) {
			retVal = convertToShort(bytes);
		} else if (bytes.length == 1) {
			i = (int) bytes[0];
			retVal = (int) (i << 0);
		}

		return retVal;
	}

	/**
	 * Convert a double to its byte components.
	 * 
	 * @param f
	 *            The double
	 * @return The equivalent bytes
	 */
	public static byte[] convertToBinary(double f) {
		long i = Double.doubleToLongBits(f);
		return convertToBinary(i);
	}

	/**
	 * Convert bytes to an double. Works with 4,2 or 1 byte.
	 * 
	 * @param bytes
	 *            The bytes to convert
	 * @return The double created from the bytes.
	 */
	public static double convertToDouble(byte[] bytes) {
		long i = convertToLong(bytes);
		return Double.longBitsToDouble(i);
	}

	/**
	 * Convert a float to its byte components.
	 * 
	 * @param f
	 *            The float
	 * @return The equivalent bytes
	 */
	public static byte[] convertToBinary(float f) {
		int i = Float.floatToIntBits(f);
		return convertToBinary(i);
	}

	/**
	 * Convert bytes to an float. Works with 4,2 or 1 byte.
	 * 
	 * @param bytes
	 *            The bytes to convert
	 * @return The float created from the bytes.
	 */
	public static float convertToFloat(byte[] bytes) {
		int i = convertToInt(bytes);
		return Float.intBitsToFloat(i);
	}

	/**
	 * Convert a BigInteger to its byte components.
	 * 
	 * @param i
	 *            The BigInteger
	 * @return The equivalent bytes
	 */
	public static byte[] convertToBinary(BigInteger i) {
		return i.toByteArray();
	}

	/**
	 * Convert bytes to an BigInteger. Works with 4,2 or 1 byte.
	 * 
	 * @param bytes
	 *            The bytes to convert
	 * @return The BigInteger created from the bytes.
	 */
	public static BigInteger convertToBigInteger(byte[] bytes) {
		return new BigInteger(bytes);
	}

	/**
	 * Convert a BigDecimal to its byte components.
	 * 
	 * @param i
	 *            The BigDecimal
	 * @return The equivalent bytes
	 */
	public static byte[] convertToBinary(BigDecimal i) {
		byte[] retVal = null;

		try {
			retVal = i.toString().getBytes("UTF-8");
		} catch (Exception exp) {
			retVal = new byte[0];
		}

		return retVal;
	}

	/**
	 * Convert bytes to an BigDecimal. Works with 4,2 or 1 byte.
	 * 
	 * @param bytes
	 *            The bytes to convert
	 * @return The BigDecimal created from the bytes.
	 */
	public static BigDecimal convertToBigDecimal(byte[] bytes) {
		BigDecimal retVal = null;

		try {
			retVal = new BigDecimal(new String(bytes, "UTF-8"));
		} catch (Exception exp) {
			retVal = null;
		}

		return retVal;
	}

	/**
	 * Find the closest prime above a value, up to 11000.
	 * 
	 * @param aValue
	 *            The int to check
	 * @return The closes prime
	 */
	public static int ceilPrime(int aValue) {
		int retVal;

		retVal = aValue;

		if (aValue <= 5)
			retVal = 5;
		else if (aValue <= 7)
			retVal = 7;
		else if (aValue <= 13)
			retVal = 13;
		else if (aValue <= 17)
			retVal = 17;
		else if (aValue <= 51)
			retVal = 51;
		else if (aValue <= 71)
			retVal = 71;
		else if (aValue <= 125)
			retVal = 97;
		else if (aValue <= 275)
			retVal = 241;
		else if (aValue <= 425)
			retVal = 397;
		else if (aValue <= 525)
			retVal = 499;
		else if (aValue <= 800)
			retVal = 743;
		else if (aValue <= 1100)
			retVal = 997;
		else if (aValue <= 1750)
			retVal = 1499;
		else if (aValue <= 2250)
			retVal = 1999;
		else if (aValue <= 4250)
			retVal = 3989;
		else if (aValue <= 5500)
			retVal = 4999;
		else if (aValue <= 8000)
			retVal = 7499;
		else if (aValue <= 11000)
			retVal = 9973;
		else if ((aValue % 2) == 0)
			retVal += 1;

		return retVal;
	}

	/**
	 * Calculates the sum of 1->n.
	 * 
	 * @param n
	 * @return
	 */
	public static int calculateSum(int n) {
		int retVal = 0;

		if (n % 2 == 1) {
			retVal = ((n - 1) / 2) * n;
			retVal += n;
		} else {
			retVal = (n / 2) * n;
			retVal += n / 2;
		}

		return retVal;
	}

	/**
	 * Encode a number on an interval to a set of bits representing the binary
	 * value for the step closest to the number. The number of steps must be >0
	 * and <1024.
	 * 
	 * @param number
	 * @param min
	 * @param max
	 * @param steps
	 * @return
	 */
	public static byte[] encodeToInterval(int number, int min, int max,
			int steps) {
		if (steps <= 0)
			throw new IllegalArgumentException("Steps must be > 0");
		if (steps > 128)
			throw new IllegalArgumentException("Steps must be < 128");

		int bits = 0;
		int temp = steps;

		while (temp > 0) {
			bits++;
			temp /= 2;
		}

		byte[] retVal = new byte[bits];
		int scaled = number - min;
		double stepSize = (double) ((double) (max - min)) / (double) steps;
		int numSteps = (int) Math.round(scaled / stepSize);
		byte[] binary = convertToBinary(numSteps);
		int curByte;

		for (int i = 0; i < bits; i++) {
			curByte = i / 8;
			retVal[i] = (byte) (binary[curByte] & 0x01);
			binary[curByte] = (byte) (((int) binary[curByte]) >>> 1);
		}

		return retVal;
	}

	public static int decodeFromInterval(byte encoded[], int min, int max,
			int steps) {
		if (steps <= 0)
			throw new IllegalArgumentException("Steps must be > 0");
		if (steps > 128)
			throw new IllegalArgumentException("Steps must be < 128");

		int bits = encoded.length;
		double stepSize = (double) ((double) (max - min)) / (double) steps;
		int retVal = 0;
		int numSteps = 0;
		int twoCount = 1;

		for (int i = 0; i < bits; i++) {
			if (encoded[i] > 0)
				numSteps += twoCount;
			twoCount *= 2;
		}

		retVal = ((int) (numSteps * stepSize)) + min;

		return retVal;
	}

	public static int mapToInterval(int number, int min, int max, int steps) {
		int scaled = number - min;
		double stepSize = (double) ((double) (max - min)) / (double) steps;
		int closest = (int) Math.round(scaled / stepSize);
		return ((int) (closest * stepSize)) + min;
	}

	public static byte[] encodeToInterval(double number, double min,
			double max, int steps) {
		if (steps <= 0)
			throw new IllegalArgumentException("Steps must be > 0");
		if (steps > 1024)
			throw new IllegalArgumentException("Steps must be < 1024");

		int bits = 0;
		int temp = steps;

		while (temp > 0) {
			bits++;
			temp /= 2;
		}

		byte[] retVal = new byte[bits];
		double stepSize = (max - min) / (double) steps;
		double scaled = number - min - (stepSize / 2);
		int numSteps = (int) Math.round(scaled / stepSize);
		byte[] binary = convertToBinary(numSteps);
		int curByte;

		for (int i = 0; i < bits; i++) {
			curByte = i / 8;
			retVal[i] = (byte) (binary[curByte] & 0x01);
			binary[curByte] = (byte) (((int) binary[curByte]) >>> 1);
		}

		return retVal;
	}

	public static double decodeFromInterval(byte encoded[], double min,
			double max, int steps) {
		if (steps <= 0)
			throw new IllegalArgumentException("Steps must be > 0");
		if (steps > 1024)
			throw new IllegalArgumentException("Steps must be < 1024");

		int bits = encoded.length;
		double stepSize = (max - min) / (double) steps;
		double retVal = 0;
		int numSteps = 0;
		int twoCount = 1;

		for (int i = 0; i < bits; i++) {
			if (encoded[i] > 0)
				numSteps += twoCount;
			twoCount *= 2;
		}

		retVal = (numSteps * stepSize) + min + (stepSize / 2);

		return retVal;
	}

	public static double mapToInterval(double number, double min, double max,
			int steps) {
		double stepSize = (max - min) / (double) steps;
		double scaled = number - min - (stepSize / 2);
		int closest = (int) Math.round(scaled / stepSize);
		return (closest * stepSize) + min + (stepSize / 2);
	}
}
