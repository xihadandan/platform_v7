package com.wellsoft.pt.app.dingtalk.service.impl;

import com.wellsoft.pt.app.dingtalk.dao.WorkRecordDao;
import com.wellsoft.pt.app.dingtalk.entity.WorkRecord;
import com.wellsoft.pt.app.dingtalk.service.WorkRecordService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 钉钉待办service实现类
 *
 * @author bryanlin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月18日.1	bryanlin		2020年5月18日		Create
 *          </pre>
 * @date 2020年5月18日
 */
@Service
@Deprecated
public class WorkRecordServiceImpl extends AbstractJpaServiceImpl<WorkRecord, WorkRecordDao, String> implements
        WorkRecordService {

    @Override
    public WorkRecord getWorkRecordByCondition(String bizId, String userId, String recordId) {
        String hql;
        Map<String, Object> params = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(recordId)) {
            params.put("recordId", recordId);
            hql = "from WorkRecord t where t.dingRecordId = :recordId";
        } else {
            params.put("bizId", bizId);
            params.put("userId", userId);
            hql = "from WorkRecord t where t.bizId = :bizId and t.dingUserId = :userId";
        }
        List<WorkRecord> list = dao.listByHQL(hql, params);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    @Transactional
    public void delete(String uuid) {
        WorkRecord entity = getOne(uuid);
        if (entity != null) {
            delete(entity);
        }
    }
}
