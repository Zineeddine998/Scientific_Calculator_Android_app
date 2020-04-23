package com.sothree.slidinguppanel;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

public class ScrollableViewHelper {
    public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
        if (scrollableView == null) {
            return 0;
        }
        View firstChild;
        View lastChild;
        if (scrollableView instanceof ScrollView) {
            if (isSlidingUp) {
                return scrollableView.getScrollY();
            }
            ScrollView sv = (ScrollView) scrollableView;
            return sv.getChildAt(0).getBottom() - (sv.getHeight() + sv.getScrollY());
        } else if ((scrollableView instanceof ListView) && ((ListView) scrollableView).getChildCount() > 0) {
            ListView lv = (ListView) scrollableView;
            if (lv.getAdapter() == null) {
                return 0;
            }
            if (isSlidingUp) {
                firstChild = lv.getChildAt(0);
                return (lv.getFirstVisiblePosition() * firstChild.getHeight()) - firstChild.getTop();
            }
            lastChild = lv.getChildAt(lv.getChildCount() - 1);
            return ((((lv.getAdapter().getCount() - lv.getLastVisiblePosition()) - 1) * lastChild.getHeight()) + lastChild.getBottom()) - lv.getBottom();
        } else if (!(scrollableView instanceof RecyclerView) || ((RecyclerView) scrollableView).getChildCount() <= 0) {
            return 0;
        } else {
            RecyclerView rv = (RecyclerView) scrollableView;
            LayoutManager lm = rv.getLayoutManager();
            if (rv.getAdapter() == null) {
                return 0;
            }
            if (isSlidingUp) {
                firstChild = rv.getChildAt(0);
                return (rv.getChildLayoutPosition(firstChild) * lm.getDecoratedMeasuredHeight(firstChild)) - lm.getDecoratedTop(firstChild);
            }
            lastChild = rv.getChildAt(rv.getChildCount() - 1);
            return (((rv.getAdapter().getItemCount() - 1) * lm.getDecoratedMeasuredHeight(lastChild)) + lm.getDecoratedBottom(lastChild)) - rv.getBottom();
        }
    }
}
