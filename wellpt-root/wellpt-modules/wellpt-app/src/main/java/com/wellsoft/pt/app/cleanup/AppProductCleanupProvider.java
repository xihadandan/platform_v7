package com.wellsoft.pt.app.cleanup;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.entity.AppProduct;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
 * 2024年06月04日   chenq	 Create
 * </pre>
 */
@Service
public class AppProductCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    public static final String[] INNER_PRODUCT_ID = new String[]{"PRD_PT", "system_manager"};

    @Override
    public String getType() {
        return "appProduct";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        Map<String, Object> queryMap = Maps.newHashMap();
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (CollectionUtils.isEmpty(clearTypes)) {
            return ExpectCleanupResult.total(0);
        }
        List<Integer> status = Lists.newArrayList();
        for (String s : clearTypes) {
            status.add(AppProduct.Status.valueOf(s).ordinal());
        }
        queryMap.put("status", status);
        queryMap.put("excludes", INNER_PRODUCT_ID);
        String prodWhere = " p.id not in (:excludes) and ( p.id not like 'pt-%' and p.id not like 'pt_%' ) and p.status in ( :status ) ";
        nativeDao.batchExecute("delete from app_prod_acl a where exists ( select 1 from app_product p where " + prodWhere + " and a.prod_id = p.id " + " )", queryMap);
        // 删除产品关联表
        String[] relas = new String[]{"app_prod_anon_url", "app_prod_rela_page", "app_prod_version_log", "app_prod_version_login", "app_prod_version_param",
                "app_prod_version_setting"};
        for (String rela : relas) {
            nativeDao.batchExecute("delete from " + rela + " a where exists ( select 1 from app_product p ," +
                    "app_prod_version v where v.prod_id =p.id and v.uuid=a.prod_version_uuid and " + prodWhere + " )", queryMap);

        }

        nativeDao.batchExecute("delete from app_prod_version a where exists ( select 1 from app_product p  " +
                "  where a.prod_id =p.id and " + prodWhere + " and a.prod_id = p.id " + " )", queryMap);

        List<QueryItem> prods = nativeDao.query("select id from app_product p where " + prodWhere, queryMap, QueryItem.class);
        if (CollectionUtils.isEmpty(prods)) {
            for (QueryItem i : prods) {
                this.cleanupByTypeParams("appSystemInfo", Params.build(new String[]{"appId"}, new String[]{i.getString("id")}));
            }
        }

        int count = nativeDao.batchExecute("delete from app_product p where " + prodWhere, queryMap);

        return ExpectCleanupResult.total(count);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        Map<String, Object> queryMap = Maps.newHashMap();
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (CollectionUtils.isEmpty(clearTypes)) {
            return ExpectCleanupResult.total(0);
        }
        List<Integer> status = Lists.newArrayList();
        for (String s : clearTypes) {
            status.add(AppProduct.Status.valueOf(s).ordinal());
        }
        queryMap.put("status", status);
        queryMap.put("excludes", INNER_PRODUCT_ID);
        List<QueryItem> items = nativeDao.query("select count(1) as total from app_product where " +
                " id not in ( :excludes ) and ( id not like 'pt-%' and id not like 'pt_%' ) and status in (:status) ", queryMap, QueryItem.class);
        return ExpectCleanupResult.total(items.get(0).getLong("total"));
    }
}
