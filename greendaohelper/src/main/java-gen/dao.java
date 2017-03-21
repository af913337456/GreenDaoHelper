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
        Entity create = schema.addEntity(tableName);
        JSONObject jsonObject = JSONObject.fromObject(json);
        Iterator iterator = jsonObject.keys();
        String key;
        Object value;
        while(iterator.hasNext()){
            key = (String) iterator.next();
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

}
