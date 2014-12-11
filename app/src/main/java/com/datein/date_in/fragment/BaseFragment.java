package com.datein.date_in.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.datein.date_in.R;

public class BaseFragment extends Fragment {

	protected void showRegisterView(View view) {
		if(view == null) {
			return;
		}
		if(view.getVisibility() == View.VISIBLE) {
			return;
		}

		Animation anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in_right);
		anim.setFillAfter(true);
		view.startAnimation(anim);
		view.setVisibility(View.VISIBLE);
	}

	protected void hideRegisterView(View view) {
		if(view == null) {
			return;
		}
		if(view.getVisibility() != View.VISIBLE) {
			return;
		}

		Animation anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_out_right);
		view.startAnimation(anim);
		view.setVisibility(View.INVISIBLE);
	}

	protected void showView(View view) {
		showView(view, true);
	}

	protected void showView(View view, boolean isAnimating) {
		if(view == null) {
			return;
		}
		if(view.getVisibility() == View.VISIBLE) {
			return;
		}

		if(isAnimating) {
			AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
			alpha.setDuration(250);
			alpha.setFillAfter(false);
			view.startAnimation(alpha);
		}
		view.setVisibility(View.VISIBLE);
	}

	protected void hideView(View view) {
		hideView(view, true);
	}

	protected void hideView(View view, boolean isAnimating) {
		if(view == null) {
			return;
		}
		if(view.getVisibility() != View.VISIBLE) {
			return;
		}

		if(isAnimating) {
			AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
			alpha.setDuration(250);
			alpha.setFillAfter(false);
			view.startAnimation(alpha);
		}
		view.setVisibility(View.INVISIBLE);
	}

	protected void goneView(View view) {
		goneView(view, true);
	}

	protected void goneView(View view, boolean isAnimating) {
		if(view == null) {
			return;
		}
		if(view.getVisibility() != View.VISIBLE) {
			return;
		}

		if(isAnimating) {
			AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
			alpha.setDuration(250);
			alpha.setFillAfter(false);
			view.startAnimation(alpha);
		}
		view.setVisibility(View.GONE);
	}
}
