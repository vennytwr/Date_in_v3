package com.datein.date_in.calendar;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;
import com.datein.date_in.StateChangeListener;
import com.datein.date_in.log.Logger;

import java.util.ArrayList;

public class CalendarFragment extends Fragment implements StateChangeListener {

	private static final String TAG = "CalendarFragment";

	private DateInActivity activity;
	private Typeface font;
	private TextView currentSelected;
	private RelativeLayout loadingContainer;
	private CalendarPagerAdapter calendarAdapter;

	public static CalendarFragment newInstance() {
		return new CalendarFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View resView = inflater.inflate(R.layout.fragment_calendar, container, false);

		activity = (DateInActivity) getActivity();

		font = Typeface.createFromAsset(activity.getAssets(), "Roboto-Regular.ttf");
		final TextView calendarHeader = (TextView) resView.findViewById(R.id.calendarHeader);
		TextView calendarSubHeader1 = (TextView) resView.findViewById(R.id.calendarSubHeader1);
		TextView calendarSubHeader2 = (TextView) resView.findViewById(R.id.calendarSubHeader2);
		TextView calendarSubHeader3 = (TextView) resView.findViewById(R.id.calendarSubHeader3);
		TextView calendarSubHeader4 = (TextView) resView.findViewById(R.id.calendarSubHeader4);
		TextView calendarSubHeader5 = (TextView) resView.findViewById(R.id.calendarSubHeader5);
		TextView calendarSubHeader6 = (TextView) resView.findViewById(R.id.calendarSubHeader6);
		TextView calendarSubHeader7 = (TextView) resView.findViewById(R.id.calendarSubHeader7);
		TextView calendarLoading = (TextView) resView.findViewById(R.id.txtCalendarLoading);
		calendarHeader.setTypeface(font);
		calendarSubHeader1.setTypeface(font);
		calendarSubHeader2.setTypeface(font);
		calendarSubHeader3.setTypeface(font);
		calendarSubHeader4.setTypeface(font);
		calendarSubHeader5.setTypeface(font);
		calendarSubHeader6.setTypeface(font);
		calendarSubHeader7.setTypeface(font);
		calendarLoading.setTypeface(font);

		loadingContainer = (RelativeLayout) resView.findViewById(R.id.loadingContainer);
		loadingContainer.setVisibility(View.GONE);

		ViewPager calendarViewPager = (ViewPager) resView.findViewById(R.id.calendarViewPager);
		calendarViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		calendarAdapter = new CalendarPagerAdapter();
		calendarViewPager.setAdapter(calendarAdapter);

		int thisMonth = activity.getCalendarBuilder().getThisMonth();
		calendarHeader.setText(activity.getCalendarBuilder().getMonthTitle(thisMonth));
		calendarViewPager.setCurrentItem(thisMonth);
//		calendarAdapter.setCurrentPosition(thisMonth);
//		calendarAdapter.setPreviousPosition(thisMonth);
		calendarViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				calendarHeader.setText(activity.getCalendarBuilder().getMonthTitle(position));
//				calendarAdapter.setCurrentPosition(position);
			}
		});

		activity.getCalendarController().onCalendar();
		return resView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (activity.getCalendarController().getListener() != this)
			activity.getCalendarController().setListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		activity.getCalendarController().setListener(null);
	}

	@Override
	public void onStateChanged(String state) {
		String currentState = activity.getCalendarController().getCurrentState();
		Logger.d(TAG, "Current State: " + currentState);
		Logger.d(TAG, "State: " + state);
		if (currentState.equals(state)) {
			Logger.d(TAG, "ERROR: BOTH STATE ARE THE SAME!");
			return;
		}

		if(state.equals(Constants.STATE_CALENDAR_REQUESTING))
			loadingContainer.setVisibility(View.VISIBLE);

		if(state.equals(Constants.STATE_CALENDAR)) {
			loadingContainer.setVisibility(View.GONE);
			calendarAdapter.notifyDataSetChanged();
		}


		activity.getCalendarController().setCurrentState(state);
	}

	public class CalendarPagerAdapter extends PagerAdapter {

//		private int currentPosition;
//		private int previousPosition;
		private ArrayList<CalendarMonth> calendarMonths;

		public CalendarPagerAdapter() {
			calendarMonths = activity.getCalendarBuilder().getMonthList();
		}

//		public void setCurrentPosition(int position) {
//			this.currentPosition = position;
//		}
//
//		public void setPreviousPosition(int position) {
//			this.previousPosition = position;
//		}

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

//					if (calendarGridItem.getTag(R.id.tag).equals(CalendarDay.PREVIOUS) ||
//							calendarGridItem.getTag(R.id.tag).equals(CalendarDay.NEXT))
//						return;
//
//					if (currentPosition == previousPosition) {
//						CalendarDay cDay = calendarMonths.get(currentPosition).getCalendarDayList().get(position);
//						// If you are not selecting the same date..
//						if (!currentSelected.getText().equals(calendarGridItem.getText())
//								&& calendarGridItem.getTag(R.id.tag).equals(CalendarDay.CURRENT)) {
//							cDay.setTag(CalendarDay.CURRENT_SELECTED);
//
//							cDay = calendarMonths.get(currentPosition).getCalendarDayList().get((int) currentSelected.getTag(R.id.position));
//							cDay.setTag(CalendarDay.CURRENT);
//						}
//						calendarDayAdapter.notifyDataSetChanged();
//						currentAdapter = calendarDayAdapter;
//						currentSelected = calendarGridItem;
//					} else if (currentPosition != previousPosition && calendarGridItem.getTag(R.id.tag).equals(CalendarDay.CURRENT)) {
//						CalendarDay cDay = calendarMonths.get(currentPosition).getCalendarDayList().get(position);
//						cDay.setTag(CalendarDay.CURRENT_SELECTED);
//
//						cDay = calendarMonths.get(previousPosition).getCalendarDayList().get((int) currentSelected.getTag(R.id.position));
//						cDay.setTag(CalendarDay.CURRENT);
//
//						calendarDayAdapter.notifyDataSetChanged();
//						currentAdapter.notifyDataSetChanged();
//						currentAdapter = calendarDayAdapter;
//						currentSelected = calendarGridItem;
//					}
//
//					previousPosition = currentPosition;
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

			if (currentSelected == null) {
				if (cDay.isToday())
					currentSelected = holder.calendarGridItem;
			}

			return v;
		}
	}
}
