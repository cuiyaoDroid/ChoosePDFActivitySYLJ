package com.xianzhi.tool.db;

public class sendMailHolder {
	private int id;
	private String userName;
	private String subject;
	private String content;
	private String emailTo;
	private String cc;
	private String bcc;
	private String sentDate;
	private String achment;
	
	public sendMailHolder(int id, String userName, String subject,
			String content, String emailTo, String cc, String bcc,
			String sentDate, String achment) {
		this.id = id;
		this.userName = userName;
		this.subject = subject;
		this.content = content;
		this.emailTo = emailTo;
		this.cc = cc;
		this.bcc = bcc;
		this.sentDate = sentDate;
		this.achment = achment;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getSentDate() {
		return sentDate;
	}
	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}
	public String getAchment() {
		return achment;
	}
	public void setAchment(String achment) {
		this.achment = achment;
	}
	
}