package com.datein.date_in.calendar;

import android.os.Bundle;

import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;
import com.datein.date_in.log.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CalendarBuilder {

	private DateInActivity activity;
	private int thisYear;
	private int thisMonth;
	private ArrayList<CalendarMonth> cMonthList;
	private ArrayList<String> newBlockDates;
	private ArrayList<String> newUnblockDates;
	private static final int minYear = 2014, maxYear = 2016;
	private final int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	private final String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST",
			"SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

	public CalendarBuilder(DateInActivity activity) {
		cMonthList = new ArrayList<>();
		newBlockDates = new ArrayList<>();
		newUnblockDates = new ArrayList<>();
		this.activity = activity;
		buildCalendar(activity);
	}

	public void buildCalendar(DateInActivity activity) {
		cMonthList.clear();
		Calendar cal = Calendar.getInstance(Locale.getDefault());

		thisYear = cal.get(Calendar.YEAR);
		thisMonth = cal.get(Calendar.MONTH);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

		for (int year = minYear; year <= maxYear; year++) {
			for (int month = 0; month <= 11; month++) {
				GregorianCalendar gCal = new GregorianCalendar(year, month, dayOfMonth);

				CalendarMonth cMonth = new CalendarMonth();
				cMonth.setCurrentYear(year);
				cMonth.setCurrentMonth(month);

				int daysInPrevMonth, daysInThisMonth;
				if (month == 0)
					daysInPrevMonth = getDaysInMonth(11);
				else if (month == 11)
					daysInPrevMonth = getDaysInMonth(month - 1);
				else
					daysInPrevMonth = getDaysInMonth(month - 1);
				daysInThisMonth = getDaysInMonth(month);

				boolean isLeapYear = gCal.isLeapYear(year);
				if (isLeapYear && month == 1)
					daysInThisMonth++;
				else if (isLeapYear && month == 2) {
					daysInPrevMonth++;
				}

				cMonth.setDaysInThisMonth(daysInThisMonth);
				cMonth.setDaysInPrevMonth(daysInPrevMonth);
				cMonth.setNameOfMonth(getNameOfMonth(month));

				cMonth.init(activity, dayOfMonth, thisYear, thisMonth);
				cMonthList.add(cMonth);
			}
		}
	}

	private String getNameOfMonth(int position) {
		return months[position];
	}

	private int getDaysInMonth(int position) {
		return daysInMonth[position];
	}

	public int getThisMonth() {
		return (((thisYear - minYear) * 12) + thisMonth);
	}

	public String getMonthTitle(int position) {
		String monthTitle = cMonthList.get(position).getNameOfMonth();
		String yearTitle = cMonthList.get(position).getYearString();
		return monthTitle + " " + yearTitle;
	}

	public ArrayList<CalendarMonth> getMonthList() {
		return cMonthList;
	}

	public void addNewBlockDate(String blockDate) {
		newBlockDates.add(blockDate);
	}

	public void addNewUnblockDates(String unblockDate) {
		newUnblockDates.add(unblockDate);
	}

	public void removeNewUnblockDate(String unblockDate) {
		for(int i = 0; i < newUnblockDates.size(); i++) {
			if(newUnblockDates.get(i).equals(unblockDate)) {
				newUnblockDates.remove(i);
				break;
			}
		}
	}

	public void removeNewBlockDate(String unblockedDate) {
		for(int i = 0; i < newBlockDates.size(); i++) {
			if(newBlockDates.get(i).equals(unblockedDate)) {
				newBlockDates.remove(i);
				break;
			}
		}
	}

	public ArrayList<String> getNewBlockDates() {
		return newBlockDates;
	}

	public ArrayList<String> getNewUnblockDates() {
		return newUnblockDates;
	}

	public void updateCalendar(Bundle data) {
		buildCalendar(activity);

		if(data.getString("calendarBlockIndex") != null) {
			int calendarBlockIndex = Integer.valueOf(data.getString("calendarBlockIndex"));
			ArrayList<String> blockedDates = new ArrayList<>();

			for (int i = 0; i < calendarBlockIndex; i++)
				blockedDates.add(data.getString(String.valueOf(i)));

			for(int i = 0; i < blockedDates.size(); i++) {
				for (int j = 0; j < cMonthList.size(); j++) {
					for (int k = 0; k < cMonthList.get(j).getCalendarDayList().size(); k++) {
						CalendarDay cDay = cMonthList.get(j).getCalendarDayList().get(k);
						if (cDay.getTagDate().equals(blockedDates.get(i)) && (cDay.getTag().equals(CalendarDay.CURRENT) ||
								cDay.getTag().equals(CalendarDay.CURRENT_SELECTED))) {
							cDay.setTag(CalendarDay.BLOCK);
							cDay.setState(CalendarDay.BLOCK);
						}
					}
				}
			}
		}
	}
}
