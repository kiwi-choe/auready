package com.kiwi.auready.util.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kiwi.auready.R;

public class SplitView extends LinearLayout implements View.OnTouchListener {

    private int mHandleId;
    private View mHandle;

    private int mPrimaryContentId;
    private View mPrimaryContent;

    private int mSecondaryContentId;
    private View mSecondaryContent;

    private boolean mIsDragged = false;
    private long mDraggingStarted;
    private float mDragStartX;
    private float mDragStartY;

    private float mPointerOffset;
    private float mLastRowY;

    private int mPrimaryListViewHeight = -1;
    private int mSecondaryListViewHeight = -1;

    private int mMaxSplitViewHeight = -1;

    final static private int MAXIMIZED_VIEW_TOLERANCE_DIP = 30;
    final static private int TAP_DRIFT_TOLERANCE = 3;
    final static private int SINGLE_TAP_MAX_TIME = 175;

    public SplitView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray viewAttrs = context.obtainStyledAttributes(attrs, R.styleable.SplitView);

        RuntimeException e = null;
        mHandleId = viewAttrs.getResourceId(R.styleable.SplitView_handle, 0);
        if (mHandleId == 0) {
            e = new IllegalArgumentException(viewAttrs.getPositionDescription() +
                    ": The required attribute handle must refer to a valid child view.");
        }

        mPrimaryContentId = viewAttrs.getResourceId(R.styleable.SplitView_primarycontent, 0);
        if (mPrimaryContentId == 0) {
            e = new IllegalArgumentException(viewAttrs.getPositionDescription() +
                    ": The required attribute primaryContent must refer to a valid child view.");
        }


        mSecondaryContentId = viewAttrs.getResourceId(R.styleable.SplitView_secondarycontent, 0);
        if (mSecondaryContentId == 0) {
            e = new IllegalArgumentException(viewAttrs.getPositionDescription() +
                    ": The required attribute secondaryContent must refer to a valid child view.");
        }

        viewAttrs.recycle();

        if (e != null) {
            throw e;
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mHandle = findViewById(mHandleId);
        if (mHandle == null) {
            String name = getResources().getResourceEntryName(mHandleId);
            throw new RuntimeException("Your Panel must have a child View whose id attribute is 'R.id." + name + "'");

        }

        mPrimaryContent = findViewById(mPrimaryContentId);
        if (mPrimaryContent == null) {
            String name = getResources().getResourceEntryName(mPrimaryContentId);
            throw new RuntimeException("Your Panel must have a child View whose id attribute is 'R.id." + name + "'");

        }

        mSecondaryContent = findViewById(mSecondaryContentId);
        if (mSecondaryContent == null) {
            String name = getResources().getResourceEntryName(mSecondaryContentId);
            throw new RuntimeException("Your Panel must have a child View whose id attribute is 'R.id." + name + "'");

        }

        mHandle.setOnTouchListener(this);
    }

    public boolean isDragged() {
        return mIsDragged;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // Only capture drag events if we start
        if (view != mHandle) {
            return false;
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            mDraggingStarted = SystemClock.elapsedRealtime();
            mDragStartX = motionEvent.getX();
            mDragStartY = motionEvent.getY();
            if (getOrientation() == VERTICAL) {
                mPointerOffset = motionEvent.getRawY() - getPrimaryContentSize();
            } else {
                mPointerOffset = motionEvent.getRawX() - getPrimaryContentSize();
            }
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            mIsDragged = true;
            // expand splitView when clicking the handler
            if (
                    mDragStartX < (motionEvent.getX() + TAP_DRIFT_TOLERANCE) &&
                            mDragStartX > (motionEvent.getX() - TAP_DRIFT_TOLERANCE) &&
                            mDragStartY < (motionEvent.getY() + TAP_DRIFT_TOLERANCE) &&
                            mDragStartY > (motionEvent.getY() - TAP_DRIFT_TOLERANCE) &&
                            ((SystemClock.elapsedRealtime() - mDraggingStarted) < SINGLE_TAP_MAX_TIME)) {
                if (isPrimaryContentMaximized() || isSecondaryContentMaximized()) {
//                    setPrimaryContentSize(mLastPrimaryContentSize);
                } else {
//                    maximizeSecondaryContent();
                }
            }
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

            if (getOrientation() == VERTICAL) {

                if (mPrimaryListViewHeight == -1) {
                    mPrimaryListViewHeight = ViewUtils.getListViewHeightBasedOnChildren((ListView) mPrimaryContent);
                }

                if (mSecondaryListViewHeight == -1) {
                    mSecondaryListViewHeight = ViewUtils.getListViewHeightBasedOnChildren((ListView) mSecondaryContent);
                }

                int primaryHeight = (int) (motionEvent.getRawY() - mPointerOffset);
                if (primaryHeight <= 0) {
                    primaryHeight = 0;
                } else if (primaryHeight >= mPrimaryListViewHeight) {
                    primaryHeight = mPrimaryListViewHeight;
                }

                int totalHeight;
                if (mLastRowY > motionEvent.getRawY()) {
                    // up
                    totalHeight = primaryHeight + Math.max(mSecondaryContent.getMeasuredHeight(), mSecondaryListViewHeight);
                } else {
                    // down
                    totalHeight = primaryHeight + Math.min(mSecondaryContent.getMeasuredHeight(), mSecondaryListViewHeight);
                }

                if (totalHeight >= mMaxSplitViewHeight) {
                    totalHeight = mMaxSplitViewHeight;
                }

                if (totalHeight - primaryHeight <= 0) {
                    primaryHeight = totalHeight;
                }

                setMinimumHeight(totalHeight);
                setSecondaryContentHeight(totalHeight - primaryHeight);
                setPrimaryContentHeight(primaryHeight);
            } else {
                setPrimaryContentWidth((int) (motionEvent.getRawX() - mPointerOffset));
            }

            mLastRowY = motionEvent.getRawY();
        }

        return true;
    }

