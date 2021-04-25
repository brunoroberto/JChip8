package com.github.brunoroberto.chip8;

public class InvalidHexOperation extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidHexOperation(String msg) {
		super(msg);
	}

}
