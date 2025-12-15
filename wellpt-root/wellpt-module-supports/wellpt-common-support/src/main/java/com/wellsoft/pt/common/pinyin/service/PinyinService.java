/*
 * @(#)2013-8-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.pinyin.service;

import com.wellsoft.pt.common.pinyin.entity.Pinyin;

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
public interface PinyinService {
    /**
     * 如何描述该方法
     *
     * @param userPinyin
     */
    void save(Pinyin pinyin);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    void deleteByEntityUuid(String entityUuid);

}
