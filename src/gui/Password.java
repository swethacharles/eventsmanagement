package gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import client.Client;
import model.Model;

/** Class that builds a Jpanel that contains menuPanel and PasswordPanel
 * 
 * @author nataliemcdonnell
 *
 */
public class Password extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6891502734315534592L;
	private Client controller = null;
	private Model model;
	MenuPanel bar;
	PasswordPanel password;
	
	/**
	 * 
	 * @param controller an object that connects the view to the server
	 * @param model an object that contains the methods to update the view
	 * @param menuPanel a MenuPanel object
	 */
	public Password(Client controller, Model model, MenuPanel menuPanel){
		
		this.controller = controller;
		this.model = model;
		bar = menuPanel;
		password = new PasswordPanel(controller, model);
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setPreferredSize(new Dimension(1000,650));
		setMaximumSize(new Dimension(1000,650));
		setMinimumSize(new Dimension(1000,650));

		add(bar);
		add(password);
	}

	/**
	 * 
	 */
	public void refresh() {
		this.removeAll();
		this.add(bar);
		this.add(password);
		
	}

}
