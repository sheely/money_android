package com.damon.ds.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.damon.ds.library.R;

public class BasicSingleItem extends LinearLayout {

	protected TextView titleTextView;
	protected TextView subTitleTextView;
	protected ImageView iconImageView;
	protected TextView countTextView;
	protected ImageView moreImageView;

	public BasicSingleItem(Context context) {
		this(context, null);
	}

	public BasicSingleItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(context, R.layout.basic_single_item, this);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BasicSingleItem);

		int iconResId = array.getResourceId(R.styleable.BasicSingleItem_item_icon, 0);

		String title = array.getString(R.styleable.BasicSingleItem_item_title);
		int defSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources()
				.getDisplayMetrics());
		int titleSize = array.getDimensionPixelSize(R.styleable.BasicSingleItem_item_titleSize, defSize);
		int titleColor = array.getColor(R.styleable.BasicSingleItem_item_titleColor, 0xff323232);

		String subTitle = array.getString(R.styleable.BasicSingleItem_item_subTitle);
		defSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
		int subTitleSize = array.getDimensionPixelSize(R.styleable.BasicSingleItem_item_subTitleSize, defSize);
		int subTitleColor = array.getColor(R.styleable.BasicSingleItem_item_subTitleColor, 0xff878787);

		String count = array.getString(R.styleable.BasicSingleItem_item_count);
		defSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics());
		int countSize = array.getDimensionPixelSize(R.styleable.BasicSingleItem_item_countSize, defSize);
		int countColor = array.getColor(R.styleable.BasicSingleItem_item_countColor, 0xff878787);
		
		boolean clickable = array.getBoolean(R.styleable.BasicSingleItem_item_clickable, true);
		array.recycle();

		iconImageView = (ImageView) findViewById(R.id.icon);
		setLeftImageView(iconResId);

		titleTextView = (TextView) findViewById(R.id.title);
		setTitle(title);
		setTitleSize(titleSize);
		setTitleColor(titleColor);

		subTitleTextView = (TextView) findViewById(R.id.subtitle);
		setSubTitle(subTitle);
		setSubTitleSize(subTitleSize);
		setSubTitleColor(subTitleColor);

		countTextView = (TextView) findViewById(R.id.count);
		setCount(count);
		setCountColor(countColor);
		setCountSize(countSize);

		moreImageView = (ImageView) findViewById(R.id.more);
		setClickable(clickable);
	}

	public ImageView getLeftImageView() {
		return iconImageView;
	}

	public TextView getTitleView() {
		return titleTextView;
	}

	public TextView getSubTitleView() {
		return subTitleTextView;
	}

	public TextView getCountView() {
		return countTextView;
	}

	public ImageView getRightImageView() {
		return moreImageView;
	}

	public void setLeftImageView(int resId) {
		if (resId > 0) {
			iconImageView.setImageResource(resId);
		}
		iconImageView.setVisibility(resId == 0 ? GONE : VISIBLE);
	}

	public void setTitle(String title) {
		titleTextView.setText(title);
	}

	public void setTitleSize(int size) {
		titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}

	public void setTitleColor(int color) {
		titleTextView.setTextColor(color);
	}

	public void setSubTitle(String title) {
		subTitleTextView.setText(title);
		if (TextUtils.isEmpty(title)) {
			subTitleTextView.setVisibility(GONE);
		} else {
			subTitleTextView.setVisibility(VISIBLE);
		}
	}

	public void setSubTitleSize(int size) {
		subTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}

	public void setSubTitleColor(int color) {
		subTitleTextView.setTextColor(color);
	}

	public void setCount(String count) {
		if (!TextUtils.isEmpty(count) && "0".equals(count.trim())) {
			count = "";
		}
		countTextView.setText(count);
		if (TextUtils.isEmpty(count)) {
			countTextView.setVisibility(GONE);
		} else {
			countTextView.setVisibility(VISIBLE);
		}
	}

	public void setCountSize(int size) {
		countTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}

	public void setCountColor(int value) {
		countTextView.setTextColor(value);
	}

	@Override
	public void setClickable(boolean clickable) {
		super.setClickable(clickable);
		moreImageView.setVisibility(clickable ? VISIBLE : GONE);
	}

}
