package com.fimtrus.securitycard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.view.MenuItem;
import com.fimtrus.securitycard.R;
import com.fimtrus.securitycard.fragment.LeftSlidingFragment;
import com.fimtrus.securitycard.fragment.MainFragment;
import com.jhlibrary.slidingmenu.activity.SlidingBaseActivity;
import com.jhlibrary.util.Util;

public class MainActivity extends SlidingBaseActivity {

	private LeftSlidingFragment mLeftSlidingFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Util.updateCheck(this); 
		
		addLeftSlidingMenu(savedInstanceState, R.layout.fragment_left_sliding, R.id.fragment_left_sliding,
				mLeftSlidingFragment = new LeftSlidingFragment());

		// addLeftRightSlidingMenu(savedInstanceState,
		// R.layout.fragment_left_sliding,
		// R.layout.fragment_right_sliding, R.id.fragment_left_sliding,
		// R.id.fragment_right_sliding,
		// new LeftSlidingFragment(), new RightSlidingFragment());
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
		getSupportActionBar().setTitle("보안카드");
		// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// Util.registerGcm(this);

	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// TODO Auto-generated method stub
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent( this, SettingActivity.class );
			startActivityForResult(intent, 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent ) {
		super.onActivityResult(requestCode, responseCode, intent);
		
		if ( requestCode == 0 && responseCode == 1000 ) {
			this.finish();
			System.exit(0);
		}
	}
	
	public void notifyDataChanged() {
		mLeftSlidingFragment.notifyDataChanged();
	}
}
