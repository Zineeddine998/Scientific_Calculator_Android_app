package com.baoyz.swipemenulistview;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.internal.view.SupportMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuView.OnSwipeItemClickListener;

public class SwipeMenuAdapter implements WrapperListAdapter, OnSwipeItemClickListener {
    private ListAdapter mAdapter;
    private Context mContext;
    private OnMenuItemClickListener onMenuItemClickListener;

    public SwipeMenuAdapter(Context context, ListAdapter adapter) {
        this.mAdapter = adapter;
        this.mContext = context;
    }

    public boolean areAllItemsEnabled() {
        return this.mAdapter.areAllItemsEnabled();
    }

    public void createMenu(SwipeMenu menu) {
        SwipeMenuItem item = new SwipeMenuItem(this.mContext);
        item.setTitle("Item 1");
        item.setBackground(new ColorDrawable(-7829368));
        item.setWidth(300);
        menu.addMenuItem(item);
        item = new SwipeMenuItem(this.mContext);
        item.setTitle("Item 2");
        item.setBackground(new ColorDrawable(SupportMenu.CATEGORY_MASK));
        item.setWidth(300);
        menu.addMenuItem(item);
    }

    public int getCount() {
        return this.mAdapter.getCount();
    }

    public Object getItem(int position) {
        return this.mAdapter.getItem(position);
    }

    public long getItemId(int position) {
        return this.mAdapter.getItemId(position);
    }

    public int getItemViewType(int position) {
        return this.mAdapter.getItemViewType(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SwipeMenuLayout layout;
        if (convertView == null) {
            View contentView = this.mAdapter.getView(position, convertView, parent);
            SwipeMenu menu = new SwipeMenu(this.mContext);
            menu.setViewType(this.mAdapter.getItemViewType(position));
            createMenu(menu);
            SwipeMenuView menuView = new SwipeMenuView(menu, (SwipeMenuListView) parent);
            menuView.setOnSwipeItemClickListener(this);
            SwipeMenuListView listView = (SwipeMenuListView) parent;
            layout = new SwipeMenuLayout(contentView, menuView, listView.getCloseInterpolator(), listView.getOpenInterpolator());
            layout.setPosition(position);
            return layout;
        }
        layout = (SwipeMenuLayout) convertView;
        layout.closeMenu();
        layout.setPosition(position);
        this.mAdapter.getView(position, layout.getContentView(), parent);
        return layout;
    }

    public int getViewTypeCount() {
        return this.mAdapter.getViewTypeCount();
    }

    public ListAdapter getWrappedAdapter() {
        return this.mAdapter;
    }

    public boolean hasStableIds() {
        return this.mAdapter.hasStableIds();
    }

    public boolean isEmpty() {
        return this.mAdapter.isEmpty();
    }

    public boolean isEnabled(int position) {
        return this.mAdapter.isEnabled(position);
    }

    public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
        if (this.onMenuItemClickListener != null) {
            this.onMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
        }
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.mAdapter.registerDataSetObserver(observer);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.mAdapter.unregisterDataSetObserver(observer);
    }
}
