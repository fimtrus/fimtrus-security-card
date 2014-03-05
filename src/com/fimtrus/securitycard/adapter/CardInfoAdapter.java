package com.fimtrus.securitycard.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fimtrus.securitycard.R;
import com.fimtrus.securitycard.model.CardInfoModel;

public class CardInfoAdapter extends ArrayAdapter<CardInfoModel> {
	
	public CardInfoAdapter(Context context) {
		super(context, 0);
	}
	
	public CardInfoAdapter(Context context, ArrayList<CardInfoModel> cardInfoList) {
		super(context, 0, cardInfoList);
	}
	
	

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.row, null);
		}
		ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
		int res = R.drawable.card_icon;
		if (res != -1) {
			icon.setImageResource(res);
		}
		TextView title = (TextView) convertView.findViewById(R.id.row_title);
		title.setText(getItem(position).getBankName());
		return convertView;
	}
}
