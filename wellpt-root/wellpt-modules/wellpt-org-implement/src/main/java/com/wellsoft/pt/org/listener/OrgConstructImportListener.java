package com.wellsoft.pt.org.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.excel.AbstractEasyExcelImportListener;
import com.wellsoft.context.util.excel.ExcelRowDataAnalysedResult;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.org.dto.OrgElementDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月27日   chenq	 Create
 * </pre>
 */
public class OrgConstructImportListener extends AbstractEasyExcelImportListener<OrgElementExcelDo> {

    Map<String, OrgElementExcelDo> orgElementExcelDoMap = Maps.newHashMap();

    Long orgVersionUuid;

    OrgVersionEntity orgVersionEntity;

    OrganizationEntity organizationEntity;

    String[] allowTypes;

    OrgElementService orgElementService;

    OrgElementModelService orgElementModelService;

    OrgUnitService orgUnitService;

    OrgVersionService orgVersionService;

    OrganizationService organizationService;

    OrgSettingService orgSettingService;

    OrgElementManagementService orgElementManagementService;

    Map<String, List<String>> allowMountedTypes = Maps.newHashMap(); // 允许被挂载的父节点类型

    Map<String, OrgElementModelEntity> modelNameEntity = Maps.newHashMap();

    Map<Long, Map> orgAuthority = Maps.newHashMap();
    Map<Long, OrgElementManagementEntity> elementManagementEntityMap = Maps.newHashMap();

    boolean isOrgAdmin = true;

    OrgElementPathChainService orgElementPathChainService;