    public int getHeightSize() {
        if (getOrientation() == VERTICAL) {
            return getMeasuredHeight();
        } else {
            return getMeasuredWidth();
        }
    }

    public int getPrimaryContentSize() {
        if (getOrientation() == VERTICAL) {
            return mPrimaryContent.getMeasuredHeight();
        } else {
            return mPrimaryContent.getMeasuredWidth();
        }
    }

    public boolean setPrimaryContentSize(int newSize) {
        if (getOrientation() == VERTICAL) {
            return setPrimaryContentHeight(newSize);
        } else {
            return setPrimaryContentWidth(newSize);
        }
    }

    private boolean setPrimaryContentHeight(int newHeight) {

        // the new primary content height should not be less than 0 to make the
        // handler always visible
        newHeight = Math.max(0, newHeight);
        // the new primary content height should not be more than the SplitView
        // height minus handler height to make the handler always visible
//        newHeight = Math.min(newHeight, getMeasuredHeight() - mHandle.getMeasuredHeight());
//
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPrimaryContent.getLayoutParams();
//        if (mSecondaryContent.getMeasuredHeight() < 1 && newHeight > params.height) {
//            return false;
//        }

        if (mPrimaryContent instanceof ListView) {
//            int maxHeight = Math.min(getMeasuredHeight() - mHandle.getMeasuredHeight(),
//                    ViewUtils.getListViewHeightBasedOnChildren((ListView) mPrimaryContent));

            int maxHeight = mMaxSplitViewHeight - mHandle.getMeasuredHeight();
//            if (maxHeight == 0) {
//                maxHeight = Math.min(mMaxSplitViewHeight - mHandle.getMeasuredHeight(),
//                        ViewUtils.getListViewHeightBasedOnChildren((ListView) mPrimaryContent));
//            }
//
            if (newHeight >= maxHeight) {
                newHeight = maxHeight;
            }
        }

        if (newHeight >= 0) {
            params.height = newHeight;
            // set the primary content parameter to do not stretch anymore and
            // use the height specified in the layout params
            params.weight = 0;
        }

        unMinimizeSecondaryContent();
        mPrimaryContent.setLayoutParams(params);

        return true;
    }

    private boolean setSecondaryContentHeight(int newHeight) {

        // the new primary content height should not be less than 0 to make the
        // handler always visible
        newHeight = Math.max(0, newHeight);
        // the new primary content height should not be more than the SplitView
        // height minus handler height to make the handler always visible
//        newHeight = Math.min(newHeight, getMeasuredHeight() - mHandle.getMeasuredHeight());
//
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSecondaryContent.getLayoutParams();
//        if (mSecondaryContent.getMeasuredHeight() < 1 && newHeight > params.height) {
//            return false;
//        }

//        if (mPrimaryContent instanceof ListView) {
////            int maxHeight = Math.min(getMeasuredHeight() - mHandle.getMeasuredHeight(),
////                    ViewUtils.getListViewHeightBasedOnChildren((ListView) mPrimaryContent));
//
//            int maxHeight = getMeasuredHeight() - mHandle.getMeasuredHeight();
//
//            if (newHeight >= maxHeight) {
//                newHeight = maxHeight;
//            }
//        }


        if (newHeight >= 0) {
            params.height = newHeight;
            // set the primary content parameter to do not stretch anymore and
            // use the height specified in the layout params
            params.weight = 0;
        }

        unMinimizeSecondaryContent();
        mSecondaryContent.setLayoutParams(params);

        return true;
    }

