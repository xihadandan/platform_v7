package com.wellsoft.pt.basicdata.systemtable.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.systemtable.bean.SystemTableBean;
import com.wellsoft.pt.basicdata.systemtable.dao.SystemTableDao;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Map;

/**
 * Description: 系统表数据服务层接口
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhouyq		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */

public interface SystemTableService extends JpaService<SystemTable, SystemTableDao, String> {

    /**
     * 通过实体类和属性，获取实体类属性对应的表字段名称
     *
     * @param clazz
     * @param propertyName
     * @return
     */
    public Map<String, String> getColumnMap(Class clazz, String propertyName);

    /**
     * 获取所有的模块名
     *
     * @return
     */
    public List<CdDataDictionaryItemDto> getAllModuleName();

    /**
     * 获取系统表关系关联属性
     *
     * @param id
     * @return
     */
    public List<String> getAssociatedAttributes(SystemTableBean bean) throws ClassNotFoundException;

    /**
     * 通过UUID获取系统表数据
     *
     * @param id
     * @return
     */
    public SystemTable getByUuid(String formUuid);

    /**
     * 通过ID获取系统表数据VO对象
     *
     * @param id
     * @return
     */
    public SystemTableBean getBeanById(String uuid) throws ClassNotFoundException;

    /**
     * 保存系统表数据
     *
     * @param uuid
     * @return
     */
    public boolean saveBean(SystemTableBean bean) throws Exception;

    /**
     * 通过UUID删除系统表数据
     *
     * @param uuid
     * @return
     */
    public void remove(SystemTableBean bean);

    /**
     * 批量删除
     *
     * @param uuids
     */
    public void deleteAllById(String[] ids);

    /**
     * 系统表数据列表查询
     *
     * @param queryInfo
     * @return
     */
    public JqGridQueryData query(JqGridQueryInfo queryInfo);

    /**
     * 获得表所有字段的集合
     *
     * @param uuid
     * @return
     */
    public List<String> getFieldByForm(SystemTable systemTable) throws ClassNotFoundException;

    /**
     * 系统表数据查询
     *
     * @param systemTable
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param firstResult
     * @param maxResults
     * @return
     */
    public List<Map<String, Object>> query(SystemTable systemTable, Boolean distinct,
                                           String[] projection,
                                           String selection, Map<String, Object> selectionArgs,
                                           String groupBy, String having, String orderBy,
                                           int firstResult, int maxResults) throws Exception;

    /**
     * 根据表的uuid得到表的所有字段的类型
     *
     * @param uuid
     * @return
     * @throws Exception
     */
    public Map<String, String> getFieldsTypeByForm(SystemTable systemTable) throws Exception;

    /**
     * 获得所有系统表集合
     *
     * @return
     */
    public List<SystemTable> getAllSystemTables();

    /**
     * 返回指定模块ID下的所有系统表属性集合
     *
     * @param ModuleId
     * @return
     */
    public List<SystemTableAttribute> getSystemTableAttributesByModuleId(String moduleId);

    /**
     * 返回指定模块ID下的所有系统表关系集合
     */
    public List<SystemTableRelationship> getSystemTableRelationshipsByModuleId(String moduleId);

    public Map<String, Object> getColumnPropOfSet(Class clazz, String propertyName);

    /**
     * @param secondaryTableName
     * @return
     */
    public SystemTable getByFullEntityName(String secondaryTableName);

    Select2QueryData loadSelectionByModule(Select2QueryInfo select2QueryInfo);
}
