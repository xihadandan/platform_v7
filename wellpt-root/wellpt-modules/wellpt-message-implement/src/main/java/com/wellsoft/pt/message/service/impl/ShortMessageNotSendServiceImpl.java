package com.wellsoft.pt.message.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.ShortMessageNotSendDao;
import com.wellsoft.pt.message.entity.ShortMessageNotSend;
import com.wellsoft.pt.message.facade.service.impl.MessageClientApiFacadeImpl;
import com.wellsoft.pt.message.service.ShortMessageNotSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author wbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-16.1	wbx		2013-10-16		Create
 * </pre>
 * @date 2013-10-16
 */
@Service
public class ShortMessageNotSendServiceImpl extends
        AbstractJpaServiceImpl<ShortMessageNotSend, ShortMessageNotSendDao, String> implements
        ShortMessageNotSendService {

    @Autowired
    private MessageClientApiFacadeImpl messageClientApiFacade;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.ShortMessageNotSendService#saveShortMessageNotSend(com.wellsoft.pt.message.entity.ShortMessageNotSend)
     */
    @Override
    @Transactional
    public void saveShortMessageNotSend(ShortMessageNotSend shortMessageNotSend) {
        dao.save(shortMessageNotSend);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.ShortMessageNotSendService#sendShortMessage(java.lang.String)
     */
    @Override
    @Transactional
    public ShortMessageNotSend sendShortMessage(String uuid) {
        ShortMessageNotSend shortMessageNotSend = dao.getOne(uuid);
        try {
            messageClientApiFacade.sendSmsMessages(shortMessageNotSend.getRecipientName(),
                    shortMessageNotSend.getRecipientMobilePhone(), shortMessageNotSend.getBody(),
                    shortMessageNotSend.getReservedText1(), shortMessageNotSend.getReservedText2(),
                    shortMessageNotSend.getReservedText3(), false);
            // 未发送短信发送完后则删除
            dao.delete(uuid);
        } catch (Exception e) {
            logger.error("ShortMessageNotSendServiceImpl → sendShortMessage：" + e.getStackTrace());
            return null;
        }
        return shortMessageNotSend;
    }
}