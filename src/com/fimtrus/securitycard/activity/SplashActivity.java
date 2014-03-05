package com.fimtrus.securitycard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.fimtrus.securitycard.R;
import com.fimtrus.securitycard.helper.CardInfoHelper;
import com.fimtrus.securitycard.receiver.AutoStartReceiver;
import com.fimtrus.securitycard.service.FloatingCardService;
import com.fimtrus.securitycard.util.AesCrypto;
import com.jhlibrary.util.Util;

public class SplashActivity extends Activity {


	private String mPassword;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initialize();
//		Util.registerGcm(this);
	}
	
	private void initialize() {
		
		if ( Util.isRooted() ) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle( R.string.rooted )
					.setMessage(R.string.rooted_description)
					.setCancelable(false);
			builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SplashActivity.this.finish();
				}
			});
			
			builder.show();
			return;
		}
		
		Object obj = Util.getPreference(this, AesCrypto.KEY_GENERATED_KEY);
		
		if ( obj == null ) {
			AesCrypto.generateKey(this);
		} else {
		}
		
		mPassword = CardInfoHelper.getPassword( this );
		
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean isLockDisplay = preference.getBoolean(SettingActivity.PREFERENCE_LOCK_DISPLAY, true);
		
		if ( isLockDisplay ) {
			if ( mPassword == null ) {
				//패스워드가 없을 경우에는 패스워드 설정창.
				showSettingPasswordDialog();
			} else {
				//패스워드가 없을 경우에는 패스워드 입력창.
				showInputPasswordDialog();
			}
		} else {
			start();
		}
	}

	/**
	 * 앱을 시작한다. 패스워드 잠금이 설정되어 있지 않을 경우. 바로 실행됨.
	 */
	private void start() {
		
		Intent receiveIntent = getIntent();
		
		boolean isFromNotification = receiveIntent.getBooleanExtra(AutoStartReceiver.FLAG_NOTIFICATION, false);
		
		if ( isFromNotification ) {
			
			if ( CardInfoHelper.getAll(this).size() == 0) {
				Intent intent1 = new Intent(this, MainActivity.class);
				startActivity(intent1);
			} else {
				startService(new Intent(this, FloatingCardService.class));
			}
			
		} else {
			
			SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
			
			boolean isTopBarDisplay = preference.getBoolean(SettingActivity.PREFERENCE_DISPLAY_TOPBAR, true);
			
			if ( isTopBarDisplay ) {
				
				Intent intent = new Intent(this, AutoStartReceiver.class);
//		Intent intent = new Intent("com.fimtrus.securitycard.ACTION_NOTIFICATION_START");
				sendBroadcast(intent);
			}
			
			
			Intent intent1 = new Intent(this, MainActivity.class);
			startActivity(intent1);
		}
		this.finish();
	}
	
	/**
	 * 패스워드가 설정되어 있고. 잠금 설정이 되어 있을경우 실행된다.
	 */
	private void showInputPasswordDialog () {
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.view_input_password, null);
								
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle( R.string.title_input_password );
		dialogBuilder.setCancelable(false);
		dialogBuilder.setView(layout);
								
		final EditText passwordEditText = (EditText) layout.findViewById(R.id.edittext_password);
		final CheckBox displayPasswordCheckBox = (CheckBox) layout.findViewById(R.id.checkbox_display_password);

		dialogBuilder.setPositiveButton(android.R.string.ok, null);
		dialogBuilder.setNegativeButton(android.R.string.cancel, null);
		
		final AlertDialog dialog = dialogBuilder.create();
		
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
		    @Override
		    public void onShow(DialogInterface arg0) {
		        Button positiveButton = ( (AlertDialog) arg0 ).getButton(AlertDialog.BUTTON_POSITIVE);
		        Button negativeButton = ( (AlertDialog) arg0 ).getButton(AlertDialog.BUTTON_NEGATIVE);
		        //Or AlertDialog.BUTTON_NEGATIVE
		        positiveButton.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View view) {
		            	if ( mPassword.equals( passwordEditText.getText().toString() ) ) {
							Toast.makeText(SplashActivity.this, R.string.certificate_success, Toast.LENGTH_SHORT).show();
							dialog.dismiss();
							start();
						} else {
							Toast.makeText(SplashActivity.this, R.string.certificate_failed, Toast.LENGTH_SHORT).show();
						}
		            }
		        });
		        negativeButton.setOnClickListener(new View.OnClickListener() {
		        	@Override
		        	public void onClick(View view) {
		        		dialog.dismiss();
		        		SplashActivity.this.finish();
		        	}
		        });
		    }
		});
		
		displayPasswordCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked ) {
					//비밀번호 표시
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					//비밀번호 미표시
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
		
		dialog.show();
		
	}
	/**
	 * 최초 패스워드 설정시 실행된다.
	 */
	private void showSettingPasswordDialog () {
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.view_setting_password, null);
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle( R.string.title_setting_password );
		dialogBuilder.setCancelable(false);
		dialogBuilder.setView(layout);
		
		final EditText passwordEditText1 = (EditText) layout.findViewById(R.id.edittext_password1);
		final EditText passwordEditText2 = (EditText) layout.findViewById(R.id.edittext_password2);
		
		final CheckBox displayPasswordCheckBox = (CheckBox) layout.findViewById(R.id.checkbox_display_password);
		
		dialogBuilder.setPositiveButton(android.R.string.ok, null);
		dialogBuilder.setNegativeButton(android.R.string.cancel, null);
		
		final AlertDialog dialog = dialogBuilder.create();
		
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
		    @Override
		    public void onShow(DialogInterface arg0) {
		        Button positiveButton = ( (AlertDialog) arg0 ).getButton(AlertDialog.BUTTON_POSITIVE);
		        Button negativeButton = ( (AlertDialog) arg0 ).getButton(AlertDialog.BUTTON_NEGATIVE);
		        //Or AlertDialog.BUTTON_NEGATIVE
		        positiveButton.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View view) {
		            	if ( passwordEditText1.getText().toString().equals( passwordEditText2.getText().toString() ) ) {
							Toast.makeText(SplashActivity.this, R.string.enrollment_success, Toast.LENGTH_SHORT).show();
							CardInfoHelper.setPassword(SplashActivity.this, passwordEditText1.getText().toString());
							dialog.dismiss();
							start();
						} else {
							Toast.makeText(SplashActivity.this, R.string.enrollment_different, Toast.LENGTH_SHORT).show();
						}
		            }
		        });
		        negativeButton.setOnClickListener(new View.OnClickListener() {
		        	@Override
		        	public void onClick(View view) {
		        		dialog.dismiss();
		        		SplashActivity.this.finish();
		        	}
		        });
		    }
		});
		
		
		displayPasswordCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked ) {
					//비밀번호 표시
					passwordEditText1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					passwordEditText2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					//비밀번호 미표시
					passwordEditText1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					passwordEditText2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
		
		dialog.show();
	}
	
}
