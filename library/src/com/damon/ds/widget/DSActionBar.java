package com.damon.ds.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.damon.ds.library.R;
import com.damon.ds.util.Pix2Utils;
import com.nineoldandroids.animation.ObjectAnimator;

public class DSActionBar extends FrameLayout {

	private FrameLayout titleContainer, homeAsUpContainer;
	private LinearLayout actionMenuContainer;
	private TextView titleView, subTitleView;
	private ImageButton homeAsUp;

	public DSActionBar(Context context) {
		super(context);
	}

	public DSActionBar(Context context, AttributeSet attr) {
		super(context, attr);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		titleContainer = (FrameLayout) findViewById(R.id.ds_title_container);
		homeAsUpContainer = (FrameLayout) findViewById(R.id.ds_home_as_up_container);
		actionMenuContainer = (LinearLayout) findViewById(R.id.ds_action_bar_menu);

		titleView = (TextView) findViewById(R.id.ds_title);
		if (titleView == null) {
			titleView = (TextView) findViewById(android.R.id.title);
		}
		subTitleView = (TextView) findViewById(R.id.ds_subtitle);

		homeAsUp = (ImageButton) findViewById(R.id.ds_home_as_up);
	}

	public void setTitle(CharSequence title) {
		if (titleView != null) {
			titleView.setText(title);
		}
	}

	public void setSubTitle(CharSequence title) {
		if (subTitleView == null) {
			return;
		}
		if (!TextUtils.isEmpty(title)) {
			subTitleView.setText(title);
			subTitleView.setVisibility(VISIBLE);
		} else {
			subTitleView.setVisibility(GONE);
		}
	}

	public void setDisplayHomeAsUpEnabled(boolean enable) {
		if (homeAsUpContainer != null) {
			homeAsUpContainer.setVisibility(enable ? VISIBLE : INVISIBLE);
		}
	}

	public void setHomeAsUpListener(OnClickListener listener) {
		if (homeAsUp != null) {
			homeAsUp.setOnClickListener(listener);
		}
	}

	public void setHomeAsUpResource(int drawableId) {
		if (homeAsUp != null) {
			homeAsUp.setImageDrawable(getResources().getDrawable(drawableId));
		}
	}

	public void setCustomHomeAsUpView(View view) {
		if (homeAsUpContainer != null) {
			homeAsUpContainer.removeAllViews();
			homeAsUpContainer.addView(view);
		}
	}

	public void setCustomTitleView(View view) {
		if (titleContainer != null) {
			titleContainer.removeAllViews();
			titleContainer.addView(view);
		}
	}

	public void addAction(int drawableId, String tag, OnClickListener listener) {
		ImageButton imageButton = new ImageButton(getContext());
		imageButton.setBackgroundResource(android.R.color.transparent);
		imageButton.setImageDrawable(getResources().getDrawable(drawableId));
		addAction(imageButton, tag, listener);
	}

	public void addAction(String title, String tag, OnClickListener listener) {
		TextView textView = new TextView(getContext());
		textView.setText(title);
		textView.setTextColor(getResources().getColor(R.color.white));
		textView.setPadding(Pix2Utils.dip2px(getContext(), 5), 0, 0, 0);
		textView.setTextAppearance(getContext(), android.R.attr.textAppearanceSmall);
		addAction(textView, tag, listener);
	}

	public void addAction(View view, String tag, OnClickListener listener) {
		if (view == null || actionMenuContainer == null) {
			return;
		}
		if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
			((LinearLayout.LayoutParams) view.getLayoutParams()).gravity = Gravity.CENTER_VERTICAL;
		}
		view.setOnClickListener(listener);
		view.setTag(tag);
		final int index = actionMenuContainer.getChildCount();
		actionMenuContainer.addView(view, index);
	}

	public View findAction(String tag) {
		if (TextUtils.isEmpty(tag)) {
			return null;
		}
		if (actionMenuContainer == null) {
			return null;
		}
		return actionMenuContainer.findViewWithTag(tag);
	}

	public void removeAction(String tag) {
		View view = findAction(tag);
		if (view != null) {
			actionMenuContainer.removeView(view);
		}
	}

	public void removeAllAction() {
		actionMenuContainer.removeAllViews();
	}

	// =====hide or show===
	private ObjectAnimator objectAnimator;

	public void show() {
		objectAnimator = ObjectAnimator.ofFloat(this, "y", 0);
		objectAnimator.setDuration(400);
		objectAnimator.start();
		postDelayed(new Runnable() {

			@Override
			public void run() {
				setVisibility(View.VISIBLE);
			}
		}, 100);

	}

	public void hide() {
		objectAnimator = ObjectAnimator.ofFloat(this, "y", -getHeight());
		objectAnimator.setDuration(400);
		objectAnimator.start();
		postDelayed(new Runnable() {

			@Override
			public void run() {
				setVisibility(View.GONE);
			}
		}, 350);

	}
}
