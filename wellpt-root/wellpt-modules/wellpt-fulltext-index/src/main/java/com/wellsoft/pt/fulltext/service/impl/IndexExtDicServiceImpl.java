package com.wellsoft.pt.fulltext.service.impl;

import com.wellsoft.pt.fulltext.dao.impl.IndexExtDicDaoImpl;
import com.wellsoft.pt.fulltext.entity.IndexExtDicEntity;
import com.wellsoft.pt.fulltext.service.IndexExtDicService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月10日   chenq	 Create
 * </pre>
 */
@Service
public class IndexExtDicServiceImpl extends AbstractJpaServiceImpl<IndexExtDicEntity, IndexExtDicDaoImpl, String> implements IndexExtDicService {
    @Override
    public IndexExtDicEntity get() {
        List<IndexExtDicEntity> list = this.listAll();
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }
}
