package com.wellsoft.oauth2.service.impl;

import com.wellsoft.oauth2.entity.BatchDataImportDetailsHisEntity;
import com.wellsoft.oauth2.entity.BatchDataImportHisEntity;
import com.wellsoft.oauth2.repository.BatchDataImportDetailsHisRepository;
import com.wellsoft.oauth2.repository.BatchDataImportHisRepository;
import com.wellsoft.oauth2.service.BatchDataImportHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
public class BatchDataImportHisServiceImpl extends
        AbstractJpaServiceImpl<BatchDataImportHisEntity, Long, BatchDataImportHisRepository> implements
        BatchDataImportHisService {
    @Autowired
    BatchDataImportDetailsHisRepository detailsHisRepository;

    @Override
    @Transactional
    public void saveDetails(BatchDataImportDetailsHisEntity detailsHisEntity) {
        detailsHisRepository.save(detailsHisEntity);
    }
}
