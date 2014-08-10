package td4v2;

import java.io.StringWriter;
import java.util.Collections;

import SudokuModelv2.AnalyseRequestData;
import SudokuModelv2.Cell;

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

	protected void setup() {
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
			MessageTemplate filtre = MessageTemplate
					.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = myAgent.receive(filtre);

			if (message != null) {
				ObjectMapper mapper = new ObjectMapper();

				try {
					AnalyseRequestData data = mapper.readValue(
							message.getContent(), AnalyseRequestData.class);
					addBehaviour(new ProceedAnalyseBehaviour(data, message));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
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

		public ProceedAnalyseBehaviour(AnalyseRequestData datas,
				ACLMessage sender) {
			this.datas = new AnalyseRequestData(datas.getValues().clone(),
					datas.getZone());
			this.sender = sender;
			Done = false;
		}

		@Override
		public void action() {

			// get data

			Cell[] answerData = Trier(datas.getValues());
			// answerData = datas.getValues();
			// answerData = Trier(answerData);

			AnalyseRequestData answer = new AnalyseRequestData(answerData,
					datas.getZone());
			StringWriter sw = new StringWriter();
			ObjectMapper mapper = new ObjectMapper();

			try {
				mapper.writeValue(sw, answer);

				ACLMessage reply = sender.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(sw.toString());
				myAgent.send(reply);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Done = true;
		}

		private Cell[] checkDeterminedValues(Cell[] values) {
			// Met �jour les valeur des cellules quand il ne reste qu'un
			// possible.
			for (int i = 0; i < 9; i++) {
				if (values[i].getPossibles().size() == 1) {
					values[i].setValue(Integer.parseInt(values[i]
							.getPossibles().get(0)));
					values[i].getPossibles().clear();

					if (values[i].getPossibles().size() == 0) {
						for (int k = 0; k < 9; k++) {
							values[k].getPossibles().remove(
									Integer.toString(values[i].getValue()));
							Collections.sort(values[k].getPossibles());
						}
					}
				}
			}

			return values;
		}

		private Cell[] Trier(Cell[] values) {
			if (values == null)
				return null;

			Cell[] result = values;
			for (int i = 0; i < 9; i++) {
				if (!result[i].getPossibles().isEmpty())
					Collections.sort(result[i].getPossibles());
			}

			// On retire des possibles toutes les valeurs d巎�connues.
			for (int i = 0; i < 9; i++) {
				Cell toTest = result[i];
				for (int j = i + 1; j < 9; j++) {
					// On test une celulle d巘ermin巈 : on retire sa valeur des
					// possibles des autres cellules.
					if (toTest.getValue() > 0) {
						if (result[j].getPossibles().contains(
								String.valueOf(toTest.getValue()))) {
							result[j].getPossibles().remove(
									String.valueOf(toTest.getValue()));
						}
					}
					// La valeur de la cellule teste n'est pas connu, on retire
					// de ses possibles toutes
					// les valeurs d巎�d巘ermin巈s.
					else {
						if (result[j].getValue() > 0) {
							if (toTest.getPossibles().contains(
									Integer.toString(result[j].getValue()))) {
								toTest.getPossibles().remove(
										Integer.toString(result[j].getValue()));
							}
						}
					}
				}
			}



			result = checkDeterminedValues(result);

			/**
			 * Une valeur ne se trouvant que dans une seule liste de possible
			 * est la valeur de cette cellule
			 **/

			for (int i = 0; i < 9; i++) {
				if (result[i].getValue() > 0)
					continue;

				for (int j = 0; j < result[i].getPossibles().size(); j++) {
					int value = Integer.parseInt(result[i].getPossibles()
							.get(j));

					boolean found = false;

					for (int k = 0; k < 9; k++) {
						if (k != i) {
							if (result[k].getPossibles().contains(
									Integer.toString(value))) {
								found = true;
								break;
							}
						}
					}

					if (!found) {
						result[i].setValue(value);
						result[i].getPossibles().clear();
						break;
					}
				}
			}

			/**
			 * retire 2 possibilite des auters cellules, si 2 celulle ont 2
			 * possibilites, et ce sont les memes.
			 **/

			for (int i = 0; i < 9; i++) {

				if (result[i].getPossibles().size() == 2) {
					for (int k = i + 1; k < 9; k++) {
						if (result[k].getPossibles().equals(
								result[i].getPossibles())) {
							for (int q = 0; q < 9; q++) {
								if (q != i && q != k) {
									result[q].getPossibles().remove(
											result[i].getPossibles().get(0));
									result[q].getPossibles().remove(
											result[i].getPossibles().get(1));
								}

							}
						}
					}
				}

			}

			result = checkDeterminedValues(result);

			return result;
		}

		@Override
		public boolean done() {
			return Done;
		}

	}
}
