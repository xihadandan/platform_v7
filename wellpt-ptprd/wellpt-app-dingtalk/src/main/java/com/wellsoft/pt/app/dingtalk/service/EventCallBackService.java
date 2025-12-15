package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.EventCallBackDao;
import com.wellsoft.pt.app.dingtalk.entity.EventCallBack;
import com.wellsoft.pt.app.dingtalk.vo.EventCallBackVo;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 钉钉时间回调service
 *
 * @author bryanlin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月20日.1	bryanlin		2020年5月20日  	Create
 *          </pre>
 * @date 2020年5月20日
 */
@Deprecated
public interface EventCallBackService extends JpaService<EventCallBack, EventCallBackDao, String> {

    /**
     * 根据条件过滤事件回调数据
     *
     * @param params
     * @return
     */
    public List<EventCallBack> listEventCallBack();

    public EventCallBackVo getEventCallBackDetail(String uuid);

}
