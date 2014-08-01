package com.damon.ds.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.damon.ds.adapter.CustomGridViewAdapter;
import com.damon.ds.library.R;

public class CustomGridView extends TableLayout implements View.OnClickListener {

    private OnItemClickListener mClickListener;
	private CustomGridViewAdapter adapter;
    private TableRow curRow;
    private Drawable horizontalDivider = new ColorDrawable(getResources().getColor(R.color.gray));
    private Drawable verticalDivider = new ColorDrawable(getResources().getColor(R.color.gray));
    private Drawable endHorizontalDivider = new ColorDrawable(getResources().getColor(R.color.gray));
    /** 是否需要隐藏边界 */
    private boolean needHideDivider = false;

    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what != 1)
				return;
			if (adapter == null || adapter.isEmpty()) {
				removeAllViews();
				return;
			}
            removeAllViews();
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, CustomGridView.this);
                if (view instanceof TableRow) {
                    ((TableRow) view).setBaselineAligned(false);
                    addView(view);
                    curRow = (TableRow) view;
                } else if (view != null) {
                    if (curRow != null) {
                        curRow.addView(view);
                    }
                }

            }
		}
	};

	private final DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1, 100);
        }

        @Override
        public void onInvalidated() {
            onChanged();
        }
	};

    public CustomGridView(Context context) {
        super(context);
    }

	public CustomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    public CustomGridViewAdapter getAdapter() {
        return adapter;
    }

	public void setAdapter(CustomGridViewAdapter adapter) {
		if (this.adapter != null) {
			this.adapter.unregisterDataSetObserver(observer);
		}
		this.adapter = adapter;
		if (this.adapter != null)
			this.adapter.registerDataSetObserver(observer);
		removeAllViews();
		observer.onChanged();
	}

    public TableRow getCurRow() {
        return curRow;
    }

    public void setCurRow(TableRow curRow) {
        this.curRow = curRow;
    }

    public boolean isNeedHideDivider() {
        return needHideDivider;
    }

    public void setNeedHideDivider(boolean needHideDivider) {
        this.needHideDivider = needHideDivider;
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            int position = -1;
            long id = -1;
            outer:
            for (int i = 0, curPosition = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (v == view) {
                    position = curPosition;
                    break;
                }
                if (view instanceof TableRow) {
                    TableRow tableRow = (TableRow) view;
                    for (int j = 0; j < tableRow.getChildCount(); j++, curPosition++) {
                        View child = tableRow.getChildAt(j);
                        if (v == child) {
                            position = curPosition;
                            break outer;
                        }
                    }
                } else {
                    curPosition++;
                }
            }
            if (position < 0)
                return;
            if (adapter != null)
                id = adapter.getItemId(position);
            if (id == -1) {
                id = v.getId();
            }
            mClickListener.onItemClick(this, v, position, id);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CustomGridView parent, View view, int position, long id);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TableRow) {
                TableRow tableRow = (TableRow) view;
                for (int j = 0; j < tableRow.getChildCount(); j++) {
                    View child = tableRow.getChildAt(j);
                    setChildOnClickListener(child);
                }
            } else {
                setChildOnClickListener(view);
            }
        }
    }

    private void setChildOnClickListener(View child) {
        if (child != null && child.getVisibility() == View.VISIBLE && child.isClickable()) {
            child.setOnClickListener(this);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!needHideDivider) {
            if (horizontalDivider != null || verticalDivider != null) {
                for (int i = 0; i < getChildCount(); i++) {
                    View view = getChildAt(i);
                    if (view instanceof TableRow) {
                        TableRow tableRow = (TableRow) view;
                        for (int j = 0; j < tableRow.getChildCount(); j++) {
                            View child = tableRow.getChildAt(j);
                            showDivider(canvas, tableRow, child, i == getChildCount() - 1);
                        }
                    } else {
                        showDivider(canvas, null, view, i == getChildCount() - 1);
                    }
                }
            }
        }
    }

    public Drawable getHorizontalDivider() {
        return horizontalDivider;
    }

    public void setHorizontalDivider(Drawable horizontalDivider) {
        if (horizontalDivider == this.horizontalDivider) {
            return;
        }
        this.horizontalDivider = horizontalDivider;
        requestLayout();
    }

    public Drawable getVerticalDivider() {
        return verticalDivider;
    }

    public void setVerticalDivider(Drawable verticalDivider) {
        if (verticalDivider == this.verticalDivider) {
            return;
        }
        this.verticalDivider = verticalDivider;
        requestLayout();
    }

    public Drawable getEndHorizontalDivider() {
        return endHorizontalDivider;
    }

    public void setEndHorizontalDivider(Drawable endHorizontalDivider) {
        this.endHorizontalDivider = endHorizontalDivider;
    }

    private void drawDivider(Canvas canvas, Drawable divider, Rect bounds) {
        if (divider != null) {
            divider.setBounds(bounds);
            divider.draw(canvas);
        }
    }

    private void showDivider(Canvas canvas, View parent, View child, boolean isEnd) {
        if (child != null && child.getVisibility() == View.VISIBLE) {
            final Rect bounds = new Rect();
            if (isEnd) {
                if (endHorizontalDivider != null) {
                    int height = endHorizontalDivider.getIntrinsicHeight();
                    if (height > 0) {
                        if (parent != null) {
                            bounds.left = parent.getLeft() + child.getLeft();
                            bounds.top = parent.getTop() + child.getBottom() - height;
                            bounds.right = parent.getLeft() + child.getRight();
                            bounds.bottom = parent.getTop() + child.getBottom();
                        } else {
                            bounds.left = child.getLeft();
                            bounds.top = child.getBottom() - height;
                            bounds.right = child.getRight();
                            bounds.bottom = child.getBottom();
                        }
                        drawDivider(canvas, endHorizontalDivider, bounds);
                    }
                }
            } else {
                if (horizontalDivider != null) {
                    int height = horizontalDivider.getIntrinsicHeight();
                    if (height > 0) {
                        if (parent != null) {
                            bounds.left = parent.getLeft() + child.getLeft();
                            bounds.top = parent.getTop() + child.getBottom() - height;
                            bounds.right = parent.getLeft() + child.getRight();
                            bounds.bottom = parent.getTop() + child.getBottom();
                        } else {
                            bounds.left = child.getLeft();
                            bounds.top = child.getBottom() - height;
                            bounds.right = child.getRight();
                            bounds.bottom = child.getBottom();
                        }
                        drawDivider(canvas, horizontalDivider, bounds);
                    }
                }
            }
            if (verticalDivider != null) {
                int width = verticalDivider.getIntrinsicWidth();
                if (width > 0) {
                    if (parent != null) {
                        bounds.left = parent.getLeft() + child.getRight();
                        bounds.top = parent.getTop() + child.getTop();
                        bounds.right = parent.getLeft() + child.getRight() + width;
                        bounds.bottom = parent.getTop() + child.getBottom();
                    } else {
                        bounds.left = child.getRight();
                        bounds.top = child.getTop();
                        bounds.right = child.getRight() + width;
                        bounds.bottom = child.getBottom();
                    }
                    drawDivider(canvas, verticalDivider, bounds);
                }
            }
        }
    }

    public Object getItemAtPosition(int position) {
        return (adapter == null || position < 0) ? null : adapter
                .getItem(position);
    }

}
