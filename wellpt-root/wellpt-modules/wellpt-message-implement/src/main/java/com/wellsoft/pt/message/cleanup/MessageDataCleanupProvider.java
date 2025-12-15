package com.wellsoft.pt.message.cleanup;

import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月25日   chenq	 Create
 * </pre>
 */

@Service
public class MessageDataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Override
    public String getType() {
        return "pt-message";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        return null;
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        return null;
    }
}