    public OrgConstructImportListener(Long orgVersionUuid, String[] allowTypes) {
        this.orgVersionUuid = orgVersionUuid;
        this.allowTypes = allowTypes;
        this.orgElementService = ApplicationContextHolder.getBean(OrgElementService.class);
        this.orgElementModelService = ApplicationContextHolder.getBean(OrgElementModelService.class);
        this.orgUnitService = ApplicationContextHolder.getBean(OrgUnitService.class);
        this.orgVersionService = ApplicationContextHolder.getBean(OrgVersionService.class);
        this.orgSettingService = ApplicationContextHolder.getBean(OrgSettingService.class);
        this.orgElementManagementService = ApplicationContextHolder.getBean(OrgElementManagementService.class);
        this.organizationService = ApplicationContextHolder.getBean(OrganizationService.class);
        this.orgElementPathChainService = ApplicationContextHolder.getBean(OrgElementPathChainService.class);
        List<OrgSettingEntity> settingEntities = this.orgSettingService.listBySystemAndTenant(null, null);
        Map<String, OrgSettingEntity> settingMap = Maps.newHashMap();
        for (OrgSettingEntity entity : settingEntities) {
            settingMap.put(entity.getAttrKey(), entity);
        }
        List<OrgElementModelEntity> modelEntities = this.orgElementModelService.listOrgElementModels(null, null);
        for (OrgElementModelEntity entity : modelEntities) {
            if (entity.getEnable()) {
                modelNameEntity.put(entity.getName(), entity);
                allowMountedTypes.put(entity.getId(), Lists.newArrayList());
                if (settingMap.containsKey(entity.getId().toUpperCase() + "_MOUNT_SET")) {
                    OrgSettingEntity set = settingMap.get(entity.getId().toUpperCase() + "_MOUNT_SET");
                    if (set.getEnable() && StringUtils.isNotBlank(set.getAttrVal())) {
                        Map map = JsonUtils.gson2Object(set.getAttrVal(), Map.class);
                        this.allowMountedTypes.get(entity.getId()).addAll((Collection<? extends String>) map.get("modelIds"));
                    }
                }
            }
        }
        this.orgVersionEntity = orgVersionService.getOne(this.orgVersionUuid);
        this.organizationEntity = organizationService.getOne(this.orgVersionEntity.getOrgUuid());
        if (!SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_ADMIN.name(), BuildInRole.ROLE_TENANT_ADMIN.name())) {
            if (!(StringUtils.isNotBlank(this.organizationEntity.getManager())
                    && this.organizationEntity.getManager().indexOf(SpringSecurityUtils.getCurrentUserId()) != -1)) {
                this.isOrgAdmin = false;
                List<OrgElementManagementEntity> orgElementManagementEntities = orgElementManagementService.listByOrgVersionUuid(this.orgVersionUuid);
                if (CollectionUtils.isNotEmpty(orgElementManagementEntities)) {
                    for (OrgElementManagementEntity mag : orgElementManagementEntities) {
                        if (BooleanUtils.isTrue(mag.getEnableAuthority()) && StringUtils.isNotBlank(mag.getOrgAuthority())
                                && StringUtils.isNotBlank(mag.getOrgManager()) && mag.getOrgManager().indexOf(SpringSecurityUtils.getCurrentUserId()) != -1) {
                            Map map = JsonUtils.json2Object(mag.getOrgAuthority(), Map.class);
                            this.orgAuthority.put(mag.getOrgElementUuid(), map);
                            this.elementManagementEntityMap.put(mag.getOrgElementUuid(), mag);
                            List<OrgElementPathChainEntity> pathChainEntities = orgElementPathChainService.getAllSubordinate(this.orgVersionUuid, mag.getOrgElementId());
                            if (CollectionUtils.isNotEmpty(pathChainEntities)) {
                                for (OrgElementPathChainEntity e : pathChainEntities) {
                                    this.orgAuthority.put(orgElementService.getByIdAndOrgVersionUuid(e.getSubOrgElementId(), this.orgVersionUuid).getUuid(), map);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void invoke(final OrgElementExcelDo orgElementExcelDo, final AnalysisContext analysisContext) {
        ReadSheet sheet = analysisContext.readSheetHolder().getReadSheet();
        orgElementExcelDo.setResult(new OrgElementExcelDo.Result(analysisContext.readRowHolder().getRowIndex(), sheet.getSheetName()));
        if (modelNameEntity.containsKey(sheet.getSheetName())) {
            orgElementExcelDo.setType(modelNameEntity.get(sheet.getSheetName()).getId());
        } else {
            orgElementExcelDo.getResult().msg("无法识别组织单元模型: " + sheet.getSheetName()).success(false);
        }
        orgElementExcelDoMap.put(orgElementExcelDo.getName(), orgElementExcelDo);
    }

    @Override
    public ExcelRowDataAnalysedResult dataAnalysed(OrgElementExcelDo orgElementExcelDo, int rowIndex, AnalysisContext analysisContext) {
        return null;
    }

    @Override
    public void doAfterAllAnalysed(final AnalysisContext analysisContext) {

    }

    @Override
    public void finish() {
        // 构建树形
        Set<String> keys = orgElementExcelDoMap.keySet();
        List<OrgElementExcelDo> dos = Lists.newArrayList();
        for (String name : keys) {
            OrgElementExcelDo excelDo = orgElementExcelDoMap.get(name);
            if (!this.isOrgAdmin && StringUtils.isBlank(excelDo.getParent())) {
                excelDo.getResult().success(false).msg("非管理员, 仅支持创建节点下的组织元素");
                continue;
            }
            if (StringUtils.isBlank(excelDo.getParent())) {
                dos.add(excelDo);
            } else {
                if (keys.contains(excelDo.getParent())) {
                    if (orgElementExcelDoMap.get(excelDo.getParent()).getChildren() == null) {
                        orgElementExcelDoMap.get(excelDo.getParent()).setChildren(Lists.newArrayList());
                    }
                    orgElementExcelDoMap.get(excelDo.getParent()).getChildren().add(excelDo);
                } else {
                    if (!orgElementExcelDoMap.containsKey(excelDo.getParent())) {// 父级不在本次导入批次内
                        OrgElementExcelDo temp = new OrgElementExcelDo();
                        temp.setName(excelDo.getParent());
                        temp.setChildren(Lists.newArrayList());
                        Map<String, Object> params = Maps.newHashMap();
                        params.put("orgVersionUuid", this.orgVersionUuid);
                        // 判断是否已经存在该节点
                        params.put("name", temp.getName());
                        List<OrgElementEntity> parent = orgElementService.listByHQL("from OrgElementEntity o where o.name=:name and o.orgVersionUuid=:orgVersionUuid", params);
                        if (CollectionUtils.isEmpty(parent) || parent.size() > 1) {
                            excelDo.getResult().success(false).msg(CollectionUtils.isEmpty(parent) ? "不存在上级: " + excelDo.getParent() : "存在多个同名上级: " + excelDo.getParent());
                        } else {
                            temp.setUuid(parent.get(0).getUuid());
                            temp.setType(parent.get(0).getType());
                        }
                        orgElementExcelDoMap.put(temp.getName(), temp);
                        dos.add(temp);
                    }
                    orgElementExcelDoMap.get(excelDo.getParent()).getChildren().add(excelDo);
                }
            }
        }


        if (CollectionUtils.isNotEmpty(dos)) {
            for (OrgElementExcelDo excelDo : dos) {
                this.cacadeSave(excelDo);
            }
        }

    }

    private void cacadeSave(OrgElementExcelDo excelDo) {
        if (excelDo.getResult().isSuccess() && excelDo.getUuid() == null) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("orgVersionUuid", this.orgVersionUuid);
            // 判断是否已经存在该节点
            params.put("name", excelDo.getName());
            params.put("parentName", excelDo.getParent());
            params.put("type", excelDo.getType());


            OrgUnitEntity unitEntity = null;
            if (OrgElementModelEntity.ORG_UNIT_ID.equals(excelDo.getType())) {
                List<OrgUnitEntity> unitEntities = orgUnitService.listByHQL("from OrgUnitEntity where name =:name", params);
                if (CollectionUtils.isNotEmpty(unitEntities)) {
                    unitEntity = unitEntities.get(0);
                }
                if (unitEntity == null) {
                    excelDo.getResult().success(false).msg("单位不存在");
                }
            }

            // 通过名称检索是否已经存在的节点，有则更新，无则新增
            List<OrgElementEntity> elementEntities = orgElementService.listByHQL("from OrgElementEntity o  where o.name=:name " +
                    "and o.orgVersionUuid=:orgVersionUuid and o.type=:type and (" +
                    (StringUtils.isBlank(excelDo.getParent()) ? "o.parentUuid is null" : " exists (" +
                            "select 1 from OrgElementEntity p where p.name =:parentName and p.uuid = o.parentUuid" +
                            ")") +
                    ")", params);
            OrgElementEntity elementEntity = null;
            if (CollectionUtils.isNotEmpty(elementEntities)) {
                elementEntity = elementEntities.get(0);
            }
            if (elementEntity != null) {
                if (!this.elementManagementEntityMap.containsKey(elementEntity.getUuid()) && this.orgAuthority.containsKey(elementEntity.getUuid())) {
                    Map<String, List> map = (Map<String, List>) this.orgAuthority.get(elementEntity.getUuid()).get(elementEntity.getType());
                    if (!(map.containsKey(elementEntity.getType()) && map.get(elementEntity.getType()).contains("edit"))) {
                        excelDo.getResult().success(false).msg("无权限编辑该类型组织元素");
                        return;
                    }
                }
                // 已存在，则更新
                elementEntity.setCode(excelDo.getCode());
                elementEntity.setShortName(excelDo.getShortName());
                elementEntity.setRemark(excelDo.getRemark());
                orgElementService.save(elementEntity);
                excelDo.setUuid(elementEntity.getUuid());
            } else {
                // 新增
                OrgElementDto dto = new OrgElementDto();
                if (unitEntity != null) {
                    dto.setId(unitEntity.getId());
                }
                dto.setCode(excelDo.getCode());
                dto.setShortName(excelDo.getShortName());
                dto.setName(excelDo.getName());
                dto.setType(excelDo.getType());
                dto.setRemark(excelDo.getRemark());
                dto.setOrgVersionUuid(orgVersionUuid);
                dto.setOrgVersionId(this.orgVersionEntity.getId());
                dto.setState(this.orgVersionEntity.getState());
                if (StringUtils.isNotBlank(excelDo.getParent())) {
                    if (orgElementExcelDoMap.containsKey(excelDo.getParent())) {
                        dto.setParentUuid(orgElementExcelDoMap.get(excelDo.getParent()).getUuid());
                        if (!this.allowMountedTypes.get(dto.getType()).contains(orgElementExcelDoMap.get(excelDo.getParent()).getType())) {
                            excelDo.getResult().success(false).msg("不允许挂载在" + excelDo.getParent());
                        }
                        if (this.orgAuthority.containsKey(dto.getParentUuid())) {
                            List list = (List) this.orgAuthority.get(dto.getParentUuid()).get(dto.getType());
                            if (list == null || !list.contains("add")) {
                                excelDo.getResult().success(false).msg("无管理权限创建该类型组织元素");
                            }
                        }


                    } else {
                        //FIXME: 要确保名称唯一，不然无法识别哪个父类
                        List<OrgElementEntity> parent = orgElementService.listByHQL("from OrgElementEntity o where o.name=:name and o.orgVersionUuid=:orgVersionUuid", params);
                        if (CollectionUtils.isEmpty(parent) || parent.size() > 1) {
                            excelDo.getResult().success(false).msg(CollectionUtils.isEmpty(parent) ? "不存在上级: " + excelDo.getParent() : "存在多个同名上级: " + excelDo.getParent());
                        } else {
                            dto.setParentUuid(parent.get(0).getUuid());
                            if (!this.allowMountedTypes.get(dto.getType()).contains(parent.get(0).getType())) {
                                excelDo.getResult().success(false).msg("不允许挂载在" + parent.get(0).getName());
                            }
                            if (this.orgAuthority.containsKey(dto.getParentUuid())) {
                                List list = (List) this.orgAuthority.get(dto.getParentUuid()).get(dto.getType());
                                if (list == null || !list.contains("add")) {
                                    excelDo.getResult().success(false).msg("无管理权限创建该类型组织元素");
                                }
                            }
                        }
                    }
                }
                if (excelDo.getResult().isSuccess()) {
                    excelDo.setUuid(orgElementService.saveOrgElement(dto));
                }

            }
        }

        if (excelDo.getResult().isSuccess()) {
            if (excelDo.getChildren() != null) {
                // 导入子级
                for (OrgElementExcelDo child : excelDo.getChildren()) {
                    this.cacadeSave(child);
                }
            }
        } else {
            // 更新所有子节点为失败
            this.cacadeUpdateChildrenFail(excelDo, "由于上级导入失败：" + excelDo.getResult().getMsg());
        }
    }

    private void cacadeUpdateChildrenFail(OrgElementExcelDo excelDo, String parentMsg) {
        if (CollectionUtils.isNotEmpty(excelDo.getChildren())) {
            for (OrgElementExcelDo child : excelDo.getChildren()) {
                child.getResult().success(false).msg(parentMsg);
                this.cacadeUpdateChildrenFail(child, "上级导入失败");
            }
        }
    }

    public Map<String, OrgElementExcelDo> getOrgElementExcelDoMap() {
        return this.orgElementExcelDoMap;
    }

    @Override
    public String name() {
        return "组织导入";
    }

}
