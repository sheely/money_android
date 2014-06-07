package com.damon.ds.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.damon.ds.library.R;

public class TabIndicator {

	private CharSequence mLabel;
	private int mIcon;
	private int mIndicatorId;
	private Context mContext;

	public TabIndicator(Context context, CharSequence label) {
		mContext = context;
		mLabel = label;
	}

	public TabIndicator(Context context, CharSequence label, int indicatorId) {
		this(context, label);
		mIndicatorId = indicatorId;
	}

	public TabIndicator(Context context, CharSequence label, int icon, int indicatorId) {
		this(context, label, indicatorId);
		mIcon = icon;

	}

	public View createIndicatorView(TabHost mTabHost) {
		if (mIndicatorId == 0) {
			mIndicatorId = R.layout.tab_indicator;
		}
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View tabIndicator = inflater.inflate(mIndicatorId, mTabHost.getTabWidget(), false);

		final TextView tv = (TextView) tabIndicator.findViewById(android.R.id.title);
		tv.setText(mLabel);

		if (mIcon > 0) {
			final ImageView ig = (ImageView) tabIndicator.findViewById(android.R.id.icon);
			ig.setImageResource(mIcon);
			ig.setVisibility(View.VISIBLE);
		}
		return tabIndicator;
	}

}
