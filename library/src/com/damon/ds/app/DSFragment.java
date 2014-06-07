package com.damon.ds.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class DSFragment extends Fragment {
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(!(activity instanceof DSActivity)){
			throw new IllegalArgumentException("DSFragment must attach to DSActivity!");
		}
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

}
