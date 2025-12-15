package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.webmail.entity.WmMailOpenServerEntity;

import java.util.List;

/**
 * Description: 第三方邮箱连接配置门面服务
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
public interface WmMailOpenServerFacadeService extends Facade {

    List<WmMailOpenServerEntity> listAll();


    WmMailOpenServerEntity getByDomain(String domain);
}
