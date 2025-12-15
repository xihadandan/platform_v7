package com.wellsoft.pt.common.i18n.dao.impl;

import com.wellsoft.pt.common.i18n.dao.I18nEntityDao;
import com.wellsoft.pt.common.i18n.entity.I18nEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月25日   chenq	 Create
 * </pre>
 */
@Repository
public class I18nEntityDaoImpl extends AbstractJpaDaoImpl<I18nEntity, Long> implements I18nEntityDao {
    @Override
    @Transactional
    public void saveAll(List<? extends I18nEntity> list) {
        int count = 0;
        for (I18nEntity entity : list) {
            save(entity);
            if (++count % 20 == 0) {
                flushSession();
                getSession().clear();
            }
        }
    }
}
