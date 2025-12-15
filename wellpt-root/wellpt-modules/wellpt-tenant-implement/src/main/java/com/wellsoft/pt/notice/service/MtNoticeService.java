/*
 * @(#)2015-5-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.notice.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.notice.dao.MtNoticeDao;
import com.wellsoft.pt.notice.entity.MtNotice;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-5-28.1	Administrator		2015-5-28		Create
 * </pre>
 * @date 2015-5-28
 */
public interface MtNoticeService extends JpaService<MtNotice, MtNoticeDao, String> {

    /**
     * 如何描述该方法
     *
     * @param fmFileUuid
     * @return
     */
    boolean isOtherMtNotice(Collection<String> dataUuids);

    /**
     * 如何描述该方法
     *
     * @param fmFileUuid
     * @return
     */
    MtNotice getByDataUuid(String dataUuid);

    /**
     * 如何描述该方法
     *
     * @param dataUuid
     */
    void removeByDataUuid(String dataUuid);

}
