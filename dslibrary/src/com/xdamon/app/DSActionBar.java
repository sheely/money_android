package com.xdamon.app;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.xdamon.library.R;
import com.xdamon.util.Pix2Utils;

public class DSActionBar extends FrameLayout {

    private RelativeLayout titleContainer;
    private FrameLayout progressContainer;
    private LinearLayout actionMenuContainer, homeAsUpContainer;
    private TextView titleView, subTitleView, progressTextView;
    private ProgressBar horizontalProgressBar;
    private ImageButton homeAsUp;
    private ProgressBarType barType = ProgressBarType.NONE;

    public DSActionBar(Context context) {
        super(context);
    }

    public DSActionBar(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleContainer = (RelativeLayout) findViewById(R.id.ds_title_container);
        homeAsUpContainer = (LinearLayout) findViewById(R.id.ds_home_as_up_container);
        actionMenuContainer = (LinearLayout) findViewById(R.id.ds_action_bar_menu);
        progressContainer = (FrameLayout) findViewById(R.id.ds_progress_container);

        titleView = (TextView) findViewById(R.id.ds_title);
        subTitleView = (TextView) findViewById(R.id.ds_subtitle);
        progressTextView = (TextView) findViewById(R.id.ds_progress_text);
        horizontalProgressBar = (ProgressBar) findViewById(R.id.ds_horizontal_progressbar);

        homeAsUp = (ImageButton) findViewById(R.id.ds_home_as_up);

        setProgressBarType(ProgressBarType.NONE);
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

    public void setHomeAsUpText(String text, OnClickListener listener) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextColor(getResources().getColor(R.color.actionbarTitleColor));
        textView.setPadding(Pix2Utils.dip2px(getContext(), 15), 0, 0, 0);
        textView.setTextAppearance(getContext(), android.R.attr.textAppearanceMedium);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        setCustomHomeAsUpView(textView, listener);
    }

    public void setCustomHomeAsUpView(View view, OnClickListener listener) {
        if (homeAsUpContainer != null) {
            homeAsUpContainer.removeAllViews();

            view.setOnClickListener(listener);
            homeAsUpContainer.addView(view);
        }
    }

    public void setCustomTitleView(View view) {
        if (titleContainer != null) {
            titleContainer.removeAllViews();
            titleContainer.addView(view);
        }
    }
    
    /**
     * 
     * @param layoutId
     * @return
     *  view inflated by {@link layoutId}
     */
    public View setCustomTitleView(int layoutId) {
        View view = LayoutInflater.from(getContext()).inflate(layoutId, titleContainer, false);
        setCustomTitleView(view);
        return view;
    }

    public void setProgressBarType(ProgressBarType type) {
        barType = type;
        resetProgressBar();
    }

    private void resetProgressBar() {
        progressTextView.setText(null);
        progressContainer.setVisibility(GONE);

        horizontalProgressBar.setMax(100);
        horizontalProgressBar.setProgress(0);
        horizontalProgressBar.setVisibility(GONE);
    }

    /**
     * 
     * @param value
     *            1~100
     */
    public void setProgressValue(int value) {
        if (value < 1 || value >= 100) {
            resetProgressBar();
            return;
        }
        if (barType == ProgressBarType.HORIZONTAL) {
            horizontalProgressBar.setVisibility(View.VISIBLE);
            horizontalProgressBar.setProgress(value);
        } else if (barType == ProgressBarType.CIRCLE) {
            progressContainer.setVisibility(View.VISIBLE);
            progressTextView.setText(value + "%");
        }
    }

    /**
     * 
     * @param drawableId
     * @param tag
     * @param listener
     */
    public void addAction(int drawableId, String tag, OnClickListener listener) {
        ImageButton imageButton = new ImageButton(getContext());
        imageButton.setBackgroundResource(android.R.color.transparent);
        imageButton.setImageDrawable(getResources().getDrawable(drawableId));
        addAction(imageButton, tag, listener);
    }

    /**
     * 
     * @param title
     * @param tag
     * @param listener
     */
    public void addAction(String title, String tag, OnClickListener listener) {
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setTextColor(getResources().getColor(R.color.actionbarTitleColor));
        textView.setPadding(0, 0, Pix2Utils.dip2px(getContext(), 15), 0);
        textView.setTextAppearance(getContext(), android.R.attr.textAppearanceMedium);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        addAction(textView, tag, listener);
    }

    /**
     * 
     * @param view
     * @param tag
     * @param listener
     */
    public void addAction(View view, String tag, OnClickListener listener) {
        if (view == null || actionMenuContainer == null) {
            return;
        }
        if (TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("tag can not be null or empty");
        }
        if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) view.getLayoutParams()).gravity = Gravity.CENTER_VERTICAL;
        }
        view.setOnClickListener(listener);
        view.setTag(tag);
        View child = findAction(tag);
        int index = 0;
        if (child != null) {
            index = actionMenuContainer.indexOfChild(child);
            actionMenuContainer.removeView(child);
        } else {
            index = actionMenuContainer.getChildCount();
        }
        actionMenuContainer.addView(view, index);
    }

    public View findAction(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return null;
        }
        if (actionMenuContainer == null) {
            return null;
        }
        View view = null;
        for (int i = 0; i < actionMenuContainer.getChildCount(); i++) {
            view = actionMenuContainer.getChildAt(i);
            if (tag.equals(view.getTag())) {
                return view;
            }
        }
        return null;
    }

    public void removeAction(String tag) {
        View view = findAction(tag);
        if (view != null) {
            actionMenuContainer.removeView(view);
        }
    }

    public void removeAllAction() {
        if (actionMenuContainer == null) {
            return;
        }
        int count = actionMenuContainer.getChildCount();
        if (count > 1) {
            actionMenuContainer.removeViews(1, count - 1);
        }
    }

    // =====hide or show===
    private ObjectAnimator objectAnimator;

    final void show() {
        show(false);
    }

    final void show(boolean animated) {
        if (!animated) {
            setVisibility(View.VISIBLE);
            return;
        }
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

    final void hide() {
        hide(false);
    }

    final void hide(boolean animated) {
        if (!animated) {
            setVisibility(View.GONE);
            return;
        }
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

    public static enum ProgressBarType {
        NONE, HORIZONTAL, CIRCLE
    }
}
