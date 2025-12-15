/*
 * @(#)7/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service;

import com.wellsoft.pt.fulltext.dao.FulltextSearchWordDao;
import com.wellsoft.pt.fulltext.entity.FulltextSearchWordEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/25/25.1	    zhulh		7/25/25		    Create
 * </pre>
 * @date 7/25/25
 */
public interface FulltextSearchWordService extends JpaService<FulltextSearchWordEntity, FulltextSearchWordDao, Long> {

    void addUserSearchWord(String userId, String keyword);

    List<String> listHotWordBySystem(String system);

    List<String> listUserSearchHistory(String userId, String system);

    List<String> listUserCommonSearch(String userId, String system);

    List<String> listKeywordLike(String keyword, String system);
}
