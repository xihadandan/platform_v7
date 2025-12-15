package com.wellsoft.pt.common.i18n.dao;

import com.wellsoft.pt.common.i18n.entity.I18nEntity;
import com.wellsoft.pt.jpa.dao.JpaDao;

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
public interface I18nEntityDao extends JpaDao<I18nEntity, Long> {

    void saveAll(List<? extends I18nEntity> list);
}
