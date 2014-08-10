package SudokuModel;

public class AnalyseRequestData {
	private Cell[] values;
	private int zone;
	
	public AnalyseRequestData() {
		values = new Cell[9];
		zone = -1;
	}
	

	public AnalyseRequestData(Cell[] answerData, int zone2) {
		if(answerData.length > 9)
			values = new Cell[answerData.length];		
		else 
			values = new Cell[9];
		
		values = answerData;
		this.zone = zone2;
	}

	public void setValue(int index, Cell value) {
		values[index] = value;
	}
	
	public Cell getValue(int index) {
		return values[index];
	}
	
	public void setValues(Cell[] data) {
		values = data;
	}
	
	public void setZone(int zone) {
		this.zone = zone;
	}
	
	public int getZone() {
		return zone;
	}
	
	public Cell[] getValues() {
		return values;
	}
}
