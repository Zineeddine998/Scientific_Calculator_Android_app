package com.baoyz.swipemenulistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SwipeMenuListView extends ListView {
    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_RIGHT = -1;
    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;
    private int MAX_X = 3;
    private int MAX_Y = 5;
    private Interpolator mCloseInterpolator;
    private int mDirection = 1;
    private float mDownX;
    private float mDownY;
    private SwipeMenuCreator mMenuCreator;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnMenuStateChangeListener mOnMenuStateChangeListener;
    private OnSwipeListener mOnSwipeListener;
    private Interpolator mOpenInterpolator;
    private int mTouchPosition;
    private int mTouchState;
    private SwipeMenuLayout mTouchView;

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int i2);
    }

    public interface OnMenuStateChangeListener {
        void onMenuClose(int i);

        void onMenuOpen(int i);
    }

    public interface OnSwipeListener {
        void onSwipeEnd(int i);

        void onSwipeStart(int i);
    }

    public SwipeMenuListView(Context context) {
        super(context);
        init();
    }

    public SwipeMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeMenuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(1, (float) dp, getContext().getResources().getDisplayMetrics());
    }

    private void init() {
        this.MAX_X = dp2px(this.MAX_X);
        this.MAX_Y = dp2px(this.MAX_Y);
        this.mTouchState = 0;
    }

    public Interpolator getCloseInterpolator() {
        return this.mCloseInterpolator;
    }

    public Interpolator getOpenInterpolator() {
        return this.mOpenInterpolator;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0 && this.mTouchView == null) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case 0:
                int oldPos = this.mTouchPosition;
                this.mDownX = ev.getX();
                this.mDownY = ev.getY();
                this.mTouchState = 0;
                this.mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
                if (this.mTouchPosition == oldPos && this.mTouchView != null && this.mTouchView.isOpen()) {
                    this.mTouchState = 1;
                    this.mTouchView.onSwipe(ev);
                    return true;
                }
                View view = getChildAt(this.mTouchPosition - getFirstVisiblePosition());
                if (this.mTouchView == null || !this.mTouchView.isOpen()) {
                    if (view instanceof SwipeMenuLayout) {
                        this.mTouchView = (SwipeMenuLayout) view;
                        this.mTouchView.setSwipeDirection(this.mDirection);
                    }
                    if (this.mTouchView != null) {
                        this.mTouchView.onSwipe(ev);
                        break;
                    }
                }
                this.mTouchView.smoothCloseMenu();
                this.mTouchView = null;
                MotionEvent cancelEvent = MotionEvent.obtain(ev);
                cancelEvent.setAction(3);
                onTouchEvent(cancelEvent);
                if (this.mOnMenuStateChangeListener == null) {
                    return true;
                }
                this.mOnMenuStateChangeListener.onMenuClose(oldPos);
                return true;
                break;
            case 1:
                if (this.mTouchState == 1) {
                    if (this.mTouchView != null) {
                        boolean isBeforeOpen = this.mTouchView.isOpen();
                        this.mTouchView.onSwipe(ev);
                        boolean isAfterOpen = this.mTouchView.isOpen();
                        if (!(isBeforeOpen == isAfterOpen || this.mOnMenuStateChangeListener == null)) {
                            if (isAfterOpen) {
                                this.mOnMenuStateChangeListener.onMenuOpen(this.mTouchPosition);
                            } else {
                                this.mOnMenuStateChangeListener.onMenuClose(this.mTouchPosition);
                            }
                        }
                        if (!isAfterOpen) {
                            this.mTouchPosition = -1;
                            this.mTouchView = null;
                        }
                    }
                    if (this.mOnSwipeListener != null) {
                        this.mOnSwipeListener.onSwipeEnd(this.mTouchPosition);
                    }
                    ev.setAction(3);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
            case 2:
                float dy = Math.abs(ev.getY() - this.mDownY);
                float dx = Math.abs(ev.getX() - this.mDownX);
                if (this.mTouchState != 1) {
                    if (this.mTouchState == 0) {
                        if (Math.abs(dy) <= ((float) this.MAX_Y)) {
                            if (dx > ((float) this.MAX_X)) {
                                this.mTouchState = 1;
                                if (this.mOnSwipeListener != null) {
                                    this.mOnSwipeListener.onSwipeStart(this.mTouchPosition);
                                    break;
                                }
                            }
                        }
                        this.mTouchState = 2;
                        break;
                    }
                }
                if (this.mTouchView != null) {
                    this.mTouchView.onSwipe(ev);
                }
                getSelector().setState(new int[]{0});
                ev.setAction(3);
                super.onTouchEvent(ev);
                return true;
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new SwipeMenuAdapter(getContext(), adapter) {
            public void createMenu(SwipeMenu menu) {
                if (SwipeMenuListView.this.mMenuCreator != null) {
                    SwipeMenuListView.this.mMenuCreator.create(menu);
                }
            }

            public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
                boolean flag = false;
                if (SwipeMenuListView.this.mOnMenuItemClickListener != null) {
                    flag = SwipeMenuListView.this.mOnMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
                }
                if (SwipeMenuListView.this.mTouchView != null && !flag) {
                    SwipeMenuListView.this.mTouchView.smoothCloseMenu();
                }
            }
        });
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        this.mCloseInterpolator = interpolator;
    }

    public void setMenuCreator(SwipeMenuCreator menuCreator) {
        this.mMenuCreator = menuCreator;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOnMenuStateChangeListener(OnMenuStateChangeListener onMenuStateChangeListener) {
        this.mOnMenuStateChangeListener = onMenuStateChangeListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipeListener = onSwipeListener;
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        this.mOpenInterpolator = interpolator;
    }

    public void setSwipeDirection(int direction) {
        this.mDirection = direction;
    }

    public void smoothCloseMenu() {
        if (this.mTouchView != null && this.mTouchView.isOpen()) {
            this.mTouchView.smoothCloseMenu();
        }
    }

    public void smoothOpenMenu(int position) {
        if (position >= getFirstVisiblePosition() && position <= getLastVisiblePosition()) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof SwipeMenuLayout) {
                this.mTouchPosition = position;
                if (this.mTouchView != null && this.mTouchView.isOpen()) {
                    this.mTouchView.smoothCloseMenu();
                }
                this.mTouchView = (SwipeMenuLayout) view;
                this.mTouchView.setSwipeDirection(this.mDirection);
                this.mTouchView.smoothOpenMenu();
            }
        }
    }
}
