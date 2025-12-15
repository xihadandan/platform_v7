package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.org.OrgDutyData;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgDutyService;
import com.wellsoft.pt.multi.org.service.MultiOrgSystemUnitService;
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
public class OrgDutyExpImpServiceImpl extends AbstractOrgExportService<OrgDutyData, MultiOrgDuty> {

    @Autowired
    private MultiOrgDutyService multiOrgDutyService;

    @Autowired
    private MultiOrgSystemUnitService multiOrgSystemUnitService;

    @Autowired
    private DataImportTaskLogService dataImportTaskLogService;

    @Autowired
    private MultiOrgService multiOrgService;

    @Override
    public Class dataClass() {
        return OrgDutyData.class;
    }

    @Override
    public String fileName() {
        return "组织数据_职务";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_ORG_DUTY;
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public long total(String systemUnitId) {
        MultiOrgDuty multiOrgDuty = new MultiOrgDuty();
        multiOrgDuty.setSystemUnitId(systemUnitId);
        return multiOrgDutyService.getDao().countByEntity(multiOrgDuty);
    }

    @Override
    public List<MultiOrgDuty> queryAll(String systemUnitId) {
        MultiOrgDuty multiOrgDuty = new MultiOrgDuty();
        multiOrgDuty.setSystemUnitId(systemUnitId);
        return multiOrgDutyService.queryAllDutyBySystemUnitId(systemUnitId);
    }

    @Override
    public List<MultiOrgDuty> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        MultiOrgDuty multiOrgDuty = new MultiOrgDuty();
        multiOrgDuty.setSystemUnitId(systemUnitId);
        return multiOrgDutyService.listAllByPage(multiOrgDuty, new PagingInfo(currentPage, pageSize, true), null);
    }

    @Override
    public OrgDutyData toData(MultiOrgDuty multiOrgDuty) {
        OrgDutyData orgDutyData = new OrgDutyData();
        BeanUtils.copyProperties(multiOrgDuty, orgDutyData);
        orgDutyData.setSystemUnitId(multiOrgDuty.getSystemUnitId());
        orgDutyData.setSystemUnitName(multiOrgSystemUnitService.getById(multiOrgDuty.getSystemUnitId()).getName());
        return orgDutyData;
    }

    @Override
    public String getUuid(MultiOrgDuty multiOrgDuty) {
        return multiOrgDuty.getUuid();
    }

    @Override
    public String getDataUuid(OrgDutyData orgDutyData) {
        return orgDutyData.getUuid();
    }

    @Override
    public String getId(MultiOrgDuty multiOrgDuty) {
        return multiOrgDuty.getId();
    }

    @Override
    public String getDataId(OrgDutyData orgDutyData) {
        return orgDutyData.getId();
    }

    @Override
    @Transactional
    public ImportEntity<MultiOrgDuty, OrgDutyData> save(OrgDutyData orgDutyData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(orgDutyData.getUuid());

        MultiOrgDuty multiOrgDuty = null;
        if (null != dataImportTaskLog) {
            multiOrgDuty = multiOrgDutyService.getOne(dataImportTaskLog.getAfterImportUuid());
        }
        if (null == multiOrgDuty) {
            replace = false;
            multiOrgDuty = new MultiOrgDuty();
        }
        multiOrgDuty.setCode(orgDutyData.getCode());
        multiOrgDuty.setName(orgDutyData.getName());
        multiOrgDuty.setRemark(orgDutyData.getRemark());
        multiOrgDuty.setSapCode(orgDutyData.getSapCode());
        multiOrgDuty.setSystemUnitId(systemUnitId);

        if (replace) {
            multiOrgService.modifyDuty(multiOrgDuty);
        } else {
            multiOrgService.addDuty(multiOrgDuty);
        }

        ImportEntity importEntity = new ImportEntity();
        importEntity.setSorce(orgDutyData);
        importEntity.setObj(multiOrgDuty);
        return importEntity;
    }

    @Override
    public void update(MultiOrgDuty multiOrgDuty, OrgDutyData orgDutyData, Map<String, String> dependentDataMap) {

    }

}
