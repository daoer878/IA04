package td3;

import jade.gui.GuiEvent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatAgentFrame extends JFrame implements PropertyChangeListener {
	private AgentChat myAgent;
	//private ActionCatch a;
	JScrollPane chatscp;
	JSplitPane chatsp;
	JPanel envoyepane;
	JTextArea chatta;
	JTextField envoyetf;
	String name;
	
	public ChatAgentFrame ( AgentChat agent,String name){
	super();
	myAgent =agent;
	this.name=name;
	//a = new ActionCatch();
	initialize();
	}
	
	
	private void initialize() {
		// TODO Auto-generated method stub
		chatta= new JTextArea();
		chatta.setLineWrap(true);
		chatta.setPreferredSize(new Dimension(190, 150));

		chatta.setBackground(Color.cyan);

		chatta.setBorder(BorderFactory.createLoweredBevelBorder());


		
		this.chatscp = new JScrollPane(chatta);
		this.envoyetf = new JTextField();
		this.envoyetf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chatta.append( "Envoye : "+envoyetf.getText()+"\n");
				System.out.println("Signal reçu");
				toAgent();
			}
		});
		
		
		 chatsp= new JSplitPane(0,chatscp,envoyetf);
		 chatsp.setOrientation(JSplitPane.VERTICAL_SPLIT);
		 chatsp.setDividerLocation(150);
		this.add( chatsp);
		
		
		

		this.setSize(400,350);
		this.setTitle(name);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true); 
		
	}

	private void toAgent() {
		GuiEvent ev = new GuiEvent(this, AgentChat.TEXT_EVENT);
		ev.addParameter(envoyetf.getText());
		myAgent.postGuiEvent(ev);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		chatta.append("Reçu : " + evt.getNewValue().toString() + "\n");
	}



	
}
