package com.wellsoft.pt.basicdata.rtx.service;

import com.wellsoft.pt.basicdata.rtx.bean.RtxBean;
import com.wellsoft.pt.basicdata.rtx.dao.RtxDao;
import com.wellsoft.pt.basicdata.rtx.entity.Rtx;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.support.Message;

import java.util.List;

/**
 * Description: Rtx设置服务层接口
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-6-17.1	zhouyq		2013-6-17		Create
 * </pre>
 * @date 2013-6-17
 */

public interface RtxService extends JpaService<Rtx, RtxDao, String> {
    public static final String ACL_SID = "ROLE_RTX";

    /**
     * 判断是否启用rtx
     *
     * @return
     */
    public Boolean isRtxEnable();

    /**
     * 通过uuid获取Rtx设置
     *
     * @param uuid
     * @return
     */
    public Rtx get(String uuid);

    /**
     * 通过uuid获取Rtx设置VO对象
     *
     * @param uuid
     * @return
     */
    public RtxBean getBeanByUuid(String uuid);

    /**
     * 保存Rtx设置
     *
     * @param uuid
     * @return
     */
    public void saveBean(RtxBean bean);

    /**
     * 通过UUID删除Rtx设置
     *
     * @param uuid
     * @return
     */
    public void remove(String uuid);

    /**
     * 获取全部的Rtx设置
     *
     * @return
     */
    List<Rtx> getAll();

    /**
     * 同步组织
     */
    public void synchronizedOrganization(RtxBean bean);

    /**
     * 向rtx发送在线消息
     *
     * @param msg
     */
    public void sendMessage(Message msg);

    /**
     * 单点登录
     *
     * @return
     */
    public String singleSignOn(RtxBean bean);

}