    private boolean setPrimaryContentWidth(int newWidth) {
        // the new primary content width should not be less than 0 to make the
        // handler always visible
        newWidth = Math.max(0, newWidth);
        // the new primary content width should not be more than the SplitView
        // width minus handler width to make the handler always visible
        newWidth = Math.min(newWidth, getMeasuredWidth() - mHandle.getMeasuredWidth());
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPrimaryContent
                .getLayoutParams();

        if (mSecondaryContent.getMeasuredWidth() < 1 && newWidth > params.width) {
            return false;
        }
        if (newWidth >= 0) {
            params.width = newWidth;
            // set the primary content parameter to do not stretch anymore and
            // use the width specified in the layout params
            params.weight = 0;
        }
        unMinimizeSecondaryContent();
        mPrimaryContent.setLayoutParams(params);
        return true;
    }

    public boolean isPrimaryContentMaximized() {
        if ((getOrientation() == VERTICAL && (mSecondaryContent.getMeasuredHeight() < MAXIMIZED_VIEW_TOLERANCE_DIP)) ||
                (getOrientation() == HORIZONTAL && (mSecondaryContent.getMeasuredWidth() < MAXIMIZED_VIEW_TOLERANCE_DIP))) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isSecondaryContentMaximized() {
        if ((getOrientation() == VERTICAL && (mPrimaryContent.getMeasuredHeight() < MAXIMIZED_VIEW_TOLERANCE_DIP)) ||
                (getOrientation() == HORIZONTAL && (mPrimaryContent.getMeasuredWidth() < MAXIMIZED_VIEW_TOLERANCE_DIP))) {
            return true;
        } else {
            return false;
        }
    }

    public void maximizePrimaryContent() {
        maximizeContentPane(mPrimaryContent, mSecondaryContent);
    }

    public void maximizeSecondaryContent() {
        maximizeContentPane(mSecondaryContent, mPrimaryContent);
    }


    private void maximizeContentPane(View toMaximize, View toUnMaximize) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toUnMaximize
                .getLayoutParams();
        LinearLayout.LayoutParams secondaryParams = (LinearLayout.LayoutParams) toMaximize
                .getLayoutParams();
        // set the primary content parameter to do not stretch anymore and use
        // the height/width specified in the layout params
        params.weight = 0;
        // set the secondary content parameter to use all the available space
        secondaryParams.weight = 1;
        if (getOrientation() == VERTICAL) {
            params.height = 1;
        } else {
            params.width = 1;
        }
        toUnMaximize.setLayoutParams(params);
        toMaximize.setLayoutParams(secondaryParams);
    }

    private void unMinimizeSecondaryContent() {
        LinearLayout.LayoutParams secondaryParams = (LinearLayout.LayoutParams) mSecondaryContent
                .getLayoutParams();
        // set the secondary content parameter to use all the available space
        secondaryParams.weight = 1;
        mSecondaryContent.setLayoutParams(secondaryParams);
    }

    public void setMaxHeight(int maxHeight) {
        mMaxSplitViewHeight = maxHeight;
    }

    public void updateContentView() {
        boolean isDown = false;

        if (mPrimaryListViewHeight <= ViewUtils.getListViewHeightBasedOnChildren((ListView) mPrimaryContent)) {
            isDown = true;
        }

        mPrimaryListViewHeight = ViewUtils.getListViewHeightBasedOnChildren((ListView) mPrimaryContent);
        mSecondaryListViewHeight = ViewUtils.getListViewHeightBasedOnChildren((ListView) mSecondaryContent);
        final int listItemHeight = ViewUtils.getListItemHeight((ListView) mPrimaryContent);

        int primaryHeight;
        if (mPrimaryContent.getMeasuredHeight() + listItemHeight == mPrimaryListViewHeight) {
            primaryHeight = Math.max(mPrimaryContent.getMeasuredHeight(), mPrimaryListViewHeight);
        } else {
            primaryHeight = Math.min(mPrimaryContent.getMeasuredHeight(), mPrimaryListViewHeight);
        }

        if (primaryHeight == 0) {
            primaryHeight = mPrimaryListViewHeight;
        }

        int totalHeight;
        if (isDown) {
            totalHeight = primaryHeight + Math.min(mSecondaryContent.getMeasuredHeight(), mSecondaryListViewHeight);
        } else {
            totalHeight = primaryHeight + Math.max(mSecondaryContent.getMeasuredHeight(), mSecondaryListViewHeight);
        }

        if (totalHeight >= mMaxSplitViewHeight) {
            totalHeight = mMaxSplitViewHeight;
        }

        if (totalHeight - primaryHeight <= 0) {
            primaryHeight = totalHeight;
        }

        setMinimumHeight(totalHeight);
        setSecondaryContentHeight(totalHeight - primaryHeight);
        setPrimaryContentHeight(primaryHeight);
    }
}
