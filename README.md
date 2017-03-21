# GreenDaoHelper

> 作者：林冠宏 / 指尖下的幽灵
> 掘金：https://juejin.im/user/587f0dfe128fe100570ce2d8
> 博客：http://www.cnblogs.com/linguanh/
> GitHub ： https://github.com/af913337456/

> 一直以来，我都是极其反感写重复的代码，所以喜欢利用面向对象的编程属性来自己造轮，或者是二次封装。

## 前序
``GreenDao`` 相信很多 ``Android`` 开发者都熟悉，不知为何物的，这里不会再介绍它，建议自行百度，介绍文很多。
前天我<strong>再次</strong>在项目中使用到 ``Sqlite`` 来做缓存，一般的代码是下面这样的。
```java
        Entity userInfo = schema.addEntity("UserEntity");
        userInfo.setTableName("UserInfo");
        userInfo.setClassNameDao("UserDao");
        userInfo.setJavaPackage(entityPath);
        userInfo.addIdProperty().autoincrement();
        userInfo.addIntProperty("peerId").unique().notNull().index();
        userInfo.addIntProperty("gender").notNull();
        userInfo.addStringProperty("mainName").notNull();
        userInfo.addStringProperty("pinyinName").notNull();
        userInfo.addStringProperty("realName").notNull();
        userInfo.addStringProperty("avatar").notNull();
        userInfo.addStringProperty("phone").notNull();
        userInfo.addStringProperty("email").notNull();
        userInfo.addIntProperty("departmentId").notNull();

```
表结构有多少个字段就写多少行，表多了还要分开写。``GreenDao``本身已经是很方便了，但我觉得还是不够方便。所以有了下面的"故事"。阅读完这个"故事"，从此你使用 GreenDao 真正需要你手写的将会单表是不超过10行！

## 思想
做过服务端开发的都知道，一般 ``C/S`` 通讯采用的数据结构是 ``Json``，当你们公司的后端人员做好了接口后，也会提供测试接口给前端开发者，因为我的APP接口一般也是我写，所以我有这个习惯，所以，为何不采用 ``Json``的格式来动态生成 ``客户端``所需要的所有类。故，选择读取``Json``

* #### GreenDao 的默认 main 函数

```java
public class dao {

    public static void main(String[] args) throws Exception {
        /** 你的生成逻辑代码 */
    }

}
```

* ####  解析JSON

由于上述是 Java 程序，所以不能使用 Android 的 Json 包，我们需要下面的几个 Jar 包，他们的作用的，在 Java 程序了里面使用到 Json 的操作 API，我们可以在解析完之后就不再引用这些 Jar 包。文末会提供
```

dependencies {
    ...
    compile files('libs/commons-beanutils-1.7.0.jar')
    compile files('libs/commons-collections-3.2.jar')
    compile files('libs/commons-lang-2.4.jar')
    compile files('libs/commons-logging-1.0.4.jar')
    compile files('libs/ezmorph-1.0.3.jar')
    compile files('libs/json-lib-2.2.3-jdk15.jar')
}
```

* #### 核心函数

利用 Java 关键字 instanceof 针对从 Json 里面解析出来的 value 的不同类型来生成不同的属性，Key 做字段名称，例如 ``{"name":"lgh"}``，解析出来就是 ``name`` 作为字段名词，由于 ``lgh`` 是字符串，所以对应的是字符串类型。

```java
private static void createTable(
            Schema schema, 
            String tableName,   /** 表名 */
            String idName,      /** 索引 */
            String json         /** Json */
    ) {
        Entity create = schema.addEntity(tableName);
        JSONObject jsonObject = JSONObject.fromObject(json);
        Iterator iterator = jsonObject.keys();
        String key;
        Object value;
        while(iterator.hasNext()){ /** 遍历 Json */
            key = (String) iterator.next();  /** 字段名词 */
            value = jsonObject.get(key);
            if(value instanceof Integer){
                if(key.equals(idName)){
                    /** 源码限制了，主键必需是 long 类型 */
                    create.addLongProperty(key).primaryKey().autoincrement();
                    continue;
                }
                create.addIntProperty(key);
            } else if (value instanceof String){
                create.addStringProperty(key).notNull();
            } else if (value instanceof Float){
                create.addFloatProperty(key).notNull();
            } else if (value instanceof Double){
                create.addDoubleProperty(key).notNull();
                /** 其它类型，请自行模仿添加 */
            } else{
                /** 集合类型违反了表结构 */
                throw new IllegalFormatFlagsException("集合类型违反了表结构");
            }
        }
        create.setHasKeepSections(true);
    }
```
* #### 一个例子

