package com.datein.date_in.events;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;
import com.datein.date_in.StateChangeListener;
import com.datein.date_in.calendar.CalendarDay;
import com.datein.date_in.calendar.CalendarMonth;
import com.datein.date_in.log.Logger;
import com.datein.date_in.views.material_edit_text.MaterialEditText;
import com.gc.materialdesign.views.CheckBox;

import java.util.ArrayList;


public class CreateEventFragment extends Fragment implements StateChangeListener {

	private final static String TAG = "CreateEventFragment";

	private DateInActivity activity;
	private ArrayList<String> friendsAdded;
	private LinearLayout createEventCalendar;
	private ScrollView createEventOptions;
	private ImageView createEventBtn;
	private ImageView createEventGetCommonTime;
	private Typeface font;
	private String selectedDate = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View resView = inflater.inflate(R.layout.fragment_create_event, container, false);

		activity = (DateInActivity) getActivity();

		friendsAdded = new ArrayList<>();

		ImageView createEventBack = (ImageView) resView.findViewById(R.id.createEventBack);
		createEventBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.closeCreateEvent();
			}
		});

		final MaterialEditText createEventName = (MaterialEditText) resView.findViewById(R.id.createEventName);
		createEventGetCommonTime = (ImageView) resView.findViewById(R.id.createEventGetCommonTime);
		createEventGetCommonTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(friendsAdded.size() > 0 && createEventName.getTag().toString().length() > 1)
					activity.getCreateEventController().onRequestCommonTime(friendsAdded);
			}
		});

		createEventBtn = (ImageView) resView.findViewById(R.id.createEventBtn);
		createEventBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(selectedDate != null)
					activity.getCreateEventController().onCreateEvent(selectedDate, friendsAdded);
			}
		});

		font = Typeface.createFromAsset(activity.getAssets(), "Roboto-Regular.ttf");
		TextView createEventHeader = (TextView) resView.findViewById(R.id.createEventHeader);
		final TextView calendarHeader = (TextView) resView.findViewById(R.id.calendarHeader);
		TextView calendarSubHeader1 = (TextView) resView.findViewById(R.id.calendarSubHeader1);
		TextView calendarSubHeader2 = (TextView) resView.findViewById(R.id.calendarSubHeader2);
		TextView calendarSubHeader3 = (TextView) resView.findViewById(R.id.calendarSubHeader3);
		TextView calendarSubHeader4 = (TextView) resView.findViewById(R.id.calendarSubHeader4);
		TextView calendarSubHeader5 = (TextView) resView.findViewById(R.id.calendarSubHeader5);
		TextView calendarSubHeader6 = (TextView) resView.findViewById(R.id.calendarSubHeader6);
		TextView calendarSubHeader7 = (TextView) resView.findViewById(R.id.calendarSubHeader7);
		calendarHeader.setTypeface(font);
		calendarSubHeader1.setTypeface(font);
		calendarSubHeader2.setTypeface(font);
		calendarSubHeader3.setTypeface(font);
		calendarSubHeader4.setTypeface(font);
		calendarSubHeader5.setTypeface(font);
		calendarSubHeader6.setTypeface(font);
		calendarSubHeader7.setTypeface(font);
		createEventHeader.setTypeface(font);

		LinearLayout createEventFriends = (LinearLayout) resView.findViewById(R.id.createEventFriends);
		for(int i = 0; i < activity.getFriendsController().getFriendList().size(); i++) {
			View row = inflater.inflate(R.layout.row_event_friend, null, false);

			final TextView rowName = (TextView) row.findViewById(R.id.row_event_friend_name);
			rowName.setText(activity.getFriendsController().getFriendList().get(i));

			CheckBox rowCheck = (CheckBox) row.findViewById(R.id.row_event_checkbox);
			rowCheck.setOncheckListener(new CheckBox.OnCheckListener() {
				@Override
				public void onCheck(boolean check) {
					if(check)
						friendsAdded.add(rowName.getText().toString());
					else {
						for(int i = 0; i < friendsAdded.size(); i++) {
							if(friendsAdded.get(i).equals(rowName.getText().toString())) {
								friendsAdded.remove(i);
								break;
							}
						}
					}
				}
			});

			createEventFriends.addView(row);
		}

		createEventCalendar = (LinearLayout) resView.findViewById(R.id.createEventShowCalendar);
		createEventOptions = (ScrollView) resView.findViewById(R.id.createEventShowOptions);

		ViewPager createEventCalendarViewPager = (ViewPager) resView.findViewById(R.id.editCalendarViewPager);
		createEventCalendarViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		final CreateEventCalendarPagerAdapter createEventCalendarAdapter = new CreateEventCalendarPagerAdapter();
		createEventCalendarViewPager.setAdapter(createEventCalendarAdapter);

		int thisMonth = activity.getCalendarBuilder().getThisMonth();
		calendarHeader.setText(activity.getCalendarBuilder().getMonthTitle(thisMonth));
		createEventCalendarViewPager.setCurrentItem(thisMonth);
		createEventCalendarAdapter.setCurrentPosition(thisMonth);
		createEventCalendarViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				calendarHeader.setText(activity.getCalendarBuilder().getMonthTitle(position));
				createEventCalendarAdapter.setCurrentPosition(position);
			}
		});

		return resView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (activity.getCreateEventController().getListener() != this)
			activity.getCreateEventController().setListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		activity.getCreateEventController().setListener(null);
	}

	@Override
	public void onStateChanged(String state) {
		String currentState = activity.getCreateEventController().getCurrentState();
		Logger.d(TAG, "Current State: " + currentState);
		Logger.d(TAG, "State: " + state);
		if (currentState.equals(state)) {
			Logger.d(TAG, "ERROR: BOTH STATE ARE THE SAME!");
			return;
		}

		if(state.equals(Constants.STATE_EVENTS_REQUEST_COMMON_TIME))
			createEventOptions.setEnabled(false);

		if(currentState.equals(Constants.STATE_EVENTS_REQUEST_COMMON_TIME) && state.equals(Constants.STATE_EVENTS_REQUEST_COMMON_TIME_OK)) {
			createEventOptions.setEnabled(true);
			createEventOptions.setVisibility(View.GONE);
			createEventCalendar.setVisibility(View.VISIBLE);
			createEventBtn.setVisibility(View.VISIBLE);
			createEventGetCommonTime.setVisibility(View.GONE);
		}

		activity.getCreateEventController().setCurrentState(state);
	}

	public class CreateEventCalendarPagerAdapter extends PagerAdapter {

		private int currentPosition;
		private ArrayList<CalendarMonth> calendarMonths;

		public CreateEventCalendarPagerAdapter() {
			calendarMonths = activity.getCalendarBuilder().getMonthList();
		}

		public void setCurrentPosition(int position) {
			this.currentPosition = position;
		}

		public View getMonthView(int position) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.calendar_month_view, null, false);
			GridView calendarGridView = (GridView) v.findViewById(R.id.calendarGridView);
			final CalendarDayAdapter calendarDayAdapter = new CalendarDayAdapter(calendarMonths.get(position).getCalendarDayList());
			calendarGridView.setAdapter(calendarDayAdapter);

			calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					TextView calendarGridItem = (TextView) view.findViewById(R.id.calendarGridItem);

					if (calendarGridItem.getTag(R.id.state).equals(CalendarDay.PREVIOUS) ||
							calendarGridItem.getTag(R.id.state).equals(CalendarDay.NEXT))
						return;

					if((calendarGridItem.getTag(R.id.state).equals(CalendarDay.CURRENT) ||
							calendarGridItem.getTag(R.id.state).equals(CalendarDay.CURRENT_SELECTED)) &&
							calendarGridItem.getTag(R.id.tag).equals(CalendarDay.EVENT_BLOCK)) {
						CalendarDay cDay = calendarMonths.get(currentPosition).getCalendarDayList().get(position);
						if(cDay.isToday())
							cDay.setTag(CalendarDay.CURRENT_SELECTED);
						else
							cDay.setTag(CalendarDay.CURRENT);
						selectedDate = null;
						calendarDayAdapter.notifyDataSetChanged();
					}
					// If user press a normal one, change to block..
					else if (calendarGridItem.getTag(R.id.state).equals(CalendarDay.CURRENT) ||
							calendarGridItem.getTag(R.id.state).equals(CalendarDay.CURRENT_SELECTED)) {
						CalendarDay cDay = calendarMonths.get(currentPosition).getCalendarDayList().get(position);
						cDay.setTag(CalendarDay.EVENT_BLOCK);
						selectedDate = String.valueOf(calendarGridItem.getTag(R.id.tagDate));
						calendarDayAdapter.notifyDataSetChanged();
					}

				}
			});

			return v;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return calendarMonths.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = getMonthView(position);
			container.addView(v);
			return v;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	public class CalendarDayAdapter extends BaseAdapter {

		private ArrayList<CalendarDay> calendarDays;

		public CalendarDayAdapter(ArrayList<CalendarDay> calendarDays) {
			this.calendarDays = calendarDays;
		}

		@Override
		public int getCount() {
			return calendarDays.size();
		}

		@Override
		public Object getItem(int position) {
			return calendarDays.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		class ViewHolder {
			public TextView calendarGridItem;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.calendar_day_view, null);
				holder = new ViewHolder();

				holder.calendarGridItem = (TextView) v.findViewById(R.id.calendarGridItem);
				holder.calendarGridItem.setTypeface(font);
				LinearLayout.LayoutParams layoutParams =
						new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (60 * activity.getDensity()));
				holder.calendarGridItem.setLayoutParams(layoutParams);
				holder.calendarGridItem.setGravity(Gravity.CENTER);
				v.setTag(holder);
			} else
				holder = (ViewHolder) v.getTag();

			CalendarDay cDay = calendarDays.get(position);
			holder.calendarGridItem.setText(cDay.getText());
			holder.calendarGridItem.setBackgroundResource(cDay.getBackgroundResource());
			holder.calendarGridItem.setTextColor(cDay.getTextColor());
			holder.calendarGridItem.setTag(R.id.tag, cDay.getTag());
			holder.calendarGridItem.setTag(R.id.tagDate, cDay.getTagDate());
			holder.calendarGridItem.setTag(R.id.position, cDay.getPosition());
			holder.calendarGridItem.setTag(R.id.state, cDay.getState());

			return v;
		}
	}
}
