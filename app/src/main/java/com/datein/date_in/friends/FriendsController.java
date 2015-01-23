package com.datein.date_in.friends;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.StateChangeListener;
import com.datein.date_in.log.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class FriendsController {

	private static final String TAG = "FriendsController";

	private Handler handler = new Handler(Looper.getMainLooper());

	private static FriendsController instance;
	private DateInActivity activity;
	private StateChangeListener listener;
	private String currentState;
	private String searchResult;
	private boolean isSearchResultInRequestList;
	private boolean isSearchResultInPendingList;
	private boolean isSearchResultInFriendList;
	private int previousFriendsCounter = 0;
	private ArrayList<String> friendList;
	private ArrayList<String> requestList;
	private ArrayList<String> pendingList;

	public FriendsController(DateInActivity activity) {
		this.activity = activity;
		this.currentState = Constants.STATE_START;
		this.friendList = new ArrayList<>();
		this.requestList = new ArrayList<>();
		this.pendingList = new ArrayList<>();
		instance = this;
	}

	public static FriendsController getInstance() {
		return instance;
	}

	public void onSearch(final String searchString) {
		doChangeState(Constants.STATE_FRIENDS_SEARCHING);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_SEARCH);
					data.putString("search", searchString);
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

	public void onSearchOK(Bundle data) {
		searchResult = data.getString("displayName");
		isSearchResultInRequestList = Boolean.valueOf(data.getString("isRequested"));
		isSearchResultInPendingList = Boolean.valueOf(data.getString("isPending"));
		isSearchResultInFriendList = Boolean.valueOf(data.getString("isFriend"));

		Logger.d(TAG, "R: " + isSearchResultInRequestList);
		Logger.d(TAG, "P: " + isSearchResultInPendingList);
		Logger.d(TAG, "F: " + isSearchResultInFriendList);

		doChangeState(Constants.STATE_FRIENDS_SEARCH_OK);
	}

	public void onAddFriend(final String requestTo) {
		doChangeState(Constants.STATE_FRIENDS_ADDING);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_FRIEND_ADD);
					data.putString("requestTo", requestTo);
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

	public void onFriendList() {
		doChangeState(Constants.STATE_FRIENDS_LIST_REQUESTING);
		friendList.clear();
		requestList.clear();
		pendingList.clear();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_FRIEND);
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

	public void onFriendListOK(Bundle data) {
		int friendListIndex = Integer.valueOf(data.getString("friendListIndex"));
		int requestListIndex = Integer.valueOf(data.getString("requestListIndex"));
		int pendingListIndex = Integer.valueOf(data.getString("pendingListIndex"));
		int i = 0, j = 0;
		for(; j < friendListIndex; i++, j++)
			friendList.add(data.getString(String.valueOf(i)));
		for(j = 0; j < requestListIndex; i++, j++)
			requestList.add(data.getString(String.valueOf(i)));
		for(j = 0; j < pendingListIndex; i++, j++)
			pendingList.add(data.getString(String.valueOf(i)));

		Logger.d(TAG, "F: " + friendListIndex);
		Logger.d(TAG, "R: " + requestListIndex);
		Logger.d(TAG, "P: " + pendingListIndex);

		previousFriendsCounter = friendListIndex + requestListIndex + pendingListIndex;
		doChangeState(Constants.STATE_FRIENDS_LIST_OK);
	}

	public void onAcceptFriend(final String acceptFrom) {
		doChangeState(Constants.STATE_FRIENDS_ACCEPTING);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_FRIEND_ACCEPT);
					data.putString("acceptFrom", acceptFrom);
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

	public void onRejectFriend(final String rejectFrom) {
		doChangeState(Constants.STATE_FRIENDS_REJECTING);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_FRIEND_REJECT);
					data.putString("rejectFrom", rejectFrom);
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

	public String getSearchResult() {
		return searchResult;
	}

	public boolean getRequestedStatus() {
		return isSearchResultInRequestList;
	}

	public boolean getPendingStatus() {
		return isSearchResultInPendingList;
	}

	public boolean getFriendStatus() {
		return isSearchResultInFriendList;
	}

	public ArrayList<String> getFriendList() {
		return friendList;
	}

	public ArrayList<String> getRequestList() {
		return requestList;
	}

	public ArrayList<String> getPendingList() {
		return pendingList;
	}

	public int getPreviousFriendsCounter() {
		return previousFriendsCounter;
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
