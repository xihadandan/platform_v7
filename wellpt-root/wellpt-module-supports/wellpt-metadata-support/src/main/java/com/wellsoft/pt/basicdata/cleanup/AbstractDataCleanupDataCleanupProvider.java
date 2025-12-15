package com.wellsoft.pt.basicdata.cleanup;

import com.wellsoft.pt.basicdata.cleanup.service.DataCleanupService;
import com.wellsoft.pt.jpa.dao.NativeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月23日   chenq	 Create
 * </pre>
 */
@Transactional(readOnly = true)
public abstract class AbstractDataCleanupDataCleanupProvider implements DataCleanupProvider {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected NativeDao nativeDao;

    @Autowired
    private DataCleanupService dataCleanupService;

    protected ExpectCleanupResult cleanupByTypeParams(String type, Params params) {
        return dataCleanupService.cleanup(type, params);
    }

    protected ExpectCleanupResult expectCleanupRowsByTypeParams(String type, Params params) {
        return dataCleanupService.expectCleanupRows(type, params);
    }
}
