package com.datein.date_in.calendar;


import android.view.View;
import android.widget.LinearLayout;

import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class CalendarMonth {

	private int currentYear, currentMonth, daysInThisMonth, daysInPrevMonth;
	private String nameOfMonth;
	private ArrayList<CalendarDay> cDayList;
	private View monthView = null;
	private View editMonthView = null;

	public void init(DateInActivity activity, int dayOfMonth, int thisYear, int thisMonth) {
		GregorianCalendar gCalprev = new GregorianCalendar(currentYear, currentMonth, 1);
		int prevMonthSpace = gCalprev.get(Calendar.DAY_OF_WEEK) - 1;
		int nextMonthSpace = 42 - prevMonthSpace - daysInThisMonth;
		int currentMonthSpace = daysInThisMonth;
		cDayList = new ArrayList<>();

		int j = 0;
		for (int i = 1; i <= prevMonthSpace; i++) {
			CalendarDay cDay = new CalendarDay(activity, i, currentMonth, currentYear);
			cDay.setText(String.valueOf(daysInPrevMonth - prevMonthSpace + i));
			cDay.setTag(CalendarDay.PREVIOUS);
			cDay.setState(CalendarDay.PREVIOUS);
			cDay.setPosition(j++);
			cDayList.add(cDay);
		}

		for (int i = 1; i <= currentMonthSpace; i++) {
			CalendarDay cDay = new CalendarDay(activity, i, currentMonth, currentYear);
			cDay.setText(String.valueOf(i));
			cDay.setPosition(j++);
			if (i == dayOfMonth && currentYear == thisYear && currentMonth == thisMonth) {
				cDay.setToday(true);
				cDay.setTag(CalendarDay.CURRENT_SELECTED);
				cDay.setState(CalendarDay.CURRENT_SELECTED);
			} else {
				cDay.setTag(CalendarDay.CURRENT);
				cDay.setState(CalendarDay.CURRENT);
			}
			cDayList.add(cDay);
		}

		for (int i = 1; i <= nextMonthSpace; i++) {
			CalendarDay cDay = new CalendarDay(activity, i, currentMonth, currentYear);
			cDay.setText(String.valueOf(i));
			cDay.setTag(CalendarDay.NEXT);
			cDay.setState(CalendarDay.NEXT);
			cDay.setPosition(j++);
			cDayList.add(cDay);
		}
	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public void setCurrentMonth(int currentMonth) {
		this.currentMonth = currentMonth;
	}

	public void setDaysInThisMonth(int daysInThisMonth) {
		this.daysInThisMonth = daysInThisMonth;
	}

	public void setDaysInPrevMonth(int daysInPrevMonth) {
		this.daysInPrevMonth = daysInPrevMonth;
	}

	public void setNameOfMonth(String nameOfMonth) {
		this.nameOfMonth = nameOfMonth;
	}

	public ArrayList<CalendarDay> getCalendarDayList() {
		return cDayList;
	}

	public String getNameOfMonth() {
		return nameOfMonth;
	}

	public String getYearString() {
		return String.valueOf(currentYear);
	}

	public View getMonthView() {
		return monthView;
	}

	public void setMonthView(View cMonthView) {
		this.monthView = cMonthView;
	}

	public View getEditMonthView() {
		return editMonthView;
	}

	public void setEditMonthView(View editMonthView) {
		this.editMonthView = editMonthView;
	}
}
