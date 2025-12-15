package com.wellsoft.pt.api.service;

import com.wellsoft.pt.api.dao.impl.ApiCommandDaoImpl;
import com.wellsoft.pt.api.entity.ApiCommandEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
public interface ApiCommandService extends JpaService<ApiCommandEntity, ApiCommandDaoImpl, String> {
    /**
     * 重试次数+1
     *
     * @param commandUuid
     */
    void updateRetryCntAdded(String commandUuid);

    /**
     * 重试指令
     *
     * @param commandUuid
     * @throws Exception
     */
    void retryCommand(String commandUuid) throws Exception;

    /**
     * 查询指定时间大于等于重试时间的
     *
     * @param date
     * @return
     */
    List<ApiCommandEntity> listByGreaterThanRetryTime(Date date);
}
