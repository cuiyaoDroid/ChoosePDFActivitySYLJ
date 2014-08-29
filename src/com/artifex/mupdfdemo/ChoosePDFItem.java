package com.artifex.mupdfdemo;

public class ChoosePDFItem {
	enum Type {
		PARENT, DIR, DOC
	}

	final public Type type;
	final public String name;
	final public String time;
	public ChoosePDFItem (Type t, String n ,String time) {
		type = t;
		name = n;
		this.time = time;
	}
}
