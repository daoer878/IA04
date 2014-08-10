package td2;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

import Model.factData;
import Model.multData;
import Model.multReplyData;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;




@SuppressWarnings("serial")
public class AgentFact extends Agent {
	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Operations");
		sd.setName("Factorielle");
		dfd.addServices(sd);
		try {
		DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
		fe.printStackTrace();
		}
		
		this.addBehaviour(new ReceiveRequestFactBehaviour());	
		
	}
	
	public AID getReceiver() {
		AID rec = null;
		DFAgentDescription template =
		new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Operations");
		sd.setName("Multiplication");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			
			if (result.length > 0) {
				System.out.println("Nombre de resultat : " + String.valueOf(result.length));
				int i = (int)(Math.random() * result.length);
				System.out.println("Valeur de i : " + String.valueOf(i));
				rec = result[i].getName();
			}
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		return rec;
	}
	
	public class ReceiveRequestFactBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate filtre = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = myAgent.receive(filtre);
			
			if(message != null)
			{
				String str = message.getContent();
				ObjectMapper mapper = new ObjectMapper();
				
				try {
					factData Data = mapper.readValue(str, factData.class);
					addBehaviour(new FactBehaviour(Data.getData(), message));
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
				block();
		}
		
	}
	
	
	public class FactBehaviour extends Behaviour {
		private long number;
		private boolean Done;
		private int machineSequentielle;
		private long result;
		private ACLMessage expediteur;
		private UUID id;
		private long beginTime;
		
		public FactBehaviour(long number, ACLMessage expediteur) {
			super();
			this.number = number;
			Done = false;
			machineSequentielle = 0;
			result = 1;
			this.expediteur = expediteur;
			this.id = UUID.randomUUID();
			
			beginTime = System.currentTimeMillis();
		}
		
		@Override
		public void action() {
			ObjectMapper mapper = new ObjectMapper();

			try
			{
				switch(machineSequentielle) {
					case 0:
						if(number < 0) {
							Done = true;
							System.out.println("Factorielle erronee");
						}
						else if(number == 1 || number == 0) {
							machineSequentielle = 2;
						}
						else {
							
							multData mult = new multData(result, number--);
							ACLMessage query = new ACLMessage(ACLMessage.REQUEST);
							StringWriter sw = new StringWriter();
							mapper.writeValue(sw, mult);									
							query.setContent(sw.toString());
							query.setConversationId(this.id.toString());
							//query.addReceiver(new AID("Mult", AID.ISLOCALNAME));
							AID agentMult = ((AgentFact) myAgent).getReceiver();
							query.addReceiver(agentMult);
							
							myAgent.send(query);
							machineSequentielle=1;
						}
						break;
					case 1:
						System.out.println("En attente du rŽsultat de multiplication");
						MessageTemplate filtre = MessageTemplate.and(
								MessageTemplate.MatchConversationId(this.id.toString()),
								MessageTemplate.MatchPerformative(ACLMessage.INFORM));
						
						ACLMessage message = myAgent.receive(filtre);
						
						if(message != null)
						{
							multReplyData data = mapper.readValue(message.getContent(), multReplyData.class);
							System.out.println("RŽsultat reu : " + String.valueOf(data.getResult()));
							result = data.getResult();
							
							machineSequentielle=0;
						}
						else
						{
							block();
						}
						break;
						
					case 2:
						ACLMessage reply = expediteur.createReply();
						factData answer = new factData(result);
						StringWriter sw = new StringWriter();
						mapper.writeValue(sw, answer);									
						reply.setContent(sw.toString());
						myAgent.send(reply);
						System.out.println("Resultat du calcul de la factorielle : " + String.valueOf(answer.getData()));
						Done = true;
						break;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public boolean done() {
			if(Done) {
				long endTime = System.currentTimeMillis();
				long diff = endTime - beginTime;
				System.out.println("Temps de calcul : " + String.valueOf(diff) + " ms.");
			}
			
			return Done;
		}
	}
}
