package com.fimtrus.securitycard.fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fimtrus.securitycard.R;
import com.fimtrus.securitycard.activity.MainActivity;
import com.jhlibrary.adapter.IconTextAdapter;
import com.jhlibrary.model.IconTextItem;

public class RightSlidingFragment extends ListFragment {

	private ListView mRootLayout;
	private IconTextAdapter mIconTextAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootLayout = (ListView) inflater.inflate(R.layout.view_swape_list, null);
		return mRootLayout; 
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initialize();
	}
	

	private void initialize() {

		initializeFields();
		initializeListeners();
		initializeView();

	}

	private void initializeFields() {
		mIconTextAdapter = new IconTextAdapter(getActivity());
		mIconTextAdapter.add(new IconTextItem("보험금 계산"));
		mIconTextAdapter.add(new IconTextItem("마일리지 계산"));
		mIconTextAdapter.add(new IconTextItem("카드 무이자 할부"));
		mIconTextAdapter.add(new IconTextItem("문자인식테스트"));
	}

	private void initializeListeners() {

	}

	private void initializeView() {
		setListAdapter(mIconTextAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		switch (position) {
		case 0 : 
		case 1 :
		case 2 :
//			getActivity().getSupportFragmentManager()
//			.beginTransaction()
//			.replace(R.id.content_frame, new SampleFragment())
//			.commit();
//			((MainActivity)getActivity()).getSlidingMenu().toggle();
			break;
		}
		
	}
}
