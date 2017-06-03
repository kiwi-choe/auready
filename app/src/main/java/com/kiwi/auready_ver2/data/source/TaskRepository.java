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
    /*
    * Force to update Local taskDataSource, getting from Remote data source
    * */
    private boolean mForceToUpdate_ATaskHeadDetail = false;
    private boolean mForceToUpdate_TaskHeadDetails = false;

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

    @Override
    public void getTaskHeadDetails(@NonNull final LoadTaskHeadDetailsCallback callback) {
        checkNotNull(callback);

        if(mForceToUpdate_TaskHeadDetails) {
            getTaskHeadDetailsFromRemote(callback);
        } else {
            mLocalDataSource.getTaskHeadDetails(new LoadTaskHeadDetailsCallback() {
                @Override
                public void onTaskHeadDetailsLoaded(List<TaskHeadDetail> taskHeadDetails) {
                    mForceToUpdate_TaskHeadDetails = false;

                    callback.onTaskHeadDetailsLoaded(taskHeadDetails);
                }

                @Override
                public void onDataNotAvailable() {
                    getTaskHeadDetailsFromRemote(callback);
                }
            });
        }
    }

    /*
    * Get taskheads, members and tasks at once from Remote
    * */
    private void getTaskHeadDetailsFromRemote(final LoadTaskHeadDetailsCallback callback) {
        mRemoteDataSource.getTaskHeadDetails(new LoadTaskHeadDetailsCallback() {
            @Override
            public void onTaskHeadDetailsLoaded(List<TaskHeadDetail> taskHeadDetails) {
                mForceToUpdate_TaskHeadDetails = false;

                refreshLocalTaskHeadDetails(taskHeadDetails, callback);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
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
        mLocalDataSource.deleteTaskHeads(taskheadIds, new DeleteTaskHeadsCallback() {
            @Override
            public void onDeleteSuccess() {
                mRemoteDataSource.deleteTaskHeads(taskheadIds, new DeleteTaskHeadsCallback() {
                    @Override
                    public void onDeleteSuccess() {
                    }

                    @Override
                    public void onDeleteFail() {
                    }
                });

                callback.onDeleteSuccess();
            }

            @Override
            public void onDeleteFail() {
                callback.onDeleteFail();
            }
        });

    }

    /*
    * Refresh after getting TaskHeadDetails from Remote
    * */
    private void refreshLocalTaskHeadDetails(final List<TaskHeadDetail> taskHeadDetails, final LoadTaskHeadDetailsCallback callback) {
        // TaskHead, Member
        mLocalDataSource.deleteAllTaskHeads(new DeleteAllCallback() {
            @Override
            public void onDeleteAllSuccess() {
                mLocalDataSource.saveTaskHeadDetails(taskHeadDetails, new SaveTaskHeadDetailsCallback() {

                    @Override
                    public void onSaveSuccess() {
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

                    @Override
                    public void onSaveFailed() {
                        Log.d("Tag_refreshLocal", "refreshLocalTaskHeadDetails onSaveFailed");
                        callback.onTaskHeadDetailsLoaded(taskHeadDetails);
                    }
                });
            }

            @Override
            public void onDeleteAllFail() {
                Log.d("Tag_refreshLocal", "refreshLocalTaskHeadDetails onDeleteAllFail");
                callback.onTaskHeadDetailsLoaded(taskHeadDetails);
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
    public void updateTaskHeadOrders(@NonNull final List<TaskHead> taskHeads, @NonNull final UpdateTaskHeadOrdersCallback callback) {
        mLocalDataSource.updateTaskHeadOrders(taskHeads, new UpdateTaskHeadOrdersCallback() {
            @Override
            public void onUpdateSuccess() {
                mRemoteDataSource.updateTaskHeadOrders(taskHeads, new UpdateTaskHeadOrdersCallback() {
                    @Override
                    public void onUpdateSuccess() {


                    }

                    @Override
                    public void onUpdateFailed() {
//                        callback.onUpdateFailed();
                    }
                });
                callback.onUpdateSuccess();
            }

            @Override
            public void onUpdateFailed() {
                callback.onUpdateFailed();
            }
        });

        refreshTaskHeadsCache(taskHeads);
    }

    @Override
    public void saveTaskHeadDetails(@NonNull List<TaskHeadDetail> taskHeadDetails, @NonNull SaveTaskHeadDetailsCallback callback) {
        // implement in Local
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
            }

            @Override
            public void onSaveFailed() {
            }
        });
    }

    public void editTaskHeadDetail(@NonNull final TaskHead editTaskHead,
                                   @NonNull final List<Member> addingMembers,
                                   @NonNull final EditTaskHeadDetailCallback callback) {

        checkNotNull(editTaskHead);
        checkNotNull(addingMembers);

        mLocalDataSource.editTaskHeadDetail(editTaskHead, addingMembers, new EditTaskHeadDetailCallback() {
            @Override
            public void onEditSuccess() {
                addMembersToCache(addingMembers);

                // Put into Remote asynchronously with Local
                mRemoteDataSource.editTaskHeadDetail(editTaskHead, addingMembers, new EditTaskHeadDetailCallback() {
                    @Override
                    public void onEditSuccess() {
                        Log.d("test_EditTaskHead", "success of putting into Remote");
                    }

                    @Override
                    public void onEditFailed() {
                        Log.d("test_EditTaskHead", "fail of putting into Remote");
                    }
                });

                callback.onEditSuccess();
            }

            @Override
            public void onEditFailed() {
                callback.onEditFailed();
            }
        });
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
//        final List<String> deletingMemberIds = getDeletingMemberIds(editTaskHead.getId(), editMembers);

        // Save taskHead, Save or Delete members
        editTaskHeadDetail(editTaskHead, addingMembers, callback);
    }

    @Override
    public void getTaskHeadDetail(@NonNull final String taskHeadId, @NonNull final GetTaskHeadDetailCallback callback) {
        checkNotNull(taskHeadId);
        checkNotNull(callback);

        if (mForceToUpdate_ATaskHeadDetail) {
            getTaskHeadDetailFromRemote(taskHeadId, callback);
        } else {
            // Is the taskhead in the local? if not, query the network.
            mLocalDataSource.getTaskHeadDetail(taskHeadId, new GetTaskHeadDetailCallback() {

                @Override
                public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                    mForceToUpdate_ATaskHeadDetail = false;

                    refreshCachesOfTaskHeadDetail(taskHeadDetail);
                    showMembersCacheOf(taskHeadId);

                    callback.onTaskHeadDetailLoaded(taskHeadDetail);
                }

                @Override
                public void onDataNotAvailable() {
                    getTaskHeadDetailFromRemote(taskHeadId, callback);
                }
            });
        }
    }

    private void getTaskHeadDetailFromRemote(String taskHeadId, final GetTaskHeadDetailCallback callback) {
        mRemoteDataSource.getTaskHeadDetail(taskHeadId, new GetTaskHeadDetailCallback() {
            @Override
            public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                mForceToUpdate_ATaskHeadDetail = false;

                refreshLocalATaskHeadDetail(taskHeadDetail, callback);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshLocalATaskHeadDetail(final TaskHeadDetail taskHeadDetail, final GetTaskHeadDetailCallback callback) {
        mLocalDataSource.deleteAllTaskHeads(new DeleteAllCallback() {
            @Override
            public void onDeleteAllSuccess() {
                mLocalDataSource.saveTaskHeadDetail(taskHeadDetail, new SaveCallback() {
                    @Override
                    public void onSaveSuccess() {
                        mLocalDataSource.getTaskHeadDetail(taskHeadDetail.getTaskHead().getId(), new GetTaskHeadDetailCallback() {
                            @Override
                            public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                                callback.onTaskHeadDetailLoaded(taskHeadDetail);
                            }

                            @Override
                            public void onDataNotAvailable() {
                                callback.onDataNotAvailable();
                            }
                        });
                    }

                    @Override
                    public void onSaveFailed() {
                        Log.d("Tag_refreshLocal", "refreshLocalATaskHeadDetail onSaveFailed");
                        callback.onTaskHeadDetailLoaded(taskHeadDetail);
                    }
                });
            }

            @Override
            public void onDeleteAllFail() {
                Log.d("Tag_refreshLocal", "refreshLocalATaskHeadDetail onDeleteFailed");
                callback.onTaskHeadDetailLoaded(taskHeadDetail);
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

                mRemoteDataSource.getMembers(taskHeadId, new LoadMembersCallback() {
                    @Override
                    public void onMembersLoaded(List<Member> members) {
                        refreshLocalMembers(taskHeadId, members);
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
        });
    }

    private void refreshLocalMembers(final String taskHeadId, final List<Member> members) {
        // Delete members of TaskHead(id)
        mLocalDataSource.deleteMembers(taskHeadId, new DeleteMembersCallback() {
            @Override
            public void onDeleteSuccess() {
                // and save loaded members
                mLocalDataSource.saveMembers(members);
            }

            @Override
            public void onDeleteFail() {

            }
        });
    }

    @Override
    public void getTasksOfMember(@NonNull final String memberId, @NonNull final LoadTasksCallback callback) {

        mRemoteDataSource.getTasksOfMember(memberId, new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshLocalTasksOfMember(memberId, tasks);
                refreshCachedTasks(tasks);
                callback.onTasksLoaded(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                mLocalDataSource.getTasksOfMember(memberId, new LoadTasksCallback() {
                    @Override
                    public void onTasksLoaded(List<Task> tasks) {
                        refreshCachedTasks(tasks);
                        callback.onTasksLoaded(tasks);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });

    }

    private void refreshLocalTasksOfMember(String memberId, List<Task> tasks) {
        // Delete all tasks of member
        mLocalDataSource.deleteTasksOfMember(memberId);
        // update or insert
        for (Task task : tasks) {
            mLocalDataSource.saveTask(task, new ArrayList<Task>(), new SaveTaskCallback() {
                @Override
                public void onSaveSuccess(List<Task> tasksOfMember) {

                }

                @Override
                public void onSaveFailed() {

                }
            });
        }
    }

    @Override
    public void getTasksOfTaskHead(@NonNull String taskheadId, @NonNull final LoadTasksCallback callback) {
        mLocalDataSource.getTasksOfTaskHead(taskheadId, new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCachedTasks(tasks);
                callback.onTasksLoaded(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void deleteTask(final String memberId, @NonNull final String taskId, @NonNull final List<Task> editingTasks, @NonNull final DeleteTaskCallback callback) {
        checkNotNull(taskId);

        mLocalDataSource.deleteTask(memberId, taskId, new ArrayList<Task>(), new DeleteTaskCallback() {
            @Override
            public void onDeleteSuccess(List<Task> tasksOfMember) {

                mRemoteDataSource.deleteTask(memberId, taskId, editingTasks, new DeleteTaskCallback() {
                    @Override
                    public void onDeleteSuccess(List<Task> tasksOfMember) {
                        callback.onDeleteSuccess(tasksOfMember);
                    }

                    @Override
                    public void onDeleteFailed() {
                        callback.onDeleteFailed();
                    }
                });
            }

            @Override
            public void onDeleteFailed() {
                callback.onDeleteFailed();
            }
        });

        if (mCachedTasks != null) {
            mCachedTasks.remove(taskId);
        }
    }

    @Override
    public void editTasks(@NonNull String taskHeadId, @NonNull Map<String, List<Task>> cachedTasks) {
        mLocalDataSource.editTasks(taskHeadId, cachedTasks);
        mRemoteDataSource.editTasks(taskHeadId, cachedTasks);

        // Make the collection for all the tasks of members
        List<Task> updatingTasks = new ArrayList<>();
        for (String key : cachedTasks.keySet()) {
            List<Task> tasks = cachedTasks.get(key);
            updatingTasks.addAll(tasks);
        }

        refreshCachedTasks(updatingTasks);
    }

    @Override
    public void saveTask(@NonNull final Task task, @NonNull final List<Task> editingTasks, @NonNull final SaveTaskCallback callback) {
        checkNotNull(task);
        mLocalDataSource.saveTask(task, new ArrayList<Task>(), new SaveTaskCallback() {

            @Override
            public void onSaveSuccess(List<Task> tasksOfMember) {
                mRemoteDataSource.saveTask(task, editingTasks, new SaveTaskCallback() {
                    @Override
                    public void onSaveSuccess(List<Task> tasksOfMember) {
                        callback.onSaveSuccess(tasksOfMember);
                    }

                    @Override
                    public void onSaveFailed() {
                        callback.onSaveFailed();
                    }
                });
            }

            @Override
            public void onSaveFailed() {
                callback.onSaveFailed();
            }
        });

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void saveMembers(List<Member> members) {

    }

    @Override
    public void changeComplete(Task editedTask) {
        mLocalDataSource.changeComplete(editedTask);

        if (mCachedTasks != null) {
            mCachedTasks.put(editedTask.getId(), editedTask);
        }
    }

    @Override
    public void deleteTasksOfMember(String memberId) {
        // local
    }

    @Override
    public void deleteMembers(String taskHeadId, DeleteMembersCallback callback) {

    }

    /*
    * Remote only
    * */
    @Override
    public void editTasksOfMember(final String memberId, final List<Task> tasks,
                                  @NonNull final EditTasksOfMemberCallback callback) {

        mRemoteDataSource.editTasksOfMember(memberId, tasks, new EditTasksOfMemberCallback() {

            @Override
            public void onEditSuccess(List<Task> tasksOfMember) {
                refreshLocalTasksOfMember(memberId, tasksOfMember);
                refreshCachedTasks(tasksOfMember);
                callback.onEditSuccess(tasksOfMember);
            }

            @Override
            public void onEditFail() {
                callback.onEditFail();
            }
        });
    }

    @Override
    public void forceUpdateLocalATaskHeadDetail() {
        mForceToUpdate_ATaskHeadDetail = true;
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

    private void refreshCachedTasks(List<Task> tasks) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
//        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getId(), task);
        }
    }

    public void forceUpdateLocalTaskHeadDetails() {
        mForceToUpdate_TaskHeadDetails = true;
    }
}
