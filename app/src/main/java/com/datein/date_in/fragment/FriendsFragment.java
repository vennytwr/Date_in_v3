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
import com.gc.materialdesign.views.ButtonFloatSmall;
import com.gc.materialdesign.views.ButtonIcon;

public class FriendsFragment extends Fragment implements FriendsStateChangeListener {

	private static final String TAG = "FriendsFragment";

	private DateInActivity activity;
	private Typeface font;

	// All the container.
	private LinearLayout searchResultContainer;
	private LinearLayout friendsContainer;
	private RelativeLayout emptyContainer;

	// Search.
	private MaterialEditText friendSearch;
	private ButtonFloatSmall rowAdd;

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
		searchResultContainer = (LinearLayout) resView.findViewById(R.id.searchResultContainer);
		searchResultContainer.setVisibility(View.GONE);
		friendsContainer = (LinearLayout) resView.findViewById(R.id.friendsContainer);
		emptyContainer = (RelativeLayout) resView.findViewById(R.id.emptyContainer);

		// Set up the search input.
		friendSearch = (MaterialEditText) resView.findViewById(R.id.friendSearch);
		friendSearch.setTypeface(font);
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

		// Set up the search dismiss button.
		final ButtonIcon searchResultContainerClose = (ButtonIcon) resView.findViewById(R.id.searchResultContainerClose);
		searchResultContainerClose.setActivated(false);
		searchResultContainerClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(searchResultContainer.getVisibility() == View.VISIBLE)
					searchResultContainer.setVisibility(View.GONE);
			}
		});

		// Set texts font.
		TextView emptyFriendHeader = (TextView) resView.findViewById(R.id.emptyFriendHeader);
		emptyFriendHeader.setTypeface(font);
		TextView searchResultHeader = (TextView) resView.findViewById(R.id.searchResultHeader);
		searchResultHeader.setTypeface(font);
		TextView friendsHeader = (TextView) resView.findViewById(R.id.friendsHeader);
		friendsHeader.setTypeface(font);

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

			// If user had already searched before, remove the previous search result.
			if(searchResultContainer.getChildAt(2) != null)
				searchResultContainer.removeViewAt(2);
		}

		// If search ok, display the search result..
		if(currentState.equals(Constants.STATE_FRIENDS_SEARCHING) &&
				(state.equals(Constants.STATE_FRIENDS_SEARCH_OK) || state.equals(Constants.STATE_FRIENDS_SEARCH_FAIL))) {
			View v;
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					(int)(50 * activity.getDensity()));
			if(displayName != null) {
				v = inflater.inflate(R.layout.row_search_result, null, false);
				v.setLayoutParams(layoutParams);
				TextView rowDisplayName = (TextView) v.findViewById(R.id.row_display_name);
				rowDisplayName.setTypeface(font);
				rowDisplayName.setText(displayName);
				rowAdd = (ButtonFloatSmall) v.findViewById(R.id.row_friend_status_add);
				rowAdd.setEnabled(true);
				rowAdd.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						activity.getFriendsController().onAddFriend();
					}
				});
			} else {
				v = inflater.inflate(R.layout.row_search_fail, null, false);
				v.setLayoutParams(layoutParams);
				TextView rowErrorCode = (TextView) v.findViewById(R.id.row_error_code);
				rowErrorCode.setTypeface(font);
				rowErrorCode.setText("No Result Found!");
			}

			searchResultContainer.setVisibility(View.VISIBLE);
			searchResultContainer.addView(v);
			friendSearch.setEnabled(true);

			activity.getFriendsController().doChangeState(Constants.STATE_FRIENDS, null);
		}

		// If user add a friend..
		if(currentState.equals(Constants.STATE_FRIENDS) && state.equals(Constants.STATE_FRIENDS_ADDING))
			rowAdd.setEnabled(false);

		// If successfully added a friend..
		if(currentState.equals(Constants.STATE_FRIENDS_ADDING) && state.equals(Constants.STATE_FRIENDS_ADD_OK)) {
			// Remove the search result.
			searchResultContainer.removeViewAt(2);
			searchResultContainer.setVisibility(View.GONE);

			// Refresh the friends list
			activity.getFriendsController().onFriendList();
		}

		// Get friend list..
		if(currentState.equals(Constants.STATE_FRIENDS_LIST_REQUESTING) && state.equals(Constants.STATE_FRIENDS_LIST_OK)) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		activity.getFriendsController().setCurrentState(state);
	}
}
