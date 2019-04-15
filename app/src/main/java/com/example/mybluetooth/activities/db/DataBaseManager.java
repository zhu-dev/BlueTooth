package com.example.mybluetooth.activities.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mybluetooth.activities.manager.UserBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class DataBaseManager implements DataBaseService {

    private static volatile DataBaseManager manager;//懒汉单例模式
    private DataBaseHelper helper;
    private static final String TAG = "DataBaseManager";

    //私有的构造方法
    private DataBaseManager(Context context) {
        helper = new DataBaseHelper(context, "timetable.db", null, 1);
    }

    //获取实例的方法
    public static DataBaseManager getInstance(Context context) {
        //加入double checking
        if (manager != null) {
            return manager;
        }
        synchronized (DataBaseManager.class) {
            //初始化可能时一个耗时操作，有可能发生指令重排
            //对象没有初始化完成时，子线程就拿到对象，是一个空对象
            //加入volatile关键字，避免指令重排
            if (manager == null) {
                manager = new DataBaseManager(context);
            }
        }
        return manager;
    }

    @Override
    public Flowable<Boolean> insertUser(final List<UserBean> users) {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(FlowableEmitter<Boolean> emitter) throws Exception {
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                for (int i = 0; i < users.size(); i++) {
                    UserBean user = users.get(i);
                    cv.put("name",user.getName());
                    cv.put("time",user.getTimeStr());
                    cv.put("date",user.getDateStr());
                    cv.put("dia_pressure",user.getDia_pressure());
                    cv.put("sys_pressure",user.getSys_pressure());
                    db.insert("users", null, cv);
                }
                emitter.onNext(true);
                emitter.onComplete();//完成事件
            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<Boolean> updateUser(List<UserBean> users) {
        return null;
    }

    @Override
    public Flowable<List<UserBean>> getUser(final String name) {
        return Flowable.create(new FlowableOnSubscribe<List<UserBean>>() {
            @Override
            public void subscribe(FlowableEmitter<List<UserBean>> emitter) throws Exception {
                List<UserBean> dataList = new ArrayList<>();
               // String sql = "SELECT * FROM users WHERE name = "+name;//错误的写法，应该解析name
                SQLiteDatabase db = helper.getWritableDatabase();
               // Cursor cursor = db.rawQuery(sql, null);
                Cursor cursor = db.query("users",null,"name = ?",new String[]{name},null,null,null);
                if (cursor.moveToFirst()) {
                    //遍历所有的数据
                    do {
                        UserBean user = new UserBean();
                        user.setName(cursor.getString(cursor.getColumnIndex("name")));
                        user.setDateStr(cursor.getString(cursor.getColumnIndex("date")));
                        user.setTimeStr(cursor.getString(cursor.getColumnIndex("time")));
                        user.setDia_pressure(cursor.getInt(cursor.getColumnIndex("dia_pressure")));
                        user.setSys_pressure(cursor.getInt(cursor.getColumnIndex("sys_pressure")));
                        dataList.add(user);
                        Log.e(TAG, "subscribe: --ok2---");
                    } while (cursor.moveToNext());
                }
                Log.e(TAG, "subscribe: --size---" + dataList.size());
                cursor.close();//关闭指针
                db.close();//关闭数据库

                emitter.onNext(dataList);//发射结果
                emitter.onComplete();//完成事件
            }
        },BackpressureStrategy.BUFFER);
    }
}
