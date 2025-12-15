package com.wellsoft.pt.dyform.implement.definition.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.dyform.implement.definition.cache.DyformCacheUtils;
import com.wellsoft.pt.dyform.implement.definition.dao.FormCommonFieldDefinitionDao;
import com.wellsoft.pt.dyform.implement.definition.entity.FormCommonFieldDefinition;
import com.wellsoft.pt.dyform.implement.definition.service.FormCommonFieldDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.service.FormCommonFieldRefService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Service
public class FormCommonFieldDefinitionServiceImpl extends
        AbstractJpaServiceImpl<FormCommonFieldDefinition, FormCommonFieldDefinitionDao, String> implements
        FormCommonFieldDefinitionService {

    @Autowired
    FormCommonFieldRefService formCommonFieldRefService;

    @Autowired
    FormCommonFieldDefinitionDao dyformCommonFieldDefinitionDao;

    /**
     * 保存表单信息
     */
    @Override
    @Transactional
    public void saveDyformCommonField(FormCommonFieldDefinition fieldDefinition) {
        dyformCommonFieldDefinitionDao.save(fieldDefinition);
        DyformCacheUtils.evictFieldJSONObject(fieldDefinition.getUuid());
    }

    /**
     * 根据UUID获取表单信息
     */
    @Override
    public FormCommonFieldDefinition getDyformCommonFieldByUUID(String uuid) {
        FormCommonFieldDefinition dyformCommonFieldDefinition = new FormCommonFieldDefinition();
        FormCommonFieldDefinition entity = dyformCommonFieldDefinitionDao.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dyformCommonFieldDefinition);
        }
        return dyformCommonFieldDefinition;
    }

    /**
     * 根据UUID删除单个表单
     */
    @Override
    @Transactional
    public void deleteDyformCommonFieldDefinitionByUUID(String uuid) {
        dyformCommonFieldDefinitionDao.delete(uuid);
        DyformCacheUtils.evictFieldJSONObject(uuid);
    }

    /**
     * 批量删除表单
     */
    @Override
    @Transactional
    public void deleteAllDyformCommonFieldDefinition(String[] uuids) {
        for (int i = 0; i < uuids.length; i++) {
            dyformCommonFieldDefinitionDao.delete(uuids[i]);
        }
    }

    @Override
    public Boolean fieldExists(String fieldUuid, String moduleId, String categoryUuid, String fieldName,
                               String fieldValue) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("moduleId", moduleId);
        values.put("fieldUuid", fieldUuid);
        values.put("fieldName", fieldName);
        values.put("fieldValue", fieldValue);
        values.put("categoryUuid", categoryUuid);
        String countHql = "select count(t.uuid) from FormCommonFieldDefinition t where t.moduleId = :moduleId and t.categoryUuid = :categoryUuid";
        if (StringUtils.equals(fieldName, "name")) {
            countHql += " and t.name = :fieldValue";
        } else if (StringUtils.equals(fieldName, "displayName")) {
            countHql += " and t.displayName = :fieldValue";
        }
        if (StringUtils.isNotBlank(fieldUuid)) {
            countHql += " and t.uuid != :fieldUuid";
        }
        Boolean isInRef = dyformCommonFieldDefinitionDao.countByHQL(countHql, values) > 0;
        return isInRef;
    }

    @Override
    public List<FormCommonFieldDefinition> queryFieldsByModuleId(String moduleId, String keyword) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("moduleId", moduleId);
        values.put("keyword", StringUtils.isBlank(keyword) ? "" : keyword);
        String hql = "select t from FormCommonFieldDefinition t, FormCommonFieldCategory c where c.uuid = t.categoryUuid and (t.moduleId = 'Global' or t.moduleId = :moduleId) and (t.name like '%' || :keyword || '%' or t.displayName like '%' || :keyword || '%') order by c.seq asc, t.createTime desc";
        return listByHQL(hql, values);
    }

    @Override
    public List<TreeNode> queryFieldsTreeByModuleId(String moduleId, String keyword) {
        List<TreeNode> trees = new ArrayList<TreeNode>();
        Map<String, TreeNode> maps = new HashMap<String, TreeNode>();
        List<FormCommonFieldDefinition> lists = queryFieldsByModuleId(moduleId, keyword);
        for (FormCommonFieldDefinition field : lists) {
            String fieldModuleId = field.getModuleId();
            TreeNode pnode = null;
            if (StringUtils.isBlank(fieldModuleId) || StringUtils.equals(fieldModuleId, "Global")) {
                pnode = maps.get("Global");
                if (null == pnode) {
                    pnode = new TreeNode("Global", "全局", null);
                    //
                    trees.add(pnode);
                    //
                    maps.put("Global", pnode);
                }
            } else {
                pnode = maps.get(field.getCategoryUuid());
                if (null == pnode) {
                    pnode = new TreeNode(field.getCategoryUuid(), field.getCategoryName(), null);
                    //
                    trees.add(pnode);
                    //
                    maps.put(field.getCategoryUuid(), pnode);
                }
            }
            TreeNode node = new TreeNode(field.getUuid(), field.getDisplayName(), field.getName());
            node.setPath(field.getNotes());// 备注
            node.setType(field.getControlType());
            node.setData(field.getDefinitionJson());
            node.setIconSkin(field.getControlTypeName());
            pnode.getChildren().add(node);
        }
        return trees;
    }

    @Override
    public List<TreeNode> getModuleAsTreeAsync(String uuid) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormCommonFieldDefinition> query(QueryInfo queryInfo) {
        List<FormCommonFieldDefinition> list = dyformCommonFieldDefinitionDao.listByEntity(
                new FormCommonFieldDefinition(), queryInfo.getPropertyFilters(), queryInfo.getOrderBy(),
                queryInfo.getPagingInfo());
        for (FormCommonFieldDefinition entity : list) {
            entity.setScope(formCommonFieldRefService.countRefsByFieldUuid(entity.getUuid()).intValue());
        }
        return list;
    }

}
