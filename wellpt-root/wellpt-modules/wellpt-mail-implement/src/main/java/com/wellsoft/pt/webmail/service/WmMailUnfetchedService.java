package com.wellsoft.pt.webmail.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.dao.impl.WmMailUnfetchedDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailUnfetchedEntity;

import java.util.List;
import java.util.Set;

/**
 * Description: 邮件最近联系人服务
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年01月14日   chenq	 Create
 * </pre>
 */
public interface WmMailUnfetchedService extends JpaService<WmMailUnfetchedEntity, WmMailUnfetchedDaoImpl, String> {


    void updateFetchedFail(String mailUuid, String userId, String remark);

    void deleteByMailUuidAndUserId(String fromMailUuid, String userId);

    List<WmMailUnfetchedEntity> getUnfetchedMailUser(PagingInfo pagingInfo, String orderBy);

    void saveUnfetched(String mailUuid, Set<String> userIds);
}
