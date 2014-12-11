package com.datein.date_in.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Yiming on 8/12/2014.
 */
public class CustomView extends RelativeLayout {

	final static String MATERIALDESIGNXML = "http://schemas.android.com/apk/res-auto";
	final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

	final int disabledBackgroundColor = Color.parseColor("#E2E2E2");
	public int beforeBackground;

	// Indicate if user touched this view the last time
	public boolean isLastTouch = false;

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(enabled)
			setBackgroundColor(beforeBackground);
		else
			setBackgroundColor(disabledBackgroundColor);
	}
}
