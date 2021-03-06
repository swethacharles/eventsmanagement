package server;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
/**
 * this class specifies the look and feel of the server window, and also
 * creates the button that allows the user to safely close the server and its
 * connections
 * @author Mark
 *
 */
public class ServerController extends JPanel {
	/**
	 * this constructor intialises the model and view for the server, as well
	 * as setting up the window layout. It creates the control (listeners) and
	 * buttons that allow for the server to be closed.
	 * @param addr the address of the server as a String
	 * @param portNumber the port that the server will listen for connections on
	 */
	public ServerController(String addr, int portNumber){

		super();

		// model 
		ServerModel model = new ServerModel(addr, portNumber);

		// views
		ServerView text = new ServerView(model);

		text.setVisible(true);
		text.setOpaque(true);
		text.setBackground(Color.white);
		text.setEditable(false);

		// make views observe model
		model.addObserver(text);
		
		model.refreshText();

		// create control
		JButton closeServerButton = new JButton();
		
		closeServerButton.setText("Close Server");

		closeServerButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				model.closeServer();
			}
		});
		
		GridBagLayout layout = new GridBagLayout();
		layout.columnWeights = new double[] {1.0, 1.0, 1.0, 1.0};
		layout.rowWeights = new double[] {1.0};
		
		this.setLayout(layout);
		this.setOpaque(true);
		this.setBackground(new Color(80,80,80));

		GridBagConstraints textLayout = new GridBagConstraints();
		GridBagConstraints buttonLayout = new GridBagConstraints();

		textLayout.fill = GridBagConstraints.BOTH;
		textLayout.insets = new Insets(10,10,10,10);
		textLayout.gridx = 0;
		textLayout.gridy = 0;
		textLayout.gridwidth = 3;
		textLayout.gridheight = 2;
		buttonLayout.weightx = 1;
		textLayout.weighty = 1;
		JScrollPane jsp = new JScrollPane(text);
		add(jsp, textLayout);

		buttonLayout.fill = GridBagConstraints.BOTH;
		buttonLayout.insets = new Insets(2,2,2,2);
		buttonLayout.weightx = 0;
		buttonLayout.weighty = 1;
		buttonLayout.ipady = 10;

		buttonLayout.gridx = 3;
		buttonLayout.gridy = 0;
		add(closeServerButton, buttonLayout);
	}


}
