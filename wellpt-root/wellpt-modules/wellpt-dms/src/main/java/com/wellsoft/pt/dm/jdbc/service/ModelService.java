package com.wellsoft.pt.dm.jdbc.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.dm.enums.DataUniqueType;
import com.wellsoft.pt.dm.factory.ddl.Table;
import com.wellsoft.pt.dm.jdbc.Model;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年05月15日   chenq	 Create
 * </pre>
 */
public interface ModelService {

    /**
     * 保存或者更新数据模型
     *
     * @param model
     * @return
     */
    Long saveOrUpdate(Model model);

    /**
     * 删除指定UUID的数据
     *
     * @param table
     * @param uuid
     */
    void delete(String table, Long uuid);

    void delete(String table, String where, Map<String, Object> parameter);

    /**
     * 删除指定UUID集合的数据
     *
     * @param table
     * @param uuids
     */
    void delete(String table, List<Long> uuids);

    /**
     * 根据UUID查询表数据
     *
     * @param table
     * @param uuid
     * @return
     */
    Model getModel(String table, Long uuid);

    /**
     * 获取表列定义
     *
     * @param table
     * @return
     */
    List<Table.Column> getTableColumns(String table);

    /**
     * 判断表是否存在
     *
     * @param table
     * @return
     */
    boolean tableExist(String table);

    /**
     * 唯一性数据判断
     *
     * @param model
     * @param uniqueType
     * @return
     */
    boolean checkUnique(Model model, DataUniqueType uniqueType);

    List<Map<String, Object>> list(String table, PagingInfo page);

    List<Map<String, Object>> list(String table, PagingInfo page, String order);

    List<Map<String, Object>> list(String table, String where, Map<String, Object> parameter);

    int updateBySQL(String sql, Map<String, Object> params);

    <NBR extends Number> NBR getNumberBySQL(String sql, Map<String, Object> params, Class<NBR> nbrClass);

    void drop(String table, boolean safe);

    List<Long> queryUuidsByFields(String table, Map<String, Object> fieldValue, String condition);
}
