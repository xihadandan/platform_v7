package com.wellsoft.pt.webmail.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.webmail.dao.impl.WmMailContactBookDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailContactBookEntity;
import com.wellsoft.pt.webmail.service.WmMailContactBookService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 邮件个人通讯录服务实现类
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
@Service
public class WmMailContactBookServiceImpl extends
        AbstractJpaServiceImpl<WmMailContactBookEntity, WmMailContactBookDaoImpl, String> implements
        WmMailContactBookService {


    @Override
    public List<WmMailContactBookEntity> listByUserId(String userId) {
        return this.dao.listByUserId(userId);
    }

    @Override
    public List<WmMailContactBookEntity> listByGrpId(String grpId) {
        return this.dao.listByGrpId(grpId);
    }

    @Override
    public void updateGroupToNullByGroupUuids(List<String> groupUuids) {
        this.dao.updateGroupToNullByGroupUuids(groupUuids);
    }

    @Override
    public WmMailContactBookEntity getByContactId(String id) {
        return this.dao.getByContactId(id);
    }

    @Override
    public List<String> listContactIdByGrpId(String grpId) {
        return this.dao.listContactIdByGrpId(grpId);
    }


}
