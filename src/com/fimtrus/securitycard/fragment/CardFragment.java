package com.fimtrus.securitycard.fragment;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fimtrus.securitycard.R;
import com.fimtrus.securitycard.helper.CardInfoHelper;
import com.fimtrus.securitycard.model.CardInfoModel;
import com.jhlibrary.util.Util;

public class CardFragment extends Fragment implements View.OnClickListener {

	private RelativeLayout mRootLayout;
	
	private Button mConfirmButton;
	private Button mCancelButton;
	private Button mModifyButton;
	private Button mCancelButton2;
	
	private TextView mModifyTextView ;
	
	
	private Dialog mDialog;
	
	
	private String certNumber;

	private final int EDITTEXT_BANK_NAME_ID = R.id.edittext_bank_name;
	
	private final int EDITTEXT_CARD_CERT_NUMBER_ID = R.id.edittext_card_cert_number;
	
	private final int TYPE_DEFAULT = 1;
	private final int TYPE_READ_ONLY = 2;
	
	private final int[] EDITTEXT_CARD_NUMBER_IDS = {
			
		R.id.edittext_card_number1,
		R.id.edittext_card_number2,
		R.id.edittext_card_number3,
		R.id.edittext_card_number4,
		R.id.edittext_card_number5,
		R.id.edittext_card_number6,
		R.id.edittext_card_number7,
		R.id.edittext_card_number8,
		R.id.edittext_card_number9,
		R.id.edittext_card_number10,
		R.id.edittext_card_number11,
		R.id.edittext_card_number12,
		R.id.edittext_card_number13,
		R.id.edittext_card_number14,
		R.id.edittext_card_number15,
		R.id.edittext_card_number16,
		R.id.edittext_card_number17,
		R.id.edittext_card_number18,
		R.id.edittext_card_number19,
		R.id.edittext_card_number20,
		R.id.edittext_card_number21,
		R.id.edittext_card_number22,
		R.id.edittext_card_number23,
		R.id.edittext_card_number24,
		R.id.edittext_card_number25,
		R.id.edittext_card_number26,
		R.id.edittext_card_number27,
		R.id.edittext_card_number28,
		R.id.edittext_card_number29,
		R.id.edittext_card_number30,
		R.id.edittext_card_number31,
		R.id.edittext_card_number32,
		R.id.edittext_card_number33,
		R.id.edittext_card_number34,
		R.id.edittext_card_number35
	};

	private CheckBox mDefaultCheckBox;

	private boolean isModify = false;

