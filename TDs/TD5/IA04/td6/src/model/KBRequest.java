package model;

public class KBRequest {
	private int requestType;
	private String args;
	
	public KBRequest() {
		this.args = new String();
		requestType = -1;
	}
	
	public KBRequest(int requestType, String args) {
		this.requestType= requestType;
		this.args = args;
	}
	
	public KBRequest(String args) {
		this.requestType = -1;
		this.args = args;
	}
	
	public String getArgs() {
		return args;
	}
	
	public void setArgs(String args) {
		this.args = args;
	}
	
	public int getRequestType() {
		return requestType;
	}
	
	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}
}
