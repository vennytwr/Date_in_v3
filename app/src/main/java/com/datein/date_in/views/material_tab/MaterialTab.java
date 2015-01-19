package com.datein.date_in.views.material_tab;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.datein.date_in.R;

import java.util.Locale;

public class MaterialTab implements View.OnTouchListener, Animator.AnimatorListener {

	private final static int REVEAL_DURATION = 400;
	private final static int HIDE_DURATION = 500;

	private View completeView;
	private ImageView icon;
	private TextView text;
	private RevealColorView background;
	private ImageView selector;

	private Resources res;
	private MaterialTabListener listener;
	private Drawable iconDrawable;

	private int textColor;
	private int iconColor;
	private int primaryColor;
	private int accentColor;

	private boolean active;
	private int position;
	private boolean hasIcon;
	private float density;
	private Point lastTouchPoint;

	public MaterialTab(Context ctx, boolean hasIcon) {
		this.hasIcon = hasIcon;
		density = ctx.getResources().getDisplayMetrics().density;
		res = ctx.getResources();
		Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), "Roboto-Regular.ttf");

		// If there is no icon
		if(!hasIcon) {
			completeView = LayoutInflater.from(ctx).inflate(R.layout.material_tab, null);
			text = (TextView) completeView.findViewById(R.id.tabText);
			text.setTypeface(typeface);
		}
		else {
			completeView = LayoutInflater.from(ctx).inflate(R.layout.material_tab_icon, null);
			icon = (ImageView) completeView.findViewById(R.id.icon);
		}

		background = (RevealColorView) completeView.findViewById(R.id.reveal);
		selector = (ImageView) completeView.findViewById(R.id.tabSelector);

		// Set the listener
		completeView.setOnTouchListener(this);

		active = false;
		textColor = Color.WHITE;
		iconColor = Color.WHITE;
	}

	public void setAccentColor(int color) {
		this.accentColor = color;
		this.textColor = color;
		this.iconColor = color;
	}

	public void setPrimaryColor(int color) {
		this.primaryColor = color;
		background.setBackgroundColor(color);
	}

	public void setTextColor(int color) {
		this.textColor = color;
		if(text != null) {
			this.text.setTextColor(color);
		}
	}

	public void setIconColor(int color) {
		this.iconColor = color;
		if(icon != null) {
			this.icon.setColorFilter(color);
		}
	}

	public MaterialTab setText(CharSequence text) {
		this.text.setText(text.toString().toUpperCase(Locale.US));
		return this;
	}

	public MaterialTab setIcon(Drawable icon) {
		this.iconDrawable = icon;
		this.icon.setImageDrawable(icon);
		this.setIconColor(this.iconColor);
		return this;
	}

	public void disableTab() {
		// Set 60% alpha to text color
		if(text != null)
			this.text.setTextColor(Color.argb(0x99, Color.red(textColor), Color.green(textColor), Color.blue(textColor)));

		// Set 60% alpha to icon color
		if(icon != null)
			this.icon.setImageAlpha(0x99);

		// Set transparent to the selector view
		this.selector.setBackgroundColor(res.getColor(android.R.color.transparent));

		active = false;

		if(listener != null)
			listener.onTabUnselected(this);
	}

	public void activateTab() {
		// Set full color
		if(text != null)
			this.text.setTextColor(textColor);

		// Set 100% alpha to icon
		if(icon != null)
			this.icon.setImageAlpha(0xFF);

		// Set accent color to selector view
		this.selector.setBackgroundColor(accentColor);

		active = true;
	}

	public boolean isSelected() {
		return active;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		lastTouchPoint = new Point();
		lastTouchPoint.x = (int) event.getX();
		lastTouchPoint.y = (int) event.getY();

		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			// Do nothing
			return true;
		}

		if(event.getAction() == MotionEvent.ACTION_UP) {
			// Set the background color
			this.background.reveal(lastTouchPoint.x, lastTouchPoint.y,
					Color.argb(0x80, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor)),
					0, REVEAL_DURATION, this);

			// Set the click
			if(listener != null) {
				if(active) {
					// If the tab is active when the user click on it it will be reselect
					listener.onTabReselected(this);
				}
				else {
					listener.onTabSelected(this);
				}
			}

			// If the tab is not activated, it will be active
			if(!active)
				activateTab();

			return true;
		}
		return false;
	}

	public View getView() {
		return completeView;
	}

	public MaterialTab setTabListener(MaterialTabListener listener) {
		this.listener = listener;
		return this;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	private int getTextLength() {
		String textString = text.getText().toString();
		Rect bounds = new Rect();
		Paint textPaint = text.getPaint();
		textPaint.getTextBounds(textString, 0, textString.length(), bounds);
		return bounds.width();
	}

	public int getIconWidth() {
		return (int) (density * 24);
	}

	public int getTabMinWidth() {
		if(hasIcon)
			return getIconWidth();
		else
			return getTextLength();
	}

	// AnimatorListener methods
	@Override
	public void onAnimationStart(Animator animation) {

	}

	@Override
	public void onAnimationEnd(Animator animation) {
		this.background.reveal(lastTouchPoint.x, lastTouchPoint.y, primaryColor, 0, HIDE_DURATION, null);
	}

	@Override
	public void onAnimationCancel(Animator animation) {

	}

	@Override
	public void onAnimationRepeat(Animator animation) {

	}
}
