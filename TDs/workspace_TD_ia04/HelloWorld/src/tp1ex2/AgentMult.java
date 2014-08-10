package tp1ex2;

import tp1.HelloWorld.HelloBehaviour;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class AgentMult extends Agent {

	protected void setup() {
		this.addBehaviour(new MultBehaviour());	
	}
	
	@SuppressWarnings("serial")
	public class MultBehaviour extends Behaviour {

		@Override
		public void action() {
			ACLMessage message = myAgent.receive();
			
			if(message != null && message.getPerformative() == ACLMessage.REQUEST) {
				String str = message.getContent();
				String [] res = str.split("[x*]");
				
				ACLMessage reply = message.createReply();
				
				if(res.length != 2) {
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("Incorrect request");
				}
				else
				{
					long a = Long.parseLong(res[0].trim());
					long b = Long.parseLong(res[1].trim());
					
					a *= b;
					
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(String.valueOf(a));
				}
					
				myAgent.send(reply);
			}
		}

		@Override
		public boolean done() {

			return false;
		}
		
	}

}