	private CardViewAutoFocus mCardViewAutoFocus;
	
	
	public static final CardFragment newInstance(String certNumber) {
		
		CardFragment instance = new CardFragment();
		
		instance.certNumber = certNumber;
		
		return instance;
	};
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_card_info, null);
		initialize();
		return mRootLayout;
	}

	private void initialize() {

		initializeFields();
		initializeListeners();
		initializeView();
	}

	private void initializeFields() {
		mDialog = Util.getDialog(getActivity());
		
		mConfirmButton = (Button) mRootLayout.findViewById(R.id.button_confirm);
		mCancelButton = (Button) mRootLayout.findViewById(R.id.button_cancel);
		mModifyButton = (Button) mRootLayout.findViewById(R.id.button_modify);
		mCancelButton2 = (Button) mRootLayout.findViewById(R.id.button_cancel2);
		mDefaultCheckBox = (CheckBox) mRootLayout.findViewById(R.id.checkbox_default);
		mModifyTextView = (TextView) mRootLayout.findViewById(R.id.textview_modify_mode);
		
		mCardViewAutoFocus = new CardViewAutoFocus();
	}

	private void initializeListeners() {
		mConfirmButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);
		mModifyButton.setOnClickListener(this);
		mCancelButton2.setOnClickListener(this);
	}

	private void initializeView() {
		
		//아이템 선택에 따라 다른 동작을하도록.... 
		if ( certNumber == null ) {
			//새로운 아이템
			
			for ( int i = 0; i < EDITTEXT_CARD_NUMBER_IDS.length; i++ ) {
				
				int id = EDITTEXT_CARD_NUMBER_IDS[i];
				
				final EditText editText = (EditText) mRootLayout.findViewById(id);
				editText.setTag(i);
				editText.addTextChangedListener( mCardViewAutoFocus );
			}
			
//			for (int id : EDITTEXT_CARD_NUMBER_IDS ) {
//				final EditText editText = (EditText) mRootLayout.findViewById(id);
//				editText.setTag(id);
//				editText.addTextChangedListener( new CardViewAutoFocus( editText ) );
////				editText.requestFocus(View.FOCUS_DOWN);
//			}
		} else {
			//기존 아이템
			CardInfoModel cardInfo = CardInfoHelper.getCardInfo(getActivity(), certNumber);
			
			((EditText) (mRootLayout.findViewById(EDITTEXT_BANK_NAME_ID))).setText(cardInfo.getBankName());
			((EditText) (mRootLayout.findViewById(EDITTEXT_CARD_CERT_NUMBER_ID))).setText(cardInfo.getCardCertNumber());
			
			mRootLayout.findViewById(EDITTEXT_BANK_NAME_ID).setEnabled(false);
			mRootLayout.findViewById(EDITTEXT_CARD_CERT_NUMBER_ID).setEnabled(false);
			
			//확인버튼 컨테이너를 숨기고, 수정 버튼 컨테이너를 보이도록한다.
			mRootLayout.findViewById(R.id.layout_button_container).setVisibility(View.GONE);
			mRootLayout.findViewById(R.id.layout_button_container2).setVisibility(View.VISIBLE);
			
			setCardTableText();
			setCardTableEnable( false );
		}
	}
	
	/**
	 * 카드 테이블 텍스트를 입력한다.
	 * @param isEnabled : true : enabled, false : disabled
	 */
	private void setCardTableText () {
		
		CardInfoModel cardInfo = CardInfoHelper.getCardInfo(getActivity(), certNumber);
		//카드정보에서 보안카드 정보를 들고온다.
		ArrayList<String> certNumbers = cardInfo.getCertNumbers();
		
		//읽기 전용이므로, 모든 에디트텍스트를 읽기전용으로 변경. 그리고 버튼들을 숨겨야한다.
		for ( int i = 0 ; i < certNumbers.size(); i++ ) {
			int id = EDITTEXT_CARD_NUMBER_IDS[i];
			EditText editText = (EditText) mRootLayout.findViewById(id);
			editText.setText( String.valueOf( certNumbers.get(i) ) );
		}
	}
	/**
	 * 카드 테이블 타입을 정한다.
	 * @param isEnabled : true : enabled, false : disabled
	 */
	private void setCardTableEnable ( boolean isEnabled ) {
		//카드정보에서 보안카드 정보를 들고온다.
		for ( int i = 0 ; i < EDITTEXT_CARD_NUMBER_IDS.length; i++ ) {
			int id = EDITTEXT_CARD_NUMBER_IDS[i];
			EditText editText = (EditText) mRootLayout.findViewById(id);
			editText.setEnabled( isEnabled );
		}
	}
	
	/**
	 * 카드정보를 저장한다.
	 * @return
	 */
	private boolean saveCardInformation () {
		
		String bankName = ( ( EditText ) mRootLayout.findViewById(EDITTEXT_BANK_NAME_ID) ).getText().toString();
		String cardCertNumber = ( ( EditText ) mRootLayout.findViewById(EDITTEXT_CARD_CERT_NUMBER_ID) ).getText().toString();
		
		ArrayList<String> certList = new ArrayList<String>();
		
		for (int i = 0; i < EDITTEXT_CARD_NUMBER_IDS.length; i++) {
			int id = EDITTEXT_CARD_NUMBER_IDS[i] ;
			final EditText editText = (EditText) mRootLayout.findViewById(id);
			if ( editText.getText().toString().equals("") ) {
				break;
			}
			
			certList.add( editText.getText().toString() );
			
//			certNumbers[i] = Integer.parseInt( editText.getText().toString() ) ;
		}

		CardInfoModel cardInfo = CardInfoModel.newInstance(bankName, cardCertNumber, certList);
		
		if (mDefaultCheckBox.isChecked()) {
			CardInfoHelper.setDefault(getActivity(), cardInfo.getCardCertNumber());
		}
		
		return CardInfoHelper.setCardInfo(getActivity(), cardInfo);
	}
	
	private class CardViewAutoFocus implements TextWatcher {
		
		
		public CardViewAutoFocus() {
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			
			// TODO Auto-generated method stub
			if ( s.length() > 3 ) {
//				int id = mEditText.getId();
//				int length = CardFragment.this.EDITTEXT_CARD_NUMBER_IDS.length - 1;
//				for ( int i = 0; i < length; i++ ) {
//					
//					if ( id == CardFragment.this.EDITTEXT_CARD_NUMBER_IDS[i] ) {
//						mRootLayout.findViewById(CardFragment.this.EDITTEXT_CARD_NUMBER_IDS[i + 1]).requestFocus(); 
//					}
//				}
				
				View view = getActivity().getCurrentFocus();
				int index = (Integer) view.getTag();
				
				if ( index < EDITTEXT_CARD_NUMBER_IDS.length - 1 ) {
					
					mRootLayout.findViewById(CardFragment.this.EDITTEXT_CARD_NUMBER_IDS[index + 1]).requestFocus(); 
				}
			} else {
				
			}
		}
	}

	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		
		switch ( id ) {
		case R.id.button_confirm :
			boolean isSucceed = saveCardInformation();
			if ( isSucceed ) {
				Toast.makeText(getActivity(), R.string.save_succes, Toast.LENGTH_SHORT).show();
				
				getActivity().getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_left_sliding, new LeftSlidingFragment())
				.commit();
				
				getActivity().getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, new MainFragment())
				.commit();
				
			} else {
				Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.button_cancel : 
			getActivity().getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, new MainFragment())
			.commit();
			break;
		case R.id.button_modify :
			//확인버튼 컨테이너를 숨기고, 수정 버튼 컨테이너를 보이도록한다.
			if ( isModify ) {
				//수정모드 상태에서 확인 버튼 클릭
				mModifyButton.setText(R.string.modify);
				mModifyTextView.setVisibility(View.GONE);
				saveCardInformation();
				setCardTableEnable( false );
				isModify = false;
			} else {
				//수정모드 진입하기
				isModify = true;
				mModifyTextView.setVisibility(View.VISIBLE);
				setCardTableEnable( true );
				mModifyButton.setText(R.string.confirm);
			}
			break;
		case R.id.button_cancel2 : 
			
			//화면 초기화.
			for ( int i = 0 ; i < EDITTEXT_CARD_NUMBER_IDS.length; i++ ) {
				int id1 = EDITTEXT_CARD_NUMBER_IDS[i];
				EditText editText = (EditText) mRootLayout.findViewById(id1);
				editText.setText( "" );
			}
			//확인버튼 컨테이너를 숨기고, 수정 버튼 컨테이너를 보이도록한다.
			mModifyTextView.setVisibility(View.GONE);
			mModifyButton.setText(R.string.modify);
			setCardTableText();
			setCardTableEnable( false );
			isModify = false;
			break;
		}
	}
}
