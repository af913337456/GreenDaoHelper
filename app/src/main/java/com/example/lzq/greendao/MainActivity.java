package com.example.lzq.greendao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import dao.lghTable;
import dao.lghTableDao;
import dao.pushVideo;

public class MainActivity extends AppCompatActivity {

    List<pushVideo> list;
    List<lghTable>  lghList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new VideoInfoDbCache().loadAllBeans();
        list = new VideoInfoDbCache().loadAllBeans();
        list = new VideoInfoDbCache().loadAllBeans();
        list = new VideoInfoDbCache().loadAllBeans();
        list = new VideoInfoDbCache().loadAllBeans();
        list = new VideoInfoDbCache().loadAllBeans();

        lghList = new LghTableDbCache().loadAllBeans();
        lghList = new LghTableDbCache().loadAllBeansWithLike(
                lghTableDao.Properties.Name,"林冠宏"
        );

    }
}
