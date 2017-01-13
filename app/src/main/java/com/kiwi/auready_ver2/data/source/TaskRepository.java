package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/23/16.
 */
public class TaskRepository implements TaskDataSource {

    private static TaskRepository INSTANCE = null;

    private TaskDataSource mLocalDataSource;

    private boolean mCacheIsDirty;

    /*
    * This variable has package local visibility so it can be accessed from tests.
    * */
    Map<String, TaskHead> mCachedTaskHeads = null;
    // Key: taskHeadId
    Map<String, List<Member>> mCachedMembersOfTaskHead = null;
    // Key: member id
    Map<String, Member> mCachedMembers = null;

    // Prevent direct instantiation
    private TaskRepository(TaskDataSource taskLocalDataSource) {

        mLocalDataSource = taskLocalDataSource;
    }

    public static TaskRepository getInstance(@NonNull TaskDataSource taskLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TaskRepository(checkNotNull(taskLocalDataSource));
        }
        return INSTANCE;
    }

    /*
    * Used to force {@link #getInstance(TaskDataSource, TaskDataSource)} to create a new instance
    * next time it's called.
    * */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getMembers(@NonNull String taskHeadId, @NonNull LoadMembersCallback callback) {

    }

    @Override
    public void getTaskHeads(@NonNull final LoadTaskHeadsCallback callback) {
        checkNotNull(callback);

        mLocalDataSource.getTaskHeads(new LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                callback.onTaskHeadsLoaded(taskHeads);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void deleteTaskHeads(List<String> taskheadIds) {
        mLocalDataSource.deleteTaskHeads(taskheadIds);

        // Delete from cache
        if (mCachedTaskHeads != null) {
            for (String id : taskheadIds) {
                mCachedTaskHeads.remove(id);
            }
        }
    }

    @Override
    public int getTaskHeadsCount() {
        if (mCachedTaskHeads != null) {
            return mCachedTaskHeads.size();
        }
        return mLocalDataSource.getTaskHeadsCount();
    }

    @Override
    public void updateTaskHeadOrders(@NonNull List<TaskHead> taskHeads) {
        mLocalDataSource.updateTaskHeadOrders(taskHeads);

        refreshTaskHeadsCache(taskHeads);
    }

    private void refreshTaskHeadsCache(List<TaskHead> taskHeads) {
        if (mCachedTaskHeads == null) {
            mCachedTaskHeads = new LinkedHashMap<>();
        }
        mCachedTaskHeads.clear();
        for (TaskHead taskHead : taskHeads) {
            mCachedTaskHeads.put(taskHead.getId(), taskHead);
        }
    }

    /*
    * TaskHeadDetail
    * */
    @Override
    public void saveTaskHeadDetail(@NonNull final TaskHeadDetail taskHeadDetail, @NonNull final SaveCallback callback) {
        checkNotNull(taskHeadDetail);
        checkNotNull(callback);

        mLocalDataSource.saveTaskHeadDetail(taskHeadDetail, new SaveCallback() {
            @Override
            public void onSaveSuccess() {
                refreshTaskHeadDetailCache(taskHeadDetail);
                Log.d("TEST_NOW", "after saveTaskHeadDetail");
                showMembersCacheOf(taskHeadDetail.getTaskHead().getId());
                callback.onSaveSuccess();
            }

            @Override
            public void onSaveFailed() {
                callback.onSaveFailed();
            }
        });
    }

    @Override
    public void editTaskHeadDetail(@NonNull TaskHead editTaskHead,
                                   @NonNull List<Member> addingMembers,
                                   @NonNull List<String> deletingMemberIds,
                                   @NonNull final EditTaskHeadDetailCallback callback) {

        checkNotNull(editTaskHead);
        checkNotNull(addingMembers);
        checkNotNull(deletingMemberIds);

        mLocalDataSource.editTaskHeadDetail(editTaskHead, addingMembers, deletingMemberIds, callback);
    }

    public void editTaskHeadDetail(@NonNull TaskHeadDetail taskheadDetail, @NonNull final EditTaskHeadDetailCallback callback) {
        checkNotNull(taskheadDetail);

        TaskHead editTaskHead = taskheadDetail.getTaskHead();
        List<Member> editMembers = taskheadDetail.getMembers();

        // Compare cachedMembersOfTaskHead to editMembers
        final List<Member> addingMembers = getAddingMembers(editTaskHead.getId(), editMembers);
        Log.d("TEST_NOW", "addingMembers: " + addingMembers.size());
        final List<String> deletingMemberIds = getDeletingMemberIds(editTaskHead.getId(), editMembers);
        // Save taskHead, Save or Delete members
        editTaskHeadDetail(editTaskHead, addingMembers, deletingMemberIds, new EditTaskHeadDetailCallback() {
            @Override
            public void onEditSuccess() {
                addMembersToCache(addingMembers);
                deleteMembersFromCache(deletingMemberIds);

                callback.onEditSuccess();
            }

            @Override
            public void onEditFailed() {
                callback.onEditFailed();
            }
        });
    }

    @Override
    public void getTaskHeadDetail(@NonNull final String taskHeadId, @NonNull final GetTaskHeadDetailCallback callback) {
        checkNotNull(taskHeadId);
        checkNotNull(callback);

        // Is the taskhead in the local? if not, query the network.
        mLocalDataSource.getTaskHeadDetail(taskHeadId, new GetTaskHeadDetailCallback() {

            @Override
            public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                refreshTaskHeadDetailCache(taskHeadDetail);
                Log.d("TEST_NOW", "after getTaskHeadDetail");
                showMembersCacheOf(taskHeadId);
                callback.onTaskHeadDetailLoaded(taskHeadDetail);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void showMembersCacheOf(String taskHeadId) {
        if(mCachedMembersOfTaskHead != null) {
            List<Member> members = mCachedMembersOfTaskHead.get(taskHeadId);
            if(members != null) {
                for(Member member: members) {
                    Log.d("TEST_NOW", member.toString());
                }
            }
        }
    }

    private List<String> getDeletingMemberIds(String taskHeadId, List<Member> editMembers) {
        // Compare cachedMembersOfTaskHead to editMembers
        List<String> deletingMemberIds = new ArrayList<>();

        if (mCachedMembersOfTaskHead != null) {
            List<Member> cachedMembers = mCachedMembersOfTaskHead.get(taskHeadId);
            if (cachedMembers != null) {
                for (Member member : cachedMembers) {
                    if (!editMembers.contains(member)) {
                        deletingMemberIds.add(member.getId());
                    }
                }
            }
        }
        return deletingMemberIds;

    }

    private void deleteMembersFromCache(List<String> deletingMemberIds) {
        if (mCachedMembers == null) {
            mCachedMembers = new LinkedHashMap<>();
        }
        for (String id : deletingMemberIds) {
            mCachedMembers.remove(id);
        }
    }

    private void addMembersToCache(List<Member> addingMembers) {
        if (mCachedMembers == null) {
            mCachedMembers = new LinkedHashMap<>();
        }
        for (Member member : addingMembers) {
            mCachedMembers.put(member.getId(), member);
        }
    }

    private List<Member> getAddingMembers(String taskHeadId, List<Member> editMembers) {
        List<Member> addingMembers = new ArrayList<>();

        if (mCachedMembersOfTaskHead != null) {
            List<Member> cachedMembers = mCachedMembersOfTaskHead.get(taskHeadId);
            if (cachedMembers != null) {

                Log.d("TEST_NOW", "getAddingMembers, cachedMember: " + cachedMembers.size());
                for (Member cachedMember : cachedMembers) {
                    Log.d("TEST_NOW", "getAddingMembers, cachedMember: " + cachedMember.toString());
                }

                for (Member member : editMembers) {
                    Log.d("TEST_NOW", "getAddingMembers, member: " + member.toString());
                    if (!cachedMembers.contains(member)) {
                        addingMembers.add(member);
                    }
                }
            }
        }
        return addingMembers;
    }

    private void refreshTaskHeadDetailCache(TaskHeadDetail taskHeadDetail) {
        // Save taskHead
        TaskHead taskHead = taskHeadDetail.getTaskHead();
        if (mCachedTaskHeads == null) {
            mCachedTaskHeads = new LinkedHashMap<>();
        }
        mCachedTaskHeads.remove(taskHead.getId());
        mCachedTaskHeads.put(taskHead.getId(), taskHead);

        // Save members of taskHead
        if (mCachedMembersOfTaskHead == null) {
            mCachedMembersOfTaskHead = new LinkedHashMap<>();
        }
        Log.d("TEST_NOW", "taskHead in refreshTaskHeadDetailCache is " + taskHead.getId());
        mCachedMembersOfTaskHead.remove(taskHead.getId());
        mCachedMembersOfTaskHead.put(taskHead.getId(), taskHeadDetail.getMembers());

        // Save members by member id
        if (mCachedMembers == null) {
            mCachedMembers = new LinkedHashMap<>();
        }
        List<Member> members = taskHeadDetail.getMembers();
        for (Member member : members) {
            mCachedMembers.remove(member.getId());
            mCachedMembers.put(member.getId(), member);
        }
    }
}
