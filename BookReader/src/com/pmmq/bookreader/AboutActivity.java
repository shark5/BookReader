package com.pmmq.bookreader;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setTitle(getResources().getString(R.string.about_book));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
    
    @Override
    public void onPause() {
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
	
	
}
