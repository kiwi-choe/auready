package com.kiwi.auready_ver2.tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.util.view.AnimatedExpandableListView;
import com.kiwi.auready_ver2.util.view.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TasksAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    final private TasksFragment.TaskItemListener mTaskItemListener;
    private ArrayList<Member> mMemberList = null;
    private HashMap<String, ArrayList<Task>> mTasksList = null;
    private HashMap<String, ArrayList<Boolean>> mSelection = new HashMap<>();
    public final int INVALID_POSITION = -1;

    private LayoutInflater mInflater = null;
    private ChildViewHolder mCurrentSelectedChildViewHolder = null;
    private int mCurrentSelectedGroupViewPosition = -1;
    private int mCurrentSelectedChildViewPosition = -1;

    public TasksAdapter(Context context, ArrayList<Member> memberList, HashMap<String, ArrayList<Task>> tasksList, TasksFragment.TaskItemListener taskItemListener) {
        super();

        mInflater = LayoutInflater.from(context);
        mMemberList = memberList;
        mTasksList = tasksList;
        mTaskItemListener = taskItemListener;
    }

    @Override
    public int getGroupCount() {
        return mMemberList.size();
    }

    @Override
    public int getRealChildrenCount(int memberPosition) {

        ArrayList<Task> tasks = mTasksList.get(getMemberId(memberPosition));
        if (tasks == null) {
            return 1;
        }

        // + 1 : footer view for each member list
        return tasks.size() + 1;
    }

    @Override
    public Object getGroup(int memberPosition) {
        if (memberPosition >= mMemberList.size()) {
            return null;
        }

        return mMemberList.get(memberPosition);
    }

    @Override
    public Object getChild(int memberPosition, int taskPosition) {
        return mTasksList.get(getMemberId(memberPosition)).get(taskPosition);
    }

    @Override
    public long getGroupId(int memberPosition) {
        return memberPosition;
    }

    @Override
    public long getChildId(int memberPosition, int taskPosition) {
        return taskPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int memberPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        final GroupViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.taskview_expand_list_group_view, parent, false);

            viewHolder = new GroupViewHolder();
            viewHolder.memberName = (TextView) view.findViewById(R.id.member_name);
            viewHolder.auready_btn = (Button) view.findViewById(R.id.auready_btn);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            viewHolder.colorPickerBtn = (Button) view.findViewById(R.id.color_picker_btn);

            view.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) view.getTag();
        }

        viewHolder.memberName.setText(mMemberList.get(memberPosition).getName());
//        if (memberPosition == mCurrentEditModeMember) {
//            view.setBackgroundColor(view.getResources().getColor(android.R.color.holo_green_light));
//        } else {
//            view.setBackgroundColor(view.getResources().getColor(android.R.color.white));
//        }

        viewHolder.auready_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.progressBar.setProgress(viewHolder.progressBar.getProgress() + 20);
            }
        });

        final View finalView = view;
        final View finalView1 = view;
        viewHolder.colorPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskItemListener.onClickColorPicker(finalView1, memberPosition);
            }
        });

        // set background of child view
        if (mBackgroudColor.get(memberPosition) != null) {

            int color = mBackgroudColor.get(memberPosition);
            view.setBackgroundColor(color);
        } else {
            view.setBackgroundColor(view.getResources().getColor(R.color.tasks_group_item_background));
        }

        return view;
    }

    Drawable mEditTextBackground = null;

    @Override
    public View getRealChildView(final int memberPosition, final int taskPosition, boolean isLastTask, View convertView, ViewGroup parent) {
        View view = convertView;
        final ChildViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.taskview_expand_list_child_view, parent, false);

            viewHolder = new ChildViewHolder();
            viewHolder.taskDescription = (EditText) view.findViewById(R.id.task_edittext);
            if (mEditTextBackground == null) {
                mEditTextBackground = viewHolder.taskDescription.getBackground();
            }

            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.deleteTaskBtn = (Button) view.findViewById(R.id.delete_task_btn);
            viewHolder.addTaskBtn = (Button) view.findViewById(R.id.add_task_btn);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }

        if (isLastTask) {
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.deleteTaskBtn.setVisibility(View.GONE);
            viewHolder.taskDescription.setVisibility(View.GONE);
            viewHolder.addTaskBtn.setVisibility(View.VISIBLE);
            viewHolder.addTaskBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mTaskItemListener.onAddTaskClick(getMemberId(memberPosition), "empty : " + taskPosition, getChildrenCount(memberPosition) - 1);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCurrentSelectedChildViewHolder != null) {
                                mCurrentSelectedChildViewHolder.taskDescription.clearFocus();
                            }

                            mCurrentSelectedChildViewHolder = viewHolder;
                            mCurrentSelectedGroupViewPosition = memberPosition;
                            mCurrentSelectedChildViewPosition = taskPosition;
                            mCurrentSelectedChildViewHolder.taskDescription.requestFocus();

                            InputMethodManager keyboard =
                                    (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                            keyboard.showSoftInput(mCurrentSelectedChildViewHolder.taskDescription, 0);
                        }
                    }, 100);

                    notifyDataSetInvalidated();
                }
            });

            return view;
        } else {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.deleteTaskBtn.setVisibility(View.VISIBLE);
            viewHolder.taskDescription.setVisibility(View.VISIBLE);
            viewHolder.addTaskBtn.setVisibility(View.GONE);
        }

        final ArrayList<Task> tasksList = mTasksList.get(getMemberId(memberPosition));
        final Task task = tasksList.get(taskPosition);

        // hold views button position
        if (taskPosition == mCurrentSelectedChildViewPosition && memberPosition == mCurrentSelectedGroupViewPosition) {
//            viewHolder.taskDescription.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_description_end_trans_x));
//            viewHolder.checkBox.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_checkbox_end_trans_x));
            viewHolder.deleteTaskBtn.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_delete_btn_end_trans_x));
        } else {
//            viewHolder.taskDescription.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_description_start_trans_x));
//            viewHolder.checkBox.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_checkbox_start_trans_x));
            viewHolder.deleteTaskBtn.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_delete_btn_start_trans_x));
        }

        // set background of group view (add alpha)
        if (mBackgroudColor.get(memberPosition) != null) {
            int color = mBackgroudColor.get(memberPosition);
            int res = (color & 0x00ffffff) | (0x99 << 24);
            view.setBackgroundColor(res);
        } else {
            view.setBackgroundColor(view.getResources().getColor(R.color.tasks_child_item_background));
        }

        viewHolder.taskDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

