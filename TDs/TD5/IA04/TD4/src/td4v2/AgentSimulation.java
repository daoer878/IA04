package td4v2;

import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class AgentSimulation extends Agent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3469167131230014776L;
	private Vector<AID> agentsAnalyse;
	private boolean endOfSubscription;
	private boolean endOfSimulation;
	
	public boolean subscriptionEnded() {
		return endOfSubscription;
	}
	
	public void terminateSubscription() {
		endOfSubscription = true;
	}
	
	public boolean simulationEnded() {
		return endOfSimulation;
	}
	
	public void terminateSimulation() {
		endOfSimulation = true;
	}
	
	public void addAgentAnalyse(AID aid) {
		boolean exist = false;
		
		for(int i = 0; i < agentsAnalyse.size(); i++)
		{
			if(agentsAnalyse.get(i).equals(aid))
			{
				exist = true;
				break;
			}
		}
		
		if(!exist)
			agentsAnalyse.add(aid);
	}
	
	public Vector<AID> getAgentsAnalyse() {
		return agentsAnalyse;
	}
	
	protected void setup(){
		agentsAnalyse = new Vector<AID>();
		endOfSubscription = false;
		endOfSimulation = false;
		
		SequentialBehaviour comportementSequentiel = new SequentialBehaviour();
		ParallelBehaviour comportementParalleleSubscription = new ParallelBehaviour(ParallelBehaviour.WHEN_ANY);
		comportementParalleleSubscription.addSubBehaviour(new SubscriptionBehaviour());
		comportementParalleleSubscription.addSubBehaviour(new SubscriptionTimeOut(this, 20000));
		
		comportementSequentiel.addSubBehaviour(comportementParalleleSubscription);
		
		ParallelBehaviour comportementParalleleSimulation = new ParallelBehaviour(ParallelBehaviour.WHEN_ANY);
		comportementParalleleSimulation.addSubBehaviour(new EndOfSimulationWaiter());
		comportementParalleleSimulation.addSubBehaviour(new SimulationBehaviour(this, 1000));
		
		comportementSequentiel.addSubBehaviour(comportementParalleleSimulation);
		
		addBehaviour(comportementSequentiel);
	}
	
	// Behaviours etat subscriptions
	
	public class SubscriptionBehaviour extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8521540236045162162L;

		@Override
		public void action() {
			MessageTemplate filtre = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
			ACLMessage message = myAgent.receive(filtre);
			
			if(message != null) {
				((AgentSimulation)myAgent).addAgentAnalyse(message.getSender());
				System.out.println("Un agent vient de s'inscrire " + message.getSender().toString());
			}
			else {
				block();
			}
		}

		@Override
		public boolean done() {
			return ((AgentSimulation)myAgent).subscriptionEnded();
		}
	}
	
	public class SubscriptionTimeOut extends WakerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6396623962535985282L;

		public SubscriptionTimeOut(Agent a, long timeout) {
			super(a, timeout);
			
		}
		
		protected void handleElapsedTimeout() {
			((AgentSimulation)myAgent).terminateSubscription();
		}
	}
	
	// Behaviours etat simulation.
	public class EndOfSimulationWaiter extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7741216023624025189L;
		private boolean Done;
		
		EndOfSimulationWaiter() {
			super();
			Done = false;
		}
		
		@Override
		public void action() {
			MessageTemplate filtre = MessageTemplate.MatchPerformative(ACLMessage.CANCEL);
			ACLMessage message = myAgent.receive(filtre);
			
			if(message != null) {
				if(message.getContent().equals("Stop"))
					Done = true;
			}
			else {
				block();
			}
		}

		@Override
		public boolean done() {
			if(Done) {
				((AgentSimulation)myAgent).terminateSimulation();
			}
			
			return Done;
		}
		
	}
	
	public class SimulationBehaviour extends TickerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2718142018729990873L;

		public SimulationBehaviour(Agent a, long period) {
			super(a, period);
			
		}
		
		protected void onTick() {
			if(!((AgentSimulation)myAgent).simulationEnded())
			{
				System.out.println("Envoi de message aux agents d'analyse ...");
				for(int i = 0; i < ((AgentSimulation)myAgent).getAgentsAnalyse().size(); i++)
				{
					//SimulationMessageData datas = new SimulationMessageData(((AgentSimulation)myAgent).getAgentsAnalyse().get(i));
					//StringWriter sw = new StringWriter();
					//ObjectMapper mapper = new ObjectMapper();
					
					try {
						//mapper.writeValue(sw, datas);
						
						ACLMessage message = new ACLMessage(ACLMessage.PROPAGATE);
						message.addReceiver(new AID("Environnement", AID.ISLOCALNAME));
						
						//************** ReplyTo  *******
						message.addReplyTo(((AgentSimulation)myAgent).getAgentsAnalyse().get(i));
						//message.setContent(sw.toString());
						myAgent.send(message);
					} 
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			else
				this.stop();
	    } 
	}
}
