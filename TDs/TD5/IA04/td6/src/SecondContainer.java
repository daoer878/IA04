
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class SecondContainer {

	public static String SECOND_PROPERTIES_FILE = "containerSecondaire";
	
	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		
		try {
			p = new ProfileImpl(SECOND_PROPERTIES_FILE);
			ContainerController cc = rt.createAgentContainer(p);
			
			AgentController ac = cc.createNewAgent("KB", "AgentKB", null);
			ac.start();

			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
