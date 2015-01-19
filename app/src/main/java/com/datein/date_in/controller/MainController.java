package com.datein.date_in.controller;

import android.os.Handler;
import android.os.Looper;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;

public class MainController {

	private static final String TAG = "MainFragmentController";

	private Handler handler = new Handler(Looper.getMainLooper());

	private static MainController instance;
	private DateInActivity activity;
	private StateChangeListener listener;
	private String currentState;

	public MainController(DateInActivity activity) {
		this.activity = activity;
		this.currentState = Constants.STATE_START;
		instance = this;
	}

	public static MainController getInstance() {
		return instance;
	}

	public void setCurrentState(String state) {
		this.currentState = state;
	}

	public String getCurrentState() {
		return currentState;
	}

	public void setListener(StateChangeListener listener) {
		this.listener = listener;
	}

	public StateChangeListener getListener() {
		return listener;
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
