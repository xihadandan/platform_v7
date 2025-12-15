/*
 * @(#)2013-8-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.pinyin.dao;

import com.wellsoft.pt.common.pinyin.entity.Pinyin;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

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
@Repository
public class PinyinDao extends HibernateDao<Pinyin, String> {

    private static final String DELETE_BY_USER = "delete from Pinyin pinyin where pinyin.entityUuid = :entityUuid";

    /**
     * @param entityUuid
     */
    public void deleteByEntityUuid(String entityUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("entityUuid", entityUuid);
        this.batchExecute(DELETE_BY_USER, values);
    }

}
