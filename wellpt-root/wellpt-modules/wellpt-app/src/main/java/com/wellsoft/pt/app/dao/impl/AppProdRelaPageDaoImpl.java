package com.wellsoft.pt.app.dao.impl;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.entity.AppProdRelaPageEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月31日   chenq	 Create
 * </pre>
 */
@Repository
public class AppProdRelaPageDaoImpl extends AbstractJpaDaoImpl<AppProdRelaPageEntity, Long> {


    public void updateProdVersionRelaPageTheme(Long prodVersionUuid, List<AppProdRelaPageEntity> pages) {
        List<AppProdRelaPageEntity> relaPageEntities = listByFieldEqValue("prodVersionUuid", prodVersionUuid);
        if (CollectionUtils.isNotEmpty(relaPageEntities)) {
            Map<String, AppProdRelaPageEntity> map = Maps.uniqueIndex(pages, new Function<AppProdRelaPageEntity, String>() {
                @Nullable
                @Override
                public String apply(@Nullable AppProdRelaPageEntity appProdRelaPageEntity) {
                    return appProdRelaPageEntity.getPageId();
                }
            });
            for (AppProdRelaPageEntity page : relaPageEntities) {
                AppProdRelaPageEntity temp = map.get(page.getPageId());
                page.setTheme(temp != null ? temp.getTheme() : null);
            }
            saveAll(relaPageEntities);
        }

    }
}
