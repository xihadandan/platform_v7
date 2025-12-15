package com.wellsoft.pt.fulltext.service;

import com.wellsoft.pt.fulltext.dao.impl.IndexExtDicDaoImpl;
import com.wellsoft.pt.fulltext.entity.IndexExtDicEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface IndexExtDicService extends JpaService<IndexExtDicEntity, IndexExtDicDaoImpl, String> {

    IndexExtDicEntity get();
}
