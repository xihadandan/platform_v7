package com.wellsoft.pt.ei.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.org.OrgElementAttr;
import com.wellsoft.pt.ei.dto.org.OrgNodeInfoData;
import com.wellsoft.pt.ei.dto.org.OrgVersionConf;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.facade.DataImportApi;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.multi.org.bean.OrgElementAttrVo;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeParams;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/10/8.1	liuyz		2021/10/8		Create
 * </pre>
 * @date 2021/10/8
 */
@Service
@Scope(value = "prototype")
@Transactional(readOnly = true)
public class OrgVersionConfExpImpServiceImpl extends AbstractOrgExportService<OrgVersionConf, MultiOrgElement> {

    @Autowired
    private DataImportTaskLogService dataImportTaskLogService;

    @Autowired
    private MultiOrgElementService multiOrgElementService;

    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;

    @Autowired
    private MultiOrgElementAttrService multiOrgElementAttrService;

    @Autowired
    private MultiOrgVersionService multiOrgVersionService;

    @Autowired
    private MultiOrgDutyService multiOrgDutyService;

    @Autowired
    private MultiOrgJobDutyService multiOrgJobDutyService;

    private List<OrgNodeInfoData> orgNodeInfoDataList;

    @Override
    public int order() {
        return 2;
    }

    @Override
    public Class dataClass() {
        return OrgVersionConf.class;
    }

    @Override
    public String fileName() {
        return "组织数据_组织版本";
    }

    @Override
    public String dataChildType() {
        return "组织架构节点";
    }

    @Override
    public long total(String systemUnitId) {
        return 0;
    }

    @Override
    public List<MultiOrgElement> queryAll(String systemUnitId) {
        return null;
    }

