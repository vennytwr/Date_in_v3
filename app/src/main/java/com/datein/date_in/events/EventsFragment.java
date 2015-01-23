package com.datein.date_in.events;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;
import com.gc.materialdesign.views.ButtonFloat;

public class EventsFragment extends Fragment {

	private static final String TAG = "FriendsFragment";

	private DateInActivity activity;

	public static EventsFragment newInstance() {
		return new EventsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View resView = inflater.inflate(R.layout.fragment_event, container, false);

		activity = (DateInActivity) getActivity();

		ButtonFloat eventAdd = (ButtonFloat) resView.findViewById(R.id.eventAdd);
		eventAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.openCreateEvent();
			}
		});

		return resView;
	}
}
