package com.fimtrus.securitycard.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fimtrus.securitycard.R;
import com.jhlibrary.util.Util;

public class MainFragment extends Fragment {

	private FrameLayout mRootLayout;
	private Dialog mDialog;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootLayout = (FrameLayout) inflater.inflate(R.layout.fragment_main,
				null);
		initialize();
		return mRootLayout;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initialize() {

		initializeFields();
		initializeListeners();
		initializeView();

	}

	private void initializeFields() {
		mDialog = Util.getDialog(getActivity());
	}

	private void initializeListeners() {

	}

	private void initializeView() {
	}
}
