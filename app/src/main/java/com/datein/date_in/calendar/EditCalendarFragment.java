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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;

import java.util.ArrayList;

public class EditCalendarFragment extends Fragment {

	private static final String TAG = "EditCalendarFragment";

	private DateInActivity activity;
	private Typeface font;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View resView = inflater.inflate(R.layout.fragment_edit_calendar, container, false);

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
		TextView editCalendarHeader = (TextView) resView.findViewById(R.id.editCalendarHeader);
		calendarHeader.setTypeface(font);
		calendarSubHeader1.setTypeface(font);
		calendarSubHeader2.setTypeface(font);
		calendarSubHeader3.setTypeface(font);
		calendarSubHeader4.setTypeface(font);
		calendarSubHeader5.setTypeface(font);
		calendarSubHeader6.setTypeface(font);
		calendarSubHeader7.setTypeface(font);
		editCalendarHeader.setTypeface(font);

		ImageView editCalendarBack = (ImageView) resView.findViewById(R.id.editCalendarBack);
		editCalendarBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.closeEditCalendar();
			}
		});

		ImageView editCalendarSync = (ImageView) resView.findViewById(R.id.editCalendarSync);
		editCalendarSync.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.closeEditCalendar();
				activity.getCalendarController().onSync();
			}
		});

		ViewPager editCalendarViewPager = (ViewPager) resView.findViewById(R.id.editCalendarViewPager);
		editCalendarViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		final EditCalendarPagerAdapter editCalendarAdapter = new EditCalendarPagerAdapter();
		editCalendarViewPager.setAdapter(editCalendarAdapter);

		int thisMonth = activity.getCalendarBuilder().getThisMonth();
		calendarHeader.setText(activity.getCalendarBuilder().getMonthTitle(thisMonth));
		editCalendarViewPager.setCurrentItem(thisMonth);
		editCalendarAdapter.setCurrentPosition(thisMonth);
		editCalendarViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				calendarHeader.setText(activity.getCalendarBuilder().getMonthTitle(position));
				editCalendarAdapter.setCurrentPosition(position);
			}
		});

		return resView;
	}

	public class EditCalendarPagerAdapter extends PagerAdapter {

		private int currentPosition;
		private ArrayList<CalendarMonth> calendarMonths;

		public EditCalendarPagerAdapter() {
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

					if(calendarGridItem.getTag(R.id.state).equals(CalendarDay.BLOCK) &&
							calendarGridItem.getTag(R.id.tag).equals(CalendarDay.BLOCK)) {
						CalendarDay cDay = calendarMonths.get(currentPosition).getCalendarDayList().get(position);
						if(cDay.isToday())
							cDay.setTag(CalendarDay.CURRENT_SELECTED);
						else
						cDay.setTag(CalendarDay.CURRENT);
						activity.getCalendarBuilder().addNewUnblockDates((String) calendarGridItem.getTag(R.id.tagDate));
						calendarDayAdapter.notifyDataSetChanged();
					}
					else if(calendarGridItem.getTag(R.id.state).equals(CalendarDay.BLOCK) &&
							(calendarGridItem.getTag(R.id.tag).equals(CalendarDay.CURRENT) ||
							calendarGridItem.getTag(R.id.tag).equals(CalendarDay.CURRENT_SELECTED))) {
						CalendarDay cDay = calendarMonths.get(currentPosition).getCalendarDayList().get(position);
						cDay.setTag(CalendarDay.BLOCK);
						activity.getCalendarBuilder().removeNewUnblockDate((String) calendarGridItem.getTag(R.id.tagDate));
						calendarDayAdapter.notifyDataSetChanged();
					}
					// If user press a block one, change back to normal..
					else if((calendarGridItem.getTag(R.id.state).equals(CalendarDay.CURRENT) ||
							calendarGridItem.getTag(R.id.state).equals(CalendarDay.CURRENT_SELECTED)) &&
							calendarGridItem.getTag(R.id.tag).equals(CalendarDay.BLOCK)) {
						CalendarDay cDay = calendarMonths.get(currentPosition).getCalendarDayList().get(position);
						if(cDay.isToday())
							cDay.setTag(CalendarDay.CURRENT_SELECTED);
						else
							cDay.setTag(CalendarDay.CURRENT);
						activity.getCalendarBuilder().removeNewBlockDate((String) calendarGridItem.getTag(R.id.tagDate));
						calendarDayAdapter.notifyDataSetChanged();
					}
					// If user press a normal one, change to block..
					else if (calendarGridItem.getTag(R.id.state).equals(CalendarDay.CURRENT) ||
							calendarGridItem.getTag(R.id.state).equals(CalendarDay.CURRENT_SELECTED)) {
						CalendarDay cDay = calendarMonths.get(currentPosition).getCalendarDayList().get(position);
						cDay.setTag(CalendarDay.BLOCK);
						activity.getCalendarBuilder().addNewBlockDate((String) calendarGridItem.getTag(R.id.tagDate));
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
