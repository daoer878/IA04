package td4;


import java.util.Vector;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class SudokuSolverContainer {
	public static String SECOND_PROPERTIES_FILE = "factorielleConfig";
	
	private static Vector<AgentController> agentAnalyse;
	
	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		
		try {
			p = new ProfileImpl(SECOND_PROPERTIES_FILE);
			ContainerController cc = rt.createAgentContainer(p);
			AgentController ac = cc.createNewAgent("Simulation", "td4.AgentSimulation", null);
			AgentController ac2 = cc.createNewAgent("Environnement", "td4.AgentEnvironnement", null);
			
			ac.start();
			ac2.start();
			
			agentAnalyse = new Vector<AgentController>();
			
			for(int i = 0; i < 27; i++)
			{
				agentAnalyse.add(cc.createNewAgent("Analyse" + String.valueOf(i), "td4.AgentAnalyse", null));
			}
			
			for(int i = 0; i < agentAnalyse.size(); i++)
			{
				agentAnalyse.get(i).start();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
