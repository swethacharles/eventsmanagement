package server;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import objectTransferrable.*;
/**
 * this is the key class to which client input objects should be passed
 * for processing. It results in an output object when necessary to pass
 * back to the client. It uses the database connection to process SQL
 * queries, which are all created using prepared statements. One instance
 * of this class is shared by all clients, so key methods are synchronised
 * @author Mark
 *
 */
public class QueryManager {

	private Server server;
	/**
	 * constructor for the query manager. It only needs the server as the input,
	 * as everything else should be passed to methods by the task that calls 
	 * the methods
	 * @param server
	 */
	public QueryManager(Server server) {
		this.server = server;
	}
	/**
	 * getter for the server
	 * @return the server object
	 */
	public Server getServer() {
		return server;
	}
	/**
	 * this is the key method that tasks will pass key client and query information
	 * to, it calls methods that deal with the object that has been passed to the class,
	 * basing its decision on the opCode of the received object
	 * @param currentOperation the object that needs to be processed
	 * @param client the client that passed the object
	 * @return the result of the object being processed
	 */
	public ObjectTransferrable runOperation(ObjectTransferrable currentOperation, ClientInfo client) {

		Connection dbconnection = getServer().getDatabase().getConnection();

		// OTUsernameCheck "0001"
		if (currentOperation.getOpCode().equals("0001")) {
			return checkUsername(dbconnection, currentOperation, client);
		}
		// OTEmailCheck = "0002"
		else if (currentOperation.getOpCode().equals("0002")) {
			return checkEmailvalid(dbconnection, currentOperation, client);
		}
		// OTRegistrationCheck = "0003"
		else if (currentOperation.getOpCode().equals("0003")) {
			return checkRegistration(dbconnection, currentOperation, client);
		}
		// OTRegistrationInformation = "0004"
		else if (currentOperation.getOpCode().equals("0004")) {
			getServer().getServerModel().addToText("opcode is presently depricated! Responding with Error Object\n");
			return new OTErrorResponse("OP code currently out of use!", false, 0004);
		}
		// OP CODE 0005 SPECIAL CASE TO EXIT PROGRAM
		else if (currentOperation.getOpCode().equals("0005")) {
			getServer().getServerModel()
			.addToText("Specially reserved opcode for exiting program has arrived at query manager!\n");
			// Acknowledge the client is exiting. 
			return new OTExitGracefully();
			//ETRunTask will close connections.  
		}
		// OP CODE 0006 RETURN FROM SERVER, SHOULD NEVER APPEAR HERE
		else if (currentOperation.getOpCode().equals("0006")) {
			getServer().getServerModel().addToText(
					"The object assocatied with this opcode should not be received from client! Responding with Error Object\n");
			return new OTErrorResponse("Server specified confirmation message received from client!", false, 0006);

		}
		// This should only be sent to the client when an error is received
		else if (currentOperation.getOpCode().equals("0007")) {
			getServer().getServerModel().addToText(
					"The object assocatied with this opcode should not be received from client! Responding with Error Object\n");
			return new OTErrorResponse("Server specified confirmation message received from client!", false, 0006);		}
		// Request for meetings on specific Day
		else if (currentOperation.getOpCode().equals("0008")) {
			return getMeetings(dbconnection, currentOperation, client);
		}
		// Server Response to get meetings, should never get here
		else if (currentOperation.getOpCode().equals("0009")) {
			getServer().getServerModel().addToText(
					"A message meant to be sent by the server (return list of meetings) has been found at the query manager!\n");
			return new OTErrorResponse(
					"A message meant to be sent by the server (return list of meetings) has been found at the query manager!",
					false);

		}
		// Request to create an event from the client
		else if (currentOperation.getOpCode().equals("0010")) {
			return createEvent(dbconnection, currentOperation, client);
		}
		// This is a return message for event creation successful and should not
		// be seen by server
		else if (currentOperation.getOpCode().equals("0011")) {
			getServer().getServerModel().addToText(
					"A message meant to be sent by the server (sucessful event creation) has been found at the query manager!\n");
			return new OTErrorResponse(
					"A message meant to be sent by the server (sucessful event creation) has been found at the query manager!",
					false);

		}
		// Get users hashed password for the client
		else if (currentOperation.getOpCode().equals("0012")) {
			return hashToClient(dbconnection, currentOperation, client);
		}
		// Gets the users login details for the client
		else if (currentOperation.getOpCode().equals("0013")) {
			return getUserDetails(dbconnection, currentOperation, client);
		} else if (currentOperation.getOpCode().equals("0014")) {
			client.setHBReceivedMillis(System.currentTimeMillis());
			return new OTHeartBeat();
			//getServer().getServerModel().addToText(
			//		"Server received heartbeat and has responded");
		}
		// This is a return message for sending the hash to the client and
		// should not be seen by server
		else if (currentOperation.getOpCode().equals("0015")) {
			getServer().getServerModel().addToText(
					"A message meant to be sent by the server (sending the hash to the client) has been found at the query manager!\n");
			return new OTErrorResponse(
					"A message meant to be sent by the server (sending the hash to the client) has been found at the query manager!",
					false);

		}
		// This is a return message for sending user details to client and
		// should not be seen by server
		else if (currentOperation.getOpCode().equals("0016")) {
			getServer().getServerModel().addToText(
					"A message meant to be sent by the server (sending user details to client) has been found at the query manager!\n");
			return new OTErrorResponse(
					"A message meant to be sent by the server (sending user details to client) has been found at the query manager!",
					false);

		}
		//Updates an events details
		else if (currentOperation.getOpCode().equals("0017")) {
			return updateEvent(dbconnection, currentOperation, client);
		}
		// This is a return message for sending meeting update success to the client and
		// should not be seen by server
		else if (currentOperation.getOpCode().equals("0018")) {
			getServer().getServerModel().addToText(
					"A message meant to be sent by the server (sending meeting update success to the client) has been found at the query manager!\n");
			return new OTErrorResponse(
					"A message meant to be sent by the server (sending meeting update success to the client) has been found at the query manager!",
					false);

		}
		//Deletes an event
		else if (currentOperation.getOpCode().equals("0019")) {
			return deleteEvent(dbconnection, currentOperation, client);
		}
		// This is a return message for sending meeting delete success to the client and
		// should not be seen by server
		else if (currentOperation.getOpCode().equals("0020")) {
			getServer().getServerModel().addToText(
					"A message meant to be sent by the server (sending meeting delete success to the client) has been found at the query manager!\n");
			return new OTErrorResponse(
					"A message meant to be sent by the server (sending meeting delete success to the client) has been found at the query manager!",
					false);

		}
		//Updates a users profile
		else if (currentOperation.getOpCode().equals("0021")) {
			return updateUserProfile(dbconnection, currentOperation, client);
		}
		// This is a return message for sending update user profile success to the client and
		// should not be seen by server
		else if (currentOperation.getOpCode().equals("0022")) {
			getServer().getServerModel().addToText(
					"A message meant to be sent by the server (sending update user profile success to the client) has been found at the query manager!\n");
			return new OTErrorResponse(
					"A message meant to be sent by the server (sending update user profile success to the client) has been found at the query manager!",
					false);

		}
		//Updates a users password
		else if (currentOperation.getOpCode().equals("0023")) {
			return updateUserPassword(dbconnection, currentOperation, client);
		}
		// This is a return message for sending update user password success to the client and
		// should not be seen by server
		else if (currentOperation.getOpCode().equals("0024")) {
			getServer().getServerModel().addToText(
					"A message meant to be sent by the server (sending update user password success to the client) has been found at the query manager!\n");
			return new OTErrorResponse(
					"A message meant to be sent by the server (sending update user password success to the client) has been found at the query manager!",
					false);

		}
		// Unknown OP code response
		else {
			getServer().getServerModel()
			.addToText("opcode of object not known by query manager! Responding with Error Object\n");
			return new OTErrorResponse("An unknown opCode has been received by the query manager!", false);

		}

	}
	/**
	 * the method called to update user passwords
	 * @param con the database connection
	 * @param operation the object that contains the password update information
	 * @param client the client that requested the password change
	 * @return the object that indicates the success or failure of the databse update
	 */
	private ObjectTransferrable updateUserPassword(Connection con, ObjectTransferrable operation, ClientInfo client) {
		OTUpdatePassword classifiedOperation = (OTUpdatePassword) operation;

		//getServer().getServerModel()
		//.addToText("Attempting to update the following users password: " + client.getUserName() + "\n");

		String update = "UPDATE users " 
				+"SET password= ? "
				+"WHERE userName= ?";
		try {
			PreparedStatement updatePassword = con.prepareStatement(update);

			updatePassword.setString(1, classifiedOperation.getPwhash());
			updatePassword.setString(2, client.getUserName());

			//getServer().getServerModel()
			//.addToText("Running this update: " + updatePassword + "\n");

			updatePassword.executeUpdate();
			//getServer().getServerModel().addToText("Successfully updated user password\n");
			return new OTUpdatePasswordSuccessful();
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't update user password\n");
			e.printStackTrace();
			return new OTErrorResponse("Couldn't update user password", false);

		}
	}

