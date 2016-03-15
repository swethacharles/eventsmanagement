package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import client.Client;
import model.Model;
import model.ModelState;
import objectTransferrable.Event;

public class ListPanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3193985081542643253L;
	Client controller;
	Model model;
	JPanel left = new JPanel();
	
	JPanel list = new JPanel();
	JScrollPane listscroll;
	
	Calendar c = new GregorianCalendar();
	
	JPanel top = new JPanel();
	JLabel date;
	JButton addEvent = new JButton("+");
	
	JPanel changeDay = new JPanel();
	JButton previous = new JButton("Previous Day");
	JButton next = new JButton("Next Day");
	
	static TitledBorder border;
	
	ArrayList<JLabel> times = new ArrayList<JLabel>();
	static ArrayList<JPanel> events = new ArrayList<JPanel>();
	
	Event clickedEvent;
	
	//----------------------Constructor------------------------------//
	
	public ListPanel(Client controller, Model model){
		
		this.controller = controller;
		this.model = model;
		//clickedEvent = new Event(null);
		
		//sets dimension and layout of the panel
		Dimension dimension = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		setPreferredSize(new Dimension((int)dimension.getWidth(), (int)(dimension.getHeight()-200)));
		setMinimumSize(new Dimension((int)dimension.getWidth(), (int)dimension.getHeight()-200));
		setMaximumSize(new Dimension((int)dimension.getWidth(), (int)dimension.getHeight()-200));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		//creates JLabel with the date on
		date = getDate(c.get(Calendar.DAY_OF_WEEK), c.get(Calendar.DATE), c.get(Calendar.MONTH), c.get(Calendar.YEAR));
		date.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 25));
		date.setForeground(Color.DARK_GRAY);
		
		//creates addEvent button
		addEvent.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 24));
		addEvent.setMinimumSize(new Dimension(35,35));
		addEvent.setMaximumSize(new Dimension(35,35));
		
		//creates panel containing date label and addEvent button
		top.setMaximumSize(new Dimension(900,70));
		top.setMinimumSize(new Dimension(900,70));
		top.setLayout(new BoxLayout(top, BoxLayout.LINE_AXIS));
		top.add(date);
		top.add(Box.createRigidArea(new Dimension(20,0)));
		top.add(addEvent);
		
		//updates model with the meetings for today and adds them to panel list
		model.updateMeetings(new Date(c.getTimeInMillis()));
		addMeetings(model.getMeetings());
		
		//adds list to listscroll and sets the size
		listscroll = new JScrollPane(list);
		listscroll.setPreferredSize(new Dimension(900,450));
		listscroll.setMaximumSize(new Dimension(900,450));
		listscroll.setMinimumSize(new Dimension(900,450));
		listscroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listscroll.setAlignmentY(Component.TOP_ALIGNMENT);
		
		//creates panel for next and previous buttons
		changeDay.setPreferredSize(new Dimension(900,50));
		changeDay.setLayout(new BoxLayout(changeDay, BoxLayout.LINE_AXIS));
		changeDay.add(previous);
		changeDay.add(next);
		
		//creates
		top.setAlignmentX(Component.CENTER_ALIGNMENT);
		listscroll.setAlignmentX(Component.CENTER_ALIGNMENT);
		changeDay.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(top);
		add(listscroll);
		add(changeDay);
		
		//----------------------Listeners----------------------//
		
		previous.addActionListener((e) -> {
			c.add(Calendar.DATE, -1);
			setDate(date, c);
			model.updateMeetings(new Date(c.getTimeInMillis()));
			
			addMeetings(model.getMeetings());
			model.changeCurrentState(ModelState.EVENTSUPDATE);
		});
		
		next.addActionListener((e) -> {
			c.add(Calendar.DATE, 1);
			setDate(date, c);
			model.updateMeetings(new Date(c.getTimeInMillis()));
			
			addMeetings(model.getMeetings());
			model.changeCurrentState(ModelState.EVENTSUPDATE);
			
		});
		
//		addEvent.addActionListener((e) -> );
		
