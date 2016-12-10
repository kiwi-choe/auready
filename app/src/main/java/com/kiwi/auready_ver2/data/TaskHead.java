package com.kiwi.auready_ver2.data;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kiwi.auready_ver2.data.source.local.SQLiteDbHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable model class for a TaskHead
 */
public class TaskHead {

    private final String mId;
    private String mTitle;
    private List<String> mMembers;
    private int mMembersCnt;

    /*
    * Use this constructor to create a new TaskHead.
    * */
    public TaskHead(String title, List<String> members) {
        mId = UUID.randomUUID().toString();
        mTitle = title;
        mMembers = members;
        setMembersCnt();
    }
    /*
    * Use the constructor when get values from DB only
    * */
    public TaskHead(String title, String strMembers) {
        mId = UUID.randomUUID().toString();
        mTitle = title;
        convertStrToMemberList(strMembers);
        setMembersCnt();
    }

    /*
    * Use this constructor to create a new TaskHead with no Title.
    * */
    public TaskHead() {
        mId = UUID.randomUUID().toString();
        mTitle = "";
    }

    /*
        * Use this constructor to create a Task if the TaskHead already has an id
        * (copy of another task)
        * */
    public TaskHead(@NonNull String id, String title, List<String> members) {
        mId = checkNotNull(id, "id cannot be null");
        mTitle = title;
        mMembers = members;
        setMembersCnt();
    }
    /*
    * Use this constructor to create a new TaskHead with no Title.
    * */
    public TaskHead(@NonNull String id, String title, String strMembers) {
        mId = checkNotNull(id, "id cannot be null");
        mTitle = title;
        convertStrToMemberList(strMembers);
        setMembersCnt();
    }


    public String getTitle() {
        return mTitle;
    }

    public String getId() {
        return mId;
    }

    public List<String> getMembers() {
        return mMembers;
    }

    public String getMembersString() {
        String strMembers = null;
        try {
            strMembers = SQLiteDbHelper.OBJECT_MAPPER.writeValueAsString(mMembers);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return strMembers;
    }

    public boolean isEmpty() {
        return (mTitle == null || "".equals(mTitle));
    }

    private void setMembersCnt() {
        if (mMembers != null) {
            mMembersCnt = mMembers.size();
        } else {
            mMembersCnt = 0;
        }
    }
    private void convertStrToMemberList(String strMembers) {
        mMembers = new ArrayList<>(0);
        if(strMembers.length() != 0) {
            try {
                mMembers =
                        SQLiteDbHelper.OBJECT_MAPPER.readValue(strMembers, new TypeReference<ArrayList<String>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
