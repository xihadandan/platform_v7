package com.wellsoft.pt.message.provider;

import com.wellsoft.pt.message.support.Message;

/**
 * Description: 发送消息触发后台接口类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年10月8日.1	Asus		2015年10月8日		Create
 * </pre>
 * @date 2015年10月8日
 */
public interface MessageIntefaceSourceProvider {

    public void doService(Message msg);

    public String getIntefaceName();

}
