package com.wellsoft.pt.fulltext.service.impl;

import com.wellsoft.pt.fulltext.dao.impl.IndexDocTemplateDaoImpl;
import com.wellsoft.pt.fulltext.entity.IndexDocTemplateEntity;
import com.wellsoft.pt.fulltext.service.IndexDocTemplateService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

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
@Service
public class IndexDocTemplateServiceImpl extends AbstractJpaServiceImpl<IndexDocTemplateEntity, IndexDocTemplateDaoImpl, String> implements IndexDocTemplateService {
    @Override
    public IndexDocTemplateEntity getByType(String type) {
        return this.dao.getOneByFieldEq("type", type);
    }
}
