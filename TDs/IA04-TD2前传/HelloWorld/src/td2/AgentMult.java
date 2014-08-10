package td2;


import java.io.StringWriter;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

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
				try {
					ObjectMapper mapper = new ObjectMapper();
					
					String str = message.getContent();
					
					Map<String, Object> map = mapper.readValue(str, Map.class);
					
					if(map.containsKey("args"))
					{
						String args = (String) map.get("args");
						
						String [] res = args.split(",");
						
						ACLMessage reply = message.createReply();
						
						if(res.length != 2) {
							reply.setPerformative(ACLMessage.FAILURE);
							reply.setContent("Incorrect request");
						}
						else
						{
							long a = Long.parseLong(res[0]);
							long b = Long.parseLong(res[1]);
							
							a *= b;
							
							reply.setPerformative(ACLMessage.INFORM);
							
							map.clear();
							map.put("action", "INFORMmult");
							map.put("value", String.valueOf(a));
							
							StringWriter sw = new StringWriter();
							mapper.writeValue(sw, map);
							
							reply.setContent(sw.toString());
						}
							
						myAgent.send(reply);
					}
				}
				catch (Exception ex)
				{
					
				}
			}
		}

		@Override
		public boolean done() {

			return false;
		}
		
	}

}
