package com.wellsoft.pt.jpa.datasource;

/**
 * Description: 数据源切换上下文
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本  修改人    修改日期      修改内容
 * V1.0   liuxj    2025/1/14    Create
 * </pre>
 * @date 2025/1/14
 */
public class DataSourceContextHolder {

    /**
     * 此类提供线程局部变量。这些变量不同于它们的正常对应关系是每个线程访问一个线程(通过get、set方法),有自己的独立初始化变量的副本。
     */
    private static final ThreadLocal<String> DATASOURCE_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源
     *
     * @param dataSourceName 数据源名称
     */
    public static void setDataSource(String dataSourceName) {
        DATASOURCE_HOLDER.set(dataSourceName);
    }

    /**
     * 获取当前线程的数据源
     *
     * @return 数据源名称
     */
    public static String getDataSource() {
        return DATASOURCE_HOLDER.get();
    }

    /**
     * 删除当前数据源
     */
    public static void removeDataSource() {
        DATASOURCE_HOLDER.remove();
    }

}