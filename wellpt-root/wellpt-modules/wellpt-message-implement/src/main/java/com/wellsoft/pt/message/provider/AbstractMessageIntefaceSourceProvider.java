package com.wellsoft.pt.message.provider;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.message.support.Message;

/**
 * Description: 发送消息触发后台接口的实现类
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
public abstract class AbstractMessageIntefaceSourceProvider extends BaseServiceImpl implements
        MessageIntefaceSourceProvider {

    @Override
    public void doService(Message msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getIntefaceName() {
        // TODO Auto-generated method stub
        return null;
    }

}
