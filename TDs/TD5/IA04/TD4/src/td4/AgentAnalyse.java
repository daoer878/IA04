package td4;

import java.io.StringWriter;
import java.util.Collections;

import SudokuModel.AnalyseRequestData;
import SudokuModel.Cell;

import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentAnalyse extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6002701119557299981L;

	protected void setup(){
		SequentialBehaviour comportementSequentiel = new SequentialBehaviour();
		
		comportementSequentiel.addSubBehaviour(new OneShotBehaviour() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -2223973858446187528L;

			@Override
			public void action() {
				ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
				message.addReceiver(new AID("Simulation", AID.ISLOCALNAME));
				myAgent.send(message);
			}
		});
		
		comportementSequentiel.addSubBehaviour(new ListenMessageBehaviour());
		
		addBehaviour(comportementSequentiel);
	}
	
	
	class ListenMessageBehaviour extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8760607043402506575L;

		@Override
		public void action() {
			MessageTemplate filtre = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = myAgent.receive(filtre);
			
			if(message != null)
			{
				ObjectMapper mapper = new ObjectMapper();
				
				try {
					AnalyseRequestData data = mapper.readValue(message.getContent(), AnalyseRequestData.class);
					addBehaviour(new ProceedAnalyseBehaviour(data, message));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
				block();
		}
	}
	
	class ProceedAnalyseBehaviour extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5714667021493745280L;
		AnalyseRequestData datas;
		ACLMessage sender;
		boolean Done;
		
		public ProceedAnalyseBehaviour(AnalyseRequestData datas, ACLMessage sender) {
			this.datas = new AnalyseRequestData(datas.getValues(), datas.getZone());
			this.sender = sender;
			Done = false;
		}

		@Override
		public void action() {

			//get data
			
			Cell [] answerData=new Cell[9];
			answerData = Trier(datas.getValues());
			//answerData = datas.getValues();
			//answerData = Trier(answerData);
			

			
			AnalyseRequestData answer = new AnalyseRequestData(answerData, datas.getZone());
			StringWriter sw = new StringWriter();
			ObjectMapper mapper = new ObjectMapper();
			
			try {
				mapper.writeValue(sw, answer);
				
				ACLMessage reply = sender.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(sw.toString());
				myAgent.send(reply);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			Done = true;
		}

		

		private Cell[] Trier(Cell[] values) {
				if (values == null)
					return null;
				Cell [] result = new Cell[9];
				result =values;
				for (int i=0;i<9;i++){	
					Collections.sort(result[i].getPossibles());
				}
			
				for (int i=0;i<9;i++){				
					/** mis le valeur, si une seul possibilite **/
					if (result[i].getPossibles().size()==1)
					{
						//result[i].setValue(Integer.parseInt(result[i].getPossibles().remove(0)));
						result[i].setValue(Integer.parseInt(result[i].getPossibles().get(0)));
						result[i].getPossibles().clear();
					}
					/** retirer des possible de tous les cellule, si une cellule est determine. **/
					if (result[i].getPossibles().size()==0){
						for (int k=0;k<9;k++){
							result[k].getPossibles().remove(Integer.toString(result[i].getValue()));
							Collections.sort(result[k].getPossibles());
						}				
					}						
				}
				
				/**retire 2 possibilite des auters cellules, si 2 celulle ont 2 possibilites, et ce sont les memes.**/
				for (int i=0;i<9;i++){	
					
					if (result[i].getPossibles().size()==2)
					{					
						for (int k=i+1;k<9;k++){
							if (result[k].getPossibles().equals(result[i].getPossibles())){
								for (int q=0;q<9;q++){
									if (q !=i && q !=k){
										result[q].getPossibles().remove(result[i].getPossibles().get(0));
										result[q].getPossibles().remove(result[i].getPossibles().get(1));
									}
									
								}
							}			
						}				
					}
					
				}
				
			
				

			
				return result;
			}


		@Override
		public boolean done() {
			return Done;
		}
		
	}
}
