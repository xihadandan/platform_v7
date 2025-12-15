package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.dao.impl.WmMailUseCapacityDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailUseCapacityEntity;

import java.util.Set;

/**
 * Description: 用户邮箱容量服务
 *
 * @author chenq
 * @date 2019/7/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/18    chenq		2019/7/18		Create
 * </pre>
 */
public interface WmMailUseCapacityService extends
        JpaService<WmMailUseCapacityEntity, WmMailUseCapacityDaoImpl, String> {

    void addUseCapacity(String userId, String mailbox, Long used);

    int updateUseCapacity(Long increasement, String userId, String systemUnitId, String mailbox);

    int updateseCapacityTransform(Long fromCapacity, Long toCapacity, String fromMailBox,
                                  String toMailbox, String userId);

    /**
     * 初始化用户的默认使用空间
     *
     * @param userId
     */
    void saveUserUseCapacityInitial(String userId);

    WmMailUseCapacityEntity getByUserIdAndMailbox(String userId, String boxname);

    void updateUseCapacity(Set<String> userIdSet, Long mailSize, String mailbox);
}
