package com.wellsoft.pt.integration.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.integration.service.ExchangeDataSynchronousService;
import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;

public class LogEntityInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;

    private Logger logger = Logger.getLogger(LogEntityInterceptor.class);

    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        ExchangeDataSynchronousService exchangeDataSynchronousService = ApplicationContextHolder
                .getBean(ExchangeDataSynchronousService.class);
        exchangeDataSynchronousService.logEntityInterceptorTool(entity, id, state, propertyNames, types, 2);
    }

    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] preState,
                                String[] propertyNames, Type[] types) {
        ExchangeDataSynchronousService exchangeDataSynchronousService = ApplicationContextHolder
                .getBean(ExchangeDataSynchronousService.class);
        exchangeDataSynchronousService.logEntityInterceptorTool(entity, id, currentState, propertyNames, types, 1);
        return true;
    }

    public boolean onSave(Object entity, Serializable id, Object[] State, String[] propertyNames, Type[] types) {
        ExchangeDataSynchronousService exchangeDataSynchronousService = ApplicationContextHolder
                .getBean(ExchangeDataSynchronousService.class);
        exchangeDataSynchronousService.logEntityInterceptorTool(entity, id, State, propertyNames, types, 1);
        return true;
    }

    //	@Override
    //	public void onCollectionRemove(Object obj, Serializable serializable) throws CallbackException {
    //TODO Auto-generated method stub
    //		ExchangeDataSynchronousService exchangeDataSynchronousService = ApplicationContextHolder
    //				.getBean(ExchangeDataSynchronousService.class);
    //		exchangeDataSynchronousService.logCollectionInterceptorTool(obj, serializable);
    //	}

    //	@Override
    //	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
    //TODO Auto-generated method stub
    //		ExchangeDataSynchronousService exchangeDataSynchronousService = ApplicationContextHolder
    //				.getBean(ExchangeDataSynchronousService.class);
    //		exchangeDataSynchronousService.logCollectionInterceptorTool(collection, key);
    //	}

}
