package Model;

public class multData {
	private long arg1;
	private long arg2;
	
	public multData() {}
	
	public multData(long arg1, long arg2) {
		setData(arg1, arg2);
	}
	
	public long getArg1() {
		return arg1;
	}
	
	public long getArg2() {
		return arg2;
	}
	
	public void setData(long arg1, long arg2) {
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
}
