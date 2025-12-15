package com.wellsoft.pt.webmail.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.webmail.dao.WmMailboxInfoDao;
import com.wellsoft.pt.webmail.entity.WmMailboxInfo;
import org.springframework.stereotype.Repository;

/**
 * @Auther: yt
 * @Date: 2022/2/12 16:28
 * @Description:
 */
@Repository
public class WmMailboxInfoDaoImpl extends AbstractJpaDaoImpl<WmMailboxInfo, String> implements WmMailboxInfoDao {
}