    @Override
    public List<MultiOrgElement> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        return null;
    }

    @Override
    public OrgVersionConf toData(MultiOrgElement multiOrgElement) {
        return null;
    }

    @Override
    public String getUuid(MultiOrgElement multiOrgElement) {
        return multiOrgElement.getUuid();
    }

    @Override
    public String getDataUuid(OrgVersionConf orgVersionConf) {
        return orgVersionConf.getEleUuid();
    }

    @Override
    public String getId(MultiOrgElement multiOrgElement) {
        return multiOrgElement.getId();
    }

    @Override
    public String getDataId(OrgVersionConf orgVersionConf) {
        return orgVersionConf.getEleId();
    }

    @Override
    public ImportEntity<MultiOrgElement, OrgVersionConf> save(OrgVersionConf orgVersionConf, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {

        return null;
    }

    @Override
    @Transactional
    public ImportEntity<MultiOrgElement, OrgVersionConf> save(OrgVersionConf orgVersionConf, String systemUnitId, boolean replace, String versionId) {
//        DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(orgVersionConf.getEleUuid());

        MultiOrgElement ele = null;
        DataImportApi dataImportApi = ApplicationContextHolder.getBean(DataImportApi.class);
//        MultiOrgTreeNode node = new MultiOrgTreeNode();
        if (IdPrefix.ORG_VERSION.getValue().equals(orgVersionConf.getType())) {
            // 引用系统单位已存在，不需要再创建了，直接引用系统内已经有的组织版本
            ele = multiOrgElementService.getById(orgVersionConf.getEleId());

/*            OrgTreeNodeDto orgTreeNodeDto = multiOrgTreeNodeService.getNodeByEleIdAndOrgVersion(ele.getId(), versionId);
            if (null == orgTreeNodeDto) {
                node = new MultiOrgTreeNode();
            } else {
                BeanUtils.copyProperties(orgTreeNodeDto, node);
            }

            // 检查一个系统单位只能引入一个版本
//            checkIsAddRepeatSystemUnit(ele.getSystemUnitId(), ele.getId());
            // 同步数据到组织树中去，添加子节点数据
//            MultiOrgTreeNode node = new MultiOrgTreeNode();

            // 引用系统单位需要添加jsonParams
            OrgTreeNodeParams params = new OrgTreeNodeParams();
            MultiOrgVersion version = multiOrgVersionService.getById(ele.getId());
            if (null != version && StringUtils.isNotBlank(version.getFunctionType())) {
                params.setAutoUpgrade(orgVersionConf.getAutoUpgrade());
                params.setRootVersionId(version.getId());
                params.setFunctionType(version.getFunctionType());
                params.setSystemUnitId(version.getSystemUnitId());
            }
            node.setJsonParams(JsonUtils.object2Json(params));

            // MultiOrgVersion ver =
            // this.orgVersionFacade.getOrgVersionById(nodeVo.getEleId());
            // 因为要计算自动更新的版本，所以这里的eleId，设置的是rootVersionId,方便将来搜索
            node.setEleId(ele.getId());
            node.setOrgVersionId(versionId);*/
        } else {
            // 对于是否重复，是根据中文路径来判断的，同名同类型同路径的节点，已经跳过了，这边都是需要创建的
            ele = new MultiOrgElement();
            ele.setId(dataImportApi.generateElementId(orgVersionConf.getType()));

            ele.setName(orgVersionConf.getName());
            ele.setShortName(orgVersionConf.getShortName());
            ele.setType(orgVersionConf.getType());
            ele.setCode(orgVersionConf.getCode());
            ele.setRemark(orgVersionConf.getRemark());
            ele.setSapCode(orgVersionConf.getSapCode());
            ele.setSystemUnitId(systemUnitId);
            if (StringUtils.isNotBlank(ele.getUuid())) {
                multiOrgElementService.update(ele);
            } else {
                multiOrgElementService.save(ele);
            }

            /*OrgTreeNodeDto orgTreeNodeDto = multiOrgTreeNodeService.getNodeByEleIdAndOrgVersion(ele.getId(), versionId);
            if (null == orgTreeNodeDto) {
                node = new MultiOrgTreeNode();
            } else {
                BeanUtils.copyProperties(orgTreeNodeDto, node);
            }

            // 同步数据到组织树中去，添加子节点数据
            node.setEleId(ele.getId());
            node.setOrgVersionId(versionId);*/

            if (StringUtils.isNotBlank(orgVersionConf.getDutyId()) && IdPrefix.JOB.getValue().equals(orgVersionConf.getType())) {
                // 存在归属职务，则判断 职务 是否存在
                MultiOrgDuty multiOrgDuty = null;
                DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceId(orgVersionConf.getDutyId());
                if (null != dataImportTaskLog) {
                    multiOrgDuty = multiOrgDutyService.getById(dataImportTaskLog.getAfterImportId());
                }
                if (null != multiOrgDuty) {
                    // 存在则关联
                    MultiOrgJobDuty duty = this.multiOrgJobDutyService.getJobDutyByJobId(ele.getId());
                    if (duty != null) {
                        this.multiOrgJobDutyService.deleteJobDutyByJobId(ele.getId());
                    }
                    duty = new MultiOrgJobDuty();
                    duty.setJobId(ele.getId());
                    duty.setDutyId(multiOrgDuty.getId());
                    multiOrgJobDutyService.save(duty);
                }
            }
        }

        // 父级节点在组织数据导入完后，进行更新......数据库设置该字段有唯一约束，不能为空。。这边需要塞个值
        /*String eleIdPath = orgVersionConf.getEleIdPath();
        eleIdPath = versionId + eleIdPath.replace(eleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0], versionId);
        eleIdPath = eleIdPath.substring(0, eleIdPath.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL));
        eleIdPath = eleIdPath + MultiOrgService.PATH_SPLIT_SYSMBOL + ele.getId();
        node.setEleIdPath(eleIdPath);
        if (StringUtils.isNotBlank(node.getUuid())) {
            multiOrgTreeNodeService.update(node);
        } else {
            multiOrgTreeNodeService.save(node);
        }*/

        List<OrgElementAttrVo> orgElementAttrVos = getOrgElementAttrVos(orgVersionConf.getOrgElementAttrs(), ele.getUuid());
        this.multiOrgElementAttrService.deleteByElementUuid(ele.getUuid());
        multiOrgElementAttrService.saveDtos(orgElementAttrVos);

        ImportEntity importEntity = new ImportEntity();
        importEntity.setPostProcess(true);
        importEntity.setSorce(orgVersionConf);
        importEntity.setObj(ele);
        return importEntity;
    }

    @Override
    public void update(MultiOrgElement multiOrgElement, OrgVersionConf orgVersionConf, Map<String, String> dependentDataMap) {

    }

    @Override
    @Transactional
    public void update(MultiOrgElement multiOrgElement, OrgVersionConf orgVersionConf, String versionId, String versionName) {
        // 父级节点的更新
        DataImportApi dataImportApi = ApplicationContextHolder.getBean(DataImportApi.class);
//        List<OrgNodeInfoData> list = dataImportApi.getAllNodeInfo(versionId);
        if (null == orgNodeInfoDataList) {
            orgNodeInfoDataList = dataImportApi.getAllNodeInfo(versionId);
        }

        String oldParentEleIdPath = orgVersionConf.getParentEleIdPath();
        String oldParentEleNamePath = orgVersionConf.getParentEleNamePath();

        oldParentEleIdPath = versionId + oldParentEleIdPath.replace(oldParentEleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0], "");
        // 为防止出现节点名称一致的情况下，replace掉除了其他节点的名称，这边只替换第一个
        oldParentEleNamePath = versionName + oldParentEleNamePath.replaceFirst(oldParentEleNamePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0], "");

        String type = oldParentEleIdPath.substring(oldParentEleIdPath.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1, oldParentEleIdPath.length()).substring(0, 1);
        String name = oldParentEleNamePath.substring(oldParentEleNamePath.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1, oldParentEleNamePath.length());
        OrgNodeInfoData parentNodeInfo = existSameNode(orgNodeInfoDataList, type, name, oldParentEleNamePath);

        MultiOrgTreeNode node;
        OrgTreeNodeDto orgTreeNodeDto = multiOrgTreeNodeService.getNodeByEleIdAndOrgVersion(multiOrgElement.getId(), versionId);
        boolean flag = false;
        if (null != parentNodeInfo) {
            // 父级节点存在
            node = new MultiOrgTreeNode();
            if (null != orgTreeNodeDto) {
                BeanUtils.copyProperties(orgTreeNodeDto, node);
            } else {
                flag = true;
            }
            if (IdPrefix.ORG_VERSION.getValue().equals(orgVersionConf.getType())) {
                // 引用系统单位需要添加jsonParams
                node.setJsonParams(getJsonParams(multiOrgElement, orgVersionConf));
            }
            node.setEleId(multiOrgElement.getId());
            node.setOrgVersionId(versionId);
            node.setEleIdPath(parentNodeInfo.getEleIdPath() + MultiOrgService.PATH_SPLIT_SYSMBOL + multiOrgElement.getId());
            multiOrgTreeNodeService.save(node);

            if (flag) {
                addOrgNodeInfoData(node.getEleId(), node.getEleIdPath(), multiOrgElement.getName(), oldParentEleNamePath + MultiOrgService.PATH_SPLIT_SYSMBOL + multiOrgElement.getName(), multiOrgElement.getType());
            }
        } else if (oldParentEleIdPath.equals(versionId)) {
            node = new MultiOrgTreeNode();
            if (null != orgTreeNodeDto) {
                BeanUtils.copyProperties(orgTreeNodeDto, node);
            } else {
                flag = true;
            }
            if (IdPrefix.ORG_VERSION.getValue().equals(orgVersionConf.getType())) {
                // 引用系统单位需要添加jsonParams
                node.setJsonParams(getJsonParams(multiOrgElement, orgVersionConf));
            }
            node.setEleId(multiOrgElement.getId());
            node.setOrgVersionId(versionId);
            node.setEleIdPath(versionId + MultiOrgService.PATH_SPLIT_SYSMBOL + multiOrgElement.getId());
            multiOrgTreeNodeService.save(node);

            if (flag) {
                addOrgNodeInfoData(node.getEleId(), node.getEleIdPath(), multiOrgElement.getName(), oldParentEleNamePath + MultiOrgService.PATH_SPLIT_SYSMBOL + multiOrgElement.getName(), multiOrgElement.getType());
            }
        } else {
            // 父级节点不存在，从不存在的节点开始，一级级创建节点
            String[] eleNamePaths = oldParentEleNamePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String[] eleIdPaths = oldParentEleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            StringBuffer eleNamePathStr = new StringBuffer(versionName);
            OrgNodeInfoData tmp = null;
            StringBuffer newEleIdPath = new StringBuffer(versionId);
            MultiOrgElement element;
            for (int i = 1, len = eleIdPaths.length; i < len; i++) {
                String oldId = eleIdPaths[i];
                String oldName = eleNamePaths[i];
                eleNamePathStr.append(MultiOrgService.PATH_SPLIT_SYSMBOL + eleNamePaths[i]);
                tmp = existSameNode(orgNodeInfoDataList, oldId.substring(0, 1), oldName, eleNamePathStr.toString());
                if (null == tmp) {
                    // 不存在，创建节点
                    element = new MultiOrgElement();
                    element.setId(dataImportApi.generateElementId(oldId.substring(0, 1)));
                    element.setType(oldId.substring(0, 1));
                    element.setName(oldName);
                    element.setSystemUnitId(multiOrgElement.getSystemUnitId());
                    multiOrgElementService.save(element);

                    node = new MultiOrgTreeNode();
                    // 同步数据到组织树中去，添加子节点数据
                    node.setEleId(element.getId());
                    node.setEleIdPath(newEleIdPath.toString() + MultiOrgService.PATH_SPLIT_SYSMBOL + element.getId());
                    node.setOrgVersionId(versionId);
                    multiOrgTreeNodeService.save(node);
                    newEleIdPath.append(MultiOrgService.PATH_SPLIT_SYSMBOL + element.getId());

                    addOrgNodeInfoData(node.getEleId(), node.getEleIdPath(), oldName, eleNamePathStr.toString(), oldId.substring(0, 1));
                } else {
                    newEleIdPath.append(MultiOrgService.PATH_SPLIT_SYSMBOL + tmp.getEleId());
                }
            }

            // 同步数据到组织树中去，添加子节点数据
            // 更新父级节点路径
            orgTreeNodeDto = multiOrgTreeNodeService.getNodeByEleIdAndOrgVersion(multiOrgElement.getId(), versionId);
            node = new MultiOrgTreeNode();
            if (null != orgTreeNodeDto) {
                BeanUtils.copyProperties(orgTreeNodeDto, node);
            } else {
                flag = true;
            }
            node.setEleId(multiOrgElement.getId());
            node.setOrgVersionId(versionId);
            node.setEleIdPath(newEleIdPath + MultiOrgService.PATH_SPLIT_SYSMBOL + multiOrgElement.getId());
            if (IdPrefix.ORG_VERSION.getValue().equals(orgVersionConf.getType())) {
                // 引用系统单位需要添加jsonParams
                node.setJsonParams(getJsonParams(multiOrgElement, orgVersionConf));
            }
            multiOrgTreeNodeService.save(node);

            if (flag) {
                addOrgNodeInfoData(node.getEleId(), node.getEleIdPath(), multiOrgElement.getName(), oldParentEleNamePath + MultiOrgService.PATH_SPLIT_SYSMBOL + multiOrgElement.getName(), multiOrgElement.getType());
            }
        }

        // 管理员的更新，在用户导入之后进行。根据源数据中负责人、管理员、分管领导的UUID匹配系统中对应的用户，匹配不上的组织架构节点不保存负责人、管理员、分管领导

    }

    // 检查是否引用了重复的系统单位
    /*private void checkIsAddRepeatSystemUnit(String systemUnitId, String orgVersionId) {
        MultiOrgTreeNode otherSystemUnit = this.multiOrgTreeNodeService.getOtherSystemUnitByOrgVersionId(
                systemUnitId, orgVersionId);
        if (otherSystemUnit != null) {
            throw new RuntimeException("该版本已经引入过该系统单位，不能重复引用");
        }
    }*/

    private List<OrgElementAttrVo> getOrgElementAttrVos(List<OrgElementAttr> orgElementAttrs, String eleUuid) {
        List<OrgElementAttrVo> orgElementAttrVos = Lists.newArrayList();

        OrgElementAttrVo orgElementAttrVo;
        for (OrgElementAttr orgElementAttr : orgElementAttrs) {
            orgElementAttrVo = new OrgElementAttrVo();
            BeanUtils.copyProperties(orgElementAttr, orgElementAttrVo);
            orgElementAttrVo.setElementUuid(eleUuid);
            orgElementAttrVo.setAttrDisplay(orgElementAttr.getAttrValue());
            orgElementAttrVos.add(orgElementAttrVo);
        }

        return orgElementAttrVos;
    }

    private String getJsonParams(MultiOrgElement multiOrgElement, OrgVersionConf orgVersionConf) {
        OrgTreeNodeParams params = new OrgTreeNodeParams();
        MultiOrgVersion version = multiOrgVersionService.getById(multiOrgElement.getId());
        if (null != version && StringUtils.isNotBlank(version.getFunctionType())) {
            params.setAutoUpgrade(orgVersionConf.getAutoUpgrade());
            params.setRootVersionId(version.getId());
            params.setFunctionType(version.getFunctionType());
            params.setSystemUnitId(version.getSystemUnitId());
        }
        return JsonUtils.object2Json(params);
    }

    private void addOrgNodeInfoData(String eleId, String eleIdPath, String name, String eleNamePath, String type) {
        OrgNodeInfoData orgNodeInfoData = new OrgNodeInfoData();
        orgNodeInfoData.setEleId(eleId);
        orgNodeInfoData.setEleIdPath(eleIdPath);
        orgNodeInfoData.setName(name);
        orgNodeInfoData.setEleNamePath(eleNamePath);
        orgNodeInfoData.setType(type);
        orgNodeInfoDataList.add(orgNodeInfoData);
    }

}
