package com.wellsoft.pt.basicdata.cleanup.service.impl;

import com.wellsoft.pt.basicdata.cleanup.DataCleanupProvider;
import com.wellsoft.pt.basicdata.cleanup.service.DataCleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
@Service
public class DataCleanupServiceImpl implements DataCleanupService {


    @Autowired
    List<DataCleanupProvider> dataCleanupProviders;


    @Override
    public DataCleanupProvider.ExpectCleanupResult expectCleanupRows(String type, DataCleanupProvider.Params params) {
        for (DataCleanupProvider provider : dataCleanupProviders) {
            if (type != null && type.equalsIgnoreCase(provider.getType())) {
                return provider.expectCleanupRows(params);
            }
        }
        return new DataCleanupProvider.ExpectCleanupResult(0);
    }

    @Override
    public DataCleanupProvider.ExpectCleanupResult cleanup(String type, DataCleanupProvider.Params params) {
        for (DataCleanupProvider provider : dataCleanupProviders) {
            if (type != null && type.equalsIgnoreCase(provider.getType())) {
                return provider.cleanup(params);
            }
        }
        return new DataCleanupProvider.ExpectCleanupResult(0);
    }
}
