package com.example.greendaohelper.cache.sqlite;

import android.database.sqlite.SQLiteDatabase;
import com.example.greendaohelper.cache.LghLogger;

import java.util.List;

import dao.DaoMaster;
import dao.DaoSession;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.DeleteQuery;

/**
 * Created by LinGuanHong on 2017/1/4.
 *
 * 作者：林冠宏
 *
 * author: LinGuanHong,lzq is my dear wife.
 *
 * My GitHub : https://github.com/af913337456/
 *
 * My Blog   : http://www.cnblogs.com/linguanh/
 */

public abstract class DBInterface<K,T extends AbstractDao<K,Long>> {

    private LghLogger lghLogger = LghLogger.getLogger(DBInterface.class);

    private DBInit dbInit;

    public DBInterface(){
        /** 不引起 DBInit 的重复实例化 */
        dbInit = DBInit.instance();
    }

    protected abstract T getWirteDao();

    protected abstract T getReadDao();

    protected abstract Property getIdProperty();

    private void isInit(){
        if(dbInit.getOpenHelper() == null){
            throw new NullPointerException("openHelper is null!");
        }
    }

    /**
     * Query for readable DB
     */
    protected DaoSession openReadableDb() {
        isInit();
        SQLiteDatabase db = dbInit.getOpenHelper().getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }

    /**
     * Query for writable DB
     */
    protected DaoSession openWritableDb(){
        isInit();
        SQLiteDatabase db = dbInit.getOpenHelper().getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }

    /** 增 */
    public void insertOrUpdateInfo(K entity){
        T userDao = getWirteDao();
        userDao.insertOrReplace(entity);
    }

    public void batchInsertOrUpdateAllInfo(List<K> entityList){
        if(entityList.size() <=0 ){
            lghLogger.d("本地数据库插入用户信息失败，条数是 0 ");
            return ;
        }
        T userDao = getWirteDao();
        userDao.insertOrReplaceInTx(entityList);
    }



    /** 删 */
    public void deleteOneById(int id){
        T userDao = getWirteDao();
        DeleteQuery<K> bd = userDao.queryBuilder()
                .where(getIdProperty().eq(id))
                .buildDelete();

        bd.executeDeleteWithoutDetachingEntities();
    }

    public void deleteAllBeans(){
        T userDao =  getWirteDao();
        DeleteQuery<K> bd = userDao.queryBuilder()
                .buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    /** 改 */
    /** 在查里面，因为本身就是 insertOrUpdate */

    /** 查，加入了模糊查找 */
    public K getBeanById(int id){
        T dao = getReadDao();
        return dao
                .queryBuilder()
                .where(getIdProperty().eq(id)).unique();
    }

    public K getBeanWithLike(Property property,String what){
        T dao = getReadDao();
        return dao
                .queryBuilder()
                .where(property.like("%"+what+"%")).unique();
    }

    public List<K> loadAllBeans(){
        T dao = getReadDao();
        /** 倒叙 */
        return dao.queryBuilder().orderDesc(getIdProperty()).list();
    }

    public List<K> loadAllBeansWithLike(Property property, String what){
        T dao = getReadDao();
        return dao
                .queryBuilder()
                .where(property.like("%"+what+"%")).orderAsc(getIdProperty()).list();
    }

}
