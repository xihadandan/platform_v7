package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.dao.impl.WmMailOpenServerDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailOpenServerEntity;

/**
 * Description: 第三方邮箱连接配置服务
 *
 * @author chenq
 * @date 2018/6/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/13    chenq		2018/6/13		Create
 * </pre>
 */
public interface WmMailOpenServerService extends
        JpaService<WmMailOpenServerEntity, WmMailOpenServerDaoImpl, String> {

    /**
     * 根据域名查询 第三方邮箱连接配置
     *
     * @param domain
     * @return
     */
    WmMailOpenServerEntity getByDomain(String domain);
}
