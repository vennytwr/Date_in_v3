package com.datein.date_in.views.material_tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.datein.date_in.R;

import java.util.LinkedList;
import java.util.List;

public class MaterialTabHost extends HorizontalScrollView {

	private int primaryColor;
	private int accentColor;
	private int textColor;
	private int iconColor;
	private List<MaterialTab> tabs;
	private List<Integer> tabsWidth;
	private boolean hasIcons;
	private boolean isTablet;
	private float density;
	private boolean scrollable;

	private LinearLayout layout;

	public MaterialTabHost(Context context) {
		this(context, null);
	}

	public MaterialTabHost(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MaterialTabHost(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		super.setOverScrollMode(OVER_SCROLL_NEVER);

		layout = new LinearLayout(context);
		addView(layout);

		// Get primary and accent color from AppCompat theme
		//Resources.Theme theme = context.getTheme();
		//TypedValue typedValue = new TypedValue();
		//theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
		primaryColor = getResources().getColor(android.R.color.holo_blue_light);
		//theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
		accentColor = getResources().getColor(R.color.background_blue);
		iconColor = Color.WHITE;
		textColor = Color.WHITE;

		// Get attributes
		if(attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaterialTabHost, 0, 0);
			try {
				hasIcons = a.getBoolean(R.styleable.MaterialTabHost_hasIcons, false);
			} finally {
				a.recycle();
			}
		} else {
			hasIcons = false;
		}

		isInEditMode();
		density = getResources().getDisplayMetrics().density;
		scrollable = false;
		isTablet = getResources().getBoolean(R.bool.isTablet);

		// Initialize tabs list
		tabs = new LinkedList<>();
		tabsWidth = new LinkedList<>();

		// Set background color
		setBackgroundColor(primaryColor);
	}

	public void setPrimaryColor(int color) {
		this.primaryColor = color;
		for(MaterialTab tab : tabs) {
			tab.setPrimaryColor(color);
		}
	}

	public void setAccentColor(int color) {
		this.accentColor = color;
		for(MaterialTab tab : tabs) {
			tab.setAccentColor(color);
		}
	}

	public void setTextColor(int color) {
		this.textColor = color;
		for(MaterialTab tab : tabs) {
			tab.setTextColor(color);
		}
	}

	public void setIconColor(int color) {
		this.iconColor = color;
		for(MaterialTab tab : tabs) {
			tab.setIconColor(color);
		}
	}

	public void addTab(MaterialTab tab) {
		// Add properties to tab;
		tab.setAccentColor(accentColor);
		tab.setPrimaryColor(primaryColor);
		tab.setTextColor(textColor);
		tab.setIconColor(iconColor);
		tab.setPosition(tabs.size());

		// Insert new tab in list
		tabs.add(tab);

		if(tabs.size() == 4) {
			// Switch tabs to scrollable before its draw
			scrollable = true;
			if(isTablet)
				throw new RuntimeException("Tablet scrollable tabs are currently not supported");
		}
	}

	public MaterialTab newTab() {
		return new MaterialTab(getContext(), hasIcons);
	}

	public void setSelectedNavigationItem(int position) {
		if(position < 0 || position > tabs.size()) {
			throw new RuntimeException("Index overflow");
		} else {
			// Tab at position will select, other will deselect
			for(int i = 0; i < tabs.size(); i++) {
				MaterialTab tab = tabs.get(i);
				if(i == position && !tab.isSelected())
					tab.activateTab();
				else
					tab.disableTab();
			}

			// Move the tab if it is slidable
			if(scrollable) {
				int totalWidth = 0;
				for(int i = 0; i < position + 1; i++) {
					totalWidth += tabsWidth.get(i);
				}
				totalWidth -= (int) (60 * density);
				smoothScrollTo(totalWidth, 0);
			}
		}
	}

	@Override
	public void removeAllViews() {
		for(int i = 0; i < tabs.size(); i++) {
			tabs.remove(i);
		}
		layout.removeAllViews();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		layout.removeAllViews();

		if(!tabs.isEmpty()) {
			if(!scrollable) {
				int tabWidth = getWidth() / tabs.size();

				// Set params for resizing tabs width
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tabWidth, LayoutParams.MATCH_PARENT);
				for(MaterialTab t : tabs) {
					layout.addView(t.getView(), params);
				}
			} else {
				for(int i = 0; i < tabs.size(); i++) {
					LinearLayout.LayoutParams params;
					MaterialTab tab = tabs.get(i);

					int tabWidth = (int) (tab.getTabMinWidth() + (24 * density)); // 12dp + text/icon width + 12dp
					//params = new LinearLayout.LayoutParams(tabWidth, LayoutParams.MATCH_PARENT);

					if(i == 0) {
						// First tab
						View view = new View(layout.getContext());
						view.setMinimumWidth((int) (60 * density));
						layout.addView(view);
						tabsWidth.add((int) (60 * density));
					}

					params = new LinearLayout.LayoutParams(tabWidth, LayoutParams.MATCH_PARENT);
					layout.addView(tab.getView(), params);
					tabsWidth.add(tabWidth);

					if(i == tabs.size() - 1) {
						// Last tab
						View view = new View(layout.getContext());
						view.setMinimumWidth((int) (60 * density));
						layout.addView(view);
						tabsWidth.add((int) (60 * density));
					}
				}
			}
			setSelectedNavigationItem(0);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// Setting measured width + 48 dp height
		setMeasuredDimension(getMeasuredWidth(), (int) (48 * density));
	}
}
