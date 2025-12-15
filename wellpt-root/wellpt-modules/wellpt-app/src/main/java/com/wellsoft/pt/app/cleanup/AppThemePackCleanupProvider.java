package com.wellsoft.pt.app.cleanup;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
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
public class AppThemePackCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    @Override
    public String getType() {
        return "themePack";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (clearTypes != null && clearTypes.contains("clearThemeSpecification")) {
            nativeDao.batchExecute("delete from theme_specification", null);
        }
        nativeDao.batchExecute("delete from theme_pack_tag", null);
        nativeDao.batchExecute("delete from theme_tag", null);
        nativeDao.batchExecute("delete from app_system_page_theme", null);
        nativeDao.batchExecute("update app_prod_rela_page set theme = null", null);
        return ExpectCleanupResult.total(nativeDao.batchExecute("delete from theme_pack", null));
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        List<QueryItem> items = nativeDao.query("select count(1) as total from theme_pack ", null, QueryItem.class);
        return ExpectCleanupResult.total(items.get(0).getLong("total"));
    }
}
