package com.wellsoft.pt.webmail.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.webmail.dao.WmMailboxInfoStatusDao;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoStatus;
import com.wellsoft.pt.webmail.service.WmMailboxInfoStatusService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: yt
 * @Date: 2022/2/12 16:32
 * @Description:
 */
@Service
public class WmMailboxInfoStatusServiceImpl extends AbstractJpaServiceImpl<WmMailboxInfoStatus, WmMailboxInfoStatusDao, String> implements WmMailboxInfoStatusService {
    @Override
    public List<WmMailboxInfoStatus> listByMialInfoUuid(String uuid) {
        List<WmMailboxInfoStatus> infoStatusList = this.getDao().listByFieldEqValue("mailInfoUuid", uuid);
        return infoStatusList;
    }
}
