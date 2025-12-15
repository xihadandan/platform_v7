package com.wellsoft.pt.webmail.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.webmail.entity.WmMailOpenServerEntity;
import com.wellsoft.pt.webmail.facade.service.WmMailOpenServerFacadeService;
import com.wellsoft.pt.webmail.service.WmMailOpenServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:第三方邮箱连接配置门面服务实现类
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
public class WmMailOpenServerFacadeServiceImpl extends AbstractApiFacade implements
        WmMailOpenServerFacadeService {

    @Autowired
    WmMailOpenServerService wmMailOpenServerService;

    @Override
    public List<WmMailOpenServerEntity> listAll() {
        return wmMailOpenServerService.listAll();
    }

    @Override
    public WmMailOpenServerEntity getByDomain(String domain) {
        return wmMailOpenServerService.getByDomain(domain);
    }


}
