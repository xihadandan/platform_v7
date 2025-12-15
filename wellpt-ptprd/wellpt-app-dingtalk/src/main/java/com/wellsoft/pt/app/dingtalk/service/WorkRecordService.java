package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.WorkRecordDao;
import com.wellsoft.pt.app.dingtalk.entity.WorkRecord;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 钉钉待办service
 *
 * @author bryanlin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月18日.1	bryanlin		2020年5月18日  	Create
 *          </pre>
 * @date 2020年5月18日
 */
@Deprecated
public interface WorkRecordService extends JpaService<WorkRecord, WorkRecordDao, String> {

    /**
     * 通过三者条件获取唯一一条数据
     *
     * @param bizId
     * @param userId
     * @param recordId
     * @return
     */
    public WorkRecord getWorkRecordByCondition(String bizId, String userId, String recordId);
}
