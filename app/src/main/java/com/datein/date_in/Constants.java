package com.datein.date_in;

public interface Constants {

	// Google cloud messaging related.
	String SENDER_ID = "994318371798";
	int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	long GCM_DEFAULT_TTL = 2 * 24 * 60 * 60; // two days
	String REGISTRATION_ID = "REGISTRATION_ID";
	String ACTION = "ACTION";


	// Other constants.
	String KEY_LOGGED_IN = "KEY_LOGGED_IN";
	String KEY_REG_ID = "KEY_REG_ID";
	String KEY_MSG_ID = "KEY_MSG_ID";
	String KEY_APP_VERSION = "KEY_APP_VERSION";
	String PACKAGE = "com.datein.date_in.gcm";

	// All the state.
	String STATE_START = "STATE_START";
	String STATE_LOGIN = "STATE_LOGIN";
	String STATE_LOGGING_IN = "STATE_LOGGING_IN";
	String STATE_LOGIN_OK = "STATE_LOGIN_OK";
	String STATE_LOGIN_FAIL = "STATE_LOGIN_FAIL";
	String STATE_REGISTER = "STATE_REGISTER";
	String STATE_REGISTERING = "STATE_REGISTERING";
	String STATE_REGISTER_OK = "STATE_REGISTER_OK";
	String STATE_EMAIL_TAKEN = "STATE_EMAIL_TAKEN";
	String STATE_DISPLAY_NAME_TAKEN = "STATE_DISPLAY_NAME_TAKEN";
	String STATE_MAIN = "STATE_MAIN";
	String STATE_CALENDAR = "STATE_CALENDAR";
	String STATE_FRIENDS = "STATE_FRIENDS";
	String STATE_FRIENDS_SEARCHING = "STATE_FRIENDS_SEARCHING";
	String STATE_FRIENDS_SEARCH_OK = "STATE_FRIENDS_SEARCH_OK";
	String STATE_FRIENDS_SEARCH_FAIL = "STATE_FRIENDS_SEARCH_FAIL";
	String STATE_EVENTS = "STATE_EVENTS";

	// Send.
	String ACTION_REGISTER = PACKAGE + ".REGISTER";
	String ACTION_UNREGISTER = PACKAGE + ".UNREGISTER";
	String ACTION_LOGIN = PACKAGE + ".LOGIN";
	String ACTION_SEARCH = PACKAGE + ".SEARCH";
	String ACTION_ECHO = PACKAGE + ".ECHO";
	String NOTIFICATION_ACTION = PACKAGE + ".NOTIFICATION";

	// Receive.
	String ACTION_LOGIN_OK = PACKAGE + ".LOGIN_OK";
	String ACTION_LOGIN_FAIL = PACKAGE + ".LOGIN_FAIL";
	String ACTION_REGISTER_OK = PACKAGE + ".REGISTER_OK";
	String ACTION_REGISTER_EMAIL_TAKEN = PACKAGE + ".REGISTER_EMAIL_TAKEN";
	String ACTION_REGISTER_DISPLAY_NAME_TAKEN = PACKAGE + ".REGISTER_DISPLAY_NAME_TAKEN";
	String ACTION_SEARCH_OK = PACKAGE + ".SEARCH_OK";
	String ACTION_SEARCH_FAIL = PACKAGE + ".SEARCH_FAIL";
}
