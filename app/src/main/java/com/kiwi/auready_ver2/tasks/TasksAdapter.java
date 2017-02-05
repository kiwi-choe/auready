package com.kiwi.auready_ver2.tasks;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.util.view.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TasksAdapter extends BaseExpandableListAdapter {

    final private TasksFragment.TaskItemListener mTaskItemListener;
    private ArrayList<Member> mMemberList = null;
    private HashMap<String, ArrayList<Task>> mTasksList = null;
    private HashMap<String, ArrayList<Boolean>> mSelection = new HashMap<>();

    public final int INVALID_POSITION = -1;

    private LayoutInflater mInflater = null;
    int mCurrentEditModeMember = INVALID_POSITION;
    private View mCurrentSelectedDeleteTask = null;

    public TasksAdapter(Context context, ArrayList<Member> memberList, HashMap<String, ArrayList<Task>> tasksList, TasksFragment.TaskItemListener taskItemListener) {
        super();

        mInflater = LayoutInflater.from(context);
        mMemberList = memberList;
        mTasksList = tasksList;
        mTaskItemListener = taskItemListener;

        mCurrentEditModeMember = INVALID_POSITION;
    }

    @Override
    public int getGroupCount() {
        return mMemberList.size();
    }

    @Override
    public int getChildrenCount(int memberPosition) {

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
        GroupViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.taskview_expand_list_group_view, parent, false);

            viewHolder = new GroupViewHolder();
            viewHolder.memberName = (TextView) view.findViewById(R.id.member_name);
            viewHolder.auready_btn = (Button) view.findViewById(R.id.member_add_bt);
            viewHolder.edit_tasks_btn = (Button) view.findViewById(R.id.delete_tasks_btn);

            view.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) view.getTag();
        }

        viewHolder.memberName.setText(mMemberList.get(memberPosition).getName());
        if (memberPosition == mCurrentEditModeMember) {
            view.setBackgroundColor(view.getResources().getColor(android.R.color.holo_red_dark));
            viewHolder.edit_tasks_btn.setText("Check Mode");
        } else {
            view.setBackgroundColor(view.getResources().getColor(android.R.color.white));
            viewHolder.edit_tasks_btn.setText("Edit Mode");
        }

        viewHolder.edit_tasks_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (memberPosition == mCurrentEditModeMember) {
                    mTaskItemListener.onStartNormalMode();
                } else {
                    mTaskItemListener.onStartEditMode(memberPosition, null);
                }
            }
        });

        return view;
    }

    Drawable mEditTextBackground = null;

    @Override
    public View getChildView(final int memberPosition, final int taskPosition, boolean isLastTask, View convertView, ViewGroup parent) {
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
            viewHolder.addTaskBtn.setVisibility(View.VISIBLE);
            viewHolder.addTaskBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (memberPosition != mCurrentEditModeMember) {
                        mCurrentEditModeMember = memberPosition;
                        notifyDataSetInvalidated();
                    }

                    mTaskItemListener.onAddTaskClick(getMemberId(memberPosition), "empty : " + taskPosition, getChildrenCount(memberPosition) - 1);
                }
            });

            return view;
        } else {
            viewHolder.addTaskBtn.setVisibility(View.GONE);
        }

        final ArrayList<Task> tasksList = mTasksList.get(getMemberId(memberPosition));
        final Task task = tasksList.get(taskPosition);

        // hold position
        viewHolder.deleteTaskBtn.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_delete_btn_start_trans_x));

        // set action mode
        if (memberPosition == mCurrentEditModeMember) {
            // hold position
            viewHolder.checkBox.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_checkbox_end_trans_x));
            viewHolder.taskDescription.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_description_end_trans_x));

            // switch textview to edittext
//            viewHolder.taskDescription.setEnabled(true);
            viewHolder.taskDescription.setFocusable(true);
            viewHolder.taskDescription.setFocusableInTouchMode(true);
