package td2;

import java.io.StringWriter;
import java.util.Map;

import Model.multData;
import Model.multReplyData;

import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgentMult extends Agent {

	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Operations");
		sd.setName("Multiplication");
		dfd.addServices(sd);
		try {
		DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		this.addBehaviour(new ReceiveRequestMultBehaviour());
	}

	@SuppressWarnings("serial")
	public class ReceiveRequestMultBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			ACLMessage message = myAgent.receive();

			if (message != null
					&& message.getPerformative() == ACLMessage.REQUEST) {
				try {
					ObjectMapper mapper = new ObjectMapper();

					String str = message.getContent();

					multData data = mapper.readValue(str, multData.class);

					addBehaviour(new MultBehaviour(data.getArg1(),
							data.getArg2(), message));
				} catch (Exception ex) {

				}
			} else
				block();
		}
	}

	public class MultBehaviour extends Behaviour {
		private ACLMessage expediteur;
		private long value1;
		private long value2;
		private boolean Done;

		public MultBehaviour(long value1, long value2, ACLMessage expediteur) {
			super();
			this.value1 = value1;
			this.value2 = value2;
			this.expediteur = expediteur;
			Done = false;
		}

		@Override
		public void action() {
			long result = value1 * value2;


			try {
				int timeout = (int)(9500*Math.random() + 500);
				System.out.println("Temps d'attente : " + String.valueOf(timeout) + " ms");
				addBehaviour(new Waker(myAgent, timeout, result, expediteur));
				Done = true;
			} catch (Exception e) {
				e.printStackTrace();
				Done = true;
			}
		}

		@Override
		public boolean done() {
			return Done;
		}

	}
	
	public class Waker extends WakerBehaviour
	{
		private long result;
		private ACLMessage expediteur;
		
		public Waker(Agent a, long timeout, long result, ACLMessage expediteur) {
			super(a, timeout);
			this.result = result;
			this.expediteur = expediteur;
		}
		
		@Override
		public void onWake() {
			multReplyData answer = new multReplyData(result);
			ObjectMapper mapper = new ObjectMapper();
			
			try {
				ACLMessage reply = expediteur.createReply();
				StringWriter sw = new StringWriter();
				mapper.writeValue(sw, answer);
				reply.setContent(sw.toString());
				reply.setPerformative(ACLMessage.INFORM);
				myAgent.send(reply);
				
				System.out.println("Result multiply : " + this.result);
			}
			catch (Exception e) {
				
			}
		}
		
	}

}
