package com.xianzhi.tool.db;

public class AchmentDetailHolder {
	private int id;
	private String name;
	private int inboxId;
	private String path;
	private Long size;
	private int downloadflag;
	private String editname;
	public AchmentDetailHolder(int id, String name, int inboxId, String path,
			Long size ,int downloadflag,String editname) {
		this.id = id;
		this.name = name;
		this.inboxId = inboxId;
		this.path = path;
		this.size = size;
		this.downloadflag = downloadflag;
		this.editname = editname;
	}
	public String getEditname() {
		return editname;
	}
	public void setEditname(String editname) {
		this.editname = editname;
	}
	public int getDownloadflag() {
		return downloadflag;
	}
	public void setDownloadflag(int downloadflag) {
		this.downloadflag = downloadflag;
	}
	public int getId() {
		return id;
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
	public int getInboxId() {
		return inboxId;
	}
	public void setInboxId(int inboxId) {
		this.inboxId = inboxId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	
}
