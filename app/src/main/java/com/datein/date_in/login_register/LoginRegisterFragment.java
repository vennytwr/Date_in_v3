package com.datein.date_in.login_register;

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
import android.widget.Toast;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;
import com.datein.date_in.StateChangeListener;
import com.datein.date_in.log.Logger;
import com.datein.date_in.views.material_edit_text.MaterialEditText;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

public class LoginRegisterFragment extends Fragment implements StateChangeListener {

	private static final String TAG = "LoginRegisterFragment";

	private static final String DISPLAY_NAME_PATTERN = "^[A-Za-z0-9_-]{3,30}$";
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PASSWORD_PATTERN =
			"^[A-Za-z0-9_-]{6,20}$";

	private DateInActivity activity;
	private Typeface font;

	// Login
	private View loginView;
	private MaterialEditText loginEmail;
	private MaterialEditText loginPassword;
	private ProgressBarCircularIndeterminate loginProgressBar;
	private ImageView loginClick;
	private TextView loginRegister;

	// Register
	private View registerView;
	private MaterialEditText registerDisplayName;
	private MaterialEditText registerEmail;
	private MaterialEditText registerPassword;
	private MaterialEditText registerConfirmPassword;
	private ProgressBarCircularIndeterminate registerProgressBar;
	private ImageView registerClick;
	private TextView registerLogin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.d(TAG, " ---------- Login Fragment ----------");
		View resView = inflater.inflate(R.layout.fragment_login_register, container, false);

		activity = (DateInActivity) getActivity();
		// Set font style.
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
		final TextView actionBarLogin = (TextView) resView.findViewById(R.id.actionBar_login);
		actionBarLogin.setTypeface(font);
		loginRegister = (TextView) resView.findViewById(R.id.login_register);
		loginRegister.setTypeface(font);
		loginEmail = (MaterialEditText) resView.findViewById(R.id.login_email);
		loginEmail.setAccentTypeface(font);
		loginEmail.setTypeface(font);
		loginPassword = (MaterialEditText) resView.findViewById(R.id.login_password);
		loginPassword.setAccentTypeface(font);
		loginPassword.setTypeface(font);
		loginProgressBar = (ProgressBarCircularIndeterminate) resView.findViewById(R.id.loginProgressBar);

