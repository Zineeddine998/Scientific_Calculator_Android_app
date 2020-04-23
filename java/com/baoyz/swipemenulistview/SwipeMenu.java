package com.baoyz.swipemenulistview;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class SwipeMenu {
    private Context mContext;
    private List<SwipeMenuItem> mItems = new ArrayList();
    private int mViewType;

    public SwipeMenu(Context context) {
        this.mContext = context;
    }

    public void addMenuItem(SwipeMenuItem item) {
        this.mItems.add(item);
    }

    public Context getContext() {
        return this.mContext;
    }

    public SwipeMenuItem getMenuItem(int index) {
        return (SwipeMenuItem) this.mItems.get(index);
    }

    public List<SwipeMenuItem> getMenuItems() {
        return this.mItems;
    }

    public int getViewType() {
        return this.mViewType;
    }

    public void removeMenuItem(SwipeMenuItem item) {
        this.mItems.remove(item);
    }

    public void setViewType(int viewType) {
        this.mViewType = viewType;
    }
}
