package com.wellsoft.pt.app.dingtalk.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dingtalk.dao.MultiOrgSyncLogDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgSyncLog;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncDeptLogService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncLogService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncUserLogService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncUserWorkLogService;
import com.wellsoft.pt.app.dingtalk.vo.MultiOrgSyncLogVo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年08月16日.1	liuyz		2021年08月16日  	Create
 *          </pre>
 * @date 2021年08月16日
 */
@Service
@Deprecated
public class MultiOrgSyncLogServiceImpl extends AbstractJpaServiceImpl<MultiOrgSyncLog, MultiOrgSyncLogDao, String> implements MultiOrgSyncLogService {

    @Autowired
    private MultiOrgSyncDeptLogService multiOrgSyncDeptLogService;
    @Autowired
    private MultiOrgSyncUserLogService multiOrgSyncUserLogService;
    @Autowired
    private MultiOrgSyncUserWorkLogService multiOrgSyncUserWorkLogService;

    @Override
    public MultiOrgSyncLog getByUuid(String uuid) {
        String hql = " from MultiOrgSyncLog where uuid = :uuid ";
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        List<MultiOrgSyncLog> list = dao.listByHQL(hql, params);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public MultiOrgSyncLogVo getMultiOrgSyncLogDetail(String uuid) {
        MultiOrgSyncLogVo vo = new MultiOrgSyncLogVo();
        MultiOrgSyncLog log = getByUuid(uuid);

        if (null != log) {
            BeanUtils.copyProperties(log, vo);

            if (multiOrgSyncDeptLogService.isExistsErrorData(uuid)) {
                vo.setDeptStatus(0);
            } else {
                vo.setDeptStatus(1);
            }

            if (multiOrgSyncUserLogService.isExistsErrorData(uuid)) {
                vo.setUserStatus(0);
            } else {
                vo.setUserStatus(1);
            }

            if (multiOrgSyncUserWorkLogService.isExistsErrorData(uuid)) {
                vo.setUserWorkStatus(0);
            } else {
                vo.setUserWorkStatus(1);
            }
        }

        return vo;
    }
}
