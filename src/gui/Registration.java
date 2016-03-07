package gui;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.Controller;

public class Registration extends JPanel{

	private Controller controller = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel background;

	/**Constructor to create a Login
	 * 
	 * @throws IOException
	 */
	public Registration(Controller controller) throws IOException{
		
		this.controller = controller;
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 3;
		gbc.gridheight = 3;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		
//		method that reads picture from file to set as background
		background = new JPanel() {
			
			private static final long serialVersionUID = 1L;
			private Image backgroundImage = ImageIO.read(new File("calendar.jpg"));
			public void paint( Graphics g ) { 
			    super.paint(g);
			    g.drawImage(backgroundImage, 0, 0, null);
			  }
			};
//		background.setPreferredSize(new Dimension(1000, 750));
//		Image backgroundImage = ImageIO.read(new File("calendar.jpg"));
//		background = new BackgroundPanel(backgroundImage);
		add(background, gbc);
		
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridy = 1;
		RegistrationPanel rp = new RegistrationPanel(this.controller);
		add(rp, gbc);
		
		rp.setOpaque(true);
		background.setOpaque(true);
	
	}
	
	public static void main(String[] args) throws IOException {
		
		JFrame frame = new JFrame();
		Controller controller = new Controller();
		
		Registration r = new Registration(controller);
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(r);
		frame.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		frame.setResizable(true);
		frame.setVisible(true);
	}
}
