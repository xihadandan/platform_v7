package com.wellsoft.pt.di.component.hibernate.save;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.component.AbstractProducer;
import com.wellsoft.pt.jpa.dao.SessionOperationHibernateDao;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;

import javax.activation.DataHandler;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/16    chenq		2019/7/16		Create
 * </pre>
 */
public class HibernateSaveEntityProducer extends AbstractProducer<HibernateSaveEntityEndpoint> {

    public HibernateSaveEntityProducer(
            HibernateSaveEntityEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void action(Object body, Map<String, Object> headers, Map<String, Object> properties,
                          Map<String, DataHandler> attachments) {
        if (body == null) {
            return;
        }
        try {
            IgnoreLoginUtils.loginSuperadmin();
            SessionOperationHibernateDao hibernateDao = (SessionOperationHibernateDao) ApplicationContextHolder.getBean(
                    "DynamicDAO_" + super.endpoint.getSessionFactoryId());
            hibernateDao.saveOrDelete(body, super.endpoint.getEntityName(), false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
        }

    }
}
