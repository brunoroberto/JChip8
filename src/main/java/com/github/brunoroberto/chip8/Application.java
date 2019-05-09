package com.github.brunoroberto.chip8;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

import com.github.brunoroberto.chip8.excep.FileExtensionException;

public class Application {

	private static boolean trace = Boolean.parseBoolean(System.getProperty("trace"));

	private static void printHelp() {
		System.out.println("JChip8 Interpreter - by brunoroberto\n");
		System.out.println("How to run:");
		System.out.println("java -jar JChip8.jar filename.ch8");
	}

	private static void validateArgs(String... args) throws NullPointerException, InvalidParameterException {
		if (args == null)
			throw new NullPointerException("arguments cannot be null");
		if (args.length != 1)
			throw new InvalidParameterException("wrong number of arguments");
	}

	private static void validateFile(String fileName) throws FileNotFoundException, FileExtensionException {
		File romFile = new File(fileName);
		if (!romFile.exists())
			throw new FileNotFoundException(String.format("%s not found", fileName));
		if (!romFile.getAbsolutePath().endsWith(".ch8"))
			throw new FileExtensionException("file must have .ch8 extension");
	}

	public static void start(String... args) {
		try {
			validateArgs(args);
			validateFile(args[0]);
		} catch (NullPointerException | InvalidParameterException | FileNotFoundException | FileExtensionException e) {
			if (trace)
				System.err.printf("Starting failed - error: %s\n", e);
			printHelp();
			return;
		}
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
			Chip8 chip8 = new Chip8(bytes);
			chip8.execute();
		} catch (IOException e) {
			if (trace)
				System.err.printf("Could not read bytes of the file - error: %s\n", e);
		} catch (InterruptedException e) {
			if (trace)
				System.err.printf("Could not execute the rom - error: %s\n", e);
		}
	}

	public static void main(String[] args) {
		Application.start(args);
	}

}
