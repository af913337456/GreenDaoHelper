package com.example.greendaohelper.cache;

import android.app.Application;
import android.content.Context;

import com.example.greendaohelper.cache.sqlite.DBInit;


/**
 * 作者：林冠宏
 *
 * author: LinGuanHong,lzq is my dear wife.
 *
 * My GitHub : https://github.com/af913337456/
 *
 * My Blog   : http://www.cnblogs.com/linguanh/
 *
 * on 2017/3/18.
 */

public class LghApp extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        DBInit.instance();
    }
}
