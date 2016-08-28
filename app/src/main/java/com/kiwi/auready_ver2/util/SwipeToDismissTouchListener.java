package com.kiwi.auready_ver2.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

public class SwipeToDismissTouchListener<SomeCollectionView extends ViewAdapter> implements View.OnTouchListener {

    // 뷰 속성과 시스템 전역적인 속성에 대한 캐시 저장
    private final int mSlop;
    private final int mMinFlingVelocity;
    private final int mMaxFlingVelocity;
    private final long mAnimationTime;

    // 고정 속성
    private final SomeCollectionView mRecyclerView; // 성능을 위해 계속 재활용할 뷰
    private final DismissCallbacks<SomeCollectionView> mCallbacks;
    private int mViewWidth = 1;

    // 가변 속성
    private ArrayList<PendingDismissData> mPendingDismiss = new ArrayList<>();
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private int mSwipingSlop;
    private VelocityTracker mVelocityTracker;
    private int mDownPosition;
    private RowContainer mRowContainer;
    private boolean mPaused;

    public class RowContainer {

        final View container;
        final View dataContainer;
        final View undoContainer;
        boolean dataContainerHasBeenDismissed;

        public RowContainer(ViewGroup container) {
            this.container = container;
            dataContainer = container.getChildAt(0);
            undoContainer = container.getChildAt(1);
            dataContainerHasBeenDismissed = false;
        }

        // 현재 스와이핑 된 뷰 리턴
        View getCurrentSwipingView() {
            return dataContainerHasBeenDismissed ? undoContainer : dataContainer;
        }
    }

    public interface DismissCallbacks<SomeCollectionView extends ViewAdapter> {
        boolean canDismiss(int position);

        void onPendingDismiss(SomeCollectionView recyclerView, int position);

        void onDismiss(SomeCollectionView recyclerView, int position);
    }

    // 인자로 사용자가 정의한 adapterView를 받아서 가지고 있음(mRecyclerView), 이걸 계속 재활용하여 사용할 예정
    public SwipeToDismissTouchListener(SomeCollectionView recyclerView, DismissCallbacks<SomeCollectionView> callbacks) {
        ViewConfiguration vc = ViewConfiguration.get(recyclerView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = recyclerView.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        mRecyclerView = recyclerView;
        mCallbacks = callbacks;
    }

    public void setEnabled(boolean enabled) {
        mPaused = !enabled;
    }

    public Object makeScrollListener() {
        return mRecyclerView.makeScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mViewWidth < 2) {
            mViewWidth = mRecyclerView.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (mPaused) {
                    return false;
                }

                // 터치된 뷰를 찾는다.
                Rect rect = new Rect();
                int childCount = mRecyclerView.getChildCount();
                int[] listViewCoords = new int[2];

                // 선택된 뷰의 top/left 저장
                mRecyclerView.getLocationOnScreen(listViewCoords);
                int x = (int) motionEvent.getRawX() - listViewCoords[0];
                int y = (int) motionEvent.getRawY() - listViewCoords[1];

                // child 뷰는 2개의 child를 가지고 있어야함 : 1. 기존 Data, 2. 펜딩 아이템(DELETE/UNDO)
                View child;
                for (int i = 0; i < childCount; i++) {
                    child = mRecyclerView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        assert child instanceof ViewGroup &&
                                ((ViewGroup) child).getChildCount() == 2 :
                                "Each child needs to extend from ViewGroup and have two children";

                        // 해제 아이템의 위치 == 터치 아이템의 위치
                        boolean dataContainerHasBeenDismissed = false;
                        
                        int size = mPendingDismiss.size();
                        for (int pending = 0; pending < size; pending++) {
                            if (mPendingDismiss.get(pending).position == mRecyclerView.getChildPosition(child)) {
                                dataContainerHasBeenDismissed = mPendingDismiss.get(pending).rowContainer.dataContainerHasBeenDismissed;
                            }
                        }

                        // 선택된 child를 일단 저장해두고, mPendingDismiss 으로 변경할 준비 시작!
                        mRowContainer = new RowContainer((ViewGroup) child);
                        mRowContainer.dataContainerHasBeenDismissed = dataContainerHasBeenDismissed;
                        break;
                    }
                }

                // mRowContainer의 동작 감지를 위한 세팅
                if (mRowContainer != null) {
                    mDownX = motionEvent.getRawX();
                    mDownY = motionEvent.getRawY();
                    mDownPosition = mRecyclerView.getChildPosition(mRowContainer.container);
                    if (mCallbacks.canDismiss(mDownPosition)) {
                        mVelocityTracker = VelocityTracker.obtain();
                        mVelocityTracker.addMovement(motionEvent);
                    } else {
                        mRowContainer = null;
                    }
                }
                return false;
            }

            // mRowContainer를 mPendingDismiss로 변경하기 위해 했던 작업들 모두 취소
            case MotionEvent.ACTION_CANCEL: {
                if (mVelocityTracker == null) {
                    break;
                }

                if (mRowContainer != null && mSwiping) {
                    mRowContainer.getCurrentSwipingView()
                            .animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mAnimationTime)
                            .setListener(null);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownY = 0;
                mRowContainer = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }


            case MotionEvent.ACTION_UP: {
                if (mVelocityTracker == null) {
                    break;
                }

                // 적절한 범위의 스와이핑 동작이 있었는지 확인
                float deltaX = motionEvent.getRawX() - mDownX;
                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());
                boolean dismiss = false;
                boolean dismissRight = false;
                if (Math.abs(deltaX) > mViewWidth / 2 && mSwiping) {
                    dismiss = true;
                    dismissRight = deltaX > 0;
                } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
                        && absVelocityY < absVelocityX && mSwiping) {
                    dismiss = (velocityX < 0) == (deltaX < 0);
                    dismissRight = mVelocityTracker.getXVelocity() > 0;
                }

                // 적절한 범위의 스와이핑이 되었다면, 아까 저장해둔(mRowContainer) 아이템 삭제를 준비!
                if (dismiss && mDownPosition != ListView.INVALID_POSITION) {
                    final RowContainer downView = mRowContainer; // mDownView gets null'd before animation ends
                    final int downPosition = mDownPosition;
                    mRowContainer.getCurrentSwipingView()
                            .animate()
                            .translationX(dismissRight ? mViewWidth : -mViewWidth)
                            .alpha(0)
                            .setDuration(mAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                // 스와이핑 애니메이션이 모두 종료된 후 삭제 처리!
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    addDismissItem(downView, downPosition);
                                }
                            });
                } else {
                    // 스와이핑이 미흡하면 삭제 취소!
                    mRowContainer.getCurrentSwipingView()
                            .animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mAnimationTime)
                            .setListener(null);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownY = 0;
                mRowContainer = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }

            // 사용자의 스와이핑 동작을 트래킹
            case MotionEvent.ACTION_MOVE: {
                if (mVelocityTracker == null || mPaused) {
                    break;
                }

                mVelocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - mDownX;
                float deltaY = motionEvent.getRawY() - mDownY;
                if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
                    mSwiping = true;
                    mSwipingSlop = deltaX > 0 ? mSlop : -mSlop;
                    mRecyclerView.requestDisallowInterceptTouchEvent(true);

                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mRecyclerView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (mSwiping) {
                    mRowContainer.getCurrentSwipingView().setTranslationX(deltaX - mSwipingSlop);
                    mRowContainer.getCurrentSwipingView().setAlpha(Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(deltaX) / mViewWidth)));
                    return true;
                }
                break;
            }
        }
        return false;
    }

    class PendingDismissData implements Comparable<PendingDismissData> {
        public int position;
        public RowContainer rowContainer;

        public PendingDismissData(int position, RowContainer rowContainer) {
            this.position = position;
            this.rowContainer = rowContainer;
        }

        @Override
        public int compareTo(@NonNull PendingDismissData other) {
            return other.position - position;
        }
    }

    // 해제 동작 처리
    private void addDismissItem(RowContainer dismissView, int dismissPosition) {
        dismissView.dataContainerHasBeenDismissed = true;
        dismissView.undoContainer.setVisibility(View.VISIBLE);
        mPendingDismiss.add(new PendingDismissData(dismissPosition, dismissView));
        mCallbacks.onPendingDismiss(mRecyclerView, dismissPosition);
    }

    public void getLog() {
//        Log.d("MY_LOG", "mPendingDismiss : " + mPendingDismiss.size());
    }

    // 만약 펜딩된 아이템이 있는데 사용자가 삭제처리하면 펜딩된 아이템을 삭제
    public boolean processPendingDismisses(int position) {
        boolean existPendingDismisses = existPendingDismisses(position);
        if (existPendingDismisses) {
            int size = mPendingDismiss.size();
            for (int i = 0; i < size; i++) {
                if (mPendingDismiss.get(i).position == position) {
                    PendingDismissData pendingDismissData = mPendingDismiss.get(i);
                    mPendingDismiss.remove(i);
                    processPendingDismisses(pendingDismissData);
                    break;
                }
            }
        }

        return existPendingDismisses;
    }

    public boolean existPendingDismisses(int position) {
        int size = mPendingDismiss.size();
        if (size == 0) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (mPendingDismiss.get(i).position == position) {
                return true;
            }
        }

        return false;
    }

    public boolean undoPendingDismiss(int position) {
        boolean existPendingDismisses = existPendingDismisses(position);
        if (existPendingDismisses) {
            int size = mPendingDismiss.size();
            RowContainer undoItem = null;
            for (int i = 0; i < size; i++) {
                if (mPendingDismiss.get(i).position == position) {
                    undoItem = mPendingDismiss.get(i).rowContainer;
                    mPendingDismiss.remove(i);
                    break;
                }
            }

            undoItem.undoContainer.setVisibility(View.GONE);
            undoItem.dataContainer
                    .animate()
                    .translationX(0)
                    .alpha(1)
                    .setDuration(mAnimationTime)
                    .setListener(null);
        }
        return existPendingDismisses;
    }

    private void processPendingDismisses(final PendingDismissData pendingDismissData) {
        final ViewGroup.LayoutParams lp = pendingDismissData.rowContainer.container.getLayoutParams();
        final int originalHeight = pendingDismissData.rowContainer.container.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCallbacks.canDismiss(pendingDismissData.position)) {
                    mCallbacks.onDismiss(mRecyclerView, pendingDismissData.position);
                }
                pendingDismissData.rowContainer.dataContainer.post(new Runnable() {
                    @Override
                    public void run() {

                        pendingDismissData.rowContainer.dataContainer.setTranslationX(0);
                        pendingDismissData.rowContainer.dataContainer.setAlpha(1);
                        pendingDismissData.rowContainer.undoContainer.setVisibility(View.GONE);
                        pendingDismissData.rowContainer.undoContainer.setTranslationX(0);
                        pendingDismissData.rowContainer.undoContainer.setAlpha(1);

                        lp.height = originalHeight;
                        pendingDismissData.rowContainer.container.setLayoutParams(lp);
                    }
                });
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                pendingDismissData.rowContainer.container.setLayoutParams(lp);
            }
        });

        animator.start();
    }
}