```java
import net.sf.json.JSONObject;

import java.util.IllegalFormatFlagsException;
import java.util.Iterator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * 作者：林冠宏
 *
 * author: LinGuanHong ,lzq is my dear wife.
 *
 * My GitHub : https://github.com/af913337456/
 *
 * My Blog   : http://www.cnblogs.com/linguanh/
 *
 * on 2017/3/21.
 *
 */

/** 创建一张表，以及它的字段逻辑，你不再需要手动一个个写，只需要传入 json */

public class dao {

    private final static String YourOutPutDirPath = "./greendaohelper/src/main/java";
    private final static String YourOutPutDirName = "dao";

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, YourOutPutDirName);

        String tableJson =
                "{\n" +
                "    \"d_id\": 1278,\n" +         /** 整数类型 */
                "    \"d_area\": \"美国\",\n" +   /** 字符串 */
                "    \"d_content\": \"讲述一个军事英雄回到美国，随之也带来了很多麻烦。他将会和CTU组织合作，来救自己的命或者来拯救一起发生在美国本土的恐怖袭击的故事。\",\n" +
                "    \"d_directed\": \"斯蒂芬·霍普金斯 / 强·卡萨 / 尼尔森·麦科米克 / 布朗温·休斯\",\n" +
                "    \"d_dayhits\": \"2.3\",\n" + /** 浮点类型 */
                "    \"d_true_play_url\": \"xxx\"\n" +
                "  }";

        createTable(
                schema,
                "pushVideo", /** 表名 */
                "d_id",      /** 主键名词 */
                tableJson
        );

        createTable(
                schema,
                "lghTable", /** 表名 */
                "id",      /** 主键名词 */
                "{\n" +
                "    \"id\": 1278,\n" +         /** 整数类型 */
                "    \"name\": \"林冠宏\",\n" +   /** 字符串 */
                "    \"address\": \"阳江市\",\n" +
                "    \"head_url\": \"xxxxxxxx\"\n" +
                "  }"
        );
        new DaoGenerator().generateAll(schema,YourOutPutDirPath);
    }

    /** o(n) */
    /** 聚合索引之类的，可以自己重载此函数 */
    private static void createTable(
            Schema schema,
            String tableName,
            String idName,
            String json
    ) {
        ...
    }
}

```

* #### 运行结果
在指定的路径``/greendaohelper/src/main/java``下生成文件夹``dao``，里面包含有
![](https://dn-mhke0kuv.qbox.me/0eabf8fc281fdf227462.png)

其中``lghTable`` 和 ``pushVideo`` 就是我们生成的 Bean 类，Dao后缀的就是数据表配置类
<strong>事实证明，完美符合理想的结果 。</strong>

## 拓展

上述讲述了如何自动快速地使用 Json 快速生成 Bean、表及其结构，我觉得还是不够爽，能更点地调用就更过瘾了。

* #### 公共的抽象
把 增、删、改、查，采用``泛型``抽象出来。

<strong>添加或更新一条</strong>

```C++
public void insertOrUpdateInfo(K entity){
    T userDao = getWirteDao();
    userDao.insertOrReplace(entity);
}
```

注意这个函数，它是标准的插入或更新一条数据，存在则更新，否则就是插入，两个泛型类型 ``K`` 和 ``T``，K 是 Bean 类，就是上面生成的， T 是dao 数据表配置类，也是上面生成的。到了这里，就是说，传入的泛型也是自动生成的，你完全不需要去手动打码。

* #### 泛型约束

上面说的 T 泛型是属于 Dao 的配置类，稍作代码分析就可以看出，GreenDao 所有生成的数据表配置类都是继承于 ``AbstractDao`` 类。

<strong>所以，操作抽象类长这样</strong>

```java
public abstract class DBInterface<K,T extends AbstractDao<K,Long>> {
    ...
}

```

* #### 完整例子

```java
public abstract class DBInterface<K,T extends AbstractDao<K,Long>> {

    private LghLogger lghLogger = LghLogger.getLogger(DBInterface.class);

    private DBInit dbInit;

    public DBInterface(){
        /** 不引起 DBInit 的重复实例化 */
        dbInit = DBInit.instance(); /** 初始化用的，这个类在后面提供 */
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
```

* #### DBInit.java 负责初始化，静态内部类单例，避免了反复创建对象
```java
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
```

* #### 使用

有了上面的准备，就可以使用了，正在需要自己动手的代码几乎没有。下面我们建一个操作类型的子类``VideoInfoDbCache``，集成于 ``DBInterface``，重写完三个抽象函数后，就是下面这样。

```java
public class VideoInfoDbCache
        extends
    DBInterface<pushVideo, pushVideoDao> {

    @Override
    protected pushVideoDao getWirteDao() {
        return openWritableDb().getPushVideoDao(); /** 该函数由 GreenDao 提供，不用自己编写 */
    }

    @Override
    protected pushVideoDao getReadDao() {
        return openReadableDb().getPushVideoDao(); /** 该函数由 GreenDao 提供，不用自己编写 */
    }

    @Override
    protected Property getIdProperty() {
        return pushVideoDao.Properties.D_id;       /** 自定义的拓展，这里获取了一般的 id 作为主属性 */
    }
}
```

现在我们看看 MainActivity 里面的使用。直接采用匿名对象，直接 new，直接用。

```java
public class MainActivity extends AppCompatActivity {

    List<pushVideo> list;
    List<lghTable>  lghList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ...
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
        ...
    }
}
```

现在，够快了吧？还不够？您请留言，我补刀。
