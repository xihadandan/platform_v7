package com.wellsoft.pt.webmail.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailContactBookGroupEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description: 邮件联系人分组dao实现
 *
 * @author chenq
 * @date 2018/6/6
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/6    chenq		2018/6/6		Create
 * </pre>
 */
@Repository
public class WmMailContactBookGrpDaoImpl extends
        AbstractJpaDaoImpl<WmMailContactBookGroupEntity, String> {
    public List<WmMailContactBookGroupEntity> listByUserId(String userId) {
        return listByFieldEqValue("creator", userId);
    }


}