//		submit.addActionListener((e) -> ;
	}
	

	
	//----------------------Change Date at the top of page------------------------------//
	
	public void setDate(JLabel date, Calendar c){
		date = getDate(c.get(Calendar.DAY_OF_WEEK), c.get(Calendar.DATE), c.get(Calendar.MONTH), c.get(Calendar.YEAR));
		this.date = date;
	}
	
	
	//---------------------Add meeting panels to listscroll-------------------------//

	public void addMeetings(ArrayList<Event> arraylist){
			
		JPanel list = new JPanel();
		events.clear();
		if (arraylist.isEmpty()){
			
			list.setPreferredSize(new Dimension(650,800));
			list.setLayout(new GridLayout(1,1));
			
			JLabel l = new JLabel("You have no events right now!");
			l.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 20));
			l.setForeground(Color.RED);
			list.add(l);
			
		} else {
			
			for(int i=0; i<arraylist.size(); i++){
			
				//creates new JLabel of event name for each event
				JLabel title = new JLabel(arraylist.get(i).getEventTitle(), SwingConstants.LEFT);
				String a = "Notes :  " + arraylist.get(i).getEventDescription();
				JLabel description = new JLabel(a);
				String b = "Location :  " + arraylist.get(i).getLocation();
				JLabel location = new JLabel(b);
				JButton edit = new JButton("Edit event");
				
				title.setVerticalAlignment(SwingConstants.CENTER);
				description.setVerticalAlignment(SwingConstants.CENTER);
				location.setVerticalAlignment(SwingConstants.CENTER);
				edit.setVerticalAlignment(SwingConstants.CENTER);
				
				title.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 20));
				description.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 16));
				location.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 16));
				edit.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 13));
				
				title.setForeground(Color.RED);
				description.setForeground(Color.DARK_GRAY);
				location.setForeground(Color.DARK_GRAY);
			
				// creates title border with time of each event
				String s = arraylist.get(i).getStartTime().toString().substring(0, 5) + " - " + arraylist.get(i).getEndTime().toString().substring(0, 5);
//				Border line = BorderFactory.createLineBorder(Color.red);
				border = BorderFactory.createTitledBorder(s);
				border.setTitleFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 20));
				
				// creates new JPanel with title border
				JPanel p = new JPanel();
				p.setMaximumSize(new Dimension(890, 200));
				p.setMinimumSize(new Dimension(890, 200));
				p.setBorder(border);
				p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
			
				//adds Jlabel to JPanel and adds to events arraylist 
				p.add(title);
				p.add(Box.createRigidArea(new Dimension(0,10)));
				p.add(location);
				p.add(Box.createRigidArea(new Dimension(0,5)));
				p.add(description);
				p.add(Box.createRigidArea(new Dimension(0,10)));
				p.add(edit);
				events.add(p);	
			}
			
			list.setLayout(new BoxLayout(list, BoxLayout.PAGE_AXIS));
			
			for(int i = 0; i<events.size(); i++){
				list.add(events.get(i));
				list.add(Box.createRigidArea(new Dimension(0,20)));
			}
		}
		
		this.list=list;
	}
	
	//---------------------Says which event is clicked on--------------//
	
//	public static void setEventClickedOn(){
//		Event eventClicked = new Event()
//		
//	}
	
	//-------------------Creates JLabel for date-----------------------//
		
	public static JLabel getDate(int day, int date, int month, int year){
		
		StringBuffer s = new StringBuffer();
		
		switch(day){
			case 1: s.append("Sunday "); break;
			case 2: s.append("Monday "); break;
			case 3: s.append("Tuesday "); break;
			case 4: s.append("Wednesday "); break;
			case 5: s.append("Thursday "); break;
			case 6: s.append("Friday "); break;
			case 7: s.append("Saturday "); break;
		}
		
		s.append(date);
		
		if (date == 1){
			s.append("st ");
		} else if (date == 2){
			s.append("nd ");
		} else if (date == 3) {
			s.append("rd ");
		} else {
			s.append("th ");
		}
		
		switch(month){
			case 0: s.append("January"); break;
			case 1: s.append("February"); break;
			case 2: s.append("March"); break;
			case 3: s.append("April"); break;
			case 4: s.append("May"); break;
			case 5: s.append("June"); break;
			case 6: s.append("July"); break;
			case 7: s.append("August"); break;
			case 8: s.append("September"); break;
			case 9: s.append("October"); break;
			case 10: s.append("November"); break;
			case 11: s.append("December"); break;
		}
		
		s.append(" " + year);
		
		return new JLabel(s.toString());
		
	}

}
