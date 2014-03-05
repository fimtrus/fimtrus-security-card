package com.fimtrus.securitycard.service;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fimtrus.securitycard.R;
import com.fimtrus.securitycard.helper.CardInfoHelper;
import com.fimtrus.securitycard.model.CardInfoModel;
import com.jhlibrary.util.Util;
import com.jhlibrary.viewontop.BaseViewOnTopService;


public class FloatingCardService extends BaseViewOnTopService {
	
	private ArrayList<CardInfoModel> mCardInfoList;
	
	private Button mSearchButton;
//	private Button mModifyButton;
	
	private EditText mKeyFirstEditText;
	private EditText mKeySecondEditText;
	
	private TextView mKeyFirstTextView;
	private TextView mKeySecondTextView;
	
	private TextView mTitleTextView;
	
	private LinearLayout mCardListLayout;

	private CardInfoModel mCurrentCardInfo;

	private String mDefaultCertNumber;

	
	//은행 정보를 읽어와서 은행 리스트를 뿌려준다.
	private LinearLayout mBankLayout;

	private Handler mKeyboardHandler;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initialize(R.layout.view_floating_card);
	}

	@Override
	public void initialize(int innerLayout) {
		// TODO Auto-generated method stub
		super.initialize(innerLayout);
		
		//initializeField
		mCardInfoList = CardInfoHelper.getAll(this);
		mDefaultCertNumber = CardInfoHelper.getDefault(this);
		
		mSearchButton = (Button) mInnerLayout.findViewById(R.id.button_search);
//		mModifyButton = (Button) mInnerLayout.findViewById(R.id.button_modify);
		
		mKeyFirstEditText = (EditText) mInnerLayout.findViewById(R.id.edittext_key_first);
		mKeySecondEditText = (EditText) mInnerLayout.findViewById(R.id.edittext_key_second);

		mKeyFirstTextView = (TextView) mInnerLayout.findViewById(R.id.textview_key_first);
		mKeySecondTextView = (TextView) mInnerLayout.findViewById(R.id.textview_key_second);
		
		mTitleTextView =  (TextView) mInnerLayout.findViewById(R.id.textview_title);
		
		mCardListLayout = (LinearLayout) mInnerLayout.findViewById(R.id.layout_card_list);
		
		mBankLayout = (LinearLayout) mInnerLayout.findViewById(R.id.layout_bank_container);
		
		mHeaderLayout.setBackgroundResource(R.drawable.ab_stacked_solid_inverse_holo);
		
		addCardList();
		
		mKeyFirstEditText.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				
				updateFloatingWindow ( true );
				mKeyFirstEditText.requestFocus();
//				InputMethodManager input= (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
//				input.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);  
//				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
			}
		});
		mKeySecondEditText.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateFloatingWindow ( true );
				mKeySecondEditText.requestFocus();
//				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.showSoftInputFromInputMethod (v.getApplicationWindowToken(),InputMethodManager.SHOW_FORCED);
			}
		});
		
		mKeyFirstEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if ( hasFocus ) {
					updateFloatingWindow ( true );
					
					Message msg = new Message();
					msg.obj = v;
					mKeyboardHandler.sendMessageDelayed(msg, 500);
					
				} else {
					v.clearFocus();
					updateFloatingWindow ( false );
				}
			}
		});
		mKeySecondEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if ( hasFocus ) {
					updateFloatingWindow ( true );
					
					Message msg = new Message();
					msg.obj = v;
					mKeyboardHandler.sendMessageDelayed(msg, 500);
				} else {
					v.clearFocus();
					updateFloatingWindow ( false );
					
				}
			}
		});
		
		//Listener
		mKeyFirstEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if ( s.toString().length() > 1 ) {
					mKeySecondEditText.requestFocus();
				}				
			}
		});
		mKeySecondEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if ( s.toString().length() > 1 ) {
//					mKeySecondEditText.requestFocus();
				}				
			}
		});
		
		mKeyboardHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput( ( View) msg.obj, InputMethodManager.SHOW_FORCED);
			}
			
		};
		
		mSearchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String keyString1 = mKeyFirstEditText.getText().toString();
				String keyString2 = mKeySecondEditText.getText().toString();
				
				if ( keyString1 == null || keyString1.equals("") || keyString2 == null || keyString2.equals("") ) {
					
					Toast.makeText(getApplicationContext(), R.string.input_cert_card_number, Toast.LENGTH_SHORT).show();
					return;
				}
				
				int key1 = Integer.valueOf( keyString1 );
				int key2 = Integer.valueOf( keyString2 );
				
				if ( key1 > mCurrentCardInfo.getCertNumbers().size() || key2 > mCurrentCardInfo.getCertNumbers().size() ||
						key1 < 1 || key2 < 1 ) {
					Toast.makeText(getApplicationContext(), R.string.search_cert_number_failed, Toast.LENGTH_SHORT).show();
					return;
				}
				//예외처리.숫자가 다 입력되었는지 확인하고...
				
				mKeyFirstTextView.setText(String.valueOf( mCurrentCardInfo.getCertNumbers().get( key1 - 1 ) ).substring(0, 2));
				mKeySecondTextView.setText(String.valueOf( mCurrentCardInfo.getCertNumbers().get( key2 - 1 ) ).substring(2));
				
			}
		});
//		mModifyButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(FloatingCardService.this, MainActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//				stopSelf();
//			}
//		});
		
		//initialize View
		
		
		
		if ( mDefaultCertNumber != null ) {
			
			for ( CardInfoModel c : mCardInfoList ) {
				
				if ( mDefaultCertNumber.equals(c.getCardCertNumber()) ) {
					
					mCurrentCardInfo = c; 
					break;
				}
				
			}
			
		} else {
			mCurrentCardInfo = mCardInfoList.get(0);
		}
		
		mTitleTextView.setText( mCurrentCardInfo.getBankName() ); 
	}

	/**
	 * 카드 정보를 불러와서 카드정보를 화면에 뿌려준다.
	 */
	private void addCardList() {
		LayoutInflater inflater = LayoutInflater.from(this);
		Button button; 
		
		for ( CardInfoModel c : mCardInfoList ) {
			button = (Button) inflater.inflate(R.layout.view_bank_button, null);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Util.getPxFromDp(40) );
			button.setLayoutParams(params);
			button.setText( c.getBankName() );
			button.setTag(c);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					mCurrentCardInfo = (CardInfoModel) v.getTag();
					mTitleTextView.setText( mCurrentCardInfo.getBankName() ); 
				}
			});
			
			mBankLayout.addView(button);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		// Checks whether a hardware keyboard is available
	    if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
	        Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
	    } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
	        Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
	    }
	}
	
}
