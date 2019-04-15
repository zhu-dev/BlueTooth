package com.example.mybluetooth.activities.db;

import com.example.mybluetooth.activities.manager.UserBean;

import java.util.List;

import io.reactivex.Flowable;

public interface DataBaseService {
    //插入数据
    Flowable<Boolean> insertUser(List<UserBean> users);

    //更新数据
    Flowable<Boolean> updateUser(List<UserBean> users);

    //查询数据
    Flowable<List<UserBean>> getUser(String name);
}
