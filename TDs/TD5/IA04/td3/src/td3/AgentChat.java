package td3;

import java.beans.PropertyChangeSupport;

import jade.core.AID;
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
	System.out.println(getLocalName ()+ "--> Installed");
	ChatAgentFrame f=new ChatAgentFrame (this,getLocalName ());
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
			
			System.out.println("Chaine reçue : " + line);
			
			DFAgentDescription[] destinataires = getReceivers();
			
			ACLMessage data = new ACLMessage(ACLMessage.INFORM);
			data.setContent(" ("+this.getAID().getLocalName()+ " ) "+line);
			
			System.out.println(String.valueOf(destinataires.length) + " destinataires trouvées.");
			System.out.println(this.getAID().getLocalName() + "my name.");
			for(int i=0; i<destinataires.length; i++)
			{	
			if (!(destinataires[i].getName().equals(this.getAID())) && !(destinataires[i].getName().getLocalName()== this.getAID().getLocalName())  ){
				AID id = destinataires[i].getName();
				data.addReceiver(id);
				System.out.println(destinataires[i].getName() + "add recever.");
				}
			}
			
			this.send(data);
		}
	}
	
	
	public DFAgentDescription[] getReceivers() {
		DFAgentDescription[] result = null;
		DFAgentDescription template =
		new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Chat");
		sd.setName("Client");
		template.addServices(sd);
		try {
			result = DFService.search(this, template);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		return result;
	}
	

	public class ChatReceiverBehaviour extends CyclicBehaviour{
		
		public ChatReceiverBehaviour(AgentChat agentChat) {
		
		}

		@Override
		public void action (){
				jade.lang.acl.ACLMessage message =myAgent.receive ();
				if (message !=null){
					String line =message.getContent();
					System.out.println("Message recu : " + line);
					((AgentChat)myAgent).sendEvent(line);
				}
				else
					block();
		}
	}
}
