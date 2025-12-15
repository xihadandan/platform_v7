package com.wellsoft.context.base.key;

/**
 * @author lilin
 * @ClassName: KeyConstants
 * @Description: 常量定义
 */
public interface KeyConstants {
    // 数据库中id对应的字段名定义
    public static final String ID = "id";
    // id的前缀
    public static final String PREFIX_USER = "U";
    public static final String PREFIX_DEP = "D";
    public static final String PREFIX_GROUP = "G";

    // 对应表名
    public static final String TABLE_USER = "org_user";
    public static final String TABLE_DEP = "org_department";
    public static final String TABLE_GROUP = "org_group";

    // 总位数
    public static final int LENGTH = 10;
}
