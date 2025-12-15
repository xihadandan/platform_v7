package com.wellsoft.pt.app.dingtalk.service.impl;

import com.wellsoft.pt.app.dingtalk.dao.EventCallBackDao;
import com.wellsoft.pt.app.dingtalk.entity.EventCallBack;
import com.wellsoft.pt.app.dingtalk.enums.EnumCallBackEventType;
import com.wellsoft.pt.app.dingtalk.service.EventCallBackService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncDeptLogService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncUserLogService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncUserWorkLogService;
import com.wellsoft.pt.app.dingtalk.vo.EventCallBackVo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 钉钉事件回调service实现类
 *
 * @author bryanlin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月20日.1	bryanlin		2020年5月20日		Create
 *          </pre>
 * @date 2020年5月20日
 */
@Service
@Deprecated
public class EventCallBackServiceImpl extends AbstractJpaServiceImpl<EventCallBack, EventCallBackDao, String> implements
        EventCallBackService {

    @Autowired
    private MultiOrgSyncDeptLogService multiOrgSyncDeptLogService;
    @Autowired
    private MultiOrgSyncUserLogService multiOrgSyncUserLogService;
    @Autowired
    private MultiOrgSyncUserWorkLogService multiOrgSyncUserWorkLogService;

    @Override
    public List<EventCallBack> listEventCallBack() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("status", 0);
        List<EventCallBack> listEventCallBack = dao.listByNameSQLQuery("listEventCallBackByConditions", params);
        return listEventCallBack;
    }

    @Override
    public EventCallBackVo getEventCallBackDetail(String uuid) {
        EventCallBackVo vo = new EventCallBackVo();
        EventCallBack eventCallBack = getOne(uuid);

        if (null != eventCallBack) {
            BeanUtils.copyProperties(eventCallBack, vo);

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

    /**
     *
     */
    @Override
    @Transactional
    public void save(EventCallBack bean) {
        EventCallBack entity = bean;
        if (StringUtils.isNotBlank(bean.getUuid())) {
            entity = getOne(bean.getUuid());
            BeanUtils.copyProperties(bean, entity);
        }
        if (StringUtils.isBlank(entity.getEventName())) {
            EnumCallBackEventType enumObj = EnumCallBackEventType.value2EnumObj(bean.getEventType());
            entity.setEventName(enumObj == null ? null : enumObj.getRemark());
        }
        super.save(entity);
    }
}
