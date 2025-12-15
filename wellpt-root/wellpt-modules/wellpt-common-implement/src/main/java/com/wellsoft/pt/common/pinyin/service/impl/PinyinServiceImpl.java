/*
 * @(#)2013-8-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.pinyin.service.impl;

import com.wellsoft.pt.common.pinyin.dao.PinyinDao;
import com.wellsoft.pt.common.pinyin.entity.Pinyin;
import com.wellsoft.pt.common.pinyin.service.PinyinService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-8-3.1	zhulh		2013-8-3		Create
 * </pre>
 * @date 2013-8-3
 */
@Service
@Transactional
public class PinyinServiceImpl extends BaseServiceImpl implements PinyinService {

    @Autowired
    private PinyinDao pinyinDao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.pinyin.service.PinyinService#save(com.wellsoft.pt.common.pinyin.entity.Pinyin)
     */
    @Override
    public void save(Pinyin pinyin) {
        pinyinDao.save(pinyin);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.pinyin.service.PinyinService#deleteByEntityUuid(java.lang.String)
     */
    @Override
    public void deleteByEntityUuid(String entityUuid) {
        pinyinDao.deleteByEntityUuid(entityUuid);
    }

}
