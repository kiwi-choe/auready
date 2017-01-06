package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;

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
        if(mCachedTaskHeads != null) {
            for(String id:taskheadIds) {
                mCachedTaskHeads.remove(id);
            }
        }
    }

    public void saveTaskHeadDetail(@NonNull final TaskHeadDetail taskHeadDetail, @NonNull final SaveCallback callback) {

        saveTaskHead(taskHeadDetail.getTaskHead(), new TaskDataSource.SaveCallback() {
            @Override
            public void onSaveSuccess() {
                saveMembers(taskHeadDetail.getMembers(), new TaskDataSource.SaveCallback() {
                    @Override
                    public void onSaveSuccess() {
                        saveTaskHeadDetailToCache(taskHeadDetail);
                        callback.onSaveSuccess();
                    }

                    @Override
                    public void onSaveFailed() {
                        callback.onSaveFailed();
                    }
                });
            }

            @Override
            public void onSaveFailed() {

            }
        });
    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead, @NonNull final SaveCallback callback) {
        checkNotNull(taskHead);
        checkNotNull(callback);
        mLocalDataSource.saveTaskHead(taskHead, callback);
    }

    @Override
    public void saveMembers(@NonNull List<Member> members, @NonNull final SaveCallback callback) {
        checkNotNull(members);
        checkNotNull(callback);

        mLocalDataSource.saveMembers(members, callback);
    }

    private void saveTaskHeadDetailToCache(TaskHeadDetail taskHeadDetail) {
        // Save taskHead
        TaskHead taskHead = taskHeadDetail.getTaskHead();
        if(mCachedTaskHeads == null) {
            mCachedTaskHeads = new LinkedHashMap<>();
        }
        mCachedTaskHeads.put(taskHead.getId(), taskHead);

        if(mCachedMembersOfTaskHead == null) {
            mCachedMembersOfTaskHead = new LinkedHashMap<>();
        }
        mCachedMembersOfTaskHead.put(taskHead.getId(), taskHeadDetail.getMembers());

        if(mCachedMembers == null) {
            mCachedMembers = new LinkedHashMap<>();
        }
        List<Member> members = taskHeadDetail.getMembers();
        for(Member member: members) {
            mCachedMembers.put(member.getId(), member);
        }
    }
}