//            viewHolder.taskDescription.setClickable(true);
//            viewHolder.taskDescription.setBackground(mEditTextBackground);
            viewHolder.taskDescription.setBackground(null);

            viewHolder.taskDescription.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        mTaskItemListener.onStartNormalMode();
                        return true;
                    }

                    return false;
                }
            });

            viewHolder.taskDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    EditText editText = ((EditText) view);
                    editText.setCursorVisible(hasFocus);
                    if (!hasFocus) {
                        if (mCurrentSelectedDeleteTask != null) {
                            startDeleteButtonAnimation(false);
                        }
                        return;
                    }

                    // place cursor at the end of text in edittext
                    editText.setSelection(editText.getText().length());

                    mCurrentSelectedDeleteTask = viewHolder.deleteTaskBtn;
                    startDeleteButtonAnimation(true);
                }
            });
        } else {
            // hold position
            viewHolder.checkBox.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_checkbox_start_trans_x));
            viewHolder.taskDescription.setTranslationX(view.getResources().getDimensionPixelSize(R.dimen.tasks_description_start_trans_x));

            // switch edittext to textview
            viewHolder.taskDescription.setFocusable(false);
            viewHolder.taskDescription.setFocusableInTouchMode(false);
            viewHolder.taskDescription.setBackground(null);
            viewHolder.taskDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditMode()) {
                        return;
                    }

                    // TODO : need to impelement Presenter
                    mTaskItemListener.onTaskChecked(task.getId());
                    viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
                }
            });

            viewHolder.taskDescription.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (isEditMode()) {
                        return false;
                    }

                    mTaskItemListener.onStartEditMode(memberPosition, view);
                    return true;
                }
            });
        }

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

        // set reorder
        viewHolder.deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mTaskItemListener.onDeleteClick(tasksList.get(taskPosition).getId());
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

    public void setActionModeMember(int memberPosition) {
        mCurrentEditModeMember = memberPosition;
        notifyDataSetChanged();
    }

    public boolean isEditMode() {
        return mCurrentEditModeMember != INVALID_POSITION;
    }


    private void startDeleteButtonAnimation(boolean isShowingDeleteBtn) {
        int destPos = mCurrentSelectedDeleteTask.getResources().getDimensionPixelSize(
                isShowingDeleteBtn ?
                        R.dimen.tasks_delete_btn_end_trans_x :
                        R.dimen.tasks_delete_btn_start_trans_x);

        mCurrentSelectedDeleteTask.animate()
                .translationX(destPos)
                .setDuration(ViewUtils.ANIMATION_DURATION)
                .setInterpolator(ViewUtils.INTERPOLATOR)
                .start();
    }

    public void startAnimation(boolean isGoingToEditMode, View view, long duration, TimeInterpolator interpolator) {
        if (view == null || view.getTag() == null) {
            return;
        }

        if (!(view.getTag() instanceof ChildViewHolder)) {
            return;
        }

        ChildViewHolder viewHolder = (ChildViewHolder) view.getTag();
        ArrayList<View> viewList = new ArrayList<>();

        int checkboxNormalPos = view.getResources().getDimensionPixelSize(R.dimen.tasks_checkbox_start_trans_x);
        int checkboxEditPos = view.getResources().getDimensionPixelSize(R.dimen.tasks_checkbox_end_trans_x);
        viewHolder.checkBox.setTranslationX(isGoingToEditMode ? checkboxNormalPos : checkboxEditPos);
        viewHolder.checkBox.animate().translationX(isGoingToEditMode ? checkboxEditPos : checkboxNormalPos);
        viewList.add(viewHolder.checkBox);

        int textNormalPos = view.getResources().getDimensionPixelSize(R.dimen.tasks_description_start_trans_x);
        int textEditPos = view.getResources().getDimensionPixelSize(R.dimen.tasks_description_end_trans_x);
        viewHolder.taskDescription.setTranslationX(isGoingToEditMode ? textNormalPos : textEditPos);
        viewHolder.taskDescription.animate().translationX(isGoingToEditMode ? textEditPos : textNormalPos);
        viewList.add(viewHolder.taskDescription);

        for (View animatedView : viewList) {
            animatedView.animate().setDuration(duration).setInterpolator(interpolator).start();
        }
    }

    public void requestFocusToEditText(View longClickedView) {
        EditText editText = (EditText) longClickedView;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    private class GroupViewHolder {
        TextView memberName;
        Button auready_btn;
        Button edit_tasks_btn;
    }

    private class ChildViewHolder {
        EditText taskDescription;
        CheckBox checkBox;
        Button deleteTaskBtn;
        Button addTaskBtn;
    }
}
