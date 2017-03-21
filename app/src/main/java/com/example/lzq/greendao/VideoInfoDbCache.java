package com.example.lzq.greendao;

import com.example.greendaohelper.cache.sqlite.DBInterface;

import dao.pushVideo;
import dao.pushVideoDao;
import de.greenrobot.dao.Property;

/**
 * Created by lzq on 2017/3/21.
 */

public class VideoInfoDbCache
        extends
    DBInterface<pushVideo, pushVideoDao> {

    @Override
    protected pushVideoDao getWirteDao() {
        return openWritableDb().getPushVideoDao();
    }

    @Override
    protected pushVideoDao getReadDao() {
        return openReadableDb().getPushVideoDao();
    }

    @Override
    protected Property getIdProperty() {
        return pushVideoDao.Properties.D_id;
    }
}
