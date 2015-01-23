package com.datein.date_in.calendar;


import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;

public class CalendarDay {

	public static final int PREVIOUS = 0;
	public static final int NEXT = 1;
	public static final int CURRENT = 2;
	public static final int CURRENT_SELECTED = 3;
	public static final int BLOCK = 4;
	public static final int EVENT_BLOCK = 5;

	private DateInActivity activity;
	private String tagDate;
	private String text;
	private Object tag;
	private int backgroundResource;
	private int textColor;
	private int position;
	private boolean isToday = false;
	private int state;

	public CalendarDay(DateInActivity activity, int day, int month, int year) {
		this.activity = activity;
		tagDate = String.valueOf(day) + "-" + String.valueOf(month) + "-" + String.valueOf(year);
	}

	public String getText() {
		return text;
	}

	public void setText(String s) {
		this.text = s;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object currentTag) {
		this.tag = currentTag;
		if(currentTag.equals(PREVIOUS) || currentTag.equals(NEXT)) {
			backgroundResource = 0;
			textColor = activity.getResources().getColor(android.R.color.white);
		} else if(currentTag.equals(CURRENT)) {
			backgroundResource = 0;
			textColor = activity.getResources().getColor(android.R.color.secondary_text_dark);
		} else if(currentTag.equals(CURRENT_SELECTED)) {
			backgroundResource = R.drawable.calendar_day_view_selected;
			textColor = activity.getResources().getColor(android.R.color.secondary_text_dark);
		} else if(currentTag.equals(BLOCK)) {
			backgroundResource = R.drawable.calendar_day_view_blocked;
			textColor = activity.getResources().getColor(android.R.color.secondary_text_dark);
		} else if(currentTag.equals(EVENT_BLOCK)) {
			backgroundResource = R.drawable.calendar_day_view_event_block;
			textColor = activity.getResources().getColor(android.R.color.secondary_text_dark);
		}
	}

	public int getTextColor() {
		return textColor;
	}

	public int getBackgroundResource() {
		return backgroundResource;
	}

	public String getTagDate() {
		return tagDate;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isToday() {
		return isToday;
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
