package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import client.Client;
import model.Model;

/** Class that builds a Registration view including menuPanel
 * 
 * @author nataliemcdonnell
 *
 */
public class Registration extends JPanel{

	private Client controller = null;
	private Model model;
	private static final long serialVersionUID = 1L;
	JPanel background;
	RegistrationPanel rp;

	/** Constructor to build Registration
	 * 
	 * @param controller an object that connects the view to the server
	 * @param model an object that contains the methods to update the view
	 */
	public Registration(Client controller, Model model){
		
		this.controller = controller;
		this.model = model;
		setPreferredSize(new Dimension(1000,650));
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(layout);
		
//		method that reads picture from file to set as background
		try {
			background = new JPanel() {
				
				private static final long serialVersionUID = 1L;
				private Image backgroundImage = ImageIO.read(new File("rsz_1calendar.jpg"));
				Image scaled = backgroundImage.getScaledInstance(1000, 650, Image.SCALE_DEFAULT);
				public void paint( Graphics g ) { 
				    super.paint(g);
				    g.drawImage(scaled, 0, 0, null);
				  }
				};
				this.add(background, gbc);
		} catch (IOException e) {
			//File could not be found
			background = null;
		}
		
		rp = new RegistrationPanel(controller, model);

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 2;
		gbc.gridy = 2;
		add(rp, gbc);

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 3;
		gbc.gridheight = 3;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(background, gbc);
	
	}
	
	/** Getter for registrationPanel
	 * 
	 * @return rp a RegistrationPanel
	 */
	public RegistrationPanel getRegistrationPanel(){
		return this.rp;
	}
	
	/**
	 * 
	 */
	public void refresh(){
		this.removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 2;
		gbc.gridy = 2;
		add(rp, gbc);
		
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 3;
		gbc.gridheight = 3;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(background, gbc);
	}
}

