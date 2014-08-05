package com.xdamon.widget;

import com.xdamon.library.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class LoadingView extends ImageView {

	public LoadingView(Context context) {
		super(context);
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_rotate_alpha); 
		animation.setInterpolator(new LinearInterpolator());
		startAnimation(animation);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		clearAnimation();
	}

}
