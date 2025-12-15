package com.wellsoft.pt.webmail.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.webmail.dao.impl.WmMailContactBookGrpDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailContactBookGroupEntity;
import com.wellsoft.pt.webmail.service.WmMailContactBookGrpService;
import com.wellsoft.pt.webmail.service.WmMailContactBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description:邮件个人通讯录分组服务实现类
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
public class WmMailContactBookGrpServiceImpl extends
        AbstractJpaServiceImpl<WmMailContactBookGroupEntity, WmMailContactBookGrpDaoImpl, String> implements
        WmMailContactBookGrpService {

    @Autowired
    WmMailContactBookService wmMailContactBookService;

    @Override
    public List<WmMailContactBookGroupEntity> listByUserId(String userId) {
        return this.dao.listByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByGroupUuids(List<String> uuids) {
        deleteByUuids(uuids);
        wmMailContactBookService.updateGroupToNullByGroupUuids(uuids);
    }
}
