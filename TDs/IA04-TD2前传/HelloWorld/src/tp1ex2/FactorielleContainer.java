package tp1ex2;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class FactorielleContainer {
	public static String SECOND_PROPERTIES_FILE = "factorielleConfig";
	
	
	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		
		try {
			p = new ProfileImpl(SECOND_PROPERTIES_FILE);
			ContainerController cc = rt.createAgentContainer(p);
			AgentController ac = cc.createNewAgent("Fact", "tp1ex2.AgentFact", null);
			AgentController ac2 = cc.createNewAgent("Mult", "tp1ex2.AgentMult", null);
			ac.start();
			ac2.start();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
