package com.wellsoft.pt.app.dingtalk.dao.impl;

import com.wellsoft.pt.app.dingtalk.dao.EventCallBackDao;
import com.wellsoft.pt.app.dingtalk.entity.EventCallBack;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 钉钉时间回调dao实现类
 *
 * @author bryanlin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020-05-20	bryanlin		2020-05-20		Create
 * </pre>
 * @date 2020-05-20
 */
@Repository
@Deprecated
public class EventCallBackDaoImpl extends AbstractJpaDaoImpl<EventCallBack, String> implements EventCallBackDao {

}
