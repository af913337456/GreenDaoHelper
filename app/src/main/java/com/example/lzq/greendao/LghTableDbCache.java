package com.example.lzq.greendao;

import com.example.greendaohelper.cache.sqlite.DBInterface;

import dao.lghTable;
import dao.lghTableDao;
import de.greenrobot.dao.Property;

/**
 * Created by lzq on 2017/3/21.
 */

public class LghTableDbCache
        extends
        DBInterface<lghTable, lghTableDao>
{

    @Override
    protected lghTableDao getWirteDao() {
        return openWritableDb().getLghTableDao();
    }

    @Override
    protected lghTableDao getReadDao() {
        return openReadableDb().getLghTableDao();
    }

    @Override
    protected Property getIdProperty() {
        return lghTableDao.Properties.Id;
    }
}
