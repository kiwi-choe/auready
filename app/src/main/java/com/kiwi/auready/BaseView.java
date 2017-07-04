package com.kiwi.auready;

/**
 * Created by kiwi on 8/3/16.
 */
public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);
}
