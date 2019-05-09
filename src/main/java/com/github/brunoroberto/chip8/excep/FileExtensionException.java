package com.github.brunoroberto.chip8.excep;

public class FileExtensionException extends Exception {

	private static final long serialVersionUID = 1L;

	public FileExtensionException() {
		super();
	}

	public FileExtensionException(String msgError) {
		super(msgError);
	}

}
