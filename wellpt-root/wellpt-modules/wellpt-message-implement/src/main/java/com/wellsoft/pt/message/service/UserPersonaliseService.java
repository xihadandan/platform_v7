package com.wellsoft.pt.message.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.UserPersonaliseDao;
import com.wellsoft.pt.message.dto.UserPerDto;
import com.wellsoft.pt.message.entity.UserPersonalise;
import com.wellsoft.pt.message.support.Message;

/**
 * @author yt
 * @title: UserPersonaliseService
 * @date 2020/5/18 9:06 下午
 */
public interface UserPersonaliseService extends JpaService<UserPersonalise, UserPersonaliseDao, String> {

    /**
     * 保存个性化设置
     *
     * @param mainSwitch  主开关
     * @param templateIds
     * @param isPopups
     */
    public void saveUserPersonalise(Integer mainSwitch, String[] templateIds, Integer[] isPopups);


    /**
     * 查询个性化设置
     *
     * @return
     */
    public UserPerDto queryList();

    /**
     * 查询是否弹窗提醒
     *
     * @param recipient 接受人
     * @param message   消息对象
     * @return
     */
    public boolean isPopupWin(String recipient, Message message);

    /**
     * 查询主开关
     *
     * @return
     */
    public boolean isMain(String userId);

    /**
     * 恢复默认
     */
    public void reset();
}
