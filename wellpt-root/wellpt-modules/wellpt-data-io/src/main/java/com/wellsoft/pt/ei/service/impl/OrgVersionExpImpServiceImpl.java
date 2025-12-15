package com.wellsoft.pt.ei.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.org.OrgElementAttr;
import com.wellsoft.pt.ei.dto.org.OrgVersionConf;
import com.wellsoft.pt.ei.dto.org.OrgVersionData;
import com.wellsoft.pt.multi.org.bean.OrgElementAttrVo;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgVersionService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 2021/9/29.1	liuyz		2021/9/29		Create
 * </pre>
 * @date 2021/9/29
 */
@Service
@Transactional(readOnly = true)
public class OrgVersionExpImpServiceImpl extends AbstractOrgExportService<OrgVersionData, MultiOrgVersion> {

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private MultiOrgVersionService multiOrgVersionService;


    @Override
    public Class dataClass() {
        return OrgVersionData.class;
    }

    @Override
    public boolean isPage() {
        return false;
    }

    @Override
    public String expFileName(OrgVersionData orgVersionData) {
        return orgVersionData.getName() + "-" + orgVersionData.getFunctionTypeName() + "-" + orgVersionData.getVersion() + Separator.UNDERLINE.getValue() + orgVersionData.getId() + ".json";
    }

    @Override
    public String fileName() {
        return "组织数据_组织版本";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_ORG_VERSION;
    }

    @Override
    public long total(String systemUnitId) {
        return multiOrgVersionService.countBySystemUnitId(systemUnitId);
    }

    @Override
    public List<MultiOrgVersion> queryAll(String systemUnitId) {
        return multiOrgVersionService.queryAllVersionListBySystemUnitId(systemUnitId);
    }

    @Override
    public List<MultiOrgVersion> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        MultiOrgVersion multiOrgVersion = new MultiOrgVersion();
        multiOrgVersion.setSystemUnitId(systemUnitId);
        return multiOrgVersionService.listAllByPage(multiOrgVersion, new PagingInfo(currentPage, pageSize, true), "sourceVersionUuid desc");
    }

    @Override
    public OrgVersionData toData(MultiOrgVersion multiOrgVersion) {
        OrgVersionData orgVersionData = new OrgVersionData();
        BeanUtils.copyProperties(multiOrgVersion, orgVersionData);

        // 获取组织版本的配置信息，节点、业务单位、部门、职位、引用系统单位
        Map<String, Object> params = Maps.newHashMap();
        params.put("versionId", multiOrgVersion.getId());
        params.put("orderBy", "create_time");
        List<QueryItem> objs = multiOrgVersionService.listItemByNameSQLQuery("queryOrgTreeNodeListForPage", QueryItem.class, params, null);

        // 遍历结果集
        List<OrgVersionConf> orgNodes = Lists.newArrayList();
        List<OrgVersionConf> businessUnits = Lists.newArrayList();
        List<OrgVersionConf> departments = Lists.newArrayList();
        List<OrgVersionConf> jobs = Lists.newArrayList();
        List<OrgVersionConf> orgVersions = Lists.newArrayList();
        OrgVersionConf orgVersionConf;

        for (QueryItem o : objs) {
            OrgTreeNodeVo orgTreeNodeVo = multiOrgService.getOrgNodeByTreeUuid(o.getString("uuid"));

            orgVersionConf = new OrgVersionConf();
            BeanUtils.copyProperties(orgTreeNodeVo, orgVersionConf);

            // 自定义属性，多个
            List<OrgElementAttr> orgElementAttrs = Lists.newArrayList();
            OrgElementAttr orgElementAttr;
            List<OrgElementAttrVo> attrVos = orgTreeNodeVo.getOrgElementAttrs();
            if (0 < attrVos.size()) {
                for (OrgElementAttrVo vo : attrVos) {
                    orgElementAttr = new OrgElementAttr();
                    BeanUtils.copyProperties(vo, orgElementAttr);
                    orgElementAttrs.add(orgElementAttr);
                }
            }
            orgVersionConf.setOrgElementAttrs(orgElementAttrs);

            String type = orgTreeNodeVo.getType();
            if (IdPrefix.ORG.getValue().equals(type)) {
                orgNodes.add(orgVersionConf);
            } else if (IdPrefix.BUSINESS_UNIT.getValue().equals(type)) {
                businessUnits.add(orgVersionConf);
            } else if (IdPrefix.DEPARTMENT.getValue().equals(type)) {
                departments.add(orgVersionConf);
            } else if (IdPrefix.JOB.getValue().equals(type)) {
                jobs.add(orgVersionConf);
            } else if (IdPrefix.ORG_VERSION.getValue().equals(type)) {
                orgVersions.add(orgVersionConf);
                orgVersionConf.setAutoUpgrade(orgTreeNodeVo.getParams().getAutoUpgrade());
            } else {
                continue;
            }
        }

        orgVersionData.setOrgNodes(orgNodes);
        orgVersionData.setBusinessUnits(businessUnits);
        orgVersionData.setDepartments(departments);
        orgVersionData.setJobs(jobs);
        orgVersionData.setOrgVersions(orgVersions);
        return orgVersionData;
    }

    @Override
    public String getUuid(MultiOrgVersion multiOrgVersion) {
        return multiOrgVersion.getUuid();
    }


    @Override
    public String getDataUuid(OrgVersionData orgVersionData) {
        return orgVersionData.getUuid();
    }

    @Override
    public String getId(MultiOrgVersion multiOrgVersion) {
        return multiOrgVersion.getId();
    }

    @Override
    public String getDataId(OrgVersionData orgVersionData) {
        return orgVersionData.getId();
    }

    @Override
    public ImportEntity<MultiOrgVersion, OrgVersionData> save(OrgVersionData orgVersionData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        return null;
    }

    @Override
    public void update(MultiOrgVersion multiOrgVersion, OrgVersionData orgVersionData, Map<String, String> dependentDataMap) {

    }

}
