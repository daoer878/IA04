package td2;

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
			AgentController ac = cc.createNewAgent("Fact", "td2.AgentFact", null);
			AgentController ac2 = cc.createNewAgent("Mult", "td2.AgentMult", null);
			AgentController ac3 = cc.createNewAgent("Mult2", "td2.AgentMult", null);
			ac.start();
			ac2.start();
			ac3.start();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