	/**
	 * the method that is called when the client wishes to update their profile
	 * information
	 * @param con the database connection
	 * @param operation the object that contains the information to update the profile 
	 * of the user
	 * @param client the client that requested the profile update
	 * @return the object that indicates the success or failure of the task
	 */
	private ObjectTransferrable updateUserProfile(Connection con, ObjectTransferrable operation, ClientInfo client) {
		OTUpdateUserProfile classifiedOperation = (OTUpdateUserProfile) operation;

//		getServer().getServerModel()
//		.addToText("Attempting to update the following users profile: " + client.getUserName() + "\n");
		try {
			String update = "UPDATE users " 
					+"SET firstName= ?, lastName= ?, userEmail= ? "
					+"WHERE userName= ?";



			PreparedStatement updatePassword = con.prepareStatement(update);

			updatePassword.setString(1, classifiedOperation.getFirstName());
			updatePassword.setString(2, classifiedOperation.getLastName());
			updatePassword.setString(3, classifiedOperation.getEmail());
			updatePassword.setString(4, client.getUserName());

//			getServer().getServerModel()
//			.addToText("Running this update: " + updatePassword + "\n");

			updatePassword.executeUpdate();
//			getServer().getServerModel().addToText("Successfully updated user profile\n");
			return new OTUpdateUserProfileSuccessful(classifiedOperation.getFirstName(), classifiedOperation.getLastName(), classifiedOperation.getEmail());
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't update user profile\n");
			e.printStackTrace();
			return new OTErrorResponse("Couldn't update user profile", false);
		}
	}
	/**
	 * the method that allows users to delete events from their calendar. This is synchronized
	 * so that multiple users can't try and delete the same meeting at once. It shares a call
	 * to the getAMeeting method with update meeting (getAMeeting us synchronised), this means that
	 * two clients can't simultaneously try and update and delete an event
	 * @param con the database connection
	 * @param operation the object that contains the event to be deleted
	 * @param client the client that requested the delete
	 * @return the object to be passed back to the client indicating the success or failure
	 * of the delete request
	 */
	private synchronized ObjectTransferrable deleteEvent(Connection con, ObjectTransferrable operation, ClientInfo client) {
		OTDeleteEvent classifiedOperation = (OTDeleteEvent) operation;
		Event eventToDelete = classifiedOperation.getEvent();
//		getServer().getServerModel().addToText("Event Title: " + eventToDelete.getEventTitle() + "\n");
//		getServer().getServerModel().addToText("Global event marker: " + eventToDelete.getGlobalEvent() + "\n");
		try {
			if(getAMeeting(con, eventToDelete, client)){
				String creator;
				if(eventToDelete.getGlobalEvent()){
					creator = "global";
				} else {
					creator = client.getUserName();
				}

//				getServer().getServerModel()
//				.addToText("Attempting to delete a meeting for: " + creator + "\n");

				String update = "DELETE FROM meetings " 
						+"WHERE creatorID= ? AND meetingDate= ? AND meetingTitle= ?"
						+" AND meetingDescription= ? AND meetingLocation= ?"
						+" AND meetingStartTime= ? AND meetingEndTime= ?"
						+" AND lockVersion= ?"
						+"";

//				getServer().getServerModel()
//				.addToText("Running this update: " + update + "\n");

				PreparedStatement deleteEvent = con.prepareStatement(update);

				deleteEvent.setString(1, creator);
				deleteEvent.setDate(2, eventToDelete.getDate());
				deleteEvent.setString(3, eventToDelete.getEventTitle());
				deleteEvent.setString(4, eventToDelete.getEventDescription());
				deleteEvent.setString(5 ,eventToDelete.getLocation());
				deleteEvent.setTime(6, eventToDelete.getStartTime());
				deleteEvent.setTime(7, eventToDelete.getEndTime());
				deleteEvent.setInt(8, eventToDelete.getLockVersion());

				deleteEvent.executeUpdate();

//				getServer().getServerModel().addToText("Successfully deleted event\n");
				return new OTDeleteEventSuccessful(true);
			} else {
				getServer().getServerModel().addToText("Couldn't delete event - stale lock version\n");
				return new OTDeleteEventSuccessful(false);
			}

		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't delete event\n");
			e.printStackTrace();
			return new OTErrorResponse("Couldn't delete event", false);
		}
	}
	/**
	 * the method that allows users to update events from their calendar. This is synchronized
	 * so that multiple users can't try and update the same meeting at once. It shares a call
	 * to the getAMeeting method with delete meeting (getAMeeting us synchronised), this means that
	 * two clients can't simultaneously try and update and delete an event
	 * @param con the database connection
	 * @param operation the object that contains the event to be deleted
	 * @param client the client that requested the delete
	 * @return the object to be passed back to the client indicating the success or failure
	 * of the update request
	 */
	private synchronized ObjectTransferrable updateEvent(Connection con, ObjectTransferrable operation, ClientInfo client) {
		OTUpdateEvent classifiedOperation = (OTUpdateEvent) operation;
		Event oldEvent = classifiedOperation.getOldEvent();
		Event newEvent = classifiedOperation.getNewEvent();

		Time oldStartTime = oldEvent.getStartTime(); 
		Time oldEndTime = oldEvent.getEndTime();
		String oldEventDescription = oldEvent.getEventDescription();
		String oldEventTitle = oldEvent.getEventTitle();
		String oldLocation = oldEvent.getLocation();
		Date oldDate = oldEvent.getDate();
		boolean oldGlobalEvent = oldEvent.getGlobalEvent();
		int oldLockVersion = oldEvent.getLockVersion();

		Time newStartTime = newEvent.getStartTime(); 
		Time newEndTime = newEvent.getEndTime();
		String newEventDescription = newEvent.getEventDescription();
		String newEventTitle = newEvent.getEventTitle();
		String newLocation = newEvent.getLocation();
		Date newDate = newEvent.getDate();
		int newLockVersion = newEvent.getLockVersion();

		try {
			if(getAMeeting(con, oldEvent, client)){
				String creator;
				if(oldGlobalEvent){
					creator = "global";
				} else {
					creator = client.getUserName();
				}


				String update = "UPDATE meetings " 
						+"SET meetingDate= ?, meetingTitle= ?, meetingDescription= ?, "
						+"meetingLocation= ?, meetingStartTime= ?, meetingEndTime= ?, lockVersion= ? "

						+"WHERE creatorID= ? AND meetingDate= ? AND meetingTitle= ? "
						+"AND meetingDescription= ? AND meetingLocation= ? "
						+"AND meetingStartTime= ? AND meetingEndTime= ? AND lockVersion= ?";


				PreparedStatement updateEvent = con.prepareStatement(update);

				updateEvent.setDate(1, newDate);
				updateEvent.setString(2, newEventTitle);
				updateEvent.setString(3, newEventDescription);
				updateEvent.setString(4, newLocation);
				updateEvent.setTime(5, newStartTime);
				updateEvent.setTime(6, newEndTime);
				updateEvent.setInt(7, newLockVersion);

				updateEvent.setString(8, creator);
				updateEvent.setDate(9, oldDate);
				updateEvent.setString(10, oldEventTitle);
				updateEvent.setString(11, oldEventDescription);
				updateEvent.setString(12, oldLocation);
				updateEvent.setTime(13, oldStartTime);
				updateEvent.setTime(14, oldEndTime);
				updateEvent.setInt(15, oldLockVersion);

//				getServer().getServerModel().addToText("QUERY: " +updateEvent.toString()+"\n");
				updateEvent.executeUpdate();
//				getServer().getServerModel().addToText("Successfully updated event\n");
				return new OTUpdateEventSuccessful(true);
			} else {
				getServer().getServerModel().addToText("Failed to update event - stale lock version\n");
				return new OTUpdateEventSuccessful(false);
			}
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't update meeting - SQL Exception\n");
			e.printStackTrace();
			return new OTErrorResponse("Couldn't update meeting - SQL Exception", false);
		}
	}
	/**
	 * a method that checks for the existence of a meeting in the database. It
	 * primary use is to check for matching lock versions
	 * @param con the database connection
	 * @param event the event to search for
	 * @param client the client that sent the corresponding  object
	 * @return true if the meeting exists, false if it doesn't
	 * @throws SQLException should be thrown if there is an issue with the prepared statement
	 */
	private boolean getAMeeting(Connection con, Event event, ClientInfo client) throws SQLException{
		String query = "SELECT * "
				+"FROM meetings m "
				+"WHERE creatorID= ? AND meetingDate= ? AND meetingTitle= ? "
				+"AND meetingDescription= ? AND meetingLocation= ? "
				+"AND meetingStartTime= ? AND meetingEndTime= ? "
				+"AND lockVersion = ?";

		PreparedStatement meetingQuery;



		meetingQuery = con.prepareStatement(query);

		String creator;

		if(event.getGlobalEvent()){
			creator = "global";
		} else {
			creator = client.getUserName();
		}

		meetingQuery.setString(1, creator);
		meetingQuery.setDate(2, event.getDate());
		meetingQuery.setString(3, event.getEventTitle());
		meetingQuery.setString(4, event.getEventDescription());
		meetingQuery.setString(5, event.getLocation());
		meetingQuery.setTime(6, event.getStartTime());
		meetingQuery.setTime(7, event.getEndTime());
		meetingQuery.setInt(8, event.getLockVersion());

//		getServer().getServerModel().addToText("Lock Version of received event: " + event.getLockVersion() + "\n");
//		getServer().getServerModel().addToText("Creator of received event: " + creator + "\n");
//		getServer().getServerModel().addToText("QUERY: " + meetingQuery.toString() + "\n");

		ResultSet rs = meetingQuery.executeQuery();

		if (rs.next()) {
//			getServer().getServerModel().addToText("Found matching meeting, returning true.\n");
			return true;
		} else {

//			getServer().getServerModel().addToText("Found no such meeting, returning false.\n");
			return false;
		}

	}
	/**
	 * a method that creates an event in the database, taking into account the flag
	 * for global events
	 * @param con the database connection
	 * @param operation the object that carries the create event information
	 * @param client the client that requested the event creation
	 * @return the object that indicates the success or failure of the create event request
	 */
	private ObjectTransferrable createEvent(Connection con, ObjectTransferrable operation, ClientInfo client) {
		OTCreateEvent classifiedOperation = (OTCreateEvent) operation;

		String update;
		String creator;

		if(classifiedOperation.getEvent().getGlobalEvent()){
			creator = "global";
		} else {
			creator = client.getUserName();
		}
//		getServer().getServerModel()
//		.addToText("Attempting to create a meeting for:"+creator+"\n");
//		getServer().getServerModel()
//		.addToText("Meeting received has date: " + classifiedOperation.getEvent().getDate().toString() + "\n");
		update = "INSERT INTO meetings VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, 1)";
		try {
			PreparedStatement newEvent = con.prepareStatement(update);

			newEvent.setString(1, creator);
			newEvent.setDate(2, classifiedOperation.getEvent().getDate());
			newEvent.setString(3, classifiedOperation.getEvent().getEventTitle());
			newEvent.setString(4, classifiedOperation.getEvent().getEventDescription());
			newEvent.setString(5, classifiedOperation.getEvent().getLocation());
			newEvent.setTime(6, classifiedOperation.getEvent().getStartTime());
			newEvent.setTime(7, classifiedOperation.getEvent().getEndTime());

//			getServer().getServerModel()
//			.addToText("Attempting to create a meeting for: " + creator + "\n");

			newEvent.executeUpdate();
//			getServer().getServerModel().addToText("Successfully created meeting\n");
			return new OTCreateEventSucessful(classifiedOperation.getEvent());
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't create meeting\n");
			e.printStackTrace();
			return new OTErrorResponse("Couldn't create meeting", false);
		}
	}
	/**
	 * the method for checking if a user is already in the database or not. It is
	 * not synchronised as it doesn't happen at a point where anything is committed
	 * to the database
	 * @param con the database connection
	 * @param operation the object that carries the username to be checked
	 * @param client the client that requested the check
	 * @return the object that indicates whether the user exists or not
	 */
	private ObjectTransferrable checkUsername(Connection con, ObjectTransferrable operation, ClientInfo client) {
		OTUsernameCheck classifiedOperation = (OTUsernameCheck) operation;
		try {
			if(checkForUserExistance(con, classifiedOperation.getUsername(), client)){
				classifiedOperation.setAlreadyExists(true);
			} else {
				classifiedOperation.setAlreadyExists(false);
			}
			return classifiedOperation;
		} catch (SQLException e) {
			e.printStackTrace();
			return new OTErrorResponse("SQL failed with username query", false);
		}
	}
	/**
	 * a method that checks to see if a user already exists in the database. It is synchronised
	 * as it is used by key methods that commit user information to the database,
	 * including information with primary key uniqueness constraints
	 * @param con the database connection
	 * @param username the username to check for
	 * @param client the client that requested the check
	 * @return true if the username already exists, false if not
	 * @throws SQLException should only be thrown if the prepared statement fails
	 */
	private synchronized boolean checkForUserExistance(Connection con, String username, ClientInfo client) throws SQLException{
//		getServer().getServerModel()
//		.addToText("Checking to see if " + username + " is in the database...\n");

		String query = "SELECT count(u.userName) " + "FROM users u " + "GROUP BY u.userName " + "HAVING u.userName = ?";

		PreparedStatement checkUser = con.prepareStatement(query);
		
		checkUser.setString(1, username);
		
		ResultSet rs = checkUser.executeQuery();

		if (rs.next()) {
//			getServer().getServerModel().addToText("Found matching username, returning true.\n");
			return true;
		} else {
//			getServer().getServerModel().addToText("Found no such user, returning false.\n");
			return false;
		}
	}
	/**
	 * a method to check if an email already exists in the database. It is not 
	 * synchronised as the email isn't committed at this point
	 * @param con the database connection
	 * @param operation the object that carries the email to be checked
	 * @param client the client that requested the check
	 * @return true if the email already exists, false if not
	 */
	private ObjectTransferrable checkEmailvalid(Connection con, ObjectTransferrable operation, ClientInfo client) {
		OTEmailCheck classifiedOperation = (OTEmailCheck) operation;
		try {
			if(checkForEmailExistance(con, classifiedOperation.getEmail(), client)){
				classifiedOperation.setAlreadyExists(true);
			} else {
				classifiedOperation.setAlreadyExists(false);
			}
			return classifiedOperation;
		} catch (SQLException e) {
			e.printStackTrace();
			return new OTErrorResponse("SQL failed with email query", false);
		}
	}
	/**
	 * a method that checks for an email already existing in the database. It is synchronised
	 * as it is used by the method that commits user information to the database, and the email
	 * has a uniqueness constraint
	 * @param con the database connection
	 * @param email the email to be checked
	 * @param client the client that requested the check
	 * @return true if the email already exists, false if not
	 * @throws SQLException should only be thrown if the execution of the prepared statement fails
	 */
	private synchronized boolean checkForEmailExistance(Connection con, String email, ClientInfo client) throws SQLException {
//		getServer().getServerModel().addToText("Checking to see if: " + email + " is in the database\n");

		String query = "SELECT count(u.userEmail) " + "FROM users u " + "GROUP BY u.userEmail "
				+ "HAVING u.userEmail = ?";

		PreparedStatement checkEmail = con.prepareStatement(query);
		
		checkEmail.setString(1, email);
		
		ResultSet rs = checkEmail.executeQuery();

		if (rs.next()) {
//			getServer().getServerModel().addToText("Email exists, returning true.\n");
			return true;
		} else {
//			getServer().getServerModel().addToText("Email not in use, returning false.\n");
			return false;
		}
	}
	/**
	 * the method that adds registering users to the database. It ensures that uniqueness
	 * constraints on the username and email are enforced, by synchronising the method
	 * @param con the database connection
	 * @param operation the object that carries the registration information
	 * @param client the client that requested the registration
	 * @return the object that indicates the success or failure of the registration
	 * request
	 */
	private synchronized ObjectTransferrable checkRegistration(Connection con, ObjectTransferrable operation, ClientInfo client) {
		OTRegistrationInformation classifiedOperation = (OTRegistrationInformation) operation;
//		getServer().getServerModel()
//		.addToText("Attempting to create a user with name: " + classifiedOperation.getUsername() + "\n");
		try {
			if(!checkForUserExistance(con, classifiedOperation.getUsername(), client)){
				if(!checkForEmailExistance(con, classifiedOperation.getEmail(), client)){
					
					String update = "INSERT INTO users VALUES (?, ?, ?, ?, ?)";
					
					PreparedStatement checkUser = con.prepareStatement(update);
					
					checkUser.setString(1, classifiedOperation.getUsername());
					checkUser.setString(2, classifiedOperation.getPwHash());
					checkUser.setString(3, classifiedOperation.getFirstname());
					checkUser.setString(4, classifiedOperation.getLastname());
					checkUser.setString(5, classifiedOperation.getEmail());
					
					checkUser.executeUpdate();
					
//					getServer().getServerModel().addToText("Succesfully created user");
					return new OTRegistrationInformationConfirmation(true, null, null);
				} else {
					return new OTRegistrationInformationConfirmation(false, null, "Email already exists");
				}
			} else {
				return new OTRegistrationInformationConfirmation(false, null, "Username already exists");
			}
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't create user");
			e.printStackTrace();
			return new OTErrorResponse("Error with SQL query", false);
		}

	}
	/**
	 * the method that retrieves a particular users meetings for a particular day
	 * @param con the database connection
	 * @param operation the object that contains the request information
	 * @param client the client that requested the meeting information
	 * @return an object contains an ArrayList of Event objects for a particular day
	 */
	private ObjectTransferrable getMeetings(Connection con, ObjectTransferrable operation, ClientInfo client) {
		OTRequestMeetingsOnDay classifiedOperation = (OTRequestMeetingsOnDay) operation;

		try {
			ArrayList<Event> meetings = retrieveMeetingsFromDB(classifiedOperation.getDate(), client, con);
//			getServer().getServerModel().addToText("Returning " + meetings.size() + " meetings to client" + "\n");
			OTReturnDayEvents returnEvents = new OTReturnDayEvents(meetings);
			return returnEvents;
		} catch (SQLException e) {
			getServer().getServerModel().addToText("SQL Server failed with user details request" + "\n");
			e.printStackTrace();
			return new OTErrorResponse("SQL Server failed with meeting request", false);
		}
	}
	/**
	 * a method that gets a users details
	 * @param con the database connection
	 * @param operation the object that contains the information needed to make the 
	 * user details request
	 * @param client the client that requested the user details
	 * @return an object that contains the users details, or a failure object if the
	 * query failed
	 */
	private ObjectTransferrable getUserDetails(Connection con, ObjectTransferrable operation, ClientInfo client) {

		OTLoginSuccessful classifiedOperation = (OTLoginSuccessful) operation;

		String query = "SELECT u.firstName, u.lastName, u.userEmail " + "FROM users u " + "WHERE u.userName = ?";
		try {
			PreparedStatement userDetails = con.prepareStatement(query);
			
			userDetails.setString(1, classifiedOperation.getUsername());
			
			ResultSet rs = userDetails.executeQuery();

			if (rs.next()) {
//				getServer().getServerModel()
//				.addToText("Retrieved user details for " + classifiedOperation.getUsername() + "\n");
				String firstName, lastName, email;
				firstName = rs.getString(1);
				lastName = rs.getString(2);
				email = rs.getString(3);
//				getServer().getServerModel()
//				.addToText("Set Client username to " + classifiedOperation.getUsername() + "\n");
				client.setUserName(classifiedOperation.getUsername());
				return new OTLoginProceed(true, firstName, lastName, email);
			} else {
//				getServer().getServerModel()
//				.addToText("User " + classifiedOperation.getUsername() + " does not exist" + "\n");
				return new OTLoginProceed(false, null, null, null);
			}
		} catch (SQLException e) {
			getServer().getServerModel().addToText("SQL Server failed with user details request" + "\n");
			e.printStackTrace();
			return new OTErrorResponse("SQL Server failed with user details request", false);
		}
	}
	/**
	 * a method that acquires a hashed version of a users password
	 * @param con the database connection
	 * @param operation the object that contains the information of the user
	 * who has requested the hash
	 * @param client the client that requested the hash
	 * @return the users hashed password in a transferable object
	 */
	private ObjectTransferrable hashToClient(Connection con, ObjectTransferrable operation, ClientInfo client) {
		OTLogin classifiedOperation = (OTLogin) operation;

		String query = "SELECT u.password " + "FROM users u " + "WHERE u.userName = ?";

		try {
			PreparedStatement hashQuery = con.prepareStatement(query);
			hashQuery.setString(1, classifiedOperation.getUsername());
			
			ResultSet rs = hashQuery.executeQuery();

			if (rs.next()) {
				String pwFromDB = rs.getString(1);

//				getServer().getServerModel().addToText("Sending user following hash: " + pwFromDB + "\n");
				return new OTHashToClient(true, pwFromDB);

			} else {
//				getServer().getServerModel()
//				.addToText("User " + classifiedOperation.getUsername() + " does not exist" + "\n");
				return new OTHashToClient(false, null);
			}
		} catch (SQLException e) {
			getServer().getServerModel().addToText("SQL Server failed with hash request" + "\n");
			e.printStackTrace();
			return new OTErrorResponse("SQL Server failed with hash request", false);
		}
	}
	/**
	 * a method that retrieves a users events for a given day from the database
	 * @param date the date of the events to be gathered
	 * @param client the client that requested the events
	 * @param con the database connection
	 * @return an ArrayList of events ordered by their start time
	 * @throws SQLException if the request fails for some reason, this error 
	 * will be thrown
	 */
	private ArrayList<Event> retrieveMeetingsFromDB(Date date, ClientInfo client, Connection con) throws SQLException {

		String query = "SELECT m.creatorID, m.meetingtitle, m.meetingdescription, m.meetinglocation, m.meetingstarttime, m.meetingendtime, m.lockVersion "
				+ "FROM meetings m " + "WHERE (m.creatorid = ? OR m.creatorid = 'global')"
				+" AND m.meetingdate = ? "
				+ "ORDER BY m.meetingstarttime ASC";

		PreparedStatement meetingsQuery = con.prepareStatement(query);
		
		meetingsQuery.setString(1, client.getUserName());
		meetingsQuery.setDate(2, date);
		
		ResultSet rs = meetingsQuery.executeQuery();
		
		ArrayList<Event> meetings = new ArrayList<Event>();
//		getServer().getServerModel()
//		.addToText("Requesting meeting information for " + client.getUserName() + "\n");
		while (rs.next()) {
			String creator = rs.getString(1);
			String title = rs.getString(2);
			String description = rs.getString(3);
			String location = rs.getString(4);
			Time startTime = rs.getTime(5);
			Time endTime = rs.getTime(6);
			int lockVersion = rs.getInt(7);
			Event event;

			if(creator.equals("global")){
				event = new Event(startTime, endTime, description, title, location, date, true, lockVersion);
			} else {
				event = new Event(startTime, endTime, description, title, location, date, false, lockVersion);
			}
			meetings.add(event);
		}
		return meetings;

	}

}