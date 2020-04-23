package com.sothree.slidinguppanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.sothree.slidinguppanel.ViewDragHelper.Callback;
import com.sothree.slidinguppanel.library.R;
import java.util.ArrayList;
import java.util.List;

public class SlidingUpPanelLayout extends ViewGroup {
    private static final float DEFAULT_ANCHOR_POINT = 1.0f;
    private static final int[] DEFAULT_ATTRS = new int[]{16842927};
    private static final boolean DEFAULT_CLIP_PANEL_FLAG = true;
    private static final int DEFAULT_FADE_COLOR = -1728053248;
    private static final int DEFAULT_MIN_FLING_VELOCITY = 400;
    private static final boolean DEFAULT_OVERLAY_FLAG = false;
    private static final int DEFAULT_PANEL_HEIGHT = 68;
    private static final int DEFAULT_PARALLAX_OFFSET = 0;
    private static final int DEFAULT_SHADOW_HEIGHT = 4;
    private static PanelState DEFAULT_SLIDE_STATE = PanelState.COLLAPSED;
    public static final String SLIDING_STATE = "sliding_state";
    private static final String TAG = SlidingUpPanelLayout.class.getSimpleName();
    private float mAnchorPoint;
    private boolean mClipPanel;
    private int mCoveredFadeColor;
    private final Paint mCoveredFadePaint;
    private final ViewDragHelper mDragHelper;
    private View mDragView;
    private int mDragViewResId;
    private OnClickListener mFadeOnClickListener;
    private boolean mFirstLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean mIsScrollableViewHandlingTouch;
    private boolean mIsSlidingUp;
    private boolean mIsTouchEnabled;
    private boolean mIsUnableToDrag;
    private PanelState mLastNotDraggingSlideState;
    private View mMainView;
    private int mMinFlingVelocity;
    private boolean mOverlayContent;
    private int mPanelHeight;
    private List<PanelSlideListener> mPanelSlideListeners;
    private int mParallaxOffset;
    private float mPrevMotionY;
    private View mScrollableView;
    private ScrollableViewHelper mScrollableViewHelper;
    private int mScrollableViewResId;
    private final Drawable mShadowDrawable;
    private int mShadowHeight;
    private float mSlideOffset;
    private int mSlideRange;
    private PanelState mSlideState;
    private View mSlideableView;
    private final Rect mTmpRect;

    public interface PanelSlideListener {
        void onPanelSlide(View view, float f);

        void onPanelStateChanged(View view, PanelState panelState, PanelState panelState2);
    }

    private class DragHelperCallback extends Callback {
        private DragHelperCallback() {
        }

        /* synthetic */ DragHelperCallback(SlidingUpPanelLayout x0, AnonymousClass1 x1) {
            this();
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            int collapsedTop = SlidingUpPanelLayout.this.computePanelTopPosition(0.0f);
            int expandedTop = SlidingUpPanelLayout.this.computePanelTopPosition(SlidingUpPanelLayout.DEFAULT_ANCHOR_POINT);
            if (SlidingUpPanelLayout.this.mIsSlidingUp) {
                return Math.min(Math.max(top, expandedTop), collapsedTop);
            }
            return Math.min(Math.max(top, collapsedTop), expandedTop);
        }

        public int getViewVerticalDragRange(View child) {
            return SlidingUpPanelLayout.this.mSlideRange;
        }

        public void onViewCaptured(View capturedChild, int activePointerId) {
            SlidingUpPanelLayout.this.setAllChildrenVisible();
        }

