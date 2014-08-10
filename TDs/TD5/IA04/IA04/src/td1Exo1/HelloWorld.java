package td1Exo1;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;


public class HelloWorld extends Agent {


	protected void setup() {
		this.addBehaviour(new HelloBehaviour());
		System.out.println(getLocalName() + " : HelloWorld");
	}
	
	public class HelloBehaviour extends Behaviour {
		
		@Override
		public void action() {
			ACLMessage message = myAgent.receive();
			
			if(message != null && message.getPerformative() == ACLMessage.INFORM)		
				System.out.println("Contact : " + message.toString());
		}

		@Override
		public boolean done() {
			
			return false;
		}

	}
}
