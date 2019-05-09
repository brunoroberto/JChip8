package com.github.brunoroberto.chip8.excep;

public class InvalidHexOperation extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidHexOperation(String msg) {
		super(msg);
	}

	public InvalidHexOperation() {
		super();
	}

}