		loginClick = (ImageView) resView.findViewById(R.id.loginClick);
		loginClick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// If email is valid.
				if(loginEmail.validate(EMAIL_PATTERN, "Invalid Email Address.")) {
					// If password is valid.
					if (loginPassword.validate(PASSWORD_PATTERN, "Password must be between 6 to 20 characters.")) {
						activity.getLoginRegisterController().onLogin(loginEmail.getText().toString(),
								loginPassword.getText().toString());
					}
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
		registerLogin = (TextView) resView.findViewById(R.id.register_login);
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
		registerProgressBar = (ProgressBarCircularIndeterminate) resView.findViewById(R.id.registerProgressBar);

		registerClick = (ImageView) resView.findViewById(R.id.registerClick);
		registerClick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// If display name is valid.
				if (registerDisplayName.validate(DISPLAY_NAME_PATTERN, "Display name must between 3 to 30 characters.")) {
					// If email is valid.
					if (registerEmail.validate(EMAIL_PATTERN, "Invalid Email Address.")) {
						// If password is valid.
						if (registerPassword.validate(PASSWORD_PATTERN, "Password must be between 6 to 20 characters.")) {
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

	private void enableLogin() {
		// Enable the inputs.
		loginEmail.setEnabled(true);
		loginPassword.setEnabled(true);
		loginClick.setEnabled(true);
		loginRegister.setEnabled(true);

		// Make the inputs focusable.
		loginEmail.setFocusable(true);
		loginEmail.setFocusableInTouchMode(true);
		loginPassword.setFocusable(true);
		loginPassword.setFocusableInTouchMode(true);

		// Show button and hide progress bar.
		hideView(loginProgressBar);
		showView(loginClick);
	}

	private void disableLogin() {
		// Disable the inputs focus.
		loginEmail.setFocusable(false);
		loginPassword.setFocusable(false);
		loginClick.setFocusable(false);
		loginRegister.setEnabled(false);

		// Disable the inputs.
		loginEmail.setEnabled(false);
		loginPassword.setEnabled(false);
		loginClick.setEnabled(false);

		// Hide button and show progress bar.
		hideView(loginClick);
		showView(loginProgressBar);
	}

	private void enableRegister() {
		// Enable the inputs
		registerDisplayName.setEnabled(true);
		registerEmail.setEnabled(true);
		registerPassword.setEnabled(true);
		registerConfirmPassword.setEnabled(true);
		registerLogin.setEnabled(true);

		// Make the inputs focusable.
		registerDisplayName.setFocusable(true);
		registerDisplayName.setFocusableInTouchMode(true);
		registerEmail.setFocusable(true);
		registerEmail.setFocusableInTouchMode(true);
		registerPassword.setFocusable(true);
		registerPassword.setFocusableInTouchMode(true);
		registerConfirmPassword.setFocusable(true);
		registerConfirmPassword.setFocusableInTouchMode(true);

		// Show button and hide progress bar.
		hideView(registerProgressBar);
		showView(registerClick);
	}

	private void disableRegister() {
		// Disable the inputs focus.
		registerDisplayName.setFocusable(false);
		registerEmail.setFocusable(false);
		registerPassword.setFocusable(false);
		registerConfirmPassword.setFocusable(false);
		registerLogin.setEnabled(false);

		// Disable the inputs.
		registerDisplayName.setEnabled(false);
		registerEmail.setEnabled(false);
		registerPassword.setEnabled(false);
		registerConfirmPassword.setEnabled(false);

		// Hide button and show progress bar.
		hideView(registerClick);
		showView(registerProgressBar);
	}

	@Override
	public void onStateChanged(String state) {
		String currentState = activity.getLoginRegisterController().getCurrentState();
		Logger.d(TAG, "Current State: " + currentState);
		Logger.d(TAG, "State: " + state);
		if (currentState.equals(state)) {
			Logger.d(TAG, "ERROR: BOTH STATE ARE THE SAME!");
			return;
		}

		if (currentState.equals(Constants.STATE_START) && state.equals(Constants.STATE_LOGIN)) {
			showView(loginView, false);
		}

		// Change from login to register.
		if (currentState.equals(Constants.STATE_LOGIN) && state.equals(Constants.STATE_REGISTER)) {
			showRegisterView(registerView);
			hideView(loginView);
		}

		// Change from register to login.
		if (currentState.equals(Constants.STATE_REGISTER) && state.equals(Constants.STATE_LOGIN)) {
			hideRegisterView(registerView);
			showView(loginView);
		}

		// Disable everything while sending logging in packet.
		if(currentState.equals(Constants.STATE_LOGIN) && state.equals(Constants.STATE_LOGGING_IN))
			disableLogin();

		// Disable everything while sending registering packet.
		if(currentState.equals(Constants.STATE_REGISTER) && state.equals(Constants.STATE_REGISTERING))
			disableRegister();

		// If login OK, move on to calendar.
		if(currentState.equals(Constants.STATE_LOGGING_IN) && state.equals(Constants.STATE_LOGIN_OK)) {
			enableLogin();

			Toast.makeText(getActivity(), "Login OK", Toast.LENGTH_SHORT).show();
			activity.getLoginRegisterController().saveSession();

			// Clear the text in the inputs.
			loginEmail.setText("");
			loginPassword.setText("");

			// Move on to main fragment.
			activity.doInitApp(false);
		}

		// If login fail, prompt user login fail.
		if(currentState.equals(Constants.STATE_LOGGING_IN) && state.equals(Constants.STATE_LOGIN_FAIL)) {
			enableLogin();

			// Clear the text in the inputs and set the errors.
			loginEmail.setText("");
			loginEmail.setError("Incorrect Email Address.");
			loginEmail.requestFocus();
			loginPassword.setText("");
			loginPassword.setError("Incorrect Password.");

		}

		// If register OK, enable everything and clear all edit text.
		if(currentState.equals(Constants.STATE_REGISTERING) && state.equals(Constants.STATE_REGISTER_OK)) {
			enableRegister();

			// Clear the text in the inputs.
			registerDisplayName.setText("");
			registerEmail.setText("");
			registerPassword.setText("");
			registerConfirmPassword.setText("");
		}

		// If email is taken, prompt the user to change.
		if(currentState.equals(Constants.STATE_REGISTERING) && state.equals(Constants.STATE_EMAIL_TAKEN)) {
			enableRegister();

			// Clear the edit text and highlight the error.
			registerEmail.setText("");
			registerEmail.setError("Email Address is already in used.");
			registerEmail.requestFocus();
		}

		// If display name is taken, prompt the user to change.
		if(currentState.equals(Constants.STATE_REGISTERING) && state.equals(Constants.STATE_DISPLAY_NAME_TAKEN)) {
			enableRegister();

			// Clear the edit text and highlight the error.
			registerDisplayName.setText("");
			registerDisplayName.setError("Display Name is already in used.");
			registerDisplayName.requestFocus();
		}

		activity.getLoginRegisterController().setCurrentState(state);
	}
}
