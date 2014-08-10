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
			//AgentController ac = cc.createNewAgent("Fact", "td2.AgentFact", null);
			//ac.start();
			//AgentController ac2 = cc.createNewAgent("Mult", "td2.AgentMult", null);
			//ac2.start();
			
			
			AgentController ac = cc.createNewAgent("Fact", "td2.AgentFactEx2", null);
			ac.start();
			AgentController ac3 = cc.createNewAgent("M1", "td2.AgentMult", null);
			AgentController ac4 = cc.createNewAgent("M2", "td2.AgentMult", null);
			ac3.start();
			ac4.start();
			
			

			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
