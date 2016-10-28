package edu.psu.os.KV511.model;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String instruction;
	private long key;
	private long value;
	
	public Message(String instruction, long key, long value) {
		super();
		this.instruction = instruction;
		this.key = key;
		this.value = value;
	}
	
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(key) + ":" + String.valueOf(value);
	}
        
        public String getString() {
		return String.valueOf(key) + ":null";
	}
	
}
