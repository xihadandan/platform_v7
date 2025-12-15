package com.wellsoft.pt.webmail.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailUseCapacityEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Description: 用户邮箱容量dao实现
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
@Repository
public class WmMailUseCapacityDaoImpl extends AbstractJpaDaoImpl<WmMailUseCapacityEntity, String> {
    public int updateUseCapacity(Long increasement, String userId, String mailbox) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("increasement", increasement);
        param.put("userId", userId);
        param.put("mailbox", mailbox);

        return this.updateByNamedSQL("updateUseCapacity", param);
    }
}