        public void onViewDragStateChanged(int state) {
            if (SlidingUpPanelLayout.this.mDragHelper.getViewDragState() == 0) {
                SlidingUpPanelLayout.this.mSlideOffset = SlidingUpPanelLayout.this.computeSlideOffset(SlidingUpPanelLayout.this.mSlideableView.getTop());
                SlidingUpPanelLayout.this.applyParallaxForCurrentSlideOffset();
                if (SlidingUpPanelLayout.this.mSlideOffset == SlidingUpPanelLayout.DEFAULT_ANCHOR_POINT) {
                    SlidingUpPanelLayout.this.updateObscuredViewVisibility();
                    SlidingUpPanelLayout.this.setPanelStateInternal(PanelState.EXPANDED);
                } else if (SlidingUpPanelLayout.this.mSlideOffset == 0.0f) {
                    SlidingUpPanelLayout.this.setPanelStateInternal(PanelState.COLLAPSED);
                } else if (SlidingUpPanelLayout.this.mSlideOffset < 0.0f) {
                    SlidingUpPanelLayout.this.setPanelStateInternal(PanelState.HIDDEN);
                    SlidingUpPanelLayout.this.mSlideableView.setVisibility(4);
                } else {
                    SlidingUpPanelLayout.this.updateObscuredViewVisibility();
                    SlidingUpPanelLayout.this.setPanelStateInternal(PanelState.ANCHORED);
                }
            }
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            SlidingUpPanelLayout.this.onPanelDragged(top);
            SlidingUpPanelLayout.this.invalidate();
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            float direction;
            int target;
            if (SlidingUpPanelLayout.this.mIsSlidingUp) {
                direction = -yvel;
            } else {
                direction = yvel;
            }
            if (direction > 0.0f && SlidingUpPanelLayout.this.mSlideOffset <= SlidingUpPanelLayout.this.mAnchorPoint) {
                target = SlidingUpPanelLayout.this.computePanelTopPosition(SlidingUpPanelLayout.this.mAnchorPoint);
            } else if (direction > 0.0f && SlidingUpPanelLayout.this.mSlideOffset > SlidingUpPanelLayout.this.mAnchorPoint) {
                target = SlidingUpPanelLayout.this.computePanelTopPosition(SlidingUpPanelLayout.DEFAULT_ANCHOR_POINT);
            } else if (direction < 0.0f && SlidingUpPanelLayout.this.mSlideOffset >= SlidingUpPanelLayout.this.mAnchorPoint) {
                target = SlidingUpPanelLayout.this.computePanelTopPosition(SlidingUpPanelLayout.this.mAnchorPoint);
            } else if (direction < 0.0f && SlidingUpPanelLayout.this.mSlideOffset < SlidingUpPanelLayout.this.mAnchorPoint) {
                target = SlidingUpPanelLayout.this.computePanelTopPosition(0.0f);
            } else if (SlidingUpPanelLayout.this.mSlideOffset >= (SlidingUpPanelLayout.this.mAnchorPoint + SlidingUpPanelLayout.DEFAULT_ANCHOR_POINT) / 2.0f) {
                target = SlidingUpPanelLayout.this.computePanelTopPosition(SlidingUpPanelLayout.DEFAULT_ANCHOR_POINT);
            } else if (SlidingUpPanelLayout.this.mSlideOffset >= SlidingUpPanelLayout.this.mAnchorPoint / 2.0f) {
                target = SlidingUpPanelLayout.this.computePanelTopPosition(SlidingUpPanelLayout.this.mAnchorPoint);
            } else {
                target = SlidingUpPanelLayout.this.computePanelTopPosition(0.0f);
            }
            SlidingUpPanelLayout.this.mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), target);
            SlidingUpPanelLayout.this.invalidate();
        }

        public boolean tryCaptureView(View child, int pointerId) {
            if (!SlidingUpPanelLayout.this.mIsUnableToDrag && child == SlidingUpPanelLayout.this.mSlideableView) {
                return SlidingUpPanelLayout.DEFAULT_CLIP_PANEL_FLAG;
            }
            return false;
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        private static final int[] ATTRS = new int[]{16843137};
        public float weight = 0.0f;

        public LayoutParams() {
            super(-1, -1);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.weight = weight;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray ta = c.obtainStyledAttributes(attrs, ATTRS);
            if (ta != null) {
                this.weight = ta.getFloat(0, 0.0f);
            }
            ta.recycle();
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }
    }

    public enum PanelState {
        EXPANDED,
        COLLAPSED,
        ANCHORED,
        HIDDEN,
        DRAGGING
    }

    public static class SimplePanelSlideListener implements PanelSlideListener {
        public void onPanelSlide(View panel, float slideOffset) {
        }

        public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        }
    }

    public SlidingUpPanelLayout(Context context) {
        this(context, null);
    }

    public SlidingUpPanelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mMinFlingVelocity = DEFAULT_MIN_FLING_VELOCITY;
        this.mCoveredFadeColor = DEFAULT_FADE_COLOR;
        this.mCoveredFadePaint = new Paint();
        this.mPanelHeight = -1;
        this.mShadowHeight = -1;
        this.mParallaxOffset = -1;
        this.mOverlayContent = false;
        this.mClipPanel = DEFAULT_CLIP_PANEL_FLAG;
        this.mDragViewResId = -1;
        this.mScrollableViewHelper = new ScrollableViewHelper();
        this.mSlideState = DEFAULT_SLIDE_STATE;
        this.mLastNotDraggingSlideState = DEFAULT_SLIDE_STATE;
        this.mAnchorPoint = DEFAULT_ANCHOR_POINT;
        this.mIsScrollableViewHandlingTouch = false;
        this.mPanelSlideListeners = new ArrayList();
        this.mFirstLayout = DEFAULT_CLIP_PANEL_FLAG;
        this.mTmpRect = new Rect();
        if (isInEditMode()) {
            this.mShadowDrawable = null;
            this.mDragHelper = null;
            return;
        }
        Interpolator scrollerInterpolator = null;
        if (attrs != null) {
            TypedArray defAttrs = context.obtainStyledAttributes(attrs, DEFAULT_ATTRS);
            if (defAttrs != null) {
                setGravity(defAttrs.getInt(0, 0));
            }
            defAttrs.recycle();
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingUpPanelLayout);
            if (ta != null) {
                this.mPanelHeight = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_umanoPanelHeight, -1);
                this.mShadowHeight = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_umanoShadowHeight, -1);
                this.mParallaxOffset = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_umanoParallaxOffset, -1);
                this.mMinFlingVelocity = ta.getInt(R.styleable.SlidingUpPanelLayout_umanoFlingVelocity, DEFAULT_MIN_FLING_VELOCITY);
                this.mCoveredFadeColor = ta.getColor(R.styleable.SlidingUpPanelLayout_umanoFadeColor, DEFAULT_FADE_COLOR);
                this.mDragViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoDragView, -1);
                this.mScrollableViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoScrollableView, -1);
                this.mOverlayContent = ta.getBoolean(R.styleable.SlidingUpPanelLayout_umanoOverlay, false);
                this.mClipPanel = ta.getBoolean(R.styleable.SlidingUpPanelLayout_umanoClipPanel, DEFAULT_CLIP_PANEL_FLAG);
                this.mAnchorPoint = ta.getFloat(R.styleable.SlidingUpPanelLayout_umanoAnchorPoint, DEFAULT_ANCHOR_POINT);
                this.mSlideState = PanelState.values()[ta.getInt(R.styleable.SlidingUpPanelLayout_umanoInitialState, DEFAULT_SLIDE_STATE.ordinal())];
                int interpolatorResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoScrollInterpolator, -1);
                if (interpolatorResId != -1) {
                    scrollerInterpolator = AnimationUtils.loadInterpolator(context, interpolatorResId);
                }
            }
            ta.recycle();
        }
        float density = context.getResources().getDisplayMetrics().density;
        if (this.mPanelHeight == -1) {
            this.mPanelHeight = (int) ((68.0f * density) + 0.5f);
        }
        if (this.mShadowHeight == -1) {
            this.mShadowHeight = (int) ((4.0f * density) + 0.5f);
        }
        if (this.mParallaxOffset == -1) {
            this.mParallaxOffset = (int) (0.0f * density);
        }
        if (this.mShadowHeight <= 0) {
            this.mShadowDrawable = null;
        } else if (this.mIsSlidingUp) {
            this.mShadowDrawable = getResources().getDrawable(R.drawable.above_shadow);
        } else {
            this.mShadowDrawable = getResources().getDrawable(R.drawable.below_shadow);
        }
        setWillNotDraw(false);
        this.mDragHelper = ViewDragHelper.create(this, 0.5f, scrollerInterpolator, new DragHelperCallback(this, null));
        this.mDragHelper.setMinVelocity(((float) this.mMinFlingVelocity) * density);
        this.mIsTouchEnabled = DEFAULT_CLIP_PANEL_FLAG;
    }

    @SuppressLint({"NewApi"})
    private void applyParallaxForCurrentSlideOffset() {
        if (this.mParallaxOffset > 0) {
            ViewCompat.setTranslationY(this.mMainView, (float) getCurrentParallaxOffset());
        }
    }

    private int computePanelTopPosition(float slideOffset) {
        int slidingViewHeight = this.mSlideableView != null ? this.mSlideableView.getMeasuredHeight() : 0;
        int slidePixelOffset = (int) (((float) this.mSlideRange) * slideOffset);
        if (this.mIsSlidingUp) {
            return ((getMeasuredHeight() - getPaddingBottom()) - this.mPanelHeight) - slidePixelOffset;
        }
        return ((getPaddingTop() - slidingViewHeight) + this.mPanelHeight) + slidePixelOffset;
    }

    private float computeSlideOffset(int topPosition) {
        int topBoundCollapsed = computePanelTopPosition(0.0f);
        return this.mIsSlidingUp ? ((float) (topBoundCollapsed - topPosition)) / ((float) this.mSlideRange) : ((float) (topPosition - topBoundCollapsed)) / ((float) this.mSlideRange);
    }

    private static boolean hasOpaqueBackground(View v) {
        Drawable bg = v.getBackground();
        return (bg == null || bg.getOpacity() != -1) ? false : DEFAULT_CLIP_PANEL_FLAG;
    }

    private boolean isViewUnder(View view, int x, int y) {
        boolean z = DEFAULT_CLIP_PANEL_FLAG;
        if (view == null) {
            return false;
        }
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        if (screenX < viewLocation[0] || screenX >= viewLocation[0] + view.getWidth() || screenY < viewLocation[1] || screenY >= viewLocation[1] + view.getHeight()) {
            z = false;
        }
        return z;
    }

    private void onPanelDragged(int newTop) {
        if (this.mSlideState != PanelState.DRAGGING) {
            this.mLastNotDraggingSlideState = this.mSlideState;
        }
        setPanelStateInternal(PanelState.DRAGGING);
        this.mSlideOffset = computeSlideOffset(newTop);
        applyParallaxForCurrentSlideOffset();
        dispatchOnPanelSlide(this.mSlideableView);
        LayoutParams lp = (LayoutParams) this.mMainView.getLayoutParams();
        int defaultHeight = ((getHeight() - getPaddingBottom()) - getPaddingTop()) - this.mPanelHeight;
        if (this.mSlideOffset <= 0.0f && !this.mOverlayContent) {
            lp.height = this.mIsSlidingUp ? newTop - getPaddingBottom() : ((getHeight() - getPaddingBottom()) - this.mSlideableView.getMeasuredHeight()) - newTop;
            if (lp.height == defaultHeight) {
                lp.height = -1;
            }
            this.mMainView.requestLayout();
        } else if (lp.height != -1 && !this.mOverlayContent) {
            lp.height = -1;
            this.mMainView.requestLayout();
        }
    }

    private void setPanelStateInternal(PanelState state) {
        if (this.mSlideState != state) {
            PanelState oldState = this.mSlideState;
            this.mSlideState = state;
            dispatchOnPanelStateChanged(this, oldState, state);
        }
    }

    public void addPanelSlideListener(PanelSlideListener listener) {
        synchronized (this.mPanelSlideListeners) {
            this.mPanelSlideListeners.add(listener);
        }
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            int scrollX = v.getScrollX();
            int scrollY = v.getScrollY();
            for (int i = group.getChildCount() - 1; i >= 0; i--) {
                View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() && y + scrollY >= child.getTop() && y + scrollY < child.getBottom()) {
                    if (canScroll(child, DEFAULT_CLIP_PANEL_FLAG, dx, (x + scrollX) - child.getLeft(), (y + scrollY) - child.getTop())) {
                        return DEFAULT_CLIP_PANEL_FLAG;
                    }
                }
            }
        }
        return (checkV && ViewCompat.canScrollHorizontally(v, -dx)) ? DEFAULT_CLIP_PANEL_FLAG : false;
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return ((p instanceof LayoutParams) && super.checkLayoutParams(p)) ? DEFAULT_CLIP_PANEL_FLAG : false;
    }

    public void computeScroll() {
        if (this.mDragHelper != null && this.mDragHelper.continueSettling(DEFAULT_CLIP_PANEL_FLAG)) {
            if (isEnabled()) {
                ViewCompat.postInvalidateOnAnimation(this);
            } else {
                this.mDragHelper.abort();
            }
        }
    }

    void dispatchOnPanelSlide(View panel) {
        synchronized (this.mPanelSlideListeners) {
            for (PanelSlideListener l : this.mPanelSlideListeners) {
                l.onPanelSlide(panel, this.mSlideOffset);
            }
        }
    }

    void dispatchOnPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        synchronized (this.mPanelSlideListeners) {
            for (PanelSlideListener l : this.mPanelSlideListeners) {
                l.onPanelStateChanged(panel, previousState, newState);
            }
        }
        sendAccessibilityEvent(32);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        int i = -1;
        int action = MotionEventCompat.getActionMasked(ev);
        if (isEnabled() && isTouchEnabled() && (!this.mIsUnableToDrag || action == 0)) {
            float y = ev.getY();
            if (action == 0) {
                this.mIsScrollableViewHandlingTouch = false;
                this.mPrevMotionY = y;
            } else if (action == 2) {
                float dy = y - this.mPrevMotionY;
                this.mPrevMotionY = y;
                if (!isViewUnder(this.mScrollableView, (int) this.mInitialMotionX, (int) this.mInitialMotionY)) {
                    return super.dispatchTouchEvent(ev);
                }
                if (((float) (this.mIsSlidingUp ? 1 : -1)) * dy <= 0.0f) {
                    if (this.mIsSlidingUp) {
                        i = 1;
                    }
                    if (((float) i) * dy < 0.0f) {
                        if (this.mSlideOffset < DEFAULT_ANCHOR_POINT) {
                            this.mIsScrollableViewHandlingTouch = false;
                            return onTouchEvent(ev);
                        }
                        if (!this.mIsScrollableViewHandlingTouch && this.mDragHelper.isDragging()) {
                            this.mDragHelper.cancel();
                            ev.setAction(0);
                        }
                        this.mIsScrollableViewHandlingTouch = DEFAULT_CLIP_PANEL_FLAG;
                        return super.dispatchTouchEvent(ev);
                    }
                } else if (this.mScrollableViewHelper.getScrollableViewScrollPosition(this.mScrollableView, this.mIsSlidingUp) > 0) {
                    this.mIsScrollableViewHandlingTouch = DEFAULT_CLIP_PANEL_FLAG;
                    return super.dispatchTouchEvent(ev);
                } else {
                    if (this.mIsScrollableViewHandlingTouch) {
                        MotionEvent up = MotionEvent.obtain(ev);
                        up.setAction(3);
                        super.dispatchTouchEvent(up);
                        up.recycle();
                        ev.setAction(0);
                    }
                    this.mIsScrollableViewHandlingTouch = false;
                    return onTouchEvent(ev);
                }
            } else if (action == 1 && this.mIsScrollableViewHandlingTouch) {
                this.mDragHelper.setDragState(0);
            }
            return super.dispatchTouchEvent(ev);
        }
        this.mDragHelper.abort();
        return super.dispatchTouchEvent(ev);
    }

    public void draw(Canvas c) {
        super.draw(c);
        if (this.mShadowDrawable != null && this.mSlideableView != null) {
            int top;
            int bottom;
            int right = this.mSlideableView.getRight();
            if (this.mIsSlidingUp) {
                top = this.mSlideableView.getTop() - this.mShadowHeight;
                bottom = this.mSlideableView.getTop();
            } else {
                top = this.mSlideableView.getBottom();
                bottom = this.mSlideableView.getBottom() + this.mShadowHeight;
            }
            this.mShadowDrawable.setBounds(this.mSlideableView.getLeft(), top, right, bottom);
            this.mShadowDrawable.draw(c);
        }
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result;
        int save = canvas.save(2);
        if (this.mSlideableView == null || this.mSlideableView == child) {
            result = super.drawChild(canvas, child, drawingTime);
        } else {
            canvas.getClipBounds(this.mTmpRect);
            if (!this.mOverlayContent) {
                if (this.mIsSlidingUp) {
                    this.mTmpRect.bottom = Math.min(this.mTmpRect.bottom, this.mSlideableView.getTop());
                } else {
                    this.mTmpRect.top = Math.max(this.mTmpRect.top, this.mSlideableView.getBottom());
                }
            }
            if (this.mClipPanel) {
                canvas.clipRect(this.mTmpRect);
            }
            result = super.drawChild(canvas, child, drawingTime);
            if (this.mCoveredFadeColor != 0 && this.mSlideOffset > 0.0f) {
                this.mCoveredFadePaint.setColor((((int) (((float) ((this.mCoveredFadeColor & ViewCompat.MEASURED_STATE_MASK) >>> 24)) * this.mSlideOffset)) << 24) | (this.mCoveredFadeColor & ViewCompat.MEASURED_SIZE_MASK));
                canvas.drawRect(this.mTmpRect, this.mCoveredFadePaint);
            }
        }
        canvas.restoreToCount(save);
        return result;
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams ? new LayoutParams((MarginLayoutParams) p) : new LayoutParams(p);
    }

    public float getAnchorPoint() {
        return this.mAnchorPoint;
    }

    public int getCoveredFadeColor() {
        return this.mCoveredFadeColor;
    }

    public int getCurrentParallaxOffset() {
        int offset = (int) (((float) this.mParallaxOffset) * Math.max(this.mSlideOffset, 0.0f));
        return this.mIsSlidingUp ? -offset : offset;
    }

    public int getMinFlingVelocity() {
        return this.mMinFlingVelocity;
    }

    public int getPanelHeight() {
        return this.mPanelHeight;
    }

    public PanelState getPanelState() {
        return this.mSlideState;
    }

    public int getShadowHeight() {
        return this.mShadowHeight;
    }

    public boolean isClipPanel() {
        return this.mClipPanel;
    }

    public boolean isOverlayed() {
        return this.mOverlayContent;
    }

    public boolean isTouchEnabled() {
        return (!this.mIsTouchEnabled || this.mSlideableView == null || this.mSlideState == PanelState.HIDDEN) ? false : DEFAULT_CLIP_PANEL_FLAG;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = DEFAULT_CLIP_PANEL_FLAG;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = DEFAULT_CLIP_PANEL_FLAG;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        if (this.mDragViewResId != -1) {
            setDragView(findViewById(this.mDragViewResId));
        }
        if (this.mScrollableViewResId != -1) {
            setScrollableView(findViewById(this.mScrollableViewResId));
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mIsScrollableViewHandlingTouch || !isTouchEnabled()) {
            this.mDragHelper.abort();
            return false;
        }
        int action = MotionEventCompat.getActionMasked(ev);
        float x = ev.getX();
        float y = ev.getY();
        float adx = Math.abs(x - this.mInitialMotionX);
        float ady = Math.abs(y - this.mInitialMotionY);
        int dragSlop = this.mDragHelper.getTouchSlop();
        switch (action) {
            case 0:
                this.mIsUnableToDrag = false;
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                if (!isViewUnder(this.mDragView, (int) x, (int) y)) {
                    this.mDragHelper.cancel();
                    this.mIsUnableToDrag = DEFAULT_CLIP_PANEL_FLAG;
                    return false;
                }
                break;
            case 1:
            case 3:
                if (this.mDragHelper.isDragging()) {
                    this.mDragHelper.processTouchEvent(ev);
                    return DEFAULT_CLIP_PANEL_FLAG;
                } else if (ady <= ((float) dragSlop) && adx <= ((float) dragSlop) && this.mSlideOffset > 0.0f && !isViewUnder(this.mSlideableView, (int) this.mInitialMotionX, (int) this.mInitialMotionY) && this.mFadeOnClickListener != null) {
                    playSoundEffect(0);
                    this.mFadeOnClickListener.onClick(this);
                    return DEFAULT_CLIP_PANEL_FLAG;
                }
            case 2:
                if (ady > ((float) dragSlop) && adx > ady) {
                    this.mDragHelper.cancel();
                    this.mIsUnableToDrag = DEFAULT_CLIP_PANEL_FLAG;
                    return false;
                }
        }
        return this.mDragHelper.shouldInterceptTouchEvent(ev);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int childCount = getChildCount();
        if (this.mFirstLayout) {
            switch (this.mSlideState) {
                case EXPANDED:
                    this.mSlideOffset = DEFAULT_ANCHOR_POINT;
                    break;
                case ANCHORED:
                    this.mSlideOffset = this.mAnchorPoint;
                    break;
                case HIDDEN:
                    int i;
                    int computePanelTopPosition = computePanelTopPosition(0.0f);
                    if (this.mIsSlidingUp) {
                        i = this.mPanelHeight;
                    } else {
                        i = -this.mPanelHeight;
                    }
                    this.mSlideOffset = computeSlideOffset(computePanelTopPosition + i);
                    break;
                default:
                    this.mSlideOffset = 0.0f;
                    break;
            }
        }
        int i2 = 0;
        while (i2 < childCount) {
            View child = getChildAt(i2);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (!(child.getVisibility() == 8 && (i2 == 0 || this.mFirstLayout))) {
                int childHeight = child.getMeasuredHeight();
                int childTop = paddingTop;
                if (child == this.mSlideableView) {
                    childTop = computePanelTopPosition(this.mSlideOffset);
                }
                if (!(this.mIsSlidingUp || child != this.mMainView || this.mOverlayContent)) {
                    childTop = computePanelTopPosition(this.mSlideOffset) + this.mSlideableView.getMeasuredHeight();
                }
                int childLeft = paddingLeft + lp.leftMargin;
                child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + childHeight);
            }
            i2++;
        }
        if (this.mFirstLayout) {
            updateObscuredViewVisibility();
        }
        applyParallaxForCurrentSlideOffset();
        this.mFirstLayout = false;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != 1073741824 && widthMode != Integer.MIN_VALUE) {
            throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
        } else if (heightMode == 1073741824 || heightMode == Integer.MIN_VALUE) {
            int childCount = getChildCount();
            if (childCount != 2) {
                throw new IllegalStateException("Sliding up panel layout must have exactly 2 children!");
            }
            this.mMainView = getChildAt(0);
            this.mSlideableView = getChildAt(1);
            if (this.mDragView == null) {
                setDragView(this.mSlideableView);
            }
            if (this.mSlideableView.getVisibility() != 0) {
                this.mSlideState = PanelState.HIDDEN;
            }
            int layoutHeight = (heightSize - getPaddingTop()) - getPaddingBottom();
            int layoutWidth = (widthSize - getPaddingLeft()) - getPaddingRight();
            int i = 0;
            while (i < childCount) {
                View child = getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (child.getVisibility() != 8 || i != 0) {
                    int childWidthSpec;
                    int childHeightSpec;
                    int height = layoutHeight;
                    int width = layoutWidth;
                    if (child == this.mMainView) {
                        if (!(this.mOverlayContent || this.mSlideState == PanelState.HIDDEN)) {
                            height -= this.mPanelHeight;
                        }
                        width -= lp.leftMargin + lp.rightMargin;
                    } else if (child == this.mSlideableView) {
                        height -= lp.topMargin;
                    }
                    if (lp.width == -2) {
                        childWidthSpec = MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE);
                    } else if (lp.width == -1) {
                        childWidthSpec = MeasureSpec.makeMeasureSpec(width, 1073741824);
                    } else {
                        childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, 1073741824);
                    }
                    if (lp.height == -2) {
                        childHeightSpec = MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE);
                    } else {
                        if (lp.weight > 0.0f && lp.weight < DEFAULT_ANCHOR_POINT) {
                            height = (int) (((float) height) * lp.weight);
                        } else if (lp.height != -1) {
                            height = lp.height;
                        }
                        childHeightSpec = MeasureSpec.makeMeasureSpec(height, 1073741824);
                    }
                    child.measure(childWidthSpec, childHeightSpec);
                    if (child == this.mSlideableView) {
                        this.mSlideRange = this.mSlideableView.getMeasuredHeight() - this.mPanelHeight;
                    }
                }
                i++;
            }
            setMeasuredDimension(widthSize, heightSize);
        } else {
            throw new IllegalStateException("Height must have an exact value or MATCH_PARENT");
        }
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.mSlideState = (PanelState) bundle.getSerializable(SLIDING_STATE);
            this.mSlideState = this.mSlideState == null ? DEFAULT_SLIDE_STATE : this.mSlideState;
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putSerializable(SLIDING_STATE, this.mSlideState != PanelState.DRAGGING ? this.mSlideState : this.mLastNotDraggingSlideState);
        return bundle;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h != oldh) {
            this.mFirstLayout = DEFAULT_CLIP_PANEL_FLAG;
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled() || !isTouchEnabled()) {
            return super.onTouchEvent(ev);
        }
        try {
            this.mDragHelper.processTouchEvent(ev);
            return DEFAULT_CLIP_PANEL_FLAG;
        } catch (Exception e) {
            return false;
        }
    }

    public void removePanelSlideListener(PanelSlideListener listener) {
        synchronized (this.mPanelSlideListeners) {
            this.mPanelSlideListeners.remove(listener);
        }
    }

    void setAllChildrenVisible() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == 4) {
                child.setVisibility(0);
            }
        }
    }

    public void setAnchorPoint(float anchorPoint) {
        if (anchorPoint > 0.0f && anchorPoint <= DEFAULT_ANCHOR_POINT) {
            this.mAnchorPoint = anchorPoint;
            this.mFirstLayout = DEFAULT_CLIP_PANEL_FLAG;
            requestLayout();
        }
    }

    public void setClipPanel(boolean clip) {
        this.mClipPanel = clip;
    }

    public void setCoveredFadeColor(int color) {
        this.mCoveredFadeColor = color;
        requestLayout();
    }

    public void setDragView(int dragViewResId) {
        this.mDragViewResId = dragViewResId;
        setDragView(findViewById(dragViewResId));
    }

    public void setDragView(View dragView) {
        if (this.mDragView != null) {
            this.mDragView.setOnClickListener(null);
        }
        this.mDragView = dragView;
        if (this.mDragView != null) {
            this.mDragView.setClickable(DEFAULT_CLIP_PANEL_FLAG);
            this.mDragView.setFocusable(false);
            this.mDragView.setFocusableInTouchMode(false);
            this.mDragView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (!SlidingUpPanelLayout.this.isEnabled() || !SlidingUpPanelLayout.this.isTouchEnabled()) {
                        return;
                    }
                    if (SlidingUpPanelLayout.this.mSlideState == PanelState.EXPANDED || SlidingUpPanelLayout.this.mSlideState == PanelState.ANCHORED) {
                        SlidingUpPanelLayout.this.setPanelState(PanelState.COLLAPSED);
                    } else if (SlidingUpPanelLayout.this.mAnchorPoint < SlidingUpPanelLayout.DEFAULT_ANCHOR_POINT) {
                        SlidingUpPanelLayout.this.setPanelState(PanelState.ANCHORED);
                    } else {
                        SlidingUpPanelLayout.this.setPanelState(PanelState.EXPANDED);
                    }
                }
            });
        }
    }

    public void setFadeOnClickListener(OnClickListener listener) {
        this.mFadeOnClickListener = listener;
    }

    public void setGravity(int gravity) {
        if (gravity == 48 || gravity == 80) {
            this.mIsSlidingUp = gravity == 80 ? DEFAULT_CLIP_PANEL_FLAG : false;
            if (!this.mFirstLayout) {
                requestLayout();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("gravity must be set to either top or bottom");
    }

    public void setMinFlingVelocity(int val) {
        this.mMinFlingVelocity = val;
    }

    public void setOverlayed(boolean overlayed) {
        this.mOverlayContent = overlayed;
    }

    public void setPanelHeight(int val) {
        if (getPanelHeight() != val) {
            this.mPanelHeight = val;
            if (!this.mFirstLayout) {
                requestLayout();
            }
            if (getPanelState() == PanelState.COLLAPSED) {
                smoothToBottom();
                invalidate();
            }
        }
    }

    public void setPanelState(PanelState state) {
        if (state == null || state == PanelState.DRAGGING) {
            throw new IllegalArgumentException("Panel state cannot be null or DRAGGING.");
        } else if (!isEnabled()) {
        } else {
            if ((this.mFirstLayout || this.mSlideableView != null) && state != this.mSlideState && this.mSlideState != PanelState.DRAGGING) {
                if (this.mFirstLayout) {
                    setPanelStateInternal(state);
                    return;
                }
                if (this.mSlideState == PanelState.HIDDEN) {
                    this.mSlideableView.setVisibility(0);
                    requestLayout();
                }
                switch (state) {
                    case EXPANDED:
                        smoothSlideTo(DEFAULT_ANCHOR_POINT, 0);
                        return;
                    case ANCHORED:
                        smoothSlideTo(this.mAnchorPoint, 0);
                        return;
                    case HIDDEN:
                        int i;
                        int computePanelTopPosition = computePanelTopPosition(0.0f);
                        if (this.mIsSlidingUp) {
                            i = this.mPanelHeight;
                        } else {
                            i = -this.mPanelHeight;
                        }
                        smoothSlideTo(computeSlideOffset(computePanelTopPosition + i), 0);
                        return;
                    case COLLAPSED:
                        smoothSlideTo(0.0f, 0);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public void setParallaxOffset(int val) {
        this.mParallaxOffset = val;
        if (!this.mFirstLayout) {
            requestLayout();
        }
    }

    public void setScrollableView(View scrollableView) {
        this.mScrollableView = scrollableView;
    }

    public void setScrollableViewHelper(ScrollableViewHelper helper) {
        this.mScrollableViewHelper = helper;
    }

    public void setShadowHeight(int val) {
        this.mShadowHeight = val;
        if (!this.mFirstLayout) {
            invalidate();
        }
    }

    public void setTouchEnabled(boolean enabled) {
        this.mIsTouchEnabled = enabled;
    }

    boolean smoothSlideTo(float slideOffset, int velocity) {
        if (!isEnabled() || this.mSlideableView == null) {
            return false;
        }
        if (!this.mDragHelper.smoothSlideViewTo(this.mSlideableView, this.mSlideableView.getLeft(), computePanelTopPosition(slideOffset))) {
            return false;
        }
        setAllChildrenVisible();
        ViewCompat.postInvalidateOnAnimation(this);
        return DEFAULT_CLIP_PANEL_FLAG;
    }

    protected void smoothToBottom() {
        smoothSlideTo(0.0f, 0);
    }

    void updateObscuredViewVisibility() {
        if (getChildCount() != 0) {
            int left;
            int vis;
            int leftBound = getPaddingLeft();
            int rightBound = getWidth() - getPaddingRight();
            int topBound = getPaddingTop();
            int bottomBound = getHeight() - getPaddingBottom();
            int bottom;
            int top;
            int right;
            if (this.mSlideableView == null || !hasOpaqueBackground(this.mSlideableView)) {
                bottom = 0;
                top = 0;
                right = 0;
                left = 0;
            } else {
                left = this.mSlideableView.getLeft();
                right = this.mSlideableView.getRight();
                top = this.mSlideableView.getTop();
                bottom = this.mSlideableView.getBottom();
            }
            View child = getChildAt(0);
            int clampedChildLeft = Math.max(leftBound, child.getLeft());
            int clampedChildTop = Math.max(topBound, child.getTop());
            int clampedChildRight = Math.min(rightBound, child.getRight());
            int clampedChildBottom = Math.min(bottomBound, child.getBottom());
            if (clampedChildLeft < left || clampedChildTop < top || clampedChildRight > right || clampedChildBottom > bottom) {
                vis = 0;
            } else {
                vis = 4;
            }
            child.setVisibility(vis);
        }
    }
}
