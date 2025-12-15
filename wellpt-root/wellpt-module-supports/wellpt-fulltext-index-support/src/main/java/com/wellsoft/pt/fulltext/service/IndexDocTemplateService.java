package com.wellsoft.pt.fulltext.service;

import com.wellsoft.pt.fulltext.dao.impl.IndexDocTemplateDaoImpl;
import com.wellsoft.pt.fulltext.entity.IndexDocTemplateEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月09日   chenq	 Create
 * </pre>
 */
public interface IndexDocTemplateService extends JpaService<IndexDocTemplateEntity, IndexDocTemplateDaoImpl, String> {

    @Override
    void save(IndexDocTemplateEntity entity);

    IndexDocTemplateEntity getByType(String type);
}