//                EditText editText = ((EditText) view);
//                Log.d("MY_LOG", "focus change : " + editText.getText() + ", hasFocus : " + hasFocus);
//                editText.setCursorVisible(hasFocus);
                if (!hasFocus) {
                    if (mCurrentSelectedChildViewHolder != null) {
                        mCurrentSelectedChildViewHolder.taskDescription.clearFocus();
                        clickTaskDescriptionAnimation(false);
                    }
                    return;
                }

                // place cursor at the end of text in edittext
//                editText.setSelection(editText.getText().length());

                mCurrentSelectedChildViewHolder = viewHolder;
                mCurrentSelectedGroupViewPosition = memberPosition;
                mCurrentSelectedChildViewPosition = taskPosition;

                Log.d("MY_LOG", "after focus change : " + mCurrentSelectedChildViewHolder.taskDescription.getText() + ", hasFocus : " + hasFocus);

                clickTaskDescriptionAnimation(true);
            }
        });

        // control back button
        viewHolder.taskDescription.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    clickTaskDescriptionAnimation(false);
                    viewHolder.taskDescription.clearFocus();
                    return true;
                }

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {

                    mTaskItemListener.onEnterKeyClicked(memberPosition, taskPosition);


//                    // check whether childview is add button
//                    if (getChildrenCount(memberPosition) == taskPosition + 2) {
//                        View view = getRealChildView(memberPosition, taskPosition + 2, true, null, null);
//                        ChildViewHolder childViewHolder = (ChildViewHolder) view.getTag();
//                        mCurrentSelectedChildViewHolder.taskDescription.clearFocus();
//                        childViewHolder.addTaskBtn.performClick();
//                        return true;
//                    }
//
//                    View view = getRealChildView(memberPosition, taskPosition + 1, false, null, null);
//                    final ChildViewHolder childViewHolder = (ChildViewHolder) view.getTag();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mCurrentSelectedChildViewHolder != null) {
//                                mCurrentSelectedChildViewHolder.taskDescription.clearFocus();
//                            }
//
//                            mCurrentSelectedChildViewHolder = childViewHolder;
////                            mCurrentSelectedGroupViewPosition = memberPosition;
////                            mCurrentSelectedChildViewPosition = taskPosition + 1;
//                            mCurrentSelectedChildViewHolder.taskDescription.requestFocus();
//
//                            Log.d("MY_LOG", "selected : " + mCurrentSelectedChildViewHolder.taskDescription.getText());
//                        }
//                    }, 100);
//
//                    notifyDataSetChanged();
//                    return true;
                }

                return false;
            }
        });

        viewHolder.taskDescription.setBackground(null);

        // set description
        viewHolder.taskDescription.setText(task.getDescription());
        if (task.getCompleted()) {
            viewHolder.taskDescription.setTextColor(view.getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            viewHolder.taskDescription.setTextColor(view.getResources().getColor(android.R.color.black));
        }

        viewHolder.taskDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTaskItemListener.onTaskDescEdited(task.getId());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // set checkbox
        viewHolder.checkBox.setChecked(task.getCompleted());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        // set reorder
        viewHolder.deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTaskItemListener.onDeleteTaskClick(tasksList.get(taskPosition).getId());
            }
        });


        return view;
    }

    @Override
    public boolean isChildSelectable(int memberPosition, int taskPosition) {
        return true;
    }

    public void replaceMemberList(List<Member> members) {
        setMemberList((ArrayList) members);
        notifyDataSetChanged();
    }

    private String getMemberId(int position) {
        return mMemberList.get(position).getId();
    }


    private void setMemberList(ArrayList<Member> members) {
        mMemberList = members;
    }

    public void replaceTasksList(List<Task> tasks) {

        mTasksList.clear();

        for (Task task : tasks) {
            if (mTasksList.get(task.getMemberId()) == null) {
                mTasksList.put(task.getMemberId(), new ArrayList<Task>());
            }

            mTasksList.get(task.getMemberId()).add(task);
        }

        notifyDataSetChanged();
    }

    public void replaceTasksList(String memberId, List<Task> tasks) {

        if (mTasksList.get(memberId) == null) {
            mTasksList.put(memberId, new ArrayList<Task>());
        }

        mTasksList.get(memberId).clear();

        for (Task task : tasks) {
            mTasksList.get(memberId).add(task);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }


    private void clickTaskDescriptionAnimation(boolean isClick) {
        startDeleteButtonAnimation(isClick);
//        startCheckBoxAnimation(isClick);
    }

    private void startCheckBoxAnimation(boolean isHidingCheckbox) {
        CheckBox checkBox = mCurrentSelectedChildViewHolder.checkBox;
        int destPos = checkBox.getResources().getDimensionPixelSize(
                isHidingCheckbox ?
                        R.dimen.tasks_checkbox_end_trans_x :
                        R.dimen.tasks_checkbox_start_trans_x);

        checkBox.animate()
                .translationX(destPos)
                .setDuration(ViewUtils.ANIMATION_DURATION)
                .setInterpolator(ViewUtils.INTERPOLATOR)
                .start();

        TextView textView = mCurrentSelectedChildViewHolder.taskDescription;
        textView.animate()
                .translationX(destPos)
                .setDuration(ViewUtils.ANIMATION_DURATION)
                .setInterpolator(ViewUtils.INTERPOLATOR)
                .start();

    }

    private void startDeleteButtonAnimation(boolean isShowingDeleteBtn) {
        Button deleteBtn = mCurrentSelectedChildViewHolder.deleteTaskBtn;
        int destPos = deleteBtn.getResources().getDimensionPixelSize(
                isShowingDeleteBtn ?
                        R.dimen.tasks_delete_btn_end_trans_x :
                        R.dimen.tasks_delete_btn_start_trans_x);

        deleteBtn.animate()
                .translationX(destPos)
                .setDuration(ViewUtils.ANIMATION_DURATION)
                .setInterpolator(ViewUtils.INTERPOLATOR)
                .start();
    }

    public void startAnimation(boolean isGoingToEditMode, View view) {
        if (view == null || view.getTag() == null) {
            return;
        }

        if (view.getTag() instanceof ChildViewHolder) {
            ChildViewHolder viewHolder = (ChildViewHolder) view.getTag();

            int checkboxNormalPos = view.getResources().getDimensionPixelSize(R.dimen.tasks_checkbox_start_trans_x);
            int checkboxEditPos = view.getResources().getDimensionPixelSize(R.dimen.tasks_checkbox_end_trans_x);
            startAnimation(viewHolder.checkBox, checkboxNormalPos, checkboxEditPos, isGoingToEditMode);

            int textNormalPos = view.getResources().getDimensionPixelSize(R.dimen.tasks_description_start_trans_x);
            int textEditPos = view.getResources().getDimensionPixelSize(R.dimen.tasks_description_end_trans_x);
            startAnimation(viewHolder.taskDescription, textNormalPos, textEditPos, isGoingToEditMode);
        }

        if (view.getTag() instanceof GroupViewHolder) {
            GroupViewHolder viewHolder = (GroupViewHolder) view.getTag();

            int aureadyBtnNormalPos = 0;
            int aureadyBtnEditPos = 500;
            startAnimation(viewHolder.auready_btn, aureadyBtnNormalPos, aureadyBtnEditPos, isGoingToEditMode);
        }
    }

    private void startAnimation(View view, int normalModePosition, int editModePosition, boolean isGoingToEditMode) {
        view.setTranslationX(isGoingToEditMode ? normalModePosition : editModePosition);
        view.animate().translationX(isGoingToEditMode ? editModePosition : normalModePosition);

        view.animate().setDuration(ViewUtils.ANIMATION_DURATION).setInterpolator(ViewUtils.INTERPOLATOR).start();
    }

    public void requestFocusToEditText(View longClickedView) {
        EditText editText = (EditText) longClickedView;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    HashMap<Integer, Integer> mBackgroudColor = new HashMap<>();

    public void setGroupBackground(int memberPosition, int color) {
        if (mBackgroudColor.get(memberPosition) != null) {
            mBackgroudColor.remove(memberPosition);
        }

        mBackgroudColor.put(memberPosition, color);
        notifyDataSetChanged();
    }

    private class GroupViewHolder {
        TextView memberName;
        ProgressBar progressBar;
        Button auready_btn;
        Button colorPickerBtn;
    }

    private class ChildViewHolder {
        EditText taskDescription;
        CheckBox checkBox;
        Button deleteTaskBtn;
        Button addTaskBtn;
    }
}
