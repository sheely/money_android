package com.damon.ds.ext;

/*
 * Copyright (C) 2010 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.damon.ds.library.R;

public class ParallaxLayout extends FrameLayout {
	//////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE CONSTANTS
	//////////////////////////////////////////////////////////////////////////////////////

	private static final float DEFAULT_FACTOR = 0.66f;

	//////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE MEMBERS
	//////////////////////////////////////////////////////////////////////////////////////

	private float mFactor = DEFAULT_FACTOR;

	//////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	//////////////////////////////////////////////////////////////////////////////////////

	public ParallaxLayout(Context context) {
		super(context);
		init(context, null, 0);
	}

	public ParallaxLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public ParallaxLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// OVERRIDES
	//////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		ViewParent parent = getParent();
		while (parent != null) {
			if (parent instanceof ObservableScrollView) {
				((ObservableScrollView) parent).addOnScrollChangedListener(mOnScrollChangedListener);
			}

			parent = parent.getParent();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	//////////////////////////////////////////////////////////////////////////////////////

	private void init(Context context, AttributeSet attrs, int defStyle) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ParallaxLayout);
		setFactor(a.getFloat(R.styleable.ParallaxLayout_factor, DEFAULT_FACTOR));
		a.recycle();
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	//////////////////////////////////////////////////////////////////////////////////////

	public void setFactor(float factor) {
		mFactor = factor;
	}

	public float getFactor() {
		return mFactor;
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// LISTENERS
	//////////////////////////////////////////////////////////////////////////////////////

	private final ObservableScrollView.OnScrollChangedListener mOnScrollChangedListener = new ObservableScrollView.OnScrollChangedListener() {
		@Override
		public void onScrollChanged(int l, int t, int oldxl, int oldt) {
			if (isEnabled()) {
				final int scrollY = (int) (t * (1f - mFactor) * -1f);
				scrollTo(0, scrollY);
			}
		}
	};
}
