/*
 * @(#)6/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service;

import com.wellsoft.pt.fulltext.support.FulltextSetting;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/20/25.1	    zhulh		6/20/25		    Create
 * </pre>
 * @date 6/20/25
 */
public interface FulltextRebuildIndexService {
    /**
     *
     */
    void rebuildIndex();

    void rebuildIndex(FulltextSetting fulltextSetting);
}
