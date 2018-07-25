package com.telino.iparapheur.domain;

public enum WorkflowStatus {
	
	REFUS("refus", 9, "R"), 
	ACCORD("accord", 11, "O"), 
	INIT("init", 0, "I");

	private String value;
	
	private int statusCode;
	
	private String civilCode;

	private WorkflowStatus(String value, int statusCode, String civilCode) {
		this.value = value;
		this.statusCode = statusCode;
		this.civilCode = civilCode;
	}

	@Override
	public String toString() {
		return value;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getCivilCode() {
		return civilCode;
	}
	
}
