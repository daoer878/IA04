package SudokuModelv2;

import jade.core.AID;

public class SimulationMessageData {
	private AID propagateTo;
	
	public SimulationMessageData() {
		
	}
	
	public SimulationMessageData(AID propagateTo) {
		this.propagateTo = propagateTo;
	}
	
	public AID getAID() {
		return propagateTo;
	}
}
