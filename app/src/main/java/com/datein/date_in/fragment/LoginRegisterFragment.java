package com.datein.date_in.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.datein.date_in.Constants;
import com.datein.date_in.MainActivity;
import com.datein.date_in.R;
import com.datein.date_in.log.Logger;
import com.datein.date_in.login.LoginRegisterListener;
import com.datein.date_in.views.material_edit_text.MaterialEditText;
import com.datein.date_in.views.material_progress_bar_circular.ProgressBarCircularIndetermininate;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class LoginRegisterFragment extends Fragment implements LoginRegisterListener {

	private static final String TAG = "LoginFragment";

	private static final String DISPLAY_NAME_PATTERN = "^[A-Za-z0-9_-]{3,30}$";
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PASSWORD_PATTERN =
			"^[A-Za-z0-9_-]{6,20}$";

	private MainActivity activity;
	private Typeface font;
	private View loginView;
	private View registerView;
	private MaterialEditText registerDisplayName;
	private MaterialEditText registerEmail;
	private MaterialEditText registerPassword;
	private MaterialEditText registerConfirmPassword;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.d(TAG, " ---------- Login Fragment ----------");
		View resView = inflater.inflate(R.layout.fragment_login_register, container, false);

		activity = (MainActivity) getActivity();
		// Get application and set font style.
		font = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");

		// Set up both login and register view.
		loginView = resView.findViewById(R.id.login_view);
		registerView = resView.findViewById(R.id.register_view);
		setUpLoginView(resView);
		setUpRegisterView(resView);
		loginView.setVisibility(View.GONE);
		registerView.setVisibility(View.GONE);

		// Show login first when start up.
		activity.getLoginRegisterController().doChangeState(Constants.STATE_LOGIN);
		return resView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (activity.getLoginRegisterController().getListener() != this)
			activity.getLoginRegisterController().setListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		activity.getLoginRegisterController().setListener(null);
	}

	private void setUpLoginView(View resView) {
		TextView actionBarLogin = (TextView) resView.findViewById(R.id.actionBar_login);
		actionBarLogin.setTypeface(font);
		TextView loginRegister = (TextView) resView.findViewById(R.id.login_register);
		loginRegister.setTypeface(font);
		final MaterialEditText loginEmail = (MaterialEditText) resView.findViewById(R.id.login_email);
		loginEmail.setAccentTypeface(font);
		loginEmail.setTypeface(font);
		final MaterialEditText loginPassword = (MaterialEditText) resView.findViewById(R.id.login_password);
		loginPassword.setAccentTypeface(font);
		loginPassword.setTypeface(font);

		ImageView loginClick = (ImageView) resView.findViewById(R.id.loginClick);
		loginClick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				if(loginUsername.getText().length() == 0)
//					loginUsername.setError("Username cannot be empty.");
//				else if(loginPassword.getText().length() == 0)
//					loginPassword.setError("Password cannot be empty.");
//				else {
//					loginUsername.setError("Incorrect Username.");
//					loginPassword.setError("Incorrect Password.");
//				}
				Bundle data = new Bundle();
				data.putString("my_message", "Hello World");
				data.putString("my_action",
						"com.google.android.gcm.demo.app.ECHO_NOW");
				String id = "1";
				try {
					GoogleCloudMessaging.getInstance(getActivity()).send("994318371798" + "@gcm.googleapis.com", id, data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		loginRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.getLoginRegisterController().doChangeState(Constants.STATE_REGISTER);
			}
		});
	}

	private void setUpRegisterView(final View resView) {
		TextView actionBarRegister = (TextView) resView.findViewById(R.id.actionBar_register);
		actionBarRegister.setTypeface(font);
		TextView registerLogin = (TextView) resView.findViewById(R.id.register_login);
		registerLogin.setTypeface(font);
		registerDisplayName = (MaterialEditText) resView.findViewById(R.id.register_display_name);
		registerDisplayName.setAccentTypeface(font);
		registerDisplayName.setTypeface(font);
		registerEmail = (MaterialEditText) resView.findViewById(R.id.register_email);
		registerEmail.setAccentTypeface(font);
		registerEmail.setTypeface(font);
		registerPassword = (MaterialEditText) resView.findViewById(R.id.register_password);
		registerPassword.setAccentTypeface(font);
		registerPassword.setTypeface(font);
		registerConfirmPassword = (MaterialEditText) resView.findViewById(R.id.register_confirm_password);
		registerConfirmPassword.setAccentTypeface(font);
		registerConfirmPassword.setTypeface(font);
		final ProgressBarCircularIndetermininate registerProgressBar =
				(ProgressBarCircularIndetermininate) resView.findViewById(R.id.registerProgressBar);

		final ImageView registerClick = (ImageView) resView.findViewById(R.id.registerClick);
		registerClick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Hide button and show progress bar.
				hideView(registerClick);
				showView(registerProgressBar);

				// If display name is valid.
				if (registerDisplayName.validate(DISPLAY_NAME_PATTERN, "Display name must between 3 to 30 characters.")) {
					// If email is valid.
					if (registerEmail.validate(EMAIL_PATTERN, "Invalid Email Address.")) {
						// If password is valid.
						if (registerPassword.validate(PASSWORD_PATTERN, "Password must between 6 to 20 characters.")) {
							// If confirm password is same.
							if (registerConfirmPassword.getText().toString()
									.compareTo(registerPassword.getText().toString()) == 0) {
								activity.getLoginRegisterController().onRegister(registerDisplayName.getText().toString(),
										registerEmail.getText().toString(), registerPassword.getText().toString());
							} else {
								registerConfirmPassword.setError("Passwords does not match.");
							}
						}
					}
				}

				// Show button and hide progress bar.
				hideView(registerProgressBar);
				showView(registerClick);
			}
		});

		registerLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.getLoginRegisterController().doChangeState(Constants.STATE_LOGIN);
			}
		});
	}

	private void showView(View view) {
		showView(view, true);
	}

	private void showView(View view, boolean isAnimating) {
		if (view == null) {
			return;
		}
		if (view.getVisibility() == View.VISIBLE) {
			return;
		}
		if (isAnimating) {
			AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
			alpha.setDuration(250);
			alpha.setFillAfter(false);
			view.startAnimation(alpha);
		}
		view.setVisibility(View.VISIBLE);
	}

	private void hideView(View view) {
		hideView(view, true);
	}

	private void hideView(View view, boolean isAnimating) {
		if (view == null) {
			return;
		}
		if (view.getVisibility() != View.VISIBLE) {
			return;
		}
		if (isAnimating) {
			AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
			alpha.setDuration(250);
			alpha.setFillAfter(false);
			view.startAnimation(alpha);
		}
		view.setVisibility(View.INVISIBLE);
	}

	private void showRegisterView(View view) {
		if (view == null) {
			return;
		}
		if (view.getVisibility() == View.VISIBLE) {
			return;
		}
		Animation anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in_right);
		anim.setFillAfter(true);
		view.startAnimation(anim);
		view.setVisibility(View.VISIBLE);
	}

	private void hideRegisterView(View view) {
		if (view == null) {
			return;
		}
		if (view.getVisibility() != View.VISIBLE) {
			return;
		}
		Animation anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_out_right);
		view.startAnimation(anim);
		view.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onStateChanged(int state) {
		Logger.d(TAG, "Current State: " + activity.getLoginRegisterController().getCurrentState());
		Logger.d(TAG, "State: " + state);
		int currentState = activity.getLoginRegisterController().getCurrentState();
		if (currentState == state) {
			return;
		}

		if (state == Constants.STATE_LOGIN) {
			showView(loginView, false);
		}

		// Change from login to register.
		if (currentState == Constants.STATE_LOGIN && state == Constants.STATE_REGISTER) {
			showRegisterView(registerView);
			hideView(loginView);
		}

		// Change from register to login.
		if (currentState == Constants.STATE_REGISTER && state == Constants.STATE_LOGIN) {
			hideRegisterView(registerView);
			showView(loginView);
		}

		// Disable everything while sending registering packet.
		if(currentState == Constants.STATE_REGISTER && state == Constants.STATE_REGISTERING) {
			// Disable the inputs focus.
			registerDisplayName.setFocusable(false);
			registerEmail.setFocusable(false);
			registerPassword.setFocusable(false);
			registerConfirmPassword.setFocusable(false);

			// Disable the inputs.
			registerDisplayName.setEnabled(false);
			registerEmail.setEnabled(false);
			registerPassword.setEnabled(false);
			registerConfirmPassword.setEnabled(false);
		}

		// If register OK, enable everything and go to login.
		if(currentState == Constants.STATE_REGISTERING && state == Constants.STATE_REGISTER_OK) {
			// Enable the inputs
			registerDisplayName.setEnabled(true);
			registerEmail.setEnabled(true);
			registerPassword.setEnabled(true);
			registerConfirmPassword.setEnabled(true);

			// Clear the text in the inputs.
			registerDisplayName.setText("");
			registerEmail.setText("");
			registerPassword.setText("");
			registerConfirmPassword.setText("");

			// Make the inputs focusable.
			registerDisplayName.setFocusable(true);
			registerDisplayName.setFocusableInTouchMode(true);
			registerEmail.setFocusable(true);
			registerEmail.setFocusableInTouchMode(true);
			registerPassword.setFocusable(true);
			registerPassword.setFocusableInTouchMode(true);
			registerConfirmPassword.setFocusable(true);
			registerConfirmPassword.setFocusableInTouchMode(true);
		}

		activity.getLoginRegisterController().setCurrentState(state);
	}
}
