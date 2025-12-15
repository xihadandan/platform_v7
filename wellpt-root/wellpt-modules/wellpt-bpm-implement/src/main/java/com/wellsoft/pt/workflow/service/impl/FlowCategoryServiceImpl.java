/*
 * @(#)2012-12-3 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.bpm.engine.dao.FlowDefinitionDao;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.log.dto.SaveLogManageOperationDto;
import com.wellsoft.pt.log.entity.LogManageDetailsEntity;
import com.wellsoft.pt.log.enums.*;
import com.wellsoft.pt.log.facade.service.LogManageOperationFacadeService;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.bean.FlowCategoryBean;
import com.wellsoft.pt.workflow.dao.FlowCategoryDao;
import com.wellsoft.pt.workflow.entity.FlowCategory;
import com.wellsoft.pt.workflow.service.FlowCategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Description: 工作流分类管理服务实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-3.1	zhulh		2012-12-3		Create
 * </pre>
 * @date 2012-12-3
 */
@Service
public class FlowCategoryServiceImpl extends AbstractJpaServiceImpl<FlowCategory, FlowCategoryDao, String>
        implements FlowCategoryService {

    // 流程分类编号
    private static final String FLOW_CATEGORY_CODE_PATTERN = "00000";

    @Autowired
    private FlowDefinitionDao flowDefinitionDao;

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    private IdGeneratorService idGeneratorService;

    @Autowired
    private LogManageOperationFacadeService logManageOperationFacadeService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    /**
     * (non-Javadoc)
     */
    @Override
    public FlowCategoryBean getBean(String uuid) {
        FlowCategoryBean bean = new FlowCategoryBean();
        FlowCategory category = dao.getOne(uuid);
        BeanUtils.copyProperties(category, bean);
        category.setI18ns(appDefElementI18nService.getI18ns(uuid, null, new BigDecimal(1), IexportType.FlowCategory));
        FlowCategory parent = category.getParent();
        if (parent != null) {
            bean.setParentUuid(parent.getUuid());
            bean.setParentName(parent.getName());
        }

        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowCategoryService#getAll()
     */
    @Override
    public List<FlowCategory> getAll() {
        List<FlowCategory> categories = listAll();
        return BeanUtils.convertCollection(categories, FlowCategory.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowCategoryService#getAllBySystemUnitIds()
     */
    @Override
    public List<FlowCategory> getAllBySystemUnitIds() {
        return this.getAllBySystemUnitIdsLikeName(null);
    }

    @Override
    public List<FlowCategory> getAllBySystemUnitIdsLikeName(String name) {
        String system = RequestSystemContextPathResolver.system();
        Map<String, Object> values = new HashMap<String, Object>();
        List<String> systemUnitIds = new ArrayList<String>();
        systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
        values.put("systemUnitIds", systemUnitIds);
        values.put("systemId", system);
        String hql = "from FlowCategory t where t.systemUnitId in(:systemUnitIds) ";
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :systemId ";
        }
        if (StringUtils.isNotBlank(name)) {
            values.put("name", "%" + name + "%");
            hql += " and t.name like :name ";
        }
        hql += " order by code asc ";
        List<FlowCategory> categories = listByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(categories)) {
            List<String> uuids = Lists.newArrayList();
            Map<String, FlowCategory> map = Maps.newHashMap();
            for (FlowCategory c : categories) {
                uuids.add(c.getUuid());
                map.put(c.getUuid(), c);
            }
            List<AppDefElementI18nEntity> i18nEntities = appDefElementI18nService.getI18ns(Sets.newHashSet(uuids), IexportType.FlowCategory, null, null);
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                for (AppDefElementI18nEntity i : i18nEntities) {
                    if (map.containsKey(i.getDefId())) {
                        FlowCategory category = map.get(i.getDefId());
                        if (category.getI18ns() == null) {
                            category.setI18ns(Lists.newArrayList());
                        }
                        category.getI18ns().add(i);
                    }
                }
            }
        }
        return BeanUtils.convertCollection(categories, FlowCategory.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public FlowCategory saveAndPublishData(FlowCategory entity) {
        dao.save(entity);
        return entity;
    }

    @Override
    @Transactional
    public int deleteWhenNotUsed(String uuid) {
        FlowCategory deleteCategory = dao.getOne(uuid);
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        int row = this.dao.deleteBySQL(
                "delete from wf_def_category where uuid=:uuid and not exists (select 1 from wf_flow_definition d where d.category=uuid) ",
                params);
        if (row > 0) {
            // 删除成功
            // task:6441 开发-主导-流程定义修改日志
            saveLogManageDeleteOperation(deleteCategory);
            return row;
        }
        return getOne(uuid) != null ? -1 : 0;
    }

    /**
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public String saveBean(FlowCategoryBean bean) {
        FlowCategory category = new FlowCategory();
        FlowCategory oldFlowCategory = null;
        String newCategoryCode = bean.getCode();
        String oldCategoryCode = newCategoryCode;
        if (StringUtils.isNotBlank(bean.getUuid())) {
            category = this.dao.getOne(bean.getUuid());
            oldFlowCategory = new FlowCategory();
            BeanUtils.copyProperties(category, oldFlowCategory);
            oldCategoryCode = category.getCode();
        } else {
            // 编号唯一性判断
            category.setCode(bean.getCode());
            if (category.getCode() != null && CollectionUtils.isNotEmpty(this.dao.listByEntity(category))) {
                throw new RuntimeException("已经存在编号为[" + category.getCode() + "]的流程分类!");
            }
            bean.setSystem(RequestSystemContextPathResolver.system());
            bean.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }

        BeanUtils.copyPropertiesExcludeBaseField(bean, category, new String[]{"systemUnitId"});
        if (StringUtils.isNotBlank(bean.getParentUuid())) {
            category.setParent(this.dao.getOne(bean.getParentUuid()));
        }

        this.dao.save(category);
        if (CollectionUtils.isNotEmpty(category.getI18ns())) {
            appDefElementI18nService.deleteAllCodeI18n(null, category.getUuid(), null, IexportType.FlowCategory);
            for (AppDefElementI18nEntity i : category.getI18ns()) {
                i.setDefId(category.getUuid());
                i.setApplyTo(IexportType.FlowCategory);
                i.setVersion(new BigDecimal(1));
            }
            appDefElementI18nService.saveAll(category.getI18ns());
        }

        // 流程分类编号变更处理
        if (!StringUtils.equals(oldCategoryCode, newCategoryCode)) {
            flowDefinitionService.updateCategory(oldCategoryCode, newCategoryCode);
        }

        // task:6441 开发-主导-流程定义修改日志
        saveLogManageOperation(category, oldFlowCategory);

        return category.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowCategoryService#saveAll(Collection)
     */
    @Override
    @Transactional
    public List<FlowCategory> saveAll(Collection<FlowCategory> entities) {
        for (FlowCategory category : entities) {
            saveAndPublishData(category);
        }
        return Arrays.asList(entities.toArray(new FlowCategory[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowCategoryService#remove(com.wellsoft.pt.workflow.entity.FlowCategory)
     */
    @Override
    @Transactional
    public void remove(FlowCategory entity) {
        dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowCategoryService#removeAll(Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<FlowCategory> entities) {
        for (FlowCategory category : entities) {
            remove(category);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowCategoryService#removeByPk(String)
     */
    @Override
    @Transactional
    public void removeByPk(String uuid) {
        FlowCategory flowCategory = this.dao.getOne(uuid);
        if (flowDefinitionDao.countByCategory(flowCategory.getCode()) > 0) {
            throw new RuntimeException("流程分类[" + flowCategory.getName() + "]已经被使用!");
        } else {
            dao.delete(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowCategoryService#removeAllByPk(Collection)
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uids) {
        for (String uid : uids) {
            removeByPk(uid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowCategoryService#getAsTreeAsyncByUnitId(String)
     */
    @Override
    public TreeNode getAsTreeAsyncByUnitId(String systemUnitId) {
        List<FlowCategory> categories = this.dao.getAsTreeAsyncByUnitId(systemUnitId);
        // 全部流程
        TreeNode root = new TreeNode();
        root.setId(TreeNode.ROOT_ID);
        root.setName("全部流程");
        for (FlowCategory flowCategory : categories) {
            TreeNode node = new TreeNode();
            node.setId(flowCategory.getUuid());
            node.setName(flowCategory.getName());
            node.setData(flowCategory.getCode());
            root.getChildren().add(node);
        }
        return root;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowCategoryService#generateFlowCategoryCode()
     */
    @Override
    @Transactional
    public String generateFlowCategoryCode() {
        return idGeneratorService.generate(FlowCategory.class, FLOW_CATEGORY_CODE_PATTERN, false);
    }

    @Override
    public FlowCategory getByCode(String code) {
        return this.dao.getByCode(code);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowCategoryService#getTopLevel()
     */
    @Override
    public List<FlowCategory> getTopLevel() {
        return dao.getTopLevel();
    }

    /**
     * 保存流程分类的管理操作日志-删除操作
     *
     * @param deleteCategory 要删除的流程分类
     * @return void
     **/
    private void saveLogManageDeleteOperation(FlowCategory deleteCategory) {

        // 删除
        SaveLogManageOperationDto dto = new SaveLogManageOperationDto();
        dto.setAfterDataName("");
        dto.setAfterMessageValue("");
        dto.setBeforeDataName(deleteCategory.getName());
        dto.setBeforeMessageValue("");
        dto.setDataId(deleteCategory.getUuid());
        dto.setDataNameInfo("");
        dto.setDataType(LogManageDataTypeEnum.FlowCategory.getName());
        dto.setDataTypeId(LogManageDataTypeEnum.FlowCategory.getValue());
        dto.setModuleId(LogManageModuleEnum.FlowDef.getValue());
        dto.setModuleName(LogManageModuleEnum.FlowDef.getName());
        dto.setOperation(LogManageOperationEnum.delete.getName());
        dto.setOperationId(LogManageOperationEnum.delete.getValue());
        logManageOperationFacadeService.saveLogManageOperation(dto, DataParseTypeEnum.Entity);

    }

    /**
     * 保存流程分类的管理操作日志-新增和编辑操作
     *
     * @param newFlowCategory 新的的流程分类数据
     * @param oldFlowCategory 旧的流程分类数据 新增时为null
     * @return void
     **/
    private void saveLogManageOperation(FlowCategory newFlowCategory, FlowCategory oldFlowCategory) {
        SaveLogManageOperationDto dto = new SaveLogManageOperationDto();
        dto.setDataType(LogManageDataTypeEnum.FlowCategory.getName());
        dto.setDataTypeId(LogManageDataTypeEnum.FlowCategory.getValue());
        dto.setModuleId(LogManageModuleEnum.FlowDef.getValue());
        dto.setModuleName(LogManageModuleEnum.FlowDef.getName());
        if (oldFlowCategory == null) {
            // 新增
            dto.setAfterDataName(newFlowCategory.getName());
            dto.setAfterMessageValue("");
            dto.setBeforeDataName("");
            dto.setBeforeMessageValue("");
            dto.setDataId(newFlowCategory.getUuid());
            dto.setDataNameInfo("");
            dto.setDataType(LogManageDataTypeEnum.FlowCategory.getName());
            dto.setDataTypeId(LogManageDataTypeEnum.FlowCategory.getValue());
            dto.setModuleId(LogManageModuleEnum.FlowDef.getValue());
            dto.setModuleName(LogManageModuleEnum.FlowDef.getName());
            dto.setOperation(LogManageOperationEnum.add.getName());
            dto.setOperationId(LogManageOperationEnum.add.getValue());
        } else {
            // 编辑
            dto.setAfterDataName(newFlowCategory.getName());
            dto.setAfterMessageValue("");
            dto.setBeforeDataName(oldFlowCategory.getName());
            dto.setBeforeMessageValue("");
            dto.setDataId(newFlowCategory.getUuid());
            dto.setDataNameInfo("");
            dto.setOperation(LogManageOperationEnum.edit.getName());
            dto.setOperationId(LogManageOperationEnum.edit.getValue());
            List<LogManageDetailsEntity> logManageDetailsEntitys = parseLogDetail(newFlowCategory, oldFlowCategory);
            dto.setLogManageDetailsEntity(logManageDetailsEntitys);
        }
        logManageOperationFacadeService.saveLogManageOperation(dto, DataParseTypeEnum.Entity);
    }

    private List<LogManageDetailsEntity> parseLogDetail(FlowCategory newFlowCategory, FlowCategory oldFlowCategory) {
        List<LogManageDetailsEntity> list = new ArrayList<>();
        LogManageDetailsEntity entity = null;
        if (!newFlowCategory.getName().equals(oldFlowCategory.getName())) {
            entity = toLogManageDetailsEntity("name", "分类名称", "string", LogManageDetailDataShowTypeEnum.text.getValue(),
                    oldFlowCategory.getName(), newFlowCategory.getName());
            list.add(entity);
        }

        if (StringUtils.isBlank(newFlowCategory.getIcon())) {
            newFlowCategory.setIcon("");
        }
        if (StringUtils.isBlank(oldFlowCategory.getIcon())) {
            oldFlowCategory.setIcon("");
        }

        if (StringUtils.isBlank(newFlowCategory.getIconColor())) {
            newFlowCategory.setIconColor("");
        }
        if (StringUtils.isBlank(oldFlowCategory.getIconColor())) {
            oldFlowCategory.setIconColor("");
        }

        if (!newFlowCategory.getIcon().equals(oldFlowCategory.getIcon())
                || !newFlowCategory.getIconColor().equals(oldFlowCategory.getIconColor())) {
            entity = toLogManageDetailsEntity("icon", "图标", "string", LogManageDetailDataShowTypeEnum.icon.getValue(),
                    oldFlowCategory.getIcon(), newFlowCategory.getIcon());
            list.add(entity);
            entity = toLogManageDetailsEntity("iconColor", "图标颜色", "string",
                    LogManageDetailDataShowTypeEnum.icon_color.getValue(), oldFlowCategory.getIconColor(),
                    newFlowCategory.getIconColor());
            list.add(entity);
        }
        if (newFlowCategory.getCode() != null && !newFlowCategory.getCode().equals(oldFlowCategory.getCode())) {
            entity = toLogManageDetailsEntity("code", "编号", "string", LogManageDetailDataShowTypeEnum.text.getValue(),
                    oldFlowCategory.getCode(), newFlowCategory.getCode());
            list.add(entity);
        }

        if (StringUtils.isBlank(newFlowCategory.getRemark())) {
            newFlowCategory.setRemark("");
        }
        if (StringUtils.isBlank(oldFlowCategory.getRemark())) {
            oldFlowCategory.setRemark("");
        }

        if (!newFlowCategory.getRemark().equals(oldFlowCategory.getRemark())) {
            entity = toLogManageDetailsEntity("remark", "描述", "string", LogManageDetailDataShowTypeEnum.text.getValue(),
                    oldFlowCategory.getRemark(), newFlowCategory.getRemark());
            list.add(entity);
        }

        return list;
    }

    /**
     * 封装管理操作日志详情entity
     *
     * @param attrId
     * @param attrName
     * @param attrType
     * @param dataShowType
     * @param beforeValue
     * @param afterValue
     * @return com.wellsoft.pt.log.entity.LogManageDetailsEntity
     **/
    private LogManageDetailsEntity toLogManageDetailsEntity(String attrId, String attrName, String attrType,
                                                            String dataShowType, String beforeValue, String afterValue) {
        LogManageDetailsEntity entity = new LogManageDetailsEntity();
        entity.setAttrId(attrId);
        entity.setAttrName(attrName);
        entity.setAttrType(attrType);
        entity.setDataShowType(dataShowType);
        entity.setBeforeValue(beforeValue);
        entity.setAfterValue(afterValue);
        return entity;
    }

}
