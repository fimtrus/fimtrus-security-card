package com.fimtrus.securitycard.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.fimtrus.securitycard.R;
import com.fimtrus.securitycard.helper.CardInfoHelper;
import com.fimtrus.securitycard.receiver.AutoStartReceiver;
import com.fimtrus.securitycard.util.AesCrypto;

public class SettingActivity extends SherlockPreferenceActivity implements Preference.OnPreferenceChangeListener,
													Preference.OnPreferenceClickListener {

	private Preference mApplicationNamePreference;
	private Preference mApplicationVersionPreference;
	private CheckBoxPreference mTopBarCheckBoxPreference;
	private CheckBoxPreference mLockDisplayCheckBoxPreference;
	private Preference mResetPreference;
	private Preference mBackUpPreference;
	private Preference mRestorePreference;

	public static final String PREFERENCE_APPLICATION_NAME = "setting_application_name";
	public static final String PREFERENCE_APPLICATION_VERSION = "setting_application_version";
	public static final String PREFERENCE_DISPLAY_TOPBAR = "setting_display_topbar";
	public static final String PREFERENCE_LOCK_DISPLAY = "setting_lock_display";
	public static final String PREFERENCE_SETTING_RESET_PASSWORD = "setting_reset_password";
	public static final String PREFERENCE_SETTING_BACKUP = "setting_backup";
	public static final String PREFERENCE_SETTING_RESTORE = "setting_restore";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_setting_preference);
		initialize();
	}

	protected void initialize() {

		initializeActionBar();
		initializeFragments();
		initializeFields();
		initializeListeners();
		initializeView();
	}

	protected void initializeFields() {

		mResetPreference = (Preference) findPreference(PREFERENCE_SETTING_RESET_PASSWORD);
		mApplicationNamePreference = (Preference) findPreference(PREFERENCE_APPLICATION_NAME);
		mApplicationVersionPreference = (Preference) findPreference(PREFERENCE_APPLICATION_VERSION);
		mTopBarCheckBoxPreference = (CheckBoxPreference) findPreference(PREFERENCE_DISPLAY_TOPBAR);
		mLockDisplayCheckBoxPreference = (CheckBoxPreference) findPreference(PREFERENCE_LOCK_DISPLAY);
		mBackUpPreference = (Preference) findPreference(PREFERENCE_SETTING_BACKUP);
		mRestorePreference = (Preference) findPreference(PREFERENCE_SETTING_RESTORE);
	}

	protected void initializeListeners() {
		mTopBarCheckBoxPreference.setOnPreferenceChangeListener(this);
		mLockDisplayCheckBoxPreference.setOnPreferenceChangeListener(this);

		mBackUpPreference.setOnPreferenceClickListener(this);
		mRestorePreference.setOnPreferenceClickListener(this);
		
		mResetPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				showChangePasswordDialog();
				return false;
			}
		});
	}

	protected void initializeView() {
		mApplicationNamePreference.setSummary(R.string.app_name);
		// TODO : AppVersion 로직 추가해야함.
		PackageInfo info;
		try {
			info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			mApplicationVersionPreference.setSummary(info.versionName.toString());
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void initializeFragments() {
	}

	// Actionbar 설정
	private void initializeActionBar() {
		this.setTitle(R.string.action_settings);
	}

	/**
	 * 패스워드 변경시 실행된다.
	 */
	private void showChangePasswordDialog() {

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.view_change_password, null);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.title_change_password);
		dialogBuilder.setCancelable(false);
		dialogBuilder.setView(layout);

		final EditText passwordEditText1 = (EditText) layout.findViewById(R.id.edittext_password1);
		final EditText passwordEditText2 = (EditText) layout.findViewById(R.id.edittext_password2);
		final EditText passwordEditText = (EditText) layout.findViewById(R.id.edittext_password);

		final CheckBox displayPasswordCheckBox = (CheckBox) layout.findViewById(R.id.checkbox_display_password);

		displayPasswordCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// 비밀번호 표시
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					passwordEditText1.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					passwordEditText2.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					// 비밀번호 미표시
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					passwordEditText1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					passwordEditText2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		dialogBuilder.setPositiveButton(android.R.string.ok, null);
		dialogBuilder.setNegativeButton(android.R.string.cancel, null);

		final AlertDialog dialog = dialogBuilder.create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface arg0) {
				Button positiveButton = ((AlertDialog) arg0).getButton(AlertDialog.BUTTON_POSITIVE);
				Button negativeButton = ((AlertDialog) arg0).getButton(AlertDialog.BUTTON_NEGATIVE);
				// Or AlertDialog.BUTTON_NEGATIVE
				positiveButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (!passwordEditText.getText().toString()
								.equals(CardInfoHelper.getPassword(SettingActivity.this))) {
							Toast.makeText(SettingActivity.this, R.string.check_your_password, Toast.LENGTH_SHORT)
									.show();
							return;
						}

						if (passwordEditText1.getText().toString().equals(passwordEditText2.getText().toString())) {
							Toast.makeText(SettingActivity.this, R.string.enrollment_success, Toast.LENGTH_SHORT)
									.show();
							CardInfoHelper.setPassword(SettingActivity.this, passwordEditText1.getText().toString());
							dialog.dismiss();
						} else {
							Toast.makeText(SettingActivity.this, R.string.enrollment_different, Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
				negativeButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});
			}
		});

		dialog.show();
	}

	
	/**
	 * 백업 다이얼로그.
	 * @params isBackup : true : 백업, false : 복구
	 */
	private void showBackupDialog( final boolean isBackup) {
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.view_backup_password, null);
								
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setCancelable(false);
		dialogBuilder.setView(layout);
								
		final EditText passwordEditText = (EditText) layout.findViewById(R.id.edittext_password);
		final TextView descriptionTextView = (TextView) layout.findViewById(R.id.textview_description);
		final TextView descriptionTextView2 = (TextView) layout.findViewById(R.id.textview_description2);
		final CheckBox displayPasswordCheckBox = (CheckBox) layout.findViewById(R.id.checkbox_display_password);
		if ( isBackup ) {
			dialogBuilder.setTitle( R.string.title_backup );
			
		} else {
			dialogBuilder.setTitle( R.string.title_restore );
			descriptionTextView.setText(R.string.dialog_restore_description);
			descriptionTextView2.setText(R.string.dialog_restore_description2);
		}
		
		dialogBuilder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String password = passwordEditText.getText().toString();
				
				boolean isSuccess = false;
				
				if ( isBackup ) {
					isSuccess = exportData( password );
					if ( isSuccess ) {
						Toast.makeText(SettingActivity.this, R.string.export_data_success, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(SettingActivity.this, R.string.export_data_failed, Toast.LENGTH_SHORT).show();
					}
				} else {
					isSuccess = importData ( password );
					if ( isSuccess ) {
						Toast.makeText(SettingActivity.this, R.string.import_data_success, Toast.LENGTH_SHORT).show();
						setResult(1000);
						SettingActivity.this.finish();
					} else {
						Toast.makeText(SettingActivity.this, R.string.import_data_failed, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		displayPasswordCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// 비밀번호 표시
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					// 비밀번호 미표시
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
		
		final AlertDialog dialog = dialogBuilder.create();
		
		dialog.show();
	}

	/**
	 * 파일 백업
	 * @param key : key
	 * @return
	 */
	public boolean exportData( String key ) {
		boolean isSuccess = false;
		File preferenceDirectory = new File(this.getFilesDir(), "../shared_prefs");
		String[] preferenceFiles;
		File directory = Environment.getExternalStoragePublicDirectory("securitycard");
		File externalFile = new File(directory, "backup.securitycard");
		File externalHash = new File(directory, "hash.securitycard");
		byte[] buf = new byte[1024];
		ZipOutputStream out = null;
		OutputStream target;
		FileInputStream in;
		
		Cipher cipher;
		
		int len;
		try {
			
			// 디렉토리가 없으면 만들고!
			if (!directory.exists()) {
				directory.mkdirs();
			}
			//존재하면 파일 삭제
//			if ( externalFile.exists() ) {
//				externalFile.delete();
//			}
//			if ( externalHash.exists() ) {
//				externalHash.delete();
//			}
			
			cipher = Cipher.getInstance(AesCrypto.TRANSFORM);
			cipher.init( Cipher.ENCRYPT_MODE, AesCrypto.getSecretKeySpec( key ) );
			
			target = new FileOutputStream( externalFile );
			target = new CipherOutputStream( target, cipher );
			
			out = new ZipOutputStream( target );
			
			preferenceFiles = preferenceDirectory.list();

			for (int i = 0; i < preferenceFiles.length; i++) {
				
				//파일을 읽어들이고
				in = new FileInputStream(preferenceDirectory.getPath() + "/" + preferenceFiles[i]);
				//파일명을 정하고
				out.putNextEntry(new ZipEntry(preferenceFiles[i]));
				//쓴다.				
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			out.close();
			
			//해시값을 추출해서 저장.
			if ( !saveHash(externalHash, externalFile) ) {
				Toast.makeText( this, R.string.compare_hash_failed, Toast.LENGTH_SHORT ).show();
				return false;
			} 
			
			isSuccess = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return isSuccess;
	}
	/**
	 * 파일 복구
	 * @param key : key
	 * @return
	 */
	public boolean importData( String key ) {
		boolean isSuccess = false;
		File preferenceDirectory = new File(this.getFilesDir(), "../shared_prefs");
		File directory = Environment.getExternalStoragePublicDirectory("securitycard");
		File externalFile = new File(directory, "backup.securitycard");
		File externalHash = new File(directory, "hash.securitycard");
		byte[] buf = new byte[1024];
		String[] preferenceFiles = null;
		ZipInputStream zis = null;
		FileOutputStream preferenceFileOutputStream = null;
		InputStream target = null;
		ZipEntry zipEntry = null;
		Cipher cipher = null;
		File preferenceFile = null;
		int len;
		try {
			
			if ( !externalFile.exists() || !externalHash.exists() ) {
				Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
				return false;
			}
			
			
			if ( compareHash(externalHash, externalFile) ) {
				Toast.makeText( this, R.string.compare_hash_success, Toast.LENGTH_SHORT ).show();
			} else {
				Toast.makeText( this, R.string.compare_hash_failed, Toast.LENGTH_SHORT ).show();
				return false;
			}
			
			cipher = Cipher.getInstance(AesCrypto.TRANSFORM);
			cipher.init( Cipher.DECRYPT_MODE, AesCrypto.getSecretKeySpec( key ) );
			
			target = new FileInputStream( externalFile );
			target = new CipherInputStream( target, cipher );
			zis = new ZipInputStream( target );
			
			preferenceFiles = preferenceDirectory.list();
			
			for ( String c : preferenceFiles ) {
				preferenceFile = new File(c);
				if ( preferenceFile.exists() ) {
					preferenceFile.deleteOnExit();
				}
			}
			
			while ( ( zipEntry = zis.getNextEntry() ) != null ) {
				
				preferenceFile = new File( preferenceDirectory.getPath() + "/" + zipEntry.getName() );
				preferenceFileOutputStream = new FileOutputStream( preferenceFile );
//				cipherOutputStream = new CipherOutputStream( preferenceFileOutputStream, cipher );
				
				while ( ( len = zis.read( buf ) ) != -1 ) {
					preferenceFileOutputStream.write( buf );
				}
				preferenceFileOutputStream.close();
			}
			isSuccess = true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (target != null) {
					target.close();
				}
				if (zis != null) {
					zis.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return isSuccess;
	}
	
	/**
	 * 해시값을 추출해서 저장한다.
	 * @param externalHash : 저장할 해시 경로
	 * @param externalFile : 해시값을 추출할 파일
	 */
	public boolean saveHash ( File externalHash, File externalFile ) {
		
		boolean isSuccess = false;
		
		FileInputStream in = null;
		FileOutputStream fos = null;
		MessageDigest md;
		byte[] buf = new byte[1024];
		int length;
		
		try {
			
			md = MessageDigest.getInstance("MD5");
			in = new FileInputStream(externalFile);
			  DigestInputStream dis = new DigestInputStream(in, md);
			  /* Read stream to EOF as normal... */
			byte[] digest = md.digest();
			fos = new FileOutputStream(externalHash);
			
			if (digest.length > 0) {
				fos.write(digest, 0, digest.length);
			}
			
			in.close();
			fos.close();
			
			isSuccess  = true;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return isSuccess;
		
	}
	/**
	 * 해시값을 비교한다.
	 * @param externalHash : 저장할 해시 경로
	 */
	@SuppressWarnings("resource")
	public boolean compareHash ( File externalHash, File externalFile ) {
		
		boolean isSuccess = false;
		
		FileInputStream externalFileInputStream = null;
		FileInputStream externalHashInputStream = null;
		MessageDigest md;
		byte[] buf = new byte[1024];
		byte[] externalHashDigest = new byte[16];
		
		try {
			
			md = MessageDigest.getInstance("MD5");
			externalFileInputStream = new FileInputStream(externalFile);
			DigestInputStream dis = new DigestInputStream(externalFileInputStream, md);
			/* Read stream to EOF as normal... */
			byte[] externalFileDigest = md.digest();
			
			externalHashInputStream = new FileInputStream( externalHash );
			externalHashInputStream.read(externalHashDigest);
			
			for ( int i = 0 ; i < externalFileDigest.length; i++ ) {
				if ( externalFileDigest[i] != externalHashDigest[i] ) {
					return false;
				}
			}
			
			isSuccess =  true;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
		return isSuccess;
		
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		
		String key = preference.getKey();
		
		if (key.equals(PREFERENCE_SETTING_BACKUP)) {

			showBackupDialog( true );
		} else if (key.equals(PREFERENCE_SETTING_RESTORE)) {

			showBackupDialog( false );
		}
		
		return true;
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = preference.getKey();

		if (key.equals(PREFERENCE_DISPLAY_TOPBAR)) {

			NotificationManager notiManager;
			notiManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

			if ((Boolean) newValue == true) {
				Intent intent = new Intent(this, AutoStartReceiver.class);
				sendBroadcast(intent);
			} else {
				notiManager.cancel(AutoStartReceiver.NOTIFY_ID);
			}

		} else if (key.equals(PREFERENCE_LOCK_DISPLAY)) {

		}
		return true;
	}

}
