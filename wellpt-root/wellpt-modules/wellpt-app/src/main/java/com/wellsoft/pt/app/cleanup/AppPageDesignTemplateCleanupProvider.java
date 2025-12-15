package com.wellsoft.pt.app.cleanup;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年06月04日   chenq	 Create
 * </pre>
 */
@Service
public class AppPageDesignTemplateCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Override
    public String getType() {
        return "appPageDesignTemplate";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (CollectionUtils.isEmpty(clearTypes)) {
            return ExpectCleanupResult.total(0);
        }
        return ExpectCleanupResult.total(this.nativeDao.batchExecute("delete from app_user_widget_definition p" +
                        " where widget_id='page_user_template'" + (clearTypes.contains("clearUserDesign") ? " and p.user_id is not null" : "")
                , null));
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (CollectionUtils.isEmpty(clearTypes)) {
            return ExpectCleanupResult.total(0);
        }
        List<QueryItem> items = this.nativeDao.query("select count(1) as total from app_user_widget_definition p" +
                        " where widget_id='page_user_template'" + (clearTypes.contains("clearUserDesign") ? " and p.user_id is not null" : "")
                , null, QueryItem.class);
        return ExpectCleanupResult.total(items.get(0).getLong("total"));
    }
}
