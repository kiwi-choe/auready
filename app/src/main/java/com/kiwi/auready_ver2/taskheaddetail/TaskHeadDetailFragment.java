package com.kiwi.auready_ver2.taskheaddetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.friend.FriendsActivity;
import com.kiwi.auready_ver2.friend.FriendsFragment;
import com.kiwi.auready_ver2.util.view.ColorPickerDialog;
import com.kiwi.auready_ver2.util.view.ColorPickerSwatch;

import java.util.ArrayList;
import java.util.List;

public class TaskHeadDetailFragment extends Fragment implements
        TaskHeadDetailContract.View, AbsListView.MultiChoiceModeListener {

    public static final String TAG = "tag_TaskHeadDetailFrag";

    public static final String EXTRA_TASKHEAD_ID = "extra_taskhead_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_COLOR = "extra_color";

    // when getArguments().getInt()
    private static final int DEFAULT_INT = 0;
    private static final int DEFAULT_COLOR = R.color.color_picker_default_color;

    private TaskHeadDetailContract.Presenter mPresenter;

    private Button mColorPickerBtn;
    ColorPickerDialog mColorPickerDialog;

    private String mTaskHeadId;
    private int mOrderOfTaskHead;

    private EditText mTitle;
    private MembersAdapter mMemberListAdapter;
    private List<Member> mMembers;
    private int mColor;

    // q find the better way
    ListView mMemberListView;
    private List<Member> mAddedMembers = new ArrayList<>();

    private ActionMode mActionMode;
    private Button mMemberAddBt;

    public TaskHeadDetailFragment() {
        // Required empty public constructor
    }

    public static TaskHeadDetailFragment newInstance() {
        return new TaskHeadDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMembers = new ArrayList<>(0);
        if (getArguments() != null) {
            mTaskHeadId = getArguments().getString(TaskHeadDetailActivity.ARG_TASKHEAD_ID);
            if (isNewTaskHead()) {
                initMembers();
                // set order for new taskHead
                mOrderOfTaskHead = getArguments().getInt(TaskHeadDetailActivity.ARG_CNT_OF_TASKHEADS, DEFAULT_INT);
            }
        }

        // init member variables
        mColor = DEFAULT_COLOR;
        mMemberListAdapter = new MembersAdapter(getActivity().getApplicationContext(), R.layout.member_item, mMembers);

        setHasOptionsMenu(true);
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull TaskHeadDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_taskhead_detail, container, false);

        mTitle = (EditText) root.findViewById(R.id.taskheaddetail_title);


        // set color picker views
        mColorPickerBtn = (Button) root.findViewById(R.id.color_picker_btn);

        int[] pickerColors = getContext().getResources().getIntArray(R.array.color_picker);
        mColorPickerDialog = ColorPickerDialog.newInstance(
                R.string.color_picker_default_title,
                pickerColors,
                ContextCompat.getColor(getActivity().getApplicationContext(), mColor),
                ColorPickerDialog.COLUMN_NUM,
                pickerColors.length
        );

        // Set member view
        mMemberListView = (ListView) root.findViewById(R.id.taskheaddetail_member_list);
        View memberAddBtLayout = inflater.inflate(R.layout.member_add_bt, null, false);
        mMemberListView.addFooterView(memberAddBtLayout);
        mMemberListView.setAdapter(mMemberListAdapter);
        mMemberListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mMemberListView.setMultiChoiceModeListener(this);

        mMemberAddBt = (Button) memberAddBtLayout.findViewById(R.id.member_add_bt);

        return root;
    }

    private void openFriendsView() {
        Intent intent = new Intent(getActivity(), FriendsActivity.class);

        // Set friendId of members
        ArrayList<String> friendIdOfMembers = new ArrayList<>();
        for (Member member : mMembers) {
            friendIdOfMembers.add(member.getFriendId());
        }
        intent.putStringArrayListExtra(FriendsFragment.EXTRA_KEY_MEMBERS, friendIdOfMembers);

        if (mActionMode != null) {
            mActionMode.finish();
        }

        startActivityForResult(intent, FriendsActivity.REQ_FRIENDS);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set color view event
        mColorPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorPickerDialog.show(getFragmentManager(), "color picker tag");
            }
        });

        mColorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                setColor(color);
                notifyColorChanged();
            }
        });

        // Set member view event
        mMemberListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                mMemberListView.setItemChecked(position, true);
                return true;
            }
        });

        mMemberAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFriendsView();
            }
        });
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setMembers(List<Member> members) {
        mMembers.clear();
        mMembers.addAll(members);
        mMembers.addAll(mAddedMembers);
        mMemberListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setColor(int color) {
        mColor = color;
        notifyColorChanged();
    }

    /*
    * Process after color changing
    * */
    private void notifyColorChanged() {
        mColorPickerBtn.setBackgroundColor(mColor);
    }

    @Override
    public void showAddedTaskHead(String taskHeadId, String title, int color) {
        Intent intent = getActivity().getIntent();
        intent.putExtra(EXTRA_TASKHEAD_ID, taskHeadId);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_COLOR, color);
        sendResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void showEmptyTaskHeadError() {
        Snackbar.make(mTitle, getString(R.string.empty_taskhead_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void cancelCreateTaskHead() {
        Intent intent = getActivity().getIntent();
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private void initMembers() {
        // Add the current user to members
        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(getActivity().getApplicationContext());
        // testing
        String myEmail = accessTokenStore.getStringValue(AccessTokenStore.USER_EMAIL, "");
        String myName = accessTokenStore.getStringValue(AccessTokenStore.USER_NAME, "");
        String myIdOfFriend = "stub_friendId";
        Friend meOfFriend = new Friend(myIdOfFriend, myEmail, myName);
        Member me = new Member(null, meOfFriend.getId(), meOfFriend.getName(), meOfFriend.getEmail());
        mMembers.add(0, me);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.taskhead_detail_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem createMenu = menu.findItem(R.id.create_menu);
        MenuItem doneMenu = menu.findItem(R.id.done_menu);

        if (isNewTaskHead()) {
            doneMenu.setVisible(false);
            createMenu.setVisible(true);
        } else {
            createMenu.setVisible(false);
            doneMenu.setVisible(true);
        }
    }

    boolean isNewTaskHead() {
        return (mTaskHeadId == null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_menu:
                mPresenter.createTaskHeadDetail(mTitle.getText().toString(), mOrderOfTaskHead, mMembers, mColor);
                break;

            case R.id.done_menu:
                mPresenter.editTaskHeadDetail(mTitle.getText().toString(), mOrderOfTaskHead, mMembers, mColor);
                break;

            case android.R.id.home:
                cancelCreateTaskHead();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setNewTaskHeadView() {
        mTitle.requestFocus();
    }

    @Override
    public void showEditedTaskHead(String title, int color) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_COLOR, color);
        sendResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void showSaveError() {
        sendResult(Activity.RESULT_CANCELED, null);
    }

    @Override
    public void addMembers(ArrayList<Member> members) {
        mAddedMembers.addAll(members);
        mMembers.addAll(members);
        mMemberListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    private void sendResult(int resultCode, @Nullable Intent intent) {
        if (intent != null) {
            getActivity().setResult(resultCode, intent);
        } else {
            getActivity().setResult(resultCode);
        }
        getActivity().finish();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (position >= 0 && position < mMemberListAdapter.getCount()) {
            if (checked) {
                mMemberListAdapter.setNewSelection(position, checked);
            } else {
                mMemberListAdapter.removeSelection(position);
            }
        }

        mode.setTitle(mMemberListAdapter.getSelectedCount() + " " + getContext().getResources().getString(R.string.item_selected));
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.contextual_menu_delete, menu);
        mActionMode = mode;
        startAnimation(true);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_delete:
                // Todo : remove member
                mMemberListAdapter.clearSelection();
                mode.finish();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mMemberListAdapter.clearSelection();
        startAnimation(false);
        mActionMode = null;
    }

    private void startAnimation(final boolean isDelete) {
        if (!mMemberListView.getViewTreeObserver().isAlive()) {
            return;
        }

        mMemberListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mMemberListView.getViewTreeObserver().removeOnPreDrawListener(this);

                int count = mMemberListView.getChildCount();
                if (mMemberListView.getLastVisiblePosition() == mMemberListView.getCount() - 1) {
                    count -= 1;
                }

                for (int i = 0; i < count; i++) {
                    View childView = mMemberListView.getChildAt(i);
                    mMemberListAdapter.startAnimation(childView, isDelete);
                }

                return true;
            }
        });
    }
}
