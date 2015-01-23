package com.datein.date_in.login_register;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.StateChangeListener;
import com.datein.date_in.log.Logger;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginRegisterController {

	private static final String TAG = "LoginController";

	private Handler handler = new Handler(Looper.getMainLooper());

	private static LoginRegisterController instance;
	private DateInActivity activity;
	private StateChangeListener listener;
	private String currentState;

	public LoginRegisterController(DateInActivity activity) {
		this.activity = activity;
		this.currentState = Constants.STATE_START;
		instance = this;
	}

	public static LoginRegisterController getInstance() {
		return instance;
	}

	public void onLogin(final String email, final String password) {
		doChangeState(Constants.STATE_LOGGING_IN);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String pwd = hashPassword(password);
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_LOGIN);
					data.putString("email", email);
					data.putString("password", pwd);
					String msgId = Integer.toString(activity.getNextMsgId());
					activity.getGcm().send(
							Constants.SENDER_ID + "@gcm.googleapis.com", msgId, Constants.GCM_DEFAULT_TTL, data);
				} catch (IOException e) {
					Logger.d(TAG, "IOException: " + e);
				}
				return null;
			}
		}.execute();
	}

	public void onRegister(final String displayName, final String email, final String password) {
		doChangeState(Constants.STATE_REGISTERING);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String pwd = hashPassword(password);
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_REGISTER);
					data.putString("displayName", displayName);
					data.putString("email", email);
					data.putString("password", pwd);
					String msgId = Integer.toString(activity.getNextMsgId());
					activity.getGcm().send(
							Constants.SENDER_ID + "@gcm.googleapis.com", msgId, Constants.GCM_DEFAULT_TTL, data);
				} catch (IOException e) {
					Logger.d(TAG, "IOException: " + e);
				}
				return null;
			}
		}.execute();
	}

	public void saveSession() {
		final SharedPreferences prefs = activity.getPrefs();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(Constants.KEY_LOGGED_IN, true);
		editor.apply();
	}

	private String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			if (md != null) {
				md.update(password.getBytes()); // Change this to "UTF-16" if needed
				byte[] hash = md.digest();
				StringBuilder sb = new StringBuilder();
				for (byte b : hash)
					sb.append(Integer.toString((b & 0xFF) + 0x100, 16).substring(1));
				 return sb.toString();
			}
		} catch (NoSuchAlgorithmException e) {
			Logger.d(TAG, "NoSuchAlgorithmException: " + e);
		}
		return null;
	}

	public void setCurrentState(String state) {
		this.currentState = state;
	}

	public String getCurrentState() {
		return currentState;
	}

	public StateChangeListener getListener() {
		return listener;
	}

	public void setListener(StateChangeListener listener) {
		this.listener = listener;
	}

	public void doChangeState(final String nState) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (listener != null) {
					listener.onStateChanged(nState);
				}
			}
		});
	}
}
