package com.xianzhi.tool.db;

public class mailContentHolder {
	private int id;
	private String userName;
	private int uid;
	private String subject;
	private String content;
	private String emailTo;
	private String cc;
	private String bcc;
	private int readFlag;
	private String fromUser;
	private String sentDate;
	private int hasAttachment;
	private int achmentcount;
	public mailContentHolder(){
		
	}
	public mailContentHolder(int id, String userName, int uid,
			String subject, String content, String emailTo, String cc,
			String bcc, int readFlag, String fromUser, String sentDate,
			int hasAttachment, int achmentcount) {
		this.id = id;
		this.userName = userName;
		this.uid = uid;
		this.subject = subject;
		this.content = content;
		this.emailTo = emailTo;
		this.cc = cc;
		this.bcc = bcc;
		this.readFlag = readFlag;
		this.fromUser = fromUser;
		this.sentDate = sentDate;
		this.hasAttachment = hasAttachment;
		this.achmentcount = achmentcount;
	}
	@Override
	public String toString() {
		return "mailContentHolder [id=" + id + ", userName=" + userName
				+ ", uid=" + uid + ", subject=" + subject + ", content="
				+ content + ", emailTo=" + emailTo + ", cc=" + cc + ", bcc="
				+ bcc + ", readFlag=" + readFlag + ", fromUser=" + fromUser
				+ ", sentDate=" + sentDate + ", hasAttachment=" + hasAttachment
				+ ", achmentcount=" + achmentcount + "]";
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	public int getReadFlag() {
		return readFlag;
	}
	public void setReadFlag(int readFlag) {
		this.readFlag = readFlag;
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getSentDate() {
		return sentDate;
	}
	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getAchmentcount() {
		return achmentcount;
	}
	public void setAchmentcount(int achmentcount) {
		this.achmentcount = achmentcount;
	}
	public int getHasAttachment() {
		return hasAttachment;
	}
	public void setHasAttachment(int hasAttachment) {
		this.hasAttachment = hasAttachment;
	}
	
}
