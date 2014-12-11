package com.datein.date_in.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datein.date_in.R;

/**
 * Created by Yiming on 3/12/2014.
 */
public class RegisterFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View resView = inflater.inflate(R.layout.fragment_register, container, false);

		return  resView;
	}
}
