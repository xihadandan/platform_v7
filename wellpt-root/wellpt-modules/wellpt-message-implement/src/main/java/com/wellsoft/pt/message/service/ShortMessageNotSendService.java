package com.wellsoft.pt.message.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.ShortMessageNotSendDao;
import com.wellsoft.pt.message.entity.ShortMessageNotSend;

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
public interface ShortMessageNotSendService extends JpaService<ShortMessageNotSend, ShortMessageNotSendDao, String> {

    public void saveShortMessageNotSend(ShortMessageNotSend shortMessageNotSend);

    /**
     * 发送
     *
     * @param uuid
     */
    public ShortMessageNotSend sendShortMessage(String uuid);

}
