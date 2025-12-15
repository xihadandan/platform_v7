package com.wellsoft.pt.dms.ext.excel.service;

import com.wellsoft.pt.dms.ext.excel.dao.ImpExcelDataBatchDao;
import com.wellsoft.pt.dms.ext.excel.entity.ImpExcelDataBatchEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface ImpExcelDataBatchService extends JpaService<ImpExcelDataBatchEntity, ImpExcelDataBatchDao, Long> {

    Long saveBatch(ImpExcelDataBatchEntity batchEntity);

    ImpExcelDataBatchEntity getBatchDetails(Long uuid);
}
