package com.wellsoft.pt.message.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.message.entity.ShortMessage;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author wbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-17.1	wbx		2013-10-17		Create
 * </pre>
 * @date 2013-10-17
 */
public interface ShortMessageDao extends JpaDao<ShortMessage, String> {

    ShortMessage getObjByMsgGroupAndRecPhone(String msgGroup, String mobile);

    List<ShortMessage> getListByIsReSend(boolean isReSend, String text2);

    List<ShortMessage> getListByIsReSendAndType1(boolean isReSend, int type, int reSendLimit);

    List<ShortMessage> getListByIsReSendAndType(boolean isReSend, int type);

    ShortMessage getObjBySmidAndRecPhone(long smID, String mobile, int type, int sendStatus);

    List<ShortMessage> getListOfNotRPT(int type, int sendStatus);

    ShortMessage getBySmid(long smid);

    List<ShortMessage> findBySmid(int smid);

    long getMaxSmid();

    void saveBatch(List<ShortMessage> shortsms);

}
