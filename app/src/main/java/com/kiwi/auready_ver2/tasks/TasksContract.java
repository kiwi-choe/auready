package com.kiwi.auready_ver2.tasks;

import android.content.Intent;

/**
 * Created by kiwi on 6/26/16.
 */
public interface TasksContract {

    interface Presenter {

        void result(int requestCode, int resultCode, Intent data);
    }

    interface View {

        void setPresenter(TasksContract.Presenter tasksPresenter);

        void setLoginSuccessUI(String loggedInEmail);
    }
}
