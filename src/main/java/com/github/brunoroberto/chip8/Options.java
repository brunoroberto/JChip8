package com.github.brunoroberto.chip8;

public class Options {

	private static final int CPU_FREQUENCY = 1000 / 60; // 60Hz
	private static final short CYCLE_PER_TICK = 10;

	private static final short DEFAULT_PC_START = 0x200; // starts from 512 address

	private static final int DEFAULT_MEMORY_SIZE = 0x1000; // 4KB (4,096 bytes)
	private static final int DEFAULT_STACK_SIZE = 0x10; // 16 default size
	private static final int DEFAULT_DISPLAY_SIZE = 64 * 32;

	private short startAddress = DEFAULT_PC_START;
	private int memorySize = DEFAULT_MEMORY_SIZE;
	private int stackSize = DEFAULT_STACK_SIZE;
	private short tickCycle = CYCLE_PER_TICK;
	private int displaySize = DEFAULT_DISPLAY_SIZE;
	private int cpuFrequency = CPU_FREQUENCY;

	public Options(short startAddress, int memorySize, int stackSize, short tickCycle, int displaySize,
			int cpuFrequency) {
		super();
		this.startAddress = startAddress;
		this.memorySize = memorySize;
		this.stackSize = stackSize;
		this.tickCycle = tickCycle;
		this.displaySize = displaySize;
		this.cpuFrequency = cpuFrequency;
	}

	public Options() {
		super();
	}

	public short getStartAddress() {
		return startAddress;
	}

	public void setStartAddress(short startAddress) {
		this.startAddress = startAddress;
	}

	public int getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	public int getStackSize() {
		return stackSize;
	}

	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	public short getTickCycle() {
		return tickCycle;
	}

	public void setTickCycle(short tickCycle) {
		this.tickCycle = tickCycle;
	}

	public int getDisplaySize() {
		return displaySize;
	}

	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}

	public int getCpuFrequency() {
		return cpuFrequency;
	}

	public void setCpuFrequency(int cpuFrequency) {
		this.cpuFrequency = cpuFrequency;
	}

}
