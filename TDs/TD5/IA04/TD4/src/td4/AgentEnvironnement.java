package td4;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import td4.AgentAnalyse.ProceedAnalyseBehaviour;
import SudokuModel.AnalyseRequestData;
import SudokuModel.SimulationMessageData;
import SudokuModel.Cell;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AgentEnvironnement extends Agent {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -7073946437981114640L;
	private Cell grille[][];
	
	protected void setup() {
		grille = new Cell[9][9];
		
		readFile();
	}
	
	public Cell [][] getGrille() {
		return grille;
	}
	
	public String RemovePossibleDeCellule (int ligne, int colonne, int index) {
		return grille[ligne][colonne].getPossibles().remove(index);
		
	}
	
	private void readFile() {
		String chaine = "";
		String fichier ="src/sudoku.txt";
		
		//lecture du fichier texte	
		try{
			InputStream ips=new FileInputStream(fichier); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			int i = 0;
			while ((ligne=br.readLine())!=null){
				System.out.println(ligne);
				chaine+=ligne+"\n";
				
				if(i<9)
				{
					for(int j = 0; j < 9; j++)
					{
						grille[i][j] = new Cell(ligne.getBytes()[j] - '0');
						System.out.print(grille[i][j].getValue());
					}
					
					i++;
					System.out.print("\n");
				}
				else 
					break;
			}
			br.close(); 
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		
		//cr閍tion ou ajout dans le fichier texte
		/*
		try {
			FileWriter fw = new FileWriter (fichier);
			BufferedWriter bw = new BufferedWriter (fw);
			PrintWriter fichierSortie = new PrintWriter (bw); 
				fichierSortie.println (chaine+"\n test de lecture et 閏riture !!"); 
			fichierSortie.close();
			System.out.println("Le fichier " + fichier + " a 閠�cr殚!"); 
		}
		catch (Exception e){
			System.out.println(e.toString());
		}	
		*/
		
		addBehaviour(new SendRequest());
		//addBehaviour (new RecieveAnalysLisener());
	}
	
	public class SendRequest extends CyclicBehaviour {

		private ArrayList<Integer> zoneToAllocate;
		
		public SendRequest() {
			zoneToAllocate = new ArrayList<Integer>();
			
			for(int i = 0; i < 27; i++)
				zoneToAllocate.add(i);
		}
		
		@Override
		public void action() {
			MessageTemplate filtre = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
			ACLMessage message = myAgent.receive(filtre);
			
			if(message != null)
			{
				ObjectMapper mapper = new ObjectMapper();
				
				try {
					//SimulationMessageData data = mapper.readValue(message.getContent(), SimulationMessageData.class);
					
					AID aid = (AID)message.getAllReplyTo().next();
					
					int zone = getZoneToProcess();
					
					if(zone != -1)
						addBehaviour(new ProcessRequest(getValuesFromZone(zone), zone, aid));
					else
						addBehaviour(new PleaseWait(message));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
				block();
		}
		
		public int getZoneToProcess() {
			int zone = -1;
			
			if(!zoneToAllocate.isEmpty())
			{
				zone = zoneToAllocate.get(0);
				zoneToAllocate.remove(0);
			}
			
			return zone;
		}
		
		public Cell [] getValuesFromZone(int zone) {
			
			Cell result[] = null;
			
			if(zone >= 0 && zone < 9) {
				result = getLine(zone);		
			}
			else if (zone >= 9 && zone < 18) {
				result = getColumn(zone);
			}
			else if (zone >= 18 && zone < 27) {
				result = getSquare(zone);
			}
			
			return result;
		}
		
		public Cell [] getLine(int id) {
			Cell result[] = new Cell[9];
			Cell grille[][] = ((AgentEnvironnement)myAgent).getGrille();
			for(int i = 0; i < 9; i++) {
				result[i] = grille[id][i];
			}
			return result;
		}
		
		public Cell [] getColumn(int id) {
			Cell result[] = new Cell[9];
			Cell grille[][] = ((AgentEnvironnement)myAgent).getGrille();
			for(int i = 0; i < 9; i++) {
				result[i] = grille[i][id - 9];
			}
			return result;
		}
		
		public Cell [] getSquare(int id) {
			Cell result[] = new Cell[9];
			Cell grille[][] = ((AgentEnvironnement)myAgent).getGrille();
			
			id -= 18;
			
			int line = id / 3;
			int column = id % 3;
			
			for(int i=0; i < 3; i++) {
				for(int j = 0; j < 3; j++) {
					result[(i*3) + j] = grille[i + (line * 3)][j + (column * 3)];
				}
			}
			
			return result;
		}
	}
	
	public class ProcessRequest extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean Done;
		private Cell values[];
		private int zone;
		private AID aid;
		private UUID id;
		
		public ProcessRequest(Cell[] cells, int zone, AID aid) {
			Done = false;
			this.values = cells;
			this.zone = zone;
			this.aid = aid;
			this.id = UUID.randomUUID();
		}
		
		@Override
		public void action() {
			AnalyseRequestData content = new AnalyseRequestData(values, zone);
			StringWriter sw = new StringWriter();
			ObjectMapper mapper = new ObjectMapper();
			
			try {
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				mapper.writeValue(sw, content);
				message.addReceiver(aid);
				message.setConversationId(id.toString());
				message.setContent(sw.toString());				
				myAgent.send(message);
				
				
				MessageTemplate filtre = MessageTemplate.and(
						MessageTemplate.MatchConversationId(this.id.toString()),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				
				ACLMessage reply = myAgent.receive(filtre);
				
				if(reply != null)
				{
					AnalyseRequestData answer = mapper.readValue(reply.getContent(), AnalyseRequestData.class);
				}
				else
					block();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			Done = true;
		}

		@Override
		public boolean done() {
			return Done;
		}
		
	}
	
	/**----------------- mis a jour la tableau des grilles-----------------------------------------------------------------------**/
	public class RecieveAnalysLisener extends CyclicBehaviour{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate filtre = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(filtre);

			if(message != null)
			{
				ObjectMapper mapper = new ObjectMapper();
				
				try {
					AnalyseRequestData data = mapper.readValue(message.getContent(), AnalyseRequestData.class);
					addBehaviour(new RenewTable(data, message));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
				block();
			
		}}

	public class RenewTable extends Behaviour {
		AnalyseRequestData datas;
		int zoon;
		Cell [] table;
		boolean Done;
		public RenewTable(AnalyseRequestData datas, ACLMessage sender) {
			this.table =new Cell[9];
			this.zoon =datas.getZone();
			this.table = datas.getValues();
			this.datas = new AnalyseRequestData(datas.getValues(), datas.getZone());
			Done = false;
		}
		@Override
		public void action() {
			Cell grille[][] = ((AgentEnvironnement)myAgent).getGrille();		
					//line
			if (zoon>=0 && zoon <=9){

				for(int i = 0; i < 9; i++) {
					if ((!table[i].equals(grille[zoon][i])) && grille[zoon][i].getPossibles().size()>0) {
						for (int k=0;k<grille[zoon][i].getPossibles().size();k++){
							String value = grille[zoon][i].getPossibles().get(k);
							if (!table[i].getPossibles().contains(value)){
								((AgentEnvironnement)myAgent).RemovePossibleDeCellule(zoon, i,k);
							}
						}
					}
				}

			}
			
			
			//column
			else if (zoon >=9 && zoon <18){
				zoon -=9;
				for(int i = 0; i < 9; i++) {
					if ((!table[i].equals(grille[i][zoon])) && grille[i][zoon].getPossibles().size()>0) {
						for (int k=0;k<grille[i][zoon].getPossibles().size();k++){
							String value = grille[i][zoon].getPossibles().get(k);
							if (!table[i].getPossibles().contains(value)){
								((AgentEnvironnement)myAgent).RemovePossibleDeCellule(i, zoon,k);
							}
						}
					}
				}
				
			}
			
			//square
			else if (zoon >=18 && zoon <27){
				zoon -= 18;
				
				int line = zoon / 3;
				int column = zoon % 3;
				
				for(int i=0; i < 3; i++) {
					for(int j = 0; j < 3; j++) {
						//result[(i*3) + j] = grille[i + (line * 3)][j + (column * 3)];
						int l = i + (line * 3);
						int c= column * 3;
						if ((!table[(i*3) + j].equals(grille[l][c])) && grille[l][c].getPossibles().size()>0) {
							for (int k=0;k<grille[l][c].getPossibles().size();k++){
								String value = grille[l][c].getPossibles().get(k);
								if (!table[(i*3) + j].getPossibles().contains(value)){
									((AgentEnvironnement)myAgent).RemovePossibleDeCellule(l, c,k);
								}
							}
						}
					}
				}
				
								
			}
			
			
			Done = true;
			
		}

		@Override
		public boolean done() {
			return Done;
		}}
	
	/**----------------- mis a jour la tableau des grilles-----------------------------------------------------------------------**/
	public class PleaseWait extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6515190844107333699L;
		private boolean Done;
		private ACLMessage sender;
		public PleaseWait(ACLMessage sender) {
			Done = false;
			this.sender = sender;
		}
		
		@Override
		public void action() {
			AID aid = sender.getSender();
			ACLMessage message = new ACLMessage(ACLMessage.REFUSE);
			message.setContent(sender.getContent());
			message.addReceiver(aid);
			myAgent.send(message);
			Done = true;
		}

		@Override
		public boolean done() {
			return Done;
		}
		
	}
}
