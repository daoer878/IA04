package td1Exo2;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class AgentFact extends Agent {
	protected void setup() {
		this.addBehaviour(new FactBehaviour());	
	}
	
	public class FactBehaviour extends Behaviour {

		@Override
		public void action() {
			ACLMessage message = myAgent.receive();
			
			if(message != null && message.getPerformative() == ACLMessage.REQUEST)
			{
				String str = message.getContent();
				long a = Long.parseLong(str);
				
				ACLMessage reply = message.createReply();
				
				
				if (a >= 0 && a <= 2)
				{
					reply.setPerformative(ACLMessage.INFORM);
					
					if(a == 2)
						reply.setContent("2");
					else
						reply.setContent("1");
				}
				else if (a < 0)
				{
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("Requête eronnée");
				}
				else
				{
					long result = a;
					
					ACLMessage request = new ACLMessage();
					request.setPerformative(ACLMessage.REQUEST);
					request.addReceiver(new AID("Mult", AID.ISLOCALNAME));
					
					
					for(long i = a - 1; i >= 2; i--)
					{
						String requete = String.valueOf(result) + "x" + String.valueOf(i);
						request.setContent(requete);
						myAgent.send(request);
						ACLMessage answer = myAgent.blockingReceive();
						
						if(answer != null)
						{
							if(answer.getPerformative() == ACLMessage.FAILURE)
							{
								reply.setPerformative(ACLMessage.FAILURE);
								reply.setContent("Echec lors du traitement");
								break;
							}
							else if(answer.getPerformative() == ACLMessage.INFORM)
							{
								result = Long.parseLong(answer.getContent());
							}
						}
						else
						{
							System.out.println("Reponse nulle");
						}
					}
					
					reply.setContent(String.valueOf(result));
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
