package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.webmail.dao.impl.WmMailOpenServerDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailOpenServerEntity;
import com.wellsoft.pt.webmail.service.WmMailOpenServerService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Description:第三方邮箱连接配置服务实现类
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
@Service
public class WmMailOpenServerServiceImpl extends
        AbstractJpaServiceImpl<WmMailOpenServerEntity, WmMailOpenServerDaoImpl, String> implements
        WmMailOpenServerService {
    @Override
    public WmMailOpenServerEntity getByDomain(String domain) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("domain", domain);
        return this.dao.getOneByHQL("from WmMailOpenServerEntity where domain=:domain", param);
    }
}
