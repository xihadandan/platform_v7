package com.wellsoft.pt.webmail.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.bean.WmMailBoxInfoBean;
import com.wellsoft.pt.webmail.dao.WmMailboxInfoDao;
import com.wellsoft.pt.webmail.entity.WmMailboxInfo;

import java.util.List;
import java.util.Map;

/**
 * @Auther: yt
 * @Date: 2022/2/12 16:32
 * @Description:
 */
public interface WmMailboxInfoService extends JpaService<WmMailboxInfo, WmMailboxInfoDao, String> {

    List<WmMailBoxInfoBean> queryAll(String sql, Map<String, Object> params);

    List<WmMailBoxInfoBean> queryByPage(String sql, Map<String, Object> params, PagingInfo pagingInfo);

    WmMailBoxInfoBean getByMailUuid(String uuid);

}
