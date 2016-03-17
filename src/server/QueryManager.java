package server;

import java.sql.*;
import java.util.*;
import objectTransferrable.*;

public class QueryManager {

	private Server server;

	public QueryManager(Server server) {
		this.server = server;

	}

	public Server getServer() {
		return server;
	}

	public ObjectTransferrable runOperation(ObjectTransferrable currentOperation, ClientInfo client) throws SQLException {
		
		Connection dbconnection = getServer().getDatabase().getConnection();
		// Prepared st?
		Statement stmnt = dbconnection.createStatement();

		/**
		 * List of present opcodes OTUsernameCheck "0001" OTEmailCheck = "0002"
		 * OTRegistrationCheck = "0003" OTRegistrationInformation = "0004"
		 * OTExitGracefully = "0005" - Should not appear at query manager
		 * OTRegistrationInformationConfirmation = "0006" - Should not appear at
		 * query manager
		 */
		// OTUsernameCheck "0001"
		if (currentOperation.getOpCode().equals("0001")) {
			return checkUsername(stmnt, currentOperation, client);
		}
		// OTEmailCheck = "0002"
		else if (currentOperation.getOpCode().equals("0002")) {
			return checkEmailvalid(stmnt, currentOperation, client);
		}
		// OTRegistrationCheck = "0003"
		else if (currentOperation.getOpCode().equals("0003")) {
			return checkRegistration(stmnt, currentOperation, client);
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
			// No reason to tell the client, they gone, possible shutdown
			// communication?
			return null;
		}
		// OP CODE 0006 RETURN FROM SERVER, SHOULD NEVER APPEAR HERE
		else if (currentOperation.getOpCode().equals("0006")) {
			getServer().getServerModel().addToText(
					"The object assocatied with this opcode should not be recieved from client! Responding with Error Object\n");
			return new OTErrorResponse("Server specified confirmation message recieved from client!", false, 0006);
			
		}
		// The client has returned an error, considering client passive previous
		// server response was bad
		else if (currentOperation.getOpCode().equals("0007")) {
			return dealWithError(stmnt, currentOperation, client);
		}
		// Request for meetings on specific Day
		else if (currentOperation.getOpCode().equals("0008")) {
			return getMeetings(stmnt, currentOperation, client);
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
			return createEvent(stmnt, currentOperation, client);
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
			return hashToClient(stmnt, currentOperation, client);
		}
		// Gets the users login details for the client
		else if (currentOperation.getOpCode().equals("0013")) {
			return getUserDetails(stmnt, currentOperation, client);
		} else if (currentOperation.getOpCode().equals("0014")) {
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
			return updateEvent(stmnt, currentOperation, client);
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
			return deleteEvent(stmnt, currentOperation, client);
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
			return updateUserProfile(stmnt, currentOperation, client);
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
			return updateUserPassword(stmnt, currentOperation, client);
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
			return new OTErrorResponse("An unknown opCode has been recieved by the query manager!", false);
			
		}

	}

