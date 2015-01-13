package com.datein.date_in.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.datein.date_in.Constants;
import com.datein.date_in.MainActivity;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginRegisterController {

	private static final String TAG = "LoginController";

	private Handler handler = new Handler(Looper.getMainLooper());

	private static LoginRegisterController instance;
	private MainActivity activity;
	private LoginRegisterListener listener;
	private int currentState;

	public LoginRegisterController(MainActivity activity) {
		this.activity = activity;
		this.currentState = Constants.STATE_START;
		instance = this;
	}

	public static LoginRegisterController getInstance() {
		return instance;
	}

	public void onRegister(final String displayName, final String email, final String password) {
		doChangeState(Constants.STATE_REGISTERING);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();

					MessageDigest md = MessageDigest.getInstance("SHA-256");
					String hashPassword = null;
					if (md != null) {
						md.update(password.getBytes()); // Change this to "UTF-16" if needed
						byte[] hash = md.digest();
						StringBuilder sb = new StringBuilder();
						for (byte b : hash)
							sb.append(Integer.toString((b & 0xFF) + 0x100, 16).substring(1));
						hashPassword = sb.toString();
					}

					String regId = activity.getRegistrationId();
					data.putString("registrationId", regId);
					data.putString("action", Constants.ACTION_REGISTER);
					data.putString("displayName", displayName);
					data.putString("email", email);
					data.putString("password", hashPassword);
					String msgId = Integer.toString(activity.getNextMsgId());
					activity.getGcm().send(
							Constants.SENDER_ID + "@gcm.googleapis.com", msgId, Constants.GCM_DEFAULT_TTL, data);
				} catch (IOException e) {
					System.err.println(new StringBuilder().append("IOException:").append(e.toString()));
				} catch (NoSuchAlgorithmException e) {
					System.err.println(new StringBuilder().append("NoSuchAlgorithmException:").append(e.toString()));
				}

				return null;
			}
		}.execute();
	}

	public void setCurrentState(int state) {
		this.currentState = state;
	}

	public int getCurrentState() {
		return currentState;
	}

	public LoginRegisterListener getListener() {
		return listener;
	}

	public void setListener(LoginRegisterListener listener) {
		this.listener = listener;
	}

	public void doChangeState(final int nState) {
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
