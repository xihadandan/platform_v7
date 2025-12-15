package com.wellsoft.pt.dms.ext.excel.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.ext.excel.dao.ImpExcelDataBatchDao;
import com.wellsoft.pt.dms.ext.excel.dao.impl.ImpExcelDataBatchDetailDaoImpl;
import com.wellsoft.pt.dms.ext.excel.entity.ImpExcelDataBatchDetailEntity;
import com.wellsoft.pt.dms.ext.excel.entity.ImpExcelDataBatchEntity;
import com.wellsoft.pt.dms.ext.excel.service.ImpExcelDataBatchService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年05月26日   chenq	 Create
 * </pre>
 */
@Service
public class ImpExcelDataBatchServiceImpl extends AbstractJpaServiceImpl<ImpExcelDataBatchEntity, ImpExcelDataBatchDao, Long> implements ImpExcelDataBatchService {

    @Autowired
    ImpExcelDataBatchDetailDaoImpl detailDao;

    @Override
    @Transactional
    public Long saveBatch(ImpExcelDataBatchEntity batchEntity) {
        save(batchEntity);
        for (ImpExcelDataBatchDetailEntity detail : batchEntity.getDetails()) {
            detail.setBatchUuid(batchEntity.getUuid());
        }
        detailDao.saveAll(batchEntity.getDetails());
        return batchEntity.getUuid();
    }

    @Override
    public ImpExcelDataBatchEntity getBatchDetails(Long uuid) {
        ImpExcelDataBatchEntity entity = getOne(uuid);
        if (entity != null) {
            Map<String, Object> param = Maps.newHashMap();
            param.put("batchUuid", uuid);
            List<ImpExcelDataBatchDetailEntity> detailEntityList = detailDao.listByHQL("from ImpExcelDataBatchDetailEntity order by sheetIndex asc,rowIndex asc", param);
            entity.setDetails(detailDao.listByFieldEqValue("batchUuid", uuid));
        }
        return entity;
    }
}
