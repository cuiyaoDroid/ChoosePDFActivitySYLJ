package com.xianzhi.tool.db;

public class ContactListStaticHolder {
	private int id;
	private String name;
	private String address;
	private int flag;
	private String childids;
	private int fatherid;
	public ContactListStaticHolder(int id, String name, String address,
			int flag, String childids,int fatherid) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.flag = flag;
		this.childids = childids;
		this.fatherid = fatherid;
	}

	public int getId() {
		return id;
	}

	public int getFatherid() {
		return fatherid;
	}

	public void setFatherid(int fatherid) {
		this.fatherid = fatherid;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getChildids() {
		return childids;
	}

	public void setChildids(String childids) {
		this.childids = childids;
	}

}