	private ObjectTransferrable updateUserPassword(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {
		OTUpdatePassword classifiedOperation = (OTUpdatePassword) operation;

		getServer().getServerModel()
		.addToText("Attempting to update the following users password: " + client.getUserName() + "\n");

		String update = "UPDATE users " 
				+"SET password= '" + classifiedOperation.getPwhash() 
				+"' "
				+"WHERE userName= '" + client.getUserName()
				+"'";
		getServer().getServerModel()
		.addToText("Running this update: " + update + "\n");
		try {
			stmnt.executeUpdate(update);
			getServer().getServerModel().addToText("Successfully updated user password\n");
			return new OTUpdatePasswordSuccessful();
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't update user password\n");
			e.printStackTrace();
			return new OTErrorResponse("Couldn't update user password", false);
			
		}
		
	}

	private ObjectTransferrable updateUserProfile(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {
		OTUpdateUserProfile classifiedOperation = (OTUpdateUserProfile) operation;

		getServer().getServerModel()
		.addToText("Attempting to update the following users profile: " + client.getUserName() + "\n");

		String update = "UPDATE users " 
				+"SET firstName= '" + classifiedOperation.getFirstName() 
				+ "', lastName= '"+ classifiedOperation.getLastName()
				+"', userEmail= '"+ classifiedOperation.getEmail()
				+"' "
				+"WHERE userName= '" + client.getUserName()
				+"'";
		
		getServer().getServerModel()
		.addToText("Running this update: " + update + "\n");
		try {
			stmnt.executeUpdate(update);
			getServer().getServerModel().addToText("Successfully updated user profile\n");
			return new OTUpdateUserProfileSuccessful(classifiedOperation.getFirstName(), classifiedOperation.getLastName(), classifiedOperation.getEmail());
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't update user profile\n");
			e.printStackTrace();
			return new OTErrorResponse("Couldn't update user profile", false);
		}
	}

	private ObjectTransferrable deleteEvent(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {
		OTDeleteEvent classifiedOperation = (OTDeleteEvent) operation;
		Event eventToDelete = classifiedOperation.getEvent();

		getServer().getServerModel()
		.addToText("Attempting to update a meeting for: " + client.getUserName() + "\n");
		
		String update = "DELETE FROM meetings " 
				+"WHERE creatorID= '" + client.getUserName() 
				+ "', meetingDate= '"+eventToDelete.getDate().toString()
				+"', meetingTitle= '"+eventToDelete.getEventTitle()
				+"', meetingDescription= '"+eventToDelete.getEventDescription()
				+"', meetingLocation= '"+eventToDelete.getLocation()
				+"', meetingStartTime= '"+eventToDelete.getStartTime().toString()
				+"', meetingEndTime=, '"+eventToDelete.getEndTime().toString()+"'";

		getServer().getServerModel()
		.addToText("Running this update: " + update + "\n");
		
		try {
			stmnt.executeUpdate(update);
			getServer().getServerModel().addToText("Successfully deleted event\n");
			return new OTDeleteEventSuccessful();
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't delete event\n");
			e.printStackTrace();
			return new OTErrorResponse("Couldn't delete event", false);
			
		}
	}

	private synchronized ObjectTransferrable updateEvent(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {
		OTUpdateEvent classifiedOperation = (OTUpdateEvent) operation;
		Event oldEvent = classifiedOperation.getOldEvent();
		Event newEvent = classifiedOperation.getNewEvent();

		getServer().getServerModel()
		.addToText("Attempting to update a meeting for: " + client.getUserName() + "\n");

		String update = "UPDATE meetings " 
				+"SET meetingDate= '"+newEvent.getDate().toString()
				+"', meetingTitle= '"+newEvent.getEventTitle()
				+"', meetingDescription= '"+newEvent.getEventDescription()
				+"', meetingLocation= '"+newEvent.getLocation()
				+"', meetingStartTime= '"+newEvent.getStartTime().toString()
				+"', meetingEndTime=, '"+newEvent.getEndTime().toString()
				+"' "
				+"WHERE creatorID= '" + client.getUserName() 
				+ "', meetingDate= '"+oldEvent.getDate().toString()
				+"', meetingTitle= '"+oldEvent.getEventTitle()
				+"', meetingDescription= '"+oldEvent.getEventDescription()
				+"', meetingLocation= '"+oldEvent.getLocation()
				+"', meetingStartTime= '"+oldEvent.getStartTime().toString()
				+"', meetingEndTime=, '"+oldEvent.getEndTime().toString()+"'";
		
		getServer().getServerModel()
		.addToText("Running this update: " + update + "\n");
		
		try {
			stmnt.executeUpdate(update);
			getServer().getServerModel().addToText("Successfully updated event\n");
			return new OTUpdateEventSuccessful();
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't update meeting\n");
			e.printStackTrace();
			return new OTErrorResponse("Couldn't update meeting", false);
			
		}
	}

	private ObjectTransferrable createEvent(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {
		OTCreateEvent classifiedOperation = (OTCreateEvent) operation;
		getServer().getServerModel()
		.addToText("Attempting to create a meeting for: " + client.getUserName() + "\n");
		getServer().getServerModel()
		.addToText("Meeting received has date: " + classifiedOperation.getEvent().getDate().toString() + "\n");
		String update = "INSERT INTO meetings VALUES (DEFAULT, '" + client.getUserName() + "', '"
				+ classifiedOperation.getEvent().getDate().toString() + "', '" + classifiedOperation.getEvent().getEventTitle() + "', '"
				+ classifiedOperation.getEvent().getEventDescription() + "', '" + classifiedOperation.getEvent().getLocation()
				+ "', '" + classifiedOperation.getEvent().getStartTime().toString() + "', '" 
				+ classifiedOperation.getEvent().getEndTime().toString() +"')";
		
		getServer().getServerModel()
		.addToText("Running this update: " + update + "\n");
		
		try {
			stmnt.executeUpdate(update);
			getServer().getServerModel().addToText("Successfully created meeting\n");
			return new OTCreateEventSucessful(classifiedOperation.getEvent());
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't create meeting\n");
			e.printStackTrace();
			return new OTErrorResponse("Couldn't create meeting", false);
		}
	}

	private ObjectTransferrable checkUsername(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {
		OTUsernameCheck classifiedOperation = (OTUsernameCheck) operation;
		getServer().getServerModel()
		.addToText("Checking to see if " + classifiedOperation.getUsername() + " is in the database...\n");
		String query = "SELECT count(u.userName) " + "FROM users u " + "GROUP BY u.userName " + "HAVING u.userName = '"
				+ classifiedOperation.getUsername() + "'";
		try {
			ResultSet rs = stmnt.executeQuery(query);

			if (rs.next()) {
				getServer().getServerModel().addToText("Found matching username, returning true.\n");
				classifiedOperation.setAlreadyExists(true);
			} else {
				getServer().getServerModel().addToText("Found no such user, returning false.\n");
				classifiedOperation.setAlreadyExists(false);
			}
			return classifiedOperation;
		} catch (SQLException e) {
			e.printStackTrace();
			return new OTErrorResponse("SQL Server failed with username query", false);
		}
	}

	private ObjectTransferrable checkEmailvalid(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {
		OTEmailCheck classifiedOperation = (OTEmailCheck) operation;
		getServer().getServerModel().addToText("Email received: " + classifiedOperation.getEmail() + "\n");
		// not sure what field name of email is? inserted guess
		String query = "SELECT count(u.userEmail) " + "FROM users u " + "GROUP BY u.userEmail "
				+ "HAVING u.userEmail = '" + classifiedOperation.getEmail() + "'";

		getServer().getServerModel().addToText(query);
		try {
			ResultSet rs = stmnt.executeQuery(query);

			if (rs.next()) {
				getServer().getServerModel().addToText("Email exists, returning true.\n");
				classifiedOperation.setAlreadyExists(true);
			} else {
				getServer().getServerModel().addToText("Email not in use, returning false.\n");
				classifiedOperation.setAlreadyExists(false);
			}
			return classifiedOperation;
		} catch (SQLException e) {
			e.printStackTrace();
			return new OTErrorResponse("SQL Server failed with email query", false);	
		}
	}

	private ObjectTransferrable checkRegistration(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {
		OTRegistrationInformation classifiedOperation = (OTRegistrationInformation) operation;
		getServer().getServerModel()
		.addToText("Attempting to create a user with name: " + classifiedOperation.getUsername() + "\n");
		String update = "INSERT INTO users VALUES ('" + classifiedOperation.getUsername() + "', '"
				+ classifiedOperation.getPwHash() + "', '" + classifiedOperation.getFirstname() + "', '"
				+ classifiedOperation.getLastname() + "', '" + classifiedOperation.getEmail() + "')";
		try {
			stmnt.executeUpdate(update);
			getServer().getServerModel().addToText("Succesfully created user");
			return new OTRegistrationInformationConfirmation(true, null, null);
		} catch (SQLException e) {
			getServer().getServerModel().addToText("Couldn't create user");
			e.printStackTrace();
			return new OTRegistrationInformationConfirmation(false, "ERROR", "ERROR");
		}

	}

	private ObjectTransferrable dealWithError(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {

		OTErrorResponse error = (OTErrorResponse) operation;
		// Is known error response can go here
		if (error.getErrCode() == 0) {
			System.err.println("Undefined error from client, Description: " + error.getErrorDescription()
			+ " Communications being shut down? " + error.isShouldShutdownCommunication());
		} else {
			/**
			 * TODO any specific error handling can go here
			 */
		}

		if (error.isShouldShutdownCommunication()) {

			/**
			 * TODO need to work out a call to shutdown, this is where being
			 * able to call exit gracefully could come in
			 */

		}
		return null;
	}

	private ObjectTransferrable getMeetings(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {
		OTRequestMeetingsOnDay classifiedOperation = (OTRequestMeetingsOnDay) operation;

		String query = "SELECT m.meetingtitle, m.meetingdescription, m.meetinglocation, m.meetingstarttime, m.meetingendtime "
				+ "FROM meetings m " + "WHERE m.creatorid = '" + client.getUserName()
				+ "' AND m.meetingdate = '" + classifiedOperation.getDate().toString() + "' "
				+ "ORDER BY m.meetingstarttime ASC";

		ResultSet rs;
		try {
			rs = stmnt.executeQuery(query);
			ArrayList<Event> meetings = new ArrayList<Event>();
			getServer().getServerModel()
			.addToText("Requesting meeting information for " + client.getUserName() + "\n");
			while (rs.next()) {
				String title = rs.getString(1);
				String description = rs.getString(2);
				String location = rs.getString(3);
				Time startTime = rs.getTime(4);
				Time endTime = rs.getTime(5);

				Event event = new Event(startTime, endTime, description, title, location, classifiedOperation.getDate());
				meetings.add(event);
			}
			getServer().getServerModel().addToText("Returning " + meetings.size() + " meetings to client" + "\n");
			OTReturnDayEvents returnEvents = new OTReturnDayEvents(meetings);
			return returnEvents;
		} catch (SQLException e) {
			getServer().getServerModel().addToText("SQL Server failed with user details request" + "\n");
			e.printStackTrace();
			return new OTErrorResponse("SQL Server failed with meeting request", false);
		}
	}

	private ObjectTransferrable getUserDetails(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {

		OTLoginSuccessful classifiedOperation = (OTLoginSuccessful) operation;

		String query = "SELECT u.firstName, u.lastName, u.userEmail " + "FROM users u " + "WHERE u.userName = '"
				+ classifiedOperation.getUsername() + "'";
		try {
			ResultSet rs = stmnt.executeQuery(query);

			if (rs.next()) {
				getServer().getServerModel()
				.addToText("Retrieved user details for " + classifiedOperation.getUsername() + "\n");
				String firstName, lastName, email;
				firstName = rs.getString(1);
				lastName = rs.getString(2);
				email = rs.getString(3);
				getServer().getServerModel()
				.addToText("Set Client username to " + classifiedOperation.getUsername() + "\n");
				client.setUserName(classifiedOperation.getUsername());
				return new OTLoginProceed(true, firstName, lastName, email);
			} else {
				getServer().getServerModel()
				.addToText("User " + classifiedOperation.getUsername() + " does not exist" + "\n");
				return new OTLoginProceed(false, null, null, null);
			}
		} catch (SQLException e) {
			getServer().getServerModel().addToText("SQL Server failed with user details request" + "\n");
			e.printStackTrace();
			return new OTErrorResponse("SQL Server failed with user details request", false);
		}
	}

	private ObjectTransferrable hashToClient(Statement stmnt, ObjectTransferrable operation, ClientInfo client) {
		OTLogin classifiedOperation = (OTLogin) operation;

		String query = "SELECT u.password " + "FROM users u " + "WHERE u.userName = '"
				+ classifiedOperation.getUsername() + "'";

		try {
			ResultSet rs = stmnt.executeQuery(query);

			if (rs.next()) {
				String pwFromDB = rs.getString(1);

				getServer().getServerModel().addToText("Sending user following hash: " + pwFromDB + "\n");
				return new OTHashToClient(true, pwFromDB);

			} else {
				getServer().getServerModel()
				.addToText("User " + classifiedOperation.getUsername() + " does not exist" + "\n");
				return new OTHashToClient(false, null);
			}
		} catch (SQLException e) {
			getServer().getServerModel().addToText("SQL Server failed with hash request" + "\n");
			e.printStackTrace();
			return new OTErrorResponse("SQL Server failed with hash request", false);
		}
	}

}