package model;

public class KBreplyData {
	private int requestType;
	private String answer;
	
	public KBreplyData() {
		this.answer = new String();
	}
	
	public KBreplyData(String answer) {
		this.requestType = -1;
		this.answer = answer;
	}

	public KBreplyData(int type, String answer ) {
		this.requestType= type;
		this.answer = answer;
	}

	public String getArgs() {
		return answer;
	}
	
	public void setArgs(String answer) {
		this.answer = answer;
	}
	
	public int getRequestType() {
		return requestType;
	}
	
	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}
	
}
