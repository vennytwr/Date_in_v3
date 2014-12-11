package com.datein.date_in.views.material_edit_text;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class Density {
	public static int dp2px(Context context, float dp) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
		return (int) px;
	}
}
