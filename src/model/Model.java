package model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Observable;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bcrypt.BCrypt;
import client.Client;
import gui.ErrorConnectionDown;
import gui.Login;
import gui.Registration;
import objectTransferrable.OTRegistrationInformation;

public class Model extends Observable {
	private ModelState currentstate;
	private Client client;

	private JScrollPane currentPanel = null;
	private Login login;
	private Registration registration;
	private JPanel meeting;
	private ErrorConnectionDown error;

	// http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
	private Pattern emailRegex = Pattern
			.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	private Pattern dobRegex = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");

	// ------------Registration information----------------------//
	private String username;
	private String email;
	private String firstName;
	private String lastname;
	private String dob;
	private char[] password;
	// --------------Boolean values for registration-----------------//
	private boolean usernameUnique = false;
	private boolean username20orLess = false;
	private boolean emailUnique = false;
	private boolean emailMatchesRegex = false;
	private boolean firstNameLessThan30 = false;
	private boolean lastNameNameLessThan30 = false;
	private boolean passwordMatchesConfirm = false;
	private boolean password60orLess = false;
	private boolean passwordatleast8 = false;
	private boolean oldEnough = false;
	// --------------Login success----------------//
	private boolean successfulLogin = false;

	public Model(Client client) {
		this.client = client;
		this.currentstate = ModelState.LOGIN;

		login = new Login(this.client, this);
		currentPanel = new JScrollPane(login);

	}

	// Checks if username is duplicated in the database
	public void checkUsername(String username) {
		this.username = username;
		if (DataValidation.checkLessThanTwenty(username)) {
			this.username20orLess = true;
			this.registration.getRegistrationPanel().setUserLabel("User*");
			this.changeCurrentState(ModelState.REGISTRATIONUPDATE);
			this.client.checkUsername(username);
		} else {
			this.username20orLess = false;
			this.registration.getRegistrationPanel().setUserLabel("User* : incorrect format");
			this.changeCurrentState(ModelState.REGISTRATIONUPDATE);
		}

	}

	// Checks if email matches regex, then check if email is in in the database
	public void checkEmail(String email) {
		this.email = email;
		if (emailRegex.matcher(email).matches()) {
			this.emailMatchesRegex = true;
			this.registration.getRegistrationPanel().setEmailLabel("Email*");
			this.changeCurrentState(ModelState.REGISTRATIONUPDATE);
			this.client.checkEmail(email);
		} else {
			this.emailMatchesRegex = false;
			this.registration.getRegistrationPanel().setEmailLabel("Email*: incorrect format*");
			this.changeCurrentState(ModelState.REGISTRATIONUPDATE);
		}

	}

	// checks whether firstname <= 30 char
	public void validateFirstName(String name) {
		this.firstName = name;
		if (DataValidation.checkLessThanThirty(name)) {
			this.firstNameLessThan30 = true;
			this.registration.getRegistrationPanel().setFirstLabel("First Name");
			this.changeCurrentState(ModelState.REGISTRATIONUPDATE);
		} else {
			this.firstNameLessThan30 = false;
			this.registration.getRegistrationPanel().setFirstLabel("First Name*: incorrect format");
			this.changeCurrentState(ModelState.REGISTRATIONUPDATE);
		}
	}

	// checks whether lastname <= 30 char
	public void validateLastName(String name) {
		this.lastname = name;
		if (DataValidation.checkLessThanThirty(name)) {
			this.lastNameNameLessThan30 = true;
			this.registration.getRegistrationPanel().setFirstLabel("Last Name");
			this.changeCurrentState(ModelState.REGISTRATIONUPDATE);
		} else {
			this.lastNameNameLessThan30 = false;
			this.registration.getRegistrationPanel().setLastLabel("Last Name*: incorrect format");
			this.changeCurrentState(ModelState.REGISTRATIONUPDATE);

		}

	}

	public boolean checkConfirmMatchesPassword(char[] confirm) {
		if (confirm.equals(password)) {
			this.passwordMatchesConfirm = true;
			return true;
		} else {
			this.passwordMatchesConfirm = false;
			return false;
		}

	}

	public void validateDOB(String dob) {

		if (dobRegex.matcher(dob).matches()) {
			int day = Integer.parseInt(dob.substring(0, 2));
			int month = Integer.parseInt(dob.substring(3, 5));
			int year = Integer.parseInt(dob.substring(5, -1));
			LocalDate birthdate = LocalDate.of(1970, 1, 20);
			LocalDate now = LocalDate.now();
			Period period = Period.between(birthdate, now);
			if (period.getYears() >= 18) {
				this.oldEnough = true;
				this.registration.getRegistrationPanel().setDobLabel("Date Of Birth");
			} else {
				this.oldEnough = false;
				this.registration.getRegistrationPanel().setDobLabel("Date Of Birth: Must be 18 or over");
			}

		} else {
			this.registration.getRegistrationPanel().setDobLabel("Date Of Birth* dd/mm/yyyy: incorrect format");
		}
	}

