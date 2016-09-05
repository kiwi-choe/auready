package com.kiwi.auready_ver2.tasks;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.kiwi.auready_ver2.customlistview.DragSortController;
import com.kiwi.auready_ver2.customlistview.DragSortListView;

public class SectionController extends DragSortController {
    private int mPos;
    private int mDivPos;

    DragSortListView mListView;
    private TasksAdapter mAdapter;

    public SectionController(DragSortListView listView, TasksAdapter adapter, int id) {
        super(listView, id, DragSortController.ON_DOWN, 0);
        setRemoveEnabled(false);
        mListView = listView;
        mAdapter = adapter;
        mDivPos = adapter.getBtnPosition();
    }

    @Override
    public int startDragPosition(MotionEvent ev) {
        int res = super.startDragPosition(ev);
        mDivPos = mAdapter.getBtnPosition();

        if (res == mDivPos) {
            return DragSortController.MISS;
        }

        int width = mListView.getWidth();
        if ((int) ev.getX() < width / 3) {
            return res;
        } else {
            return DragSortController.MISS;
        }
    }

    @Override
    public View onCreateFloatView(int position) {
        mPos = position;
        return mAdapter.getView(position, null, mListView);
    }

    private int origHeight = -1;

    @Override
    public void onDragFloatView(View floatView, Point floatPoint, Point touchPoint) {
        final int first = mListView.getFirstVisiblePosition();
        final int lvDivHeight = mListView.getDividerHeight();

        if (origHeight == -1) {
            origHeight = floatView.getHeight();
        }

        View div = mListView.getChildAt(mDivPos - first);
        if (touchPoint.x > mListView.getWidth() / 2) {
            float scale = touchPoint.x - mListView.getWidth() / 2;
            scale /= (float) (mListView.getWidth() / 5);
            ViewGroup.LayoutParams lp = floatView.getLayoutParams();
            lp.height = Math.max(origHeight, (int) (scale * origHeight));
            floatView.setLayoutParams(lp);
        }

        if (div != null) {
            // 버튼 사이로 float view 이동을 막아야함
            if (mPos > mDivPos) {
                final int limit = div.getBottom() + lvDivHeight;
                if (floatPoint.y < limit) {
                    floatPoint.y = limit;
                }
            } else {
                final int limit = div.getTop() - lvDivHeight - floatView.getHeight();
                if (floatPoint.y > limit) {
                    floatPoint.y = limit;
                }
            }
        }
    }

    @Override
    public void onDestroyFloatView(View floatView) {
        //do nothing; block super from crashing
    }
}
