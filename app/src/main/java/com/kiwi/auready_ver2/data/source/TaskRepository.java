package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link TaskHead}, {@link Member}, {@link Task} Repository
 */
public class TaskRepository implements TaskDataSource {

    private static TaskRepository INSTANCE = null;

    private TaskDataSource mLocalDataSource;
    private TaskDataSource mRemoteDataSource;

    private boolean mCacheIsDirty;

    /*
    * This variable has package local visibility so it can be accessed from tests.
    * */
    Map<String, TaskHead> mCachedTaskHeads = null;
    /*
    * This caches are refreshed by a taskHeadId
    * */
    // Key: taskHeadId
    Map<String, List<Member>> mCachedMembersOfTaskHead = null;
    // Key: member id
    Map<String, Member> mCachedMembers = null;
    Map<String, Task> mCachedTasks = null;

    // Prevent direct instantiation
    private TaskRepository(@NonNull TaskDataSource taskRemoteDataSource,
                           @NonNull TaskDataSource taskLocalDataSource) {
        mRemoteDataSource = taskRemoteDataSource;
        mLocalDataSource = taskLocalDataSource;
    }

    public static TaskRepository getInstance(@NonNull TaskDataSource taskRemoteDataSource,
                                             @NonNull TaskDataSource taskLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TaskRepository(checkNotNull(taskRemoteDataSource), checkNotNull(taskLocalDataSource));
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
    public void deleteAllTaskHeads(@NonNull DeleteAllCallback callback) {

        mLocalDataSource.deleteAllTaskHeads(new DeleteAllCallback() {
            @Override
            public void onDeleteAllSuccess() {

            }

            @Override
            public void onDeleteAllFail() {

            }
        });
//        if(mCachedTaskHeads != null) {
//            mCachedTaskHeads.clear();
//        }
    }

    @Override
    public void initializeLocalData(@NonNull final InitLocalDataCallback callback) {
        mLocalDataSource.initializeLocalData(new InitLocalDataCallback() {
            @Override
            public void onInitSuccess() {
                callback.onInitSuccess();
            }

            @Override
            public void onInitFail() {
                callback.onInitFail();
            }
        });
    }

    /*
    * Load from Remote first
    * */
    @Override
    public void getTaskHeadDetails(@NonNull final LoadTaskHeadDetailsCallback callback) {
        checkNotNull(callback);

        // Get taskheads, members and tasks at once from Remote
        mRemoteDataSource.getTaskHeadDetails(new LoadTaskHeadDetailsCallback() {
            @Override
            public void onTaskHeadDetailsLoaded(List<TaskHeadDetail> taskHeadDetails) {
                refreshLocalDataSource(taskHeadDetails);
                callback.onTaskHeadDetailsLoaded(taskHeadDetails);
            }

            @Override
            public void onDataNotAvailable() {

                mLocalDataSource.getTaskHeadDetails(new LoadTaskHeadDetailsCallback() {
                    @Override
                    public void onTaskHeadDetailsLoaded(List<TaskHeadDetail> taskHeadDetails) {
                        callback.onTaskHeadDetailsLoaded(taskHeadDetails);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void deleteTaskHeads(final List<String> taskheadIds, @NonNull final DeleteTaskHeadsCallback callback) {
        // Delete from cache
        if (mCachedTaskHeads != null) {
            for (String id : taskheadIds) {
                mCachedTaskHeads.remove(id);
            }
        }

        mRemoteDataSource.deleteTaskHeads(taskheadIds, new DeleteTaskHeadsCallback() {
            @Override
            public void onDeleteSuccess() {

                mLocalDataSource.deleteTaskHeads(taskheadIds, new DeleteTaskHeadsCallback() {
                    @Override
                    public void onDeleteSuccess() {
                        callback.onDeleteSuccess();
                    }

                    @Override
                    public void onDeleteFail() {
                        callback.onDeleteFail();
                    }
                });
            }

            @Override
            public void onDeleteFail() {
                callback.onDeleteFail();
            }
        });
    }

    private void refreshLocalDataSource(final List<TaskHeadDetail> taskHeadDetails) {
        mLocalDataSource.deleteAllTaskHeads(new DeleteAllCallback() {
            @Override
            public void onDeleteAllSuccess() {

                for(TaskHeadDetail taskHeadDetail: taskHeadDetails) {
                    mLocalDataSource.saveTaskHeadDetail(taskHeadDetail, new SaveCallback() {
                        @Override
                        public void onSaveSuccess() {}

                        @Override
                        public void onSaveFailed() {}
                    });
                }
            }

            @Override
            public void onDeleteAllFail() {

            }
        });
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
                refreshCachesOfTaskHeadDetail(taskHeadDetail);
                showMembersCacheOf(taskHeadDetail.getTaskHead().getId());

                // Save into Remote asynchronously with Local
                saveTaskHeadDetailToRemote(taskHeadDetail);

                callback.onSaveSuccess();
            }

            @Override
            public void onSaveFailed() {
                callback.onSaveFailed();
            }
        });
    }

    private void saveTaskHeadDetailToRemote(TaskHeadDetail taskHeadDetail) {

        mRemoteDataSource.saveTaskHeadDetail(taskHeadDetail, new SaveCallback() {
            @Override
            public void onSaveSuccess() {
                Log.d("test_SaveTaskHead", "success of saving into Remote");
            }

            @Override
            public void onSaveFailed() {
                Log.d("test_SaveTaskHead", "fail of saving into Remote");
            }
        });
    }

    public void editTaskHeadDetail(@NonNull TaskHead editTaskHead,
                                   @NonNull List<Member> addingMembers,
                                   @NonNull List<String> deletingMemberIds,
                                   @NonNull final EditTaskHeadDetailCallback callback) {

        checkNotNull(editTaskHead);
        checkNotNull(addingMembers);
        checkNotNull(deletingMemberIds);

        mLocalDataSource.editTaskHeadDetail(editTaskHead, addingMembers, deletingMemberIds, callback);
    }

    /*
    * In Repository, comparing to cachedMembers first
    * */
    public void editTaskHeadDetailInRepo(@NonNull TaskHeadDetail taskheadDetail, @NonNull final EditTaskHeadDetailCallback callback) {
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
                refreshCachesOfTaskHeadDetail(taskHeadDetail);
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

    /*
    * Task
    * */
    @Override
    public void getMembers(@NonNull final String taskHeadId, @NonNull final LoadMembersCallback callback) {
        checkNotNull(taskHeadId);

        mLocalDataSource.getMembers(taskHeadId, new LoadMembersCallback() {
            @Override
            public void onMembersLoaded(List<Member> members) {
                refreshMembersOfTaskHeadCache(taskHeadId, members);
                refreshMembersCache(members);
                callback.onMembersLoaded(members);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getTasksOfMember(@NonNull String memberId, @NonNull final LoadTasksCallback callback) {
        mLocalDataSource.getTasksOfMember(memberId, new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshTasksCache(tasks);
                callback.onTasksLoaded(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getTasksOfTaskHead(@NonNull String taskheadId, @NonNull final LoadTasksCallback callback) {
        mLocalDataSource.getTasksOfTaskHead(taskheadId, new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshTasksCache(tasks);
                callback.onTasksLoaded(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mLocalDataSource.saveTask(task);

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void deleteTasks(@NonNull List<String> taskIds) {
        checkNotNull(taskIds);

        mLocalDataSource.deleteTasks(taskIds);

        if (mCachedTasks != null) {
            for (String id : taskIds) {
                mCachedTasks.remove(id);
            }
        }
    }

    @Override
    public void editTasks(@NonNull List<Task> tasks) {
        mLocalDataSource.editTasks(tasks);

        refreshTasksCache(tasks);
    }

    private void showMembersCacheOf(String taskHeadId) {
        if (mCachedMembersOfTaskHead != null) {
            List<Member> members = mCachedMembersOfTaskHead.get(taskHeadId);
            if (members != null) {
                for (Member member : members) {
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

    /*
    * refresh caches methods
    * */
    private void refreshCachesOfTaskHeadDetail(TaskHeadDetail taskHeadDetail) {
        // Save taskHead
        TaskHead taskHead = taskHeadDetail.getTaskHead();
        if (mCachedTaskHeads == null) {
            mCachedTaskHeads = new LinkedHashMap<>();
        }
        mCachedTaskHeads.remove(taskHead.getId());
        mCachedTaskHeads.put(taskHead.getId(), taskHead);

        // Save members of taskHead
        refreshMembersOfTaskHeadCache(taskHead.getId(), taskHeadDetail.getMembers());

        // Save members by member id
        refreshMembersCache(taskHeadDetail.getMembers());
    }

    private void refreshMembersOfTaskHeadCache(String taskHeadId, List<Member> members) {
        // Save members of taskHead
        if (mCachedMembersOfTaskHead == null) {
            mCachedMembersOfTaskHead = new LinkedHashMap<>();
        }
        mCachedMembersOfTaskHead.remove(taskHeadId);
        mCachedMembersOfTaskHead.put(taskHeadId, members);

    }

    private void refreshMembersCache(List<Member> members) {
        // Save members by member id
        if (mCachedMembers == null) {
            mCachedMembers = new LinkedHashMap<>();
        }
        mCachedMembers.clear();
        for (Member member : members) {
            mCachedMembers.put(member.getId(), member);
        }
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

    private void refreshTasksCache(List<Task> tasks) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getId(), task);
        }
    }
}
