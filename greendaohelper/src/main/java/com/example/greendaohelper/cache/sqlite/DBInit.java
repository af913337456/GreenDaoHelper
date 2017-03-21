package com.example.greendaohelper.cache.sqlite;

import android.content.Context;

import com.example.greendaohelper.cache.LghApp;
import com.example.greendaohelper.cache.LghLogger;

import dao.DaoMaster;

/**
 * Created by LinGuanHong on 2017/3/18.
 * <p>
 * 作者：林冠宏
 * <p>
 * author: LinGuanHong,lzq is my dear wife.
 * <p>
 * My GitHub : https://github.com/af913337456/
 * <p>
 * My Blog   : http://www.cnblogs.com/linguanh/
 */

public class DBInit {

    private LghLogger lghLogger = LghLogger.getLogger(DBInit.class);

    private int loginUserId = 0;

    private DaoMaster.DevOpenHelper openHelper;

    public static DBInit instance(){
        return DBHelper.dbInit;
    }

    /** 私有 */
    private DBInit(){
        lghLogger.d("初始化 dbinit");
        initDbHelp(LghApp.context,1); /** 可以自己迁移初始化位置 */
    }

    public DaoMaster.DevOpenHelper getOpenHelper(){
        return openHelper;
    }

    private static class DBHelper{

        private static DBInit dbInit = new DBInit();

    }

    /**  十分建议使用 Application 的 context
      *  支持用用户的 ID 区分数据表
      *  */
    public void initDbHelp(Context ctx, int loginId){
        if(ctx == null || loginId <=0 ){
            throw  new RuntimeException("#DBInterface# init DB exception!");
        }
        /** 切换用户的时候， openHelper 不是 null */
        String DBName = "lgh_"+loginId+".db";
        if(openHelper!=null){
            /** 判断下 db name 是不是一样的，不是一样就重置 */
            String dbNameTemp = openHelper.getDatabaseName().trim();
            if(dbNameTemp.equals(DBName)){
                lghLogger.d("相同的用户，不用重新初始化本地 DB");
                return;
            }else{
                lghLogger.d("不是相同的用户，需要重新初始化本地 DB");
                openHelper.close();
                openHelper = null;
            }
        }
        if(loginUserId !=loginId ){
            loginUserId = loginId;
            close();
            lghLogger.d("DB init,loginId: "+loginId);
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx, DBName, null);
            this.openHelper = helper;
        }else{
            lghLogger.d("DB init,failed: "+loginId);
        }
    }

    private void close() {
        if(openHelper !=null) {
            lghLogger.d("关闭数据库接口类");
            openHelper.close();
            openHelper = null;
            loginUserId = 0;
        }
    }
}
