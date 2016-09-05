package com.kiwi.auready_ver2.customlistview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by kiwi on 9/5/16.
 */
public class BaseTasksAdapter extends BaseAdapter
implements DragSortListView.DropListener {


    protected static final int SECTION_TASKS = 0;
    protected static final int SECTION_ADD_BUTTON = 1;

    private static final int NUM_OF_VIEW_TYPES = 2;
    protected static final String ADD_BUTTON = "Add button";

    private int mAddBtnPos;

    public BaseTasksAdapter() {
        super();
        initBtnPosition();
    }
    @Override
    public int getItemViewType(int position) {
        if(position == mAddBtnPos)
            return SECTION_ADD_BUTTON;
        else
            return SECTION_TASKS;
    }

    @Override
    public int getViewTypeCount(){
        return NUM_OF_VIEW_TYPES;
    }

    @Override
    public boolean areAllItemsEnabled(){
        return false;
    }

    @Override
    public boolean isEnabled(int position){
        return position != mAddBtnPos;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        if(position == mAddBtnPos)
            return ADD_BUTTON;
        else
            return null;
    }

    public int getDataPosition(int position) {
        return position > mAddBtnPos ? position - 1 : position;
    }

    public void initBtnPosition() {
        mAddBtnPos = 0;
    }

    public int getBtnPosition(){
        return mAddBtnPos;
    }

    public void increaseButtonPosition(){
        mAddBtnPos++;
    }

    public void decreaseButtonPosition(){
        mAddBtnPos--;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void drop(int from, int to){

    }
}
