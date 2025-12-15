package com.wellsoft.context.jdbc.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.ApplicationContextHolder;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @author lilin
 * @ClassName: CommonManager
 * @Description: 通用处理类 这里可能要使用jdbctemplate来处理
 */
public class CommonSqlManager extends JdbcDaoSupport {
    private static final CommonSqlManager instance = new CommonSqlManager();

    private CommonSqlManager() {
        DataSource dataSource = (DataSource) ApplicationContextHolder.getBean(DataSource.class);
        setDataSource(dataSource);
    }

    /**
     * @param @param  sql 拼装的sql 语句，例如：SELECT COUNT(*) FROM USER where id =?
     * @param @param  Object 对应的参数，按顺序来填写
     * @param @return
     * @return boolean 返回类型
     * @throws
     * @Title: isUnique
     * @Description: 判断是否唯一
     */
    public static boolean isUnique(final String sql, final Object... args) {
        int i = instance.getJdbcTemplate().queryForObject(sql, Integer.class, args);
        if (i >= 1) {
            return true;
        }
        return false;
    }

    /**
     * @param @param  tablename 表名
     * @param @param  key 列名
     * @param @param  args 列值
     * @param @return 设定文件
     * @return boolean 返回类型
     * @throws
     * @Title: isUnique
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public static boolean isUnique(String tablename, final String key, final Object args) {
        String sql = "select count(*) from " + tablename + "  where " + key + "= ?";
        int i = instance.getJdbcTemplate().queryForObject(sql, new Object[]{args}, Integer.class);
        if (i >= 1) {
            return true;
        }
        return false;
    }

    // 根据类来查找对应的是否唯一，目前应该用不到
    public static boolean isUnique(Class clazz) {
        return false;
    }

    // 辅助输入
    // 根据类来查找辅助输入，目前应用用不到
    public static <T> List<T> getList(T t) {
        return null;
    }

    public static String getString(final String sql) {
        String str = instance.getJdbcTemplate().queryForObject(sql, String.class);
        return str;
    }

    public static List<String> getStringList(final String sql, final Object... args) {
        List<Map<String, Object>> mapList = instance.getJdbcTemplate().queryForList(sql, args);
        List<String> stringList = Lists.newArrayList();
        for (Map map : mapList) {
            // 选择第一个值
            stringList.add(map.values().iterator().next().toString());
        }
        return stringList;
    }

    public static List<String> getStringList(final String tablename, final String key) {
        String sql = "select " + key + " from " + tablename;
        List<Map<String, Object>> mapList = instance.getJdbcTemplate().queryForList(sql);
        List<String> stringList = Lists.newArrayList();
        for (Map map : mapList) {
            // 选择第一个值
            stringList.add(map.values().iterator().next().toString());
        }
        return stringList;
    }

    public static List<Map<String, Object>> getList(String tablename) {
        String sql = "select * from " + tablename;
        return instance.getJdbcTemplate().queryForList(sql);
    }

    public static List<Map<String, Object>> getList(final String sql, final Object... args) {

        return instance.getJdbcTemplate().queryForList(sql, args);
    }

    /**
     * @param @param  clazz 类名
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: getTableName
     * @Description: 根据类名获取tale注释的表名，如果没有注释table 默认为类名
     */
    public static String getTableName(Class clazz) {
        Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        if (null == entity) {
            return null;
        }
        Table table = (Table) clazz.getAnnotation(Table.class);
        if (null == table) {
            return clazz.getSimpleName();
        }
        return table.name();
    }

}
