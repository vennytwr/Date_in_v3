package com.datein.date_in.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.datein.date_in.MainActivity;
import com.datein.date_in.gcm.GcmIntentService;
import com.datein.date_in.log.Logger;

public class LoginController {

	private static final String TAG = "LoginController";

	public static final int STATE_START = 0;
	public static final int STATE_LOGIN = 1;
	public static final int STATE_REGISTER = 2;

	private Handler handler = new Handler(Looper.getMainLooper());

	enum State {
		REGISTERED, UNREGISTERED;
	}

	private MainActivity activity;
	private LoginListener listener;
	private int currentState;
	private State mState = State.UNREGISTERED;

	public LoginController(MainActivity activity) {
		this.activity = activity;
		this.currentState = STATE_START;
	}

	public void showLogin() {
		doChangeState(STATE_LOGIN);
	}

	public void showRegister() {
		doChangeState(STATE_REGISTER);
	}

	public boolean onRegister(String username, String email, String password) {
//		SharedPreferences prefs = PreferenceManager
//				.getDefaultSharedPreferences(activity.getApplicationContext());
//		int stateAsInt = prefs.getInt(Constants.KEY_STATE,
//				Constants.State.UNREGISTERED.ordinal());
//		mState = Constants.State.values()[stateAsInt];
//		Logger.d(TAG, "GCM State: " + mState.toString());
//
//		if(mState == Constants.State.UNREGISTERED) {
//			Intent regIntent = new Intent(activity.getApplicationContext(), GcmIntentService.class);
//			regIntent.setAction(Constants.ACTION_REGISTER);
//			regIntent.putExtra(Constants.KEY_USERNAME, username);
//			regIntent.putExtra(Constants.KEY_EMAIL, email);
//			regIntent.putExtra(Constants.KEY_PASSWORD, password);
//		}

		return false;
	}

	public int getCurrentState() {
		return currentState;
	}

	public LoginListener getListener() {
		return listener;
	}

	public void setListener(LoginListener listener) {
		this.listener = listener;
	}

	private void doChangeState(final int nState) {
		this.currentState = nState;
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
