package com.datein.date_in.friends;

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
import com.datein.date_in.StateChangeListener;
import com.datein.date_in.log.Logger;
import com.datein.date_in.views.material_edit_text.MaterialEditText;
import com.gc.materialdesign.views.ButtonFloatSmall;
import com.gc.materialdesign.views.ButtonIcon;

public class FriendsFragment extends Fragment implements StateChangeListener {

	private static final String TAG = "FriendsFragment";

	private DateInActivity activity;
	private Typeface font;

	// All the container.
	private LinearLayout searchResultContainer;
	private LinearLayout friendsContainer;
	private RelativeLayout emptyContainer;
	private RelativeLayout loadingContainer;

	// Search.
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
		searchResultContainer = (LinearLayout) resView.findViewById(R.id.searchResultContainer);
		searchResultContainer.setVisibility(View.GONE);
		friendsContainer = (LinearLayout) resView.findViewById(R.id.friendsContainer);
		emptyContainer = (RelativeLayout) resView.findViewById(R.id.emptyContainer);
		loadingContainer = (RelativeLayout) resView.findViewById(R.id.loadingContainer);

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
				if (searchResultContainer.getVisibility() == View.VISIBLE)
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
		TextView friendsLoading = (TextView) resView.findViewById(R.id.txtFriendsLoading);
		friendsLoading.setTypeface(font);

		activity.getFriendsController().onFriendList();

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
	public void onStateChanged(String state) {
		String currentState = activity.getFriendsController().getCurrentState();
		Logger.d(TAG, "Current State: " + currentState);
		Logger.d(TAG, "State: " + state);
		if (currentState.equals(state)) {
			Logger.d(TAG, "ERROR: BOTH STATE ARE THE SAME!");
			return;
		}

		// If user start searching for friends, remove the previous search result..
		if (currentState.equals(Constants.STATE_FRIENDS) && state.equals(Constants.STATE_FRIENDS_SEARCHING)) {
			friendSearch.setEnabled(false);

			// If user had already searched before, remove the previous search result.
			if (searchResultContainer.getChildAt(2) != null) {
				searchResultContainer.removeViewAt(2);
			}
		}

		// If search is success, display the search result..
		if (currentState.equals(Constants.STATE_FRIENDS_SEARCHING) &&
				(state.equals(Constants.STATE_FRIENDS_SEARCH_OK) || state.equals(Constants.STATE_FRIENDS_SEARCH_FAIL))) {
			View v;
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					(int) (50 * activity.getDensity()));
			if (state.equals(Constants.STATE_FRIENDS_SEARCH_OK)) {
				v = inflater.inflate(R.layout.row_friend, null, false);
				v.setLayoutParams(layoutParams);

				// Set up the display name.
				TextView rowDisplayName = (TextView) v.findViewById(R.id.row_display_name);
				rowDisplayName.setTypeface(font);
				rowDisplayName.setText(activity.getFriendsController().getSearchResult());

				final RelativeLayout rowLoadingContainer = (RelativeLayout) v.findViewById(R.id.row_friend_loading);

				// If this friend had been requested..
				if (activity.getFriendsController().getRequestedStatus()) {
					final RelativeLayout friendRequestContainer = (RelativeLayout) v.findViewById(R.id.row_friend_request_container);
					friendRequestContainer.setVisibility(View.VISIBLE);

					// Set up the accept text.
					TextView rowAcceptText = (TextView) v.findViewById(R.id.row_friend_accept_txt);
					rowAcceptText.setTypeface(font);

					// Set up the reject text.
					TextView rowRejectText = (TextView) v.findViewById(R.id.row_friend_reject_txt);
					rowRejectText.setTypeface(font);

					// Set up the accept button.
					ButtonFloatSmall btnRequestAccept = (ButtonFloatSmall) v.findViewById(R.id.row_friend_accept);
					btnRequestAccept.setTag(activity.getFriendsController().getSearchResult());
					btnRequestAccept.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							friendsContainer.setEnabled(false);
							friendRequestContainer.setVisibility(View.GONE);
							rowLoadingContainer.setVisibility(View.VISIBLE);
							friendSearch.setEnabled(false);

							// Remove the search result.
							searchResultContainer.removeViewAt(2);
							searchResultContainer.setVisibility(View.GONE);

							activity.getFriendsController().onAcceptFriend(v.getTag().toString());
						}
					});

					// Set up the reject button.
					ButtonFloatSmall btnRequestReject = (ButtonFloatSmall) v.findViewById(R.id.row_friend_reject);
					btnRequestReject.setTag(activity.getFriendsController().getSearchResult());
					btnRequestReject.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							friendsContainer.setEnabled(false);
							friendRequestContainer.setVisibility(View.GONE);
							rowLoadingContainer.setVisibility(View.VISIBLE);
							friendSearch.setEnabled(false);

							// Remove the search result.
							searchResultContainer.removeViewAt(2);
							searchResultContainer.setVisibility(View.GONE);

