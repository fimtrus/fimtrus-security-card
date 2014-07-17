package com.fimtrus.securitycard.fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fimtrus.securitycard.R;
import com.fimtrus.securitycard.activity.MainActivity;
import com.fimtrus.securitycard.adapter.CardInfoAdapter;
import com.fimtrus.securitycard.helper.CardInfoHelper;
import com.fimtrus.securitycard.model.CardInfoModel;

public class LeftSlidingFragment extends ListFragment {

	private static final int ADD_BANK_ID = 3000;

	private ListView mRootLayout;

	private LinearLayout mAddBankFooter;
	private CardInfoAdapter mIconTextAdapter;
	private ArrayList<CardInfoModel> mCardInfoList;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootLayout = (ListView) inflater.inflate(R.layout.view_swape_list, null);
		mAddBankFooter = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.row, null);

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

		mCardInfoList = CardInfoHelper.getAll(getActivity());

		mIconTextAdapter = new CardInfoAdapter(getActivity(), mCardInfoList);

		ImageView icon = (ImageView) mAddBankFooter.findViewById(R.id.row_icon);
		icon.setImageResource(R.drawable.ic_menu_btn_add);
		TextView title = (TextView) mAddBankFooter.findViewById(R.id.row_title);
		title.setText(R.string.add_bank);
		
	}

	private void initializeListeners() {
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				
				if ( arg0.getItemAtPosition(arg2) == null ) {
					
				} else {
					
					Toast.makeText(getActivity(), "On long click listener", Toast.LENGTH_LONG).show();
					final String[] items = {"삭제"};
					final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setItems(items, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							CardInfoModel cardInfo = mCardInfoList.get(arg2);
							CardInfoHelper.removeCard(getActivity(), cardInfo);
							mCardInfoList.remove(cardInfo);
							mIconTextAdapter.notifyDataSetChanged();
						}
					});
					builder.create().show();
				}
				
				
				
				return true;
			}
		});
	}

	private void initializeView() {
		mRootLayout.addFooterView(mAddBankFooter, null, true);
		setListAdapter(mIconTextAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (l.getItemAtPosition(position) == null) {
			getActivity().getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, CardFragment.newInstance(null)).commit();
			((MainActivity) getActivity()).getSlidingMenu().toggle();
		} else {
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.content_frame,
							CardFragment.newInstance(((CardInfoModel) l.getItemAtPosition(position))
									.getCardCertNumber())).commit();
			((MainActivity) getActivity()).getSlidingMenu().toggle();

		}

		// switch (position) {
		//
		// case -1 :
		//
		// break;
		// default:
		//
		// break;
		// }

	}
	
	public void notifyDataChanged() {
		mCardInfoList = CardInfoHelper.getAll(getActivity());
		mIconTextAdapter = new CardInfoAdapter(getActivity(), mCardInfoList);
		setListAdapter(mIconTextAdapter);
	}
}
