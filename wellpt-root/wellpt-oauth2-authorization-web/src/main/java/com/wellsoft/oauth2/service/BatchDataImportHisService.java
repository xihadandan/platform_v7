package com.wellsoft.oauth2.service;

import com.wellsoft.oauth2.entity.BatchDataImportDetailsHisEntity;
import com.wellsoft.oauth2.entity.BatchDataImportHisEntity;
import com.wellsoft.oauth2.repository.BatchDataImportHisRepository;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/25    chenq		2019/9/25		Create
 * </pre>
 */
public interface BatchDataImportHisService extends
        JpaService<BatchDataImportHisEntity, Long, BatchDataImportHisRepository> {


    void saveDetails(BatchDataImportDetailsHisEntity detailsHisEntity);
}
