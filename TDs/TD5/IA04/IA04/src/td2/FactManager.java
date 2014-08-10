package td2;


public class FactManager {
	protected int ID;
	protected int nextOperation;
	protected boolean processed;
	static int staticID = 1;
	
	
	FactManager(int valueToProcess) {
		ID = staticID++;
		nextOperation = valueToProcess - 1;
		processed = false;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getNextOperation() {
		if(nextOperation <= 1)
		{
			processed = true;
		}
		
		return nextOperation--;
	}
	
	public boolean isProcessed() {
		return processed;
	}
}

