package td3;

import java.beans.PropertyChangeSupport;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

public class AgentChat extends GuiAgent{
	public static int TEXT_EVENT = 0;
	PropertyChangeSupport changes = new PropertyChangeSupport(this);
	protected void setup(){
		
	super.setup();
	System.out.println(getLocalName ()+ "--> Istalled");
	ChatAgentFrame f=new ChatAgentFrame (this);
	changes.addPropertyChangeListener(f);
	addBehaviour (new ChatReceiverBehaviour (this));
	register();
	}
	
	public void register(){
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Chat");
		sd.setName("Client");
		dfd.addServices(sd);
		try {
		DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	public void sendEvent (String info){
		
		changes.firePropertyChange("line", null, info);
	}
	
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		if(arg0.getType() == TEXT_EVENT) {
			String line = arg0.getParameter(0).toString();
			
			//...
		}
	}
	
	static public void main(String [] args){
		new ChatAgentFrame();
		
	}

	public class ChatReceiverBehaviour extends CyclicBehaviour{
		
		public ChatReceiverBehaviour(AgentChat agentChat) {
			
		}

		@Override
		public void action (){
				jade.lang.acl.ACLMessage message =myAgent.receive ();
				if (message !=null){
					String line =message.getContent();
					((AgentChat)myAgent).sendEvent(line);
				}
		}
	}
}
