package SudokuModelv2;

import java.util.ArrayList;

public class Cell {
	private int value;
	private ArrayList<String> possibles;
	
	public Cell() {
		value = 0;
		possibles = new ArrayList<String>();
		
		for(int i = 1; i <= 9; i++)
		{
			possibles.add(String.valueOf(i));
		}
	}
	
	public Cell(int value) {
		possibles = new ArrayList<String>();
		
		if(value != 0) {
			this.value = value;
			possibles.clear();
		}
		else {
			value = 0;
			possibles.clear();
			for(int i = 1; i <= 9; i++)
			{
				possibles.add(String.valueOf(i));
			}
		}
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public ArrayList<String> getPossibles() {
		return possibles;
	}
	
	public void setPossibles(ArrayList<String> possibles) {
		this.possibles = possibles;
	}
}
