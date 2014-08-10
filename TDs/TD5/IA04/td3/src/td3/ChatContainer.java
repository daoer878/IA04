package td3;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class ChatContainer {
	public static String SECOND_PROPERTIES_FILE = "factorielleConfig";
	
	
	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		
		try {
			p = new ProfileImpl(SECOND_PROPERTIES_FILE);
			ContainerController cc = rt.createAgentContainer(p);
			AgentController ac = cc.createNewAgent("Chat1", "td3.AgentChat", null);
			AgentController ac2 = cc.createNewAgent("Chat2", "td3.AgentChat", null);
			AgentController ac3 = cc.createNewAgent("Chat3", "td3.AgentChat", null);
			ac.start();
			ac2.start();
			ac3.start();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
