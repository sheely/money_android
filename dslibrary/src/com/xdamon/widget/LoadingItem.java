package com.xdamon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.xdamon.library.R;

public class LoadingItem extends LinearLayout{
	
	public LoadingItem(Context context) {
		this(context, null);
	}

	public LoadingItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_rotate_alpha); 
		animation.setInterpolator(new LinearInterpolator());
		findViewById(R.id.anim_icon).startAnimation(animation);
	}

}
