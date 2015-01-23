package com.datein.date_in.main;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;
import com.datein.date_in.StateChangeListener;
import com.datein.date_in.calendar.CalendarFragment;
import com.datein.date_in.events.EventsFragment;
import com.datein.date_in.friends.FriendsFragment;
import com.datein.date_in.log.Logger;
import com.datein.date_in.views.material_drawer.DrawerArrowDrawable;
import com.datein.date_in.views.material_tab.MaterialTab;
import com.datein.date_in.views.material_tab.MaterialTabHost;
import com.datein.date_in.views.material_tab.MaterialTabListener;
import com.datein.date_in.views.material_tab.NoSwipeViewPager;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonFloatSmall;

import java.util.Locale;

import static android.view.Gravity.START;

public class MainFragment extends Fragment implements StateChangeListener, MaterialTabListener {

	private static final String TAG = "MainFragment";

	private DateInActivity activity;

	// Drawer related.
	private DrawerArrowDrawable drawerArrowDrawable;
	private DrawerLayout drawer;
	private float offset;
	private boolean flipped;
	private Typeface font;

	private MaterialTabHost tabHost;
	private NoSwipeViewPager pager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View resView = inflater.inflate(R.layout.fragment_main, container, false);

		activity = (DateInActivity) getActivity();

		// Set font style.
		font = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");

		drawer = (DrawerLayout) resView.findViewById(R.id.drawer_layout);
		drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				offset = slideOffset;

				// Sometimes slideOffset ends up so close to but not quite 1 or 0.
				if (slideOffset >= .995) {
					flipped = true;
					drawerArrowDrawable.setFlip(true);
				} else if (slideOffset <= .005) {
					flipped = false;
					drawerArrowDrawable.setFlip(false);
				}

				drawerArrowDrawable.setParameter(offset);
			}
		});

		ImageView drawerIndicator = (ImageView) resView.findViewById(R.id.drawer_indicator);
		drawerArrowDrawable = new DrawerArrowDrawable(getResources());
		drawerArrowDrawable.setStrokeColor(getResources().getColor(android.R.color.white));
		drawerIndicator.setImageDrawable(drawerArrowDrawable);
		drawerIndicator.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (drawer.isDrawerVisible(START)) {
					drawer.closeDrawer(START);
				} else {
					drawer.openDrawer(START);
				}
			}
		});

		TextView drawerEditCalendarText = (TextView) resView.findViewById(R.id.drawer_edit_calendar_txt);
		drawerEditCalendarText.setTypeface(font);
		ButtonFloatSmall drawerEditCalendarBtn = (ButtonFloatSmall) resView.findViewById(R.id.drawer_edit_calendar_btn);
		drawerEditCalendarBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.openEditCalendar();
			}
		});

		tabHost = (MaterialTabHost) resView.findViewById(R.id.tabHost);
		pager = (NoSwipeViewPager) resView.findViewById(R.id.pager);

		ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(3);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				switch (position) {
					case 0:
						activity.getMainController().doChangeState(Constants.STATE_CALENDAR);
						break;
					case 1:
						activity.getMainController().doChangeState(Constants.STATE_FRIENDS);
						break;
					case 2:
						activity.getMainController().doChangeState(Constants.STATE_EVENTS);
						break;
				}
				tabHost.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < adapter.getCount(); i++) {
			tabHost.addTab(tabHost.newTab().setText(adapter.getPageTitle(i)).setTabListener(this));
		}

		return  resView;
	}

	@Override
	public void onTabSelected(MaterialTab tab) {
		pager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(MaterialTab tab) {

	}

	@Override
	public void onTabUnselected(MaterialTab tab) {

	}

	@Override
	public void onResume() {
		super.onResume();
		if (activity.getMainController().getListener() != this)
			activity.getMainController().setListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		activity.getMainController().setListener(null);
	}

	@Override
	public void onStateChanged(String state) {
		String currentState = activity.getMainController().getCurrentState();
		Logger.d(TAG, "Current State: " + currentState);
		Logger.d(TAG, "State: " + state);
		if (currentState.equals(state)) {
			Logger.d(TAG, "ERROR: BOTH STATE ARE THE SAME!");
			return;
		}

		if(state.equals(Constants.STATE_MAIN_DRAWER_OPEN)) {
			drawer.closeDrawer(START);
		}

		activity.getMainController().setCurrentState(state);
	}

	private class ViewPagerAdapter extends FragmentStatePagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return CalendarFragment.newInstance();
				case 1:
					return FriendsFragment.newInstance();
				case 2:
					return EventsFragment.newInstance();
				default:
					return null;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_section1).toUpperCase(l);
				case 1:
					return getString(R.string.title_section2).toUpperCase(l);
				case 2:
					return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
}