	public void validatePassword(char[] password) {
		this.password = password;
		if (password.length <= 7) {
			this.passwordatleast8 = false;
			this.registration.getRegistrationPanel().setPasswordLabel("Password*: must be between 8 and 60 characters");
		} else if (password.length > 60) {
			this.password60orLess = false;
			this.registration.getRegistrationPanel().setPasswordLabel("Password*: must be less than 60 characters");
		} else {
			this.password60orLess = true;
			this.passwordatleast8 = true;
			this.registration.getRegistrationPanel().setPasswordLabel("Password*");
		}
		this.changeCurrentState(ModelState.REGISTRATIONUPDATE);
	}

	public void checkRegistrationInformation(String firstname, String lastname, String dob, String password,
			String confirm) {

		if (this.usernameUnique && this.username20orLess && this.emailUnique && this.emailMatchesRegex
				&& this.emailUnique && this.firstNameLessThan30 && this.lastNameNameLessThan30
				&& this.passwordMatchesConfirm && this.password60orLess && this.passwordatleast8 && this.oldEnough) {
			String hashedPassword = BCrypt.hashpw(this.password.toString(), BCrypt.gensalt()); // Does
																								// this
																								// work?
																								// needs
																								// some
																								// research.
			OTRegistrationInformation otri = new OTRegistrationInformation(this.username, this.email, this.firstName,
					this.lastname, hashedPassword);
			this.client.checkRegistration(otri);
		} else {

		}

	}

	public void promptRestart() {
		if (this.error != null) {
			this.error.promptRestart();
		} else {
			this.error = new ErrorConnectionDown(this);
			this.error.promptRestart();
		}
		this.changeCurrentState(ModelState.PROMPTRELOAD);
	}
	
	//-------------------------ButtonMethods---------------//
	
	public void login(String username, String date){
		
	}

	// --------Save information that returns from server----//

	public void setUsernameExists(boolean usernameExists) {
		if (usernameExists) {
			this.usernameUnique = false;
			this.registration.getRegistrationPanel().setUserLabel("Username*: already exists!*");
		} else {
			this.usernameUnique = true;
			this.registration.getRegistrationPanel().setUserLabel("Username*");
		}
		this.changeCurrentState(ModelState.REGISTRATIONUPDATE);

	}

	public void setEmailExists(boolean emailExists) {
		if (emailExists) {
			this.emailUnique = false;
			this.registration.getRegistrationPanel().setEmailLabel("Email*: already exists!*");
		} else {
			this.emailUnique = true;
			this.registration.getRegistrationPanel().setEmailLabel("Email*");
		}
		this.changeCurrentState(ModelState.REGISTRATIONUPDATE);

	}

	// --------End of information from server------------//

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmailUnique() {
		return emailUnique;
	}

	public boolean isUsernameUnique() {
		return usernameUnique;
	}

	public boolean isEmailMatchesRegex() {
		return emailMatchesRegex;
	}

	public void setUsernameUnique(boolean usernameUnique) {
		this.usernameUnique = usernameUnique;
	}

	public ModelState getCurrentState() {
		return this.currentstate;

	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public boolean getSuccessfulLogin() {
		return successfulLogin;
	}

	public synchronized void changeCurrentState(ModelState state) {
		this.currentstate = state;

		// !!--Dont forget breaks;--!!//
		switch (state) {

		case REGISTRATION:
			this.registration = new Registration(client, this);
			setPanel(this.registration);
			break;

		case LOGIN:
			this.login = new Login(client, this);
			setPanel(this.login);
			break;

		case REGISTRATIONUPDATE:
			setPanel(this.registration);
			break;

		case ERRORCONNECTIONDOWN:
			this.error = new ErrorConnectionDown(this);
			setPanel(this.error);
			break;

		case PROMPTRELOAD:
			setPanel(this.error);
			break;

		case EXIT:
			this.client.exitGracefully();
			break;
		}

	}

	public JScrollPane getCurrentPanel() {
		return this.currentPanel;
	}

	public void setPanel(JPanel panel) {
		currentPanel = new JScrollPane(panel);
		setChanged();
		notifyObservers();

	}

}
