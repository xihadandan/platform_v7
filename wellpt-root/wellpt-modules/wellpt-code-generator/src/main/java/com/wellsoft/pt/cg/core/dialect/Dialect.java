package com.wellsoft.pt.cg.core.dialect;

/**
 * 数据库方言，默认情况下数据库属性类型转为对应的java类型
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-7.9	lmw		2015-7-9		Create
 * </pre>
 * @date 2015-7-9
 */
public interface Dialect {
    /**
     * 获取数据库类型对应的java类型名称
     *
     * @param type
     * @return
     */
    public Class<?> javaTypeByColumnType(String type);

    /**
     * 获取数据类型对应的java类型
     * 如果包为java.lang则返回空
     *
     * @param type
     * @return
     */
    public String javaCanonicalName(String type);

    /**
     * 获取数据类型对应的java类型的名称
     * 如果包为java.lang则返回空
     *
     * @param type
     * @return
     */
    public String javaTypeName(String type);

    /**
     * 获取数据类型对应的java类型的名称
     * 如果包为java.lang则返回空
     *
     * @param type
     * @return
     */
    public String javaTypeSimpleName(String type);
}
