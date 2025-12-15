/*
 * @(#)6/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.support;

import org.springframework.core.Ordered;

import java.util.Date;

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
public interface FulltextRebuildIndexTask extends Ordered {

    long indexCount(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting);

    void rebuildIndex(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting);

    void buildIncrementIndex(Date fromTime, FulltextSetting fulltextSetting);

}
