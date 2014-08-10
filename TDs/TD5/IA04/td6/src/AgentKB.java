import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import model.KBRequest;
import model.KBreplyData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class AgentKB extends Agent {
	private static final long serialVersionUID = 373286715547464813L;
	
	private String prefix;
	private OntModel model;
	
	
	public void setup() {
		prefix = "http://td5#";
		model = ModelFactory.createOntologyModel();
		model.read("src/kb.txt", null, "TURTLE");
		
		addBehaviour(new WaitRDFRequestBehaviour());
		addBehaviour(new WaitSparQLRequestBehaviour());
	}
	
	public class WaitSparQLRequestBehaviour extends CyclicBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2892881557339505610L;

		@Override
		public void action() {
			MessageTemplate filtre = MessageTemplate.MatchPerformative(ACLMessage.REQUEST).MatchLanguage("sparql");
			ACLMessage request = myAgent.receive(filtre);
			
			ObjectMapper mapper = new ObjectMapper();
			
			if(request != null) {
				try {
					KBRequest data = mapper.readValue(request.getContent(), KBRequest.class);
					addBehaviour(new ProcessSparQLRequestBehaviour(data.getArgs(), request));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public class WaitRDFRequestBehaviour extends CyclicBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7194107779455717860L;

		@Override
		public void action() {
			MessageTemplate filtre = MessageTemplate.MatchPerformative(ACLMessage.REQUEST).MatchLanguage("rdf");
			ACLMessage request = myAgent.receive(filtre);
			
			ObjectMapper mapper = new ObjectMapper();
			
			if(request != null) {
				try {
					KBRequest data = mapper.readValue(request.getContent(), KBRequest.class);
					addBehaviour(new ProcessRDFRequestBehaviour (data.getRequestType(),data.getArgs(), request));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} else 
				block();
		}
	}
	
	public class ProcessRDFRequestBehaviour extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 9166766662747779857L;
		private boolean Done;
		private int type;
		private String argument;
		private ACLMessage expediteur;
		private String result;
		
		
		ProcessRDFRequestBehaviour() {
			Done = false;
		}
		
		ProcessRDFRequestBehaviour(int type , String argument, ACLMessage request) {
			Done = false;
			this.type = type;
			this.expediteur = request;
			this.argument = argument;
		}
		
		@Override
		public void action() {
			ObjectMapper mapper = new ObjectMapper();
			try
			{
			switch (type) {
				case 1:   
					result = td5GetAllStatementForPersonID (argument);
					break;				
				case 2:	
					result = td5GetAllStatementForPerson (argument);
					break;
				case 3: 	
					result = td5GetKnownPerson (argument);
					break;
				default: 
					break;
			}
			
			ACLMessage reply = expediteur.createReply();
			KBreplyData answer = new KBreplyData (type,result);
			StringWriter sw = new StringWriter();
			mapper.writeValue(sw, answer);									
			reply.setContent(sw.toString());
			myAgent.send(reply);
			Done = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		

		@Override
		public boolean done() {
			return Done;
		}
		
	}
	
	public class ProcessSparQLRequestBehaviour extends Behaviour {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2575575535341428969L;
		private boolean Done;
		private String argument;
		private ACLMessage expediteur;
		private String result;
		
		public ProcessSparQLRequestBehaviour(String argument, ACLMessage expediteur) {
			Done = false;
			this.argument = argument;
			this.expediteur = expediteur;
		}
		
		@Override
		public void action() {
			ObjectMapper mapper = new ObjectMapper();
			try
			{
				result = runExecQuery(argument);
			
				ACLMessage reply = expediteur.createReply();
				KBreplyData answer = new KBreplyData (result);
				StringWriter sw = new StringWriter();
				mapper.writeValue(sw, answer);									
				reply.setContent(sw.toString());
				myAgent.send(reply);
				Done = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public boolean done() {
			return Done;
		}
		
		
		private String runExecQuery(String query) {
			String result = new String();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			query = addPrefix(query);
			
			QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
			//QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://linkedgeodata.org/sparql", query); 
			ResultSet r = queryExecution.execSelect();
			ResultSetFormatter.out(out,r);
			
			result = out.toString();

			queryExecution.close();
			
			return result;
		}
		
		private String addPrefix(String request) {
			String result = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX dc: <http://purl.org/dc/elements/1.1/>\nprefix wot: <http://xmlns.com/wot/0.1/>\nprefix foaf: <http://xmlns.com/foaf/0.1/>\nprefix owl: <http://www.w3.org/2002/07/owl#>\nprefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nprefix vs: <http://www.w3.org/2003/06/sw-vocab-status/ns#>\nprefix lgd: <http://linkedgeodata.org/>\nprefix lgdo: <http://linkedgeodata.org/ontology/>\n\n";
			result += request;
			return result;
		}
	}
	
	public String td5GetAllStatementForPersonID(String identifiant) {
		String nstd5 			= model.getNsPrefixURI("td5");
		Resource h 				= model.getResource(nstd5 + identifiant);
		StmtIterator iterator 	= model.listStatements(new SimpleSelector(h, null, (Resource)null)) ;
		
		String result = new String();
		
		while(iterator.hasNext()) {
			result += iterator.next() + "\n";
		}
		
		return result;
	}
	
	public String td5GetAllStatementForPerson(String name) {
		DatatypeProperty property = model.getDatatypeProperty("name");
		
		StmtIterator iterator = model.listStatements(new SimpleSelector(null, property,name));
		
		String result = new String();
		
		while(iterator.hasNext()) {
			String id = iterator.next().getSubject().toString();
			result += id + " :\n";
			
			Resource h = model.getResource(id);
			StmtIterator iteratorStatements = model.listStatements(new SimpleSelector(h, (Property)null, (Resource)null));
			while(iteratorStatements.hasNext()) {
				result += iteratorStatements.next() + "\n";
			}
			result += "\n\n";
		}
		
		return result;
	}
	
	public String td5GetKnownPerson(String identifiant) {
		String nstd5 			= model.getNsPrefixURI("td5");
		String nsrdf 			= model.getNsPrefixURI("foaf");
		Resource h 				= model.getResource(nstd5 + identifiant);
		Property type 			= model.getProperty(nsrdf + "knows");
		StmtIterator iterator 	= model.listStatements(new SimpleSelector(h, type, (Resource)null)) ;
		
		String result = new String();
		
		while(iterator.hasNext()) {
			result += iterator.next() + "\n";
		}
		
		return result;
	}
}
