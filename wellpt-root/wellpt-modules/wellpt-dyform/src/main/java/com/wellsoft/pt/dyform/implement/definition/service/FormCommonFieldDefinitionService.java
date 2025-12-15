package com.wellsoft.pt.dyform.implement.definition.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dyform.implement.definition.entity.FormCommonFieldDefinition;

import java.util.List;

/**
 * Description: 表单公共字段字义service接口
 *
 * @author qiufy
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月23日.1	qiufy		2019年4月23日		Create
 * </pre>
 * @date 2019年4月23日
 */
public interface FormCommonFieldDefinitionService extends BaseService {
    /**
     * 保存表单信息
     *
     * @param fieldDefinition 要保存的DyformCommonFieldDefinition对象
     */
    void saveDyformCommonField(FormCommonFieldDefinition fieldDefinition);

    /**
     * 根据UUID获取表单信息
     *
     * @param uuid
     * @return
     */
    FormCommonFieldDefinition getDyformCommonFieldByUUID(String uuid);

    /**
     * 根据UUID删除单个表单
     *
     * @param uuid
     */
    void deleteDyformCommonFieldDefinitionByUUID(String uuid);

    /**
     * 批量删除表单
     *
     * @param uuids 需要删除的UUID数组
     */
    void deleteAllDyformCommonFieldDefinition(String[] uuids);

    /**
     * 获取所属模块信息
     *
     * @param uuid
     * @return
     */
    List<TreeNode> getModuleAsTreeAsync(String uuid);

    public abstract Boolean fieldExists(String fieldUuid, String moduleId, String categoryUuid, String fieldName, String fieldValue);

    public abstract List<FormCommonFieldDefinition> queryFieldsByModuleId(String moduleId, String keyword);

    public abstract List<TreeNode> queryFieldsTreeByModuleId(String moduleId, String keyword);

    public abstract List<FormCommonFieldDefinition> query(QueryInfo queryInfo);

}