							activity.getFriendsController().onRejectFriend(v.getTag().toString());
						}
					});
				} else if (activity.getFriendsController().getPendingStatus()) {
					final RelativeLayout pendingContainer = (RelativeLayout) v.findViewById(R.id.row_friend_pending_container);
					pendingContainer.setVisibility(View.VISIBLE);

					// Set up the pending text.
					TextView rowPendingText = (TextView) v.findViewById(R.id.row_friend_pending_txt);
					rowPendingText.setTypeface(font);

					// Set up the pending button.
					ButtonFloatSmall btnPending = (ButtonFloatSmall) v.findViewById(R.id.row_friend_pending);
					btnPending.setTag(activity.getFriendsController().getSearchResult());
					btnPending.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// DO NOTHING
						}
					});

				}
				// If this friend had been added..
				else if (activity.getFriendsController().getFriendStatus()) {
					final RelativeLayout friendContainer = (RelativeLayout) v.findViewById(R.id.row_friend_added_container);
					friendContainer.setVisibility(View.VISIBLE);

					// Set up the accepted text.
					TextView rowAcceptedText = (TextView) v.findViewById(R.id.row_friend_added_txt);
					rowAcceptedText.setTypeface(font);

					// Set up the added button
					ButtonFloatSmall btnFriendAdded = (ButtonFloatSmall) v.findViewById(R.id.row_friend_added);
					btnFriendAdded.setTag(activity.getFriendsController().getSearchResult());
					btnFriendAdded.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// DO NOTHING
						}
					});
				} else {
					final RelativeLayout friendAddContainer = (RelativeLayout) v.findViewById(R.id.row_friend_add_container);
					friendAddContainer.setVisibility(View.VISIBLE);

					// Set up the accepted text.
					TextView rowAddText = (TextView) v.findViewById(R.id.row_friend_add_txt);
					rowAddText.setTypeface(font);

					// Set up the add button
					ButtonFloatSmall btnRequestAdd = (ButtonFloatSmall) v.findViewById(R.id.row_friend_add);
					btnRequestAdd.setTag(activity.getFriendsController().getSearchResult());
					btnRequestAdd.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							friendsContainer.setEnabled(false);
							friendAddContainer.setVisibility(View.GONE);
							rowLoadingContainer.setVisibility(View.VISIBLE);
							friendSearch.setEnabled(false);

							// Remove the search result.
							searchResultContainer.removeViewAt(2);
							searchResultContainer.setVisibility(View.GONE);

							activity.getFriendsController().onAddFriend(v.getTag().toString());
						}
					});
				}
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

			activity.getFriendsController().doChangeState(Constants.STATE_FRIENDS);
		}

		// If successfully added a friend, refresh the friend list to get the latest friend list..
		if (currentState.equals(Constants.STATE_FRIENDS_ADDING) && state.equals(Constants.STATE_FRIENDS_ADD_OK)) {
			// Refresh the friends list
			activity.getFriendsController().doChangeState(Constants.STATE_FRIENDS);
			activity.getFriendsController().onFriendList();
		}

		// Remove the friend list white requesting for a new one.
		if ((currentState.equals(Constants.STATE_START) || currentState.equals(Constants.STATE_FRIENDS))
				&& state.equals(Constants.STATE_FRIENDS_LIST_REQUESTING)) {
			friendSearch.setEnabled(false);
			if (friendsContainer.getChildAt(3) != null) {
				friendsContainer.removeViews(3, activity.getFriendsController().getPreviousFriendsCounter());
			}
		}

		// Get friend list..
		if (currentState.equals(Constants.STATE_FRIENDS_LIST_REQUESTING) && state.equals(Constants.STATE_FRIENDS_LIST_OK)) {
			if (activity.getFriendsController().getFriendList().size() > 0 ||
					activity.getFriendsController().getRequestList().size() > 0 ||
					activity.getFriendsController().getPendingList().size() > 0) {
				emptyContainer.setVisibility(View.GONE);

				LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						(int) (50 * activity.getDensity()));

				// Set up the request list..
				for (int i = 0; i < activity.getFriendsController().getRequestList().size(); i++) {
					View v = inflater.inflate(R.layout.row_friend, null, false);
					v.setLayoutParams(layoutParams);
					final RelativeLayout friendRequestContainer = (RelativeLayout) v.findViewById(R.id.row_friend_request_container);

					// Set up the accept text.
					TextView rowAcceptText = (TextView) v.findViewById(R.id.row_friend_accept_txt);
					rowAcceptText.setTypeface(font);

					// Set up the reject text.
					TextView rowRejectText = (TextView) v.findViewById(R.id.row_friend_reject_txt);
					rowRejectText.setTypeface(font);

					// Set up the display name.
					TextView rowDisplayName = (TextView) v.findViewById(R.id.row_display_name);
					rowDisplayName.setTypeface(font);
					rowDisplayName.setText(activity.getFriendsController().getRequestList().get(i));

					final RelativeLayout rowLoadingContainer = (RelativeLayout) v.findViewById(R.id.row_friend_loading);

					// Set up the accept button.
					ButtonFloatSmall btnRequestAccept = (ButtonFloatSmall) v.findViewById(R.id.row_friend_accept);
					btnRequestAccept.setTag(activity.getFriendsController().getRequestList().get(i));
					btnRequestAccept.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							friendsContainer.setEnabled(false);
							friendRequestContainer.setVisibility(View.GONE);
							rowLoadingContainer.setVisibility(View.VISIBLE);
							friendSearch.setEnabled(false);
							activity.getFriendsController().onAcceptFriend(v.getTag().toString());
						}
					});

					// Set up the reject button.
					ButtonFloatSmall btnRequestReject = (ButtonFloatSmall) v.findViewById(R.id.row_friend_reject);
					btnRequestReject.setTag(activity.getFriendsController().getRequestList().get(i));
					btnRequestReject.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							friendsContainer.setEnabled(false);
							friendRequestContainer.setVisibility(View.GONE);
							rowLoadingContainer.setVisibility(View.VISIBLE);
							friendSearch.setEnabled(false);
							activity.getFriendsController().onRejectFriend(v.getTag().toString());
						}
					});

					friendRequestContainer.setVisibility(View.VISIBLE);
					friendsContainer.addView(v);
				}

				// Set up the pending list..
				for (int i = 0; i < activity.getFriendsController().getPendingList().size(); i++) {
					View v = inflater.inflate(R.layout.row_friend, null, false);
					v.setLayoutParams(layoutParams);
					RelativeLayout friendPendingContainer = (RelativeLayout) v.findViewById(R.id.row_friend_pending_container);

					// Set up the pending text.
					TextView rowPendingText = (TextView) v.findViewById(R.id.row_friend_pending_txt);
					rowPendingText.setTypeface(font);

					// Set up the display name.
					TextView rowDisplayName = (TextView) v.findViewById(R.id.row_display_name);
					rowDisplayName.setTypeface(font);
					rowDisplayName.setText(activity.getFriendsController().getPendingList().get(i));

					// Set up the pending button.
					ButtonFloatSmall btnPending = (ButtonFloatSmall) v.findViewById(R.id.row_friend_pending);
					btnPending.setTag(activity.getFriendsController().getPendingList().get(i));
					btnPending.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// DO NOTHING
						}
					});

					friendPendingContainer.setVisibility(View.VISIBLE);
					friendsContainer.addView(v);
				}

				// Set up friend list..
				for (int i = 0; i < activity.getFriendsController().getFriendList().size(); i++) {
					View v = inflater.inflate(R.layout.row_friend, null, false);
					v.setLayoutParams(layoutParams);
					RelativeLayout friendContainer = (RelativeLayout) v.findViewById(R.id.row_friend_added_container);

					// Set up the accepted text.
					TextView rowAddedText = (TextView) v.findViewById(R.id.row_friend_added_txt);
					rowAddedText.setTypeface(font);

					// Set up the display name.
					TextView rowDisplayName = (TextView) v.findViewById(R.id.row_display_name);
					rowDisplayName.setTypeface(font);
					rowDisplayName.setText(activity.getFriendsController().getFriendList().get(i));

					// Set up the added button.
					ButtonFloatSmall btnFriendAdded = (ButtonFloatSmall) v.findViewById(R.id.row_friend_added);
					btnFriendAdded.setTag(activity.getFriendsController().getFriendList().get(i));
					btnFriendAdded.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// DO NOTHING
						}
					});

					friendContainer.setVisibility(View.VISIBLE);
					friendsContainer.addView(v);
				}
			} else
				emptyContainer.setVisibility(View.VISIBLE);

			friendsContainer.setEnabled(true);
			friendSearch.setEnabled(true);

			if(loadingContainer.getVisibility() == View.VISIBLE)
				loadingContainer.setVisibility(View.GONE);

			activity.getFriendsController().doChangeState(Constants.STATE_FRIENDS);
		}

		// If user accept a request.
		if(currentState.equals(Constants.STATE_FRIENDS_ACCEPTING) && state.equals(Constants.STATE_FRIENDS_ACCEPT_OK)) {
			// Refresh the friends list
			activity.getFriendsController().doChangeState(Constants.STATE_FRIENDS);
			activity.getFriendsController().onFriendList();
		}

		// If user reject a request.
		if(currentState.equals(Constants.STATE_FRIENDS_REJECTING) && state.equals(Constants.STATE_FRIENDS_REJECT_OK)) {
			// Refresh the friends list
			activity.getFriendsController().doChangeState(Constants.STATE_FRIENDS);
			activity.getFriendsController().onFriendList();
		}

		activity.getFriendsController().setCurrentState(state);
	}
}
