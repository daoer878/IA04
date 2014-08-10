package td3;

import jade.gui.GuiEvent;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatAgentFrame extends JFrame implements PropertyChangeListener {
	private AgentChat myAgent;
	JScrollPane chatscp;
	JSplitPane chatsp;
	JPanel envoyepane;
	JTextArea chatta;
	JTextField envoyetf;
	
	public ChatAgentFrame ( AgentChat agent){
	super();
	myAgent =agent;
	initialize();
	}
	
	public ChatAgentFrame (){
		super();
		
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
		
		envoyetf = new JTextField();
		 chatsp= new JSplitPane(0,chatscp,envoyetf);
		 chatsp.setOrientation(JSplitPane.VERTICAL_SPLIT);
		 chatsp.setDividerLocation(150);
		this.add( chatsp);
		;
		
		this.setSize(400,350);
		this.setTitle("Chat");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true); 
		
	}

	private void toAgent() {
		GuiEvent ev = new GuiEvent(this, AgentChat.TEXT_EVENT);
		ev.addParameter(null);
		myAgent.postGuiEvent(ev);
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
