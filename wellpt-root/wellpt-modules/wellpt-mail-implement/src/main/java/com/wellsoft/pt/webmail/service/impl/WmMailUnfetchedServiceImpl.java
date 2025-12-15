package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.webmail.dao.impl.WmMailUnfetchedDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailUnfetchedEntity;
import com.wellsoft.pt.webmail.service.WmMailUnfetchedService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
@Service
public class WmMailUnfetchedServiceImpl extends AbstractJpaServiceImpl<WmMailUnfetchedEntity, WmMailUnfetchedDaoImpl, String> implements WmMailUnfetchedService {
    @Override
    @Transactional
    public void updateFetchedFail(String mailUuid, String userId, String remark) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", userId);
        if (StringUtils.isNotBlank(mailUuid)) {
            param.put("mailUuid", mailUuid);
        }
        param.put("remark", remark);
        this.dao.updateBySQL("update wm_mail_unfetched set fail_count = fail_count+1 ,remark =:remark where user_id=:userId" + (param.containsKey("mailUuid") ? "  and mail_uuid=:mailUuid " : ""), param);
    }

    @Override
    @Transactional
    public void deleteByMailUuidAndUserId(String mailUuid, String userId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", userId);
        param.put("mailUuid", mailUuid);
        this.dao.deleteBySQL("delete from wm_mail_unfetched   where mail_uuid=:mailUuid and user_id=:userId", param);
    }

    @Override
    public List<WmMailUnfetchedEntity> getUnfetchedMailUser(PagingInfo pagingInfo, String orderBy) {
        return this.dao.listByEntityAndPage(new WmMailUnfetchedEntity(), pagingInfo, orderBy);
    }

    @Override
    @Transactional
    public void saveUnfetched(String mailUuid, Set<String> userIds) {
        List<WmMailUnfetchedEntity> unfetchedEntities = Lists.newArrayList();
        for (String uid : userIds) {
            WmMailUnfetchedEntity entity = new WmMailUnfetchedEntity();
            entity.setUserId(uid);
            entity.setMailUuid(mailUuid);
            unfetchedEntities.add(entity);
        }
        this.dao.saveAll(unfetchedEntities);
    }
}
