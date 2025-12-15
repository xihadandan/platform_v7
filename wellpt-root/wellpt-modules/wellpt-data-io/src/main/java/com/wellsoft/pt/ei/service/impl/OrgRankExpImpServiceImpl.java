package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.org.OrgJobRankData;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobRank;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgJobRankService;
import com.wellsoft.pt.multi.org.service.MultiOrgSystemUnitService;
import com.wellsoft.pt.multi.org.vo.MultiOrgJobRankVo;
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
public class OrgRankExpImpServiceImpl extends AbstractOrgExportService<OrgJobRankData, MultiOrgJobRank> {

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private MultiOrgJobRankService multiOrgJobRankService;

    @Autowired
    private MultiOrgSystemUnitService multiOrgSystemUnitService;

    @Autowired
    private DataImportTaskLogService dataImportTaskLogService;

    @Override
    public int order() {
        return 3;
    }

    @Override
    public Class dataClass() {
        return OrgJobRankData.class;
    }

    @Override
    public String fileName() {
        return "组织数据_职级";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_ORG_RANK;
    }

    @Override
    public long total(String systemUnitId) {
        MultiOrgJobRank multiOrgJobRank = new MultiOrgJobRank();
        multiOrgJobRank.setSystemUnitId(systemUnitId);
        return multiOrgJobRankService.getDao().countByEntity(multiOrgJobRank);
    }

    @Override
    public List<MultiOrgJobRank> queryAll(String systemUnitId) {
        MultiOrgJobRank multiOrgJobRank = new MultiOrgJobRank();
        multiOrgJobRank.setSystemUnitId(systemUnitId);
        return multiOrgJobRankService.listByEntity(multiOrgJobRank);
    }

    @Override
    public List<MultiOrgJobRank> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        MultiOrgJobRank multiOrgJobRank = new MultiOrgJobRank();
        multiOrgJobRank.setSystemUnitId(systemUnitId);
        return multiOrgJobRankService.listAllByPage(multiOrgJobRank, new PagingInfo(currentPage, pageSize, true), null);
    }

    @Override
    public OrgJobRankData toData(MultiOrgJobRank multiOrgJobRank) {
        OrgJobRankData orgJobRankData = new OrgJobRankData();
        BeanUtils.copyProperties(multiOrgJobRank, orgJobRankData);
        orgJobRankData.setSystemUnitName(multiOrgSystemUnitService.getById(multiOrgJobRank.getSystemUnitId()).getName());
        return orgJobRankData;
    }

    @Override
    public String getUuid(MultiOrgJobRank multiOrgJobRank) {
        return multiOrgJobRank.getUuid();
    }

    @Override
    public String getDataUuid(OrgJobRankData orgJobRankData) {
        return orgJobRankData.getUuid();
    }

    @Override
    public String getId(MultiOrgJobRank multiOrgJobRank) {
        return multiOrgJobRank.getId();
    }

    @Override
    public String getDataId(OrgJobRankData orgJobRankData) {
        return orgJobRankData.getId();
    }

    @Override
    @Transactional
    public ImportEntity<MultiOrgJobRank, OrgJobRankData> save(OrgJobRankData orgJobRankData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(orgJobRankData.getUuid());
        MultiOrgJobRank multiOrgJobRank = null;

        if (null != dataImportTaskLog) {
            multiOrgJobRank = multiOrgJobRankService.getOne(dataImportTaskLog.getAfterImportUuid());
        }
        if (null == multiOrgJobRank) {
            multiOrgJobRank = new MultiOrgJobRank();
        }

        multiOrgJobRank.setCode(orgJobRankData.getCode());
        multiOrgJobRank.setName(orgJobRankData.getName());
        multiOrgJobRank.setSystemUnitId(systemUnitId);
        multiOrgJobRank.setJobRank(orgJobRankData.getJobRank());
        MultiOrgJobRankVo vo = new MultiOrgJobRankVo();
        BeanUtils.copyProperties(multiOrgJobRank, vo);
        if (replace) {
            multiOrgService.modifyJobRank(vo);
        } else {
            multiOrgService.addJobRank(vo);
        }

        ImportEntity importEntity = new ImportEntity();
        importEntity.setSorce(orgJobRankData);
        importEntity.setObj(multiOrgJobRank);
        return importEntity;
    }

    @Override
    public void update(MultiOrgJobRank multiOrgJobRank, OrgJobRankData orgJobRankData, Map<String, String> dependentDataMap) {

    }

}
