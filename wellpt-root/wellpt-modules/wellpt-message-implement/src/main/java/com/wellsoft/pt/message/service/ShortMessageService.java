package com.wellsoft.pt.message.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.ShortMessageDao;
import com.wellsoft.pt.message.entity.MasConfig;
import com.wellsoft.pt.message.entity.ShortMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
public interface ShortMessageService extends JpaService<ShortMessage, ShortMessageDao, String> {

    public void saveShortMessage(List<ShortMessage> shortsms);

    public Long getMaxSmid();

    public String getOnlySmid();

    public List<ShortMessage> findBySmid(String smid);

    public void doMasJob();

    public void doMasJob2();

    public MasConfig getBean();

    public String saveMas(MasConfig config);

    public void sendCommonMessages(String recipients, String mbphones, String body, String reservedText1,
                                   String reservedText2, String reservedText3);

    public void sendCommonMessages(String recipients, String mbphones, String body, String reservedText1,
                                   String reservedText2, String reservedText3, Boolean async);

    public void sendCommonMessages(Set<String> recipients, Set<String> mbphones, String body, String reservedText1,
                                   String reservedText2, String reservedText3, Boolean async);

    public boolean applyConfig(MasConfig config);

    public Map<String, String> getShortMessageByUuid(String uuid);

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    List<ShortMessage> findByExampleDesc(ShortMessage example);

    public Boolean addWsIdBySmId(long SmId, String wsId);

    public List<ShortMessage> getListOfNotRPT(int type, int sendStatus);

    /**
     * 接受云mas推送状态报告
     */
    public void cloudMasReport(String json);

    List<ShortMessage> listByMessageOutboxUuid(String messageOutboxUuid);
}
