package com.datein.date_in.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;
import com.datein.date_in.controller.FriendsStateChangeListener;
import com.datein.date_in.log.Logger;
import com.datein.date_in.views.material_edit_text.MaterialEditText;

public class FriendsFragment extends Fragment implements FriendsStateChangeListener {

	private static final String TAG = "FriendsFragment";

	private DateInActivity activity;
	private Typeface font;

	// All the container.
	private LinearLayout friendSearchContainer;
	private LinearLayout friendRequestsContainer;
	private LinearLayout friendsContainer;
	private RelativeLayout noFriendsContainer;

	private MaterialEditText friendSearch;

	public static FriendsFragment newInstance() {
		return new FriendsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View resView = inflater.inflate(R.layout.fragment_friends, container, false);

		activity = (DateInActivity) getActivity();
		// Set font style.
		font = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");

		// Get all the container.
		friendSearchContainer = (LinearLayout) resView.findViewById(R.id.friendSearchResultContainer);
		friendRequestsContainer = (LinearLayout) resView.findViewById(R.id.friendRequestsContainer);
		friendsContainer = (LinearLayout) resView.findViewById(R.id.friendsContainer);
		noFriendsContainer = (RelativeLayout) resView.findViewById(R.id.noFriendsContainer);

		friendSearch = (MaterialEditText) resView.findViewById(R.id.friendSearch);
		friendSearch.setTypeface(font);
		TextView noFriend = (TextView) resView.findViewById(R.id.txt_noFriend);
		noFriend.setTypeface(font);
		TextView friendRequests = (TextView) resView.findViewById(R.id.friendRequests);
		friendRequests.setTypeface(font);
		TextView friends = (TextView) resView.findViewById(R.id.friends);
		friends.setTypeface(font);

		friendSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					activity.getFriendsController().onSearch(v.getText().toString());
					return true;
				}
				return false;
			}
		});

		activity.getFriendsController().doChangeState(Constants.STATE_FRIENDS, null);

		return resView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (activity.getFriendsController().getListener() != this)
			activity.getFriendsController().setListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		activity.getFriendsController().setListener(null);
	}

	@Override
	public void onStateChanged(String state, String displayName) {
		String currentState = activity.getFriendsController().getCurrentState();
		Logger.d(TAG, "Current State: " + currentState);
		Logger.d(TAG, "State: " + state);
		if (currentState.equals(state)) {
			Logger.d(TAG, "ERROR: BOTH STATE ARE THE SAME!");
			return;
		}

		// If user start searching for friends..
		if(currentState.equals(Constants.STATE_FRIENDS) && state.equals(Constants.STATE_FRIENDS_SEARCHING)) {
			friendSearch.setEnabled(false);
			friendSearchContainer.removeAllViews();
		}

		// If search ok, display the search result..
		if(currentState.equals(Constants.STATE_FRIENDS_SEARCHING) &&
				(state.equals(Constants.STATE_FRIENDS_SEARCH_OK) || state.equals(Constants.STATE_FRIENDS_SEARCH_FAIL))) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View container = inflater.inflate(R.layout.friend_search_container, null, false);
			TextView friendSearchResult = (TextView) container.findViewById(R.id.friendSearchResult);
			friendSearchResult.setTypeface(font);
			View row;
			if(displayName != null) {
				row = inflater.inflate(R.layout.friend_row, null, false);
				TextView friendDisplayName = (TextView) row.findViewById(R.id.row_txt_display_name);
				friendDisplayName.setTypeface(font);
				friendDisplayName.setText(displayName);
			} else {
				row = inflater.inflate(R.layout.friend_row_null, null, false);
				TextView friendDisplayName = (TextView) row.findViewById(R.id.row_txt_display_name);
				friendDisplayName.setTypeface(font);
				friendDisplayName.setText("No Result Found!");
			}

			friendSearchContainer.setVisibility(View.VISIBLE);
			friendSearchContainer.addView(container);
			friendSearchContainer.addView(row);
			friendSearch.setEnabled(true);

			activity.getFriendsController().doChangeState(Constants.STATE_FRIENDS, null);
		}

		activity.getFriendsController().setCurrentState(state);
	}
}
