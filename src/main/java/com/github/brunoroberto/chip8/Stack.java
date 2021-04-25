package com.github.brunoroberto.chip8;

import java.util.Arrays;

/**
 * Stack representation of the CHIP-8
 *
 * @author brunoroberto
 */
public class Stack {

    private static final short SIZE = 16;

    private short[] data;
    private byte stackPointer;

    public Stack() {
        this.data = new short[SIZE];
        this.stackPointer = 0;
    }

    private boolean isFull() {
        return this.stackPointer == this.data.length;
    }

    private boolean isEmpty() {
        return this.stackPointer == 0x0;
    }

    public void put(short data) {
        if (isFull()) {
            throw new StackOverflowError("the stack is full");
        }
        this.data[this.stackPointer++] = data;
    }

    public short pop() {
        if (isEmpty()) {
            throw new StackOverflowError("the stack is empty");
        }
        return this.data[--this.stackPointer];
    }

    public void dump() {
        System.out.println(String.format("Stack: SP %d - {%s}", stackPointer, Arrays.toString(data)));
    }
}
