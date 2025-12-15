package com.wellsoft.pt.basicdata.cleanup.service;

import com.wellsoft.pt.basicdata.cleanup.DataCleanupProvider;

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
public interface DataCleanupService {
    DataCleanupProvider.ExpectCleanupResult expectCleanupRows(String type, DataCleanupProvider.Params params);

    DataCleanupProvider.ExpectCleanupResult cleanup(String type, DataCleanupProvider.Params params);
}
