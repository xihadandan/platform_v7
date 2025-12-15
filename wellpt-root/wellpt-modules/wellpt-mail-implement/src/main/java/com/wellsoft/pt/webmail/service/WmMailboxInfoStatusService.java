package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.dao.WmMailboxInfoStatusDao;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoStatus;

import java.util.List;

/**
 * @Auther: yt
 * @Date: 2022/2/12 16:32
 * @Description:
 */
public interface WmMailboxInfoStatusService extends JpaService<WmMailboxInfoStatus, WmMailboxInfoStatusDao, String> {


    List<WmMailboxInfoStatus> listByMialInfoUuid(String uuid);
}
