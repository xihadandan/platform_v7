package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.org.OrgTypeData;
import com.wellsoft.pt.multi.org.entity.MultiOrgType;
import com.wellsoft.pt.multi.org.service.MultiOrgTypeService;
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
public class OrgTypeExpImpServiceImpl extends AbstractOrgExportService<OrgTypeData, MultiOrgType> {

    @Autowired
    private MultiOrgTypeService multiOrgTypeService;

    @Override
    public Class dataClass() {
        return OrgTypeData.class;
    }

    @Override
    public String fileName() {
        return "组织数据_组织类型";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_ORG_TYPE;
    }

    @Override
    public long total(String systemUnitId) {
        MultiOrgType multiOrgType = new MultiOrgType();
        multiOrgType.setSystemUnitId(systemUnitId);
        return multiOrgTypeService.getDao().countByEntity(multiOrgType);
    }

    @Override
    public List<MultiOrgType> queryAll(String systemUnitId) {
        MultiOrgType multiOrgType = new MultiOrgType();
        multiOrgType.setSystemUnitId(systemUnitId);
        return multiOrgTypeService.listByEntity(multiOrgType);
    }

    @Override
    public List<MultiOrgType> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        MultiOrgType multiOrgType = new MultiOrgType();
        multiOrgType.setSystemUnitId(systemUnitId);
        return multiOrgTypeService.listAllByPage(multiOrgType, new PagingInfo(currentPage, pageSize, true), null);
    }

    @Override
    public OrgTypeData toData(MultiOrgType multiOrgType) {
        OrgTypeData orgTypeData = new OrgTypeData();
        BeanUtils.copyProperties(multiOrgType, orgTypeData);
        return orgTypeData;
    }

    @Override
    public String getUuid(MultiOrgType multiOrgType) {
        return multiOrgType.getUuid();
    }

    @Override
    public String getDataUuid(OrgTypeData orgTypeData) {
        return orgTypeData.getUuid();
    }

    @Override
    public String getId(MultiOrgType multiOrgType) {
        return multiOrgType.getId();
    }

    @Override
    public String getDataId(OrgTypeData orgTypeData) {
        return orgTypeData.getId();
    }

    @Override
    @Transactional
    public ImportEntity<MultiOrgType, OrgTypeData> save(OrgTypeData orgTypeData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        MultiOrgType multiOrgType = multiOrgTypeService.getByNameAndSystemUnitId(orgTypeData.getName(), systemUnitId);
        if (multiOrgType == null) {
            multiOrgType = multiOrgTypeService.getByIdAndSystemUnitId(orgTypeData.getId(), systemUnitId);
        }
        if (multiOrgType == null) {
            multiOrgType = new MultiOrgType();
            multiOrgType.setCode(orgTypeData.getCode());
            multiOrgType.setName(orgTypeData.getName());
            multiOrgType.setId(orgTypeData.getId());
            multiOrgType.setRemark(orgTypeData.getRemark());
            multiOrgType.setIsForbidden(orgTypeData.getIsForbidden());
            multiOrgType.setSystemUnitId(systemUnitId);
            multiOrgTypeService.save(multiOrgType);
        }
        ImportEntity importEntity = new ImportEntity();
        importEntity.setSorce(orgTypeData);
        importEntity.setObj(multiOrgType);
        return importEntity;
    }

    @Override
    public void update(MultiOrgType multiOrgType, OrgTypeData orgTypeData, Map<String, String> dependentDataMap) {

    }

}
