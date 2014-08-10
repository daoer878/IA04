package td1Exo1;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;


public class HelloMain {

	public static String SECOND_PROPERTIES_FILE = "containerSecondaire";
	
	
	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		
		try {
			p = new ProfileImpl(SECOND_PROPERTIES_FILE);
			ContainerController cc = rt.createAgentContainer(p);
			AgentController ac = cc.createNewAgent("Hello", "tp1.HelloWorld", null);
			ac.start();
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
