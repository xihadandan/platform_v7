package com.wellsoft.pt.message.dao.impl;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.message.dao.ShortMessageDao;
import com.wellsoft.pt.message.entity.ShortMessage;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.*;

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
@Repository
public class ShortMessageDaoImpl extends AbstractJpaDaoImpl<ShortMessage, String> implements ShortMessageDao {

    private Integer PageSize = 30;

    @Override
    public void saveBatch(List<ShortMessage> shortsms) {
        for (ShortMessage s : shortsms) {
            save(s);
        }
    }

    @Override
    public long getMaxSmid() {
        String hql = "select max(smid) from ShortMessage";
        Object obj = super.getSession().createQuery(hql).uniqueResult();
        if (obj != null) {
            return Long.valueOf(obj.toString());
        }
        return -1L;
    }

    @Override
    public List<ShortMessage> findBySmid(int smid) {
        Date date = Calendar.getInstance().getTime();
        String hql = "from ShortMessage s where s.type=0 and s.smid=:smid and s.sendTime "
                + "	between :fromdate and :nowdate";
        Map<String, Object> values = new HashMap<String, Object>();
        List<ShortMessage> list;
        values.put("smid", smid);
        values.put("nowdate", date);
        values.put("fromdate", -2);
        list = this.listByHQL(hql, values);
        return list;
    }

    @Override
    public ShortMessage getBySmid(long smid) {
        String hql = "from ShortMessage s where s.smid=:smid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("smid", smid);
        List<ShortMessage> list = this.listByHQL(hql, values);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<ShortMessage> getListOfNotRPT(int type, int sendStatus) {
        String hql = "from ShortMessage s where s.type=:type and s.sendStatus=:sendStatus and s.reservedText6 is not null";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("type", type);
        values.put("sendStatus", sendStatus);
        Page<ShortMessage> page = new Page<ShortMessage>();
        page.setPageSize(this.PageSize);
        return this.listByHQLAndPage(hql, values, new PagingInfo(1, this.PageSize));
    }

    /**
     * 通过短信编号和接收人手机号码获取实体
     *
     * @param smID   短信编号
     * @param mobile 接收人手机号码
     * @param type   消息类型(0 modem; 1 mas机)
     * @return
     */
    @Override
    public ShortMessage getObjBySmidAndRecPhone(long smID, String mobile, int type, int sendStatus) {
        String hql = "from ShortMessage s where s.type=:type and s.smid=:smid and s.recipientMobilePhone=:recipientMobilePhone and s.sendStatus=:sendStatus";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("type", type);
        values.put("smid", smID);
        values.put("recipientMobilePhone", mobile);
        values.put("sendStatus", sendStatus);
        List<ShortMessage> list = this.listByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取在重发时限内并需要重发的Max短信
     *
     * @param isReSend 是否要重发
     * @param type     短信机类型 1：max机
     * @return
     */
    @Override
    public List<ShortMessage> getListByIsReSendAndType(boolean isReSend, int type) {
        String hql = "from ShortMessage s where s.type=:type and s.isReSend=:isReSend";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("type", type);
        values.put("isReSend", isReSend);
        List<ShortMessage> list = this.listByHQLAndPage(hql, values, new PagingInfo(1, this.PageSize));
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        return null;
    }

    @Override
    public List<ShortMessage> getListByIsReSendAndType1(boolean isReSend, int type, int reSendLimit) {
        Date nowdate = Calendar.getInstance().getTime();
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.add(Calendar.DATE, -reSendLimit);
        Date fromdate = fromCalendar.getTime();
        String hql = "from ShortMessage s where s.type=:type and s.isReSend=:isReSend and s.sendTime "
                + "	between :fromdate and :nowdate";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("type", type);
        values.put("isReSend", isReSend);
        values.put("reSendLimit", reSendLimit);
        values.put("fromdate", fromdate);
        values.put("nowdate", nowdate);
        List<ShortMessage> list = this.listByHQLAndPage(hql, values, new PagingInfo(1, this.PageSize));
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        return null;
    }

    /**
     * 获取在重发时限内并需要重发的Max短信
     *
     * @param isReSend 是否要重发
     * @param type     短信机类型 1：max机
     * @return
     */
    @Override
    public List<ShortMessage> getListByIsReSend(boolean isReSend, String text2) {
        String hql = "from ShortMessage s where s.reservedText2=:text2 and s.isReSend=:isReSend";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("isReSend", isReSend);
        values.put("text2", text2);
        List<ShortMessage> list = this.listByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        return null;
    }

    /**
     * 通过消息批次号和接收人手机号码获取实体
     *
     * @param msgGroup 消息批次号
     * @param mobile   接收人手机号码
     * @return
     */
    @Override
    public ShortMessage getObjByMsgGroupAndRecPhone(String msgGroup, String mobile) {
        String hql = "from ShortMessage s where s.msgGroup=:msgGroup and s.recipientMobilePhone=:recipientMobilePhone";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("msgGroup", msgGroup);
        values.put("recipientMobilePhone", mobile);
        List<ShortMessage> list = this.listByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }
}
