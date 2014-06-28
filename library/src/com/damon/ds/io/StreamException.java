package com.damon.ds.io;

public class StreamException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3094315121118208113L;

	public StreamException(int available) {
		super("StreamException: available is less than " + available);
	}

}
