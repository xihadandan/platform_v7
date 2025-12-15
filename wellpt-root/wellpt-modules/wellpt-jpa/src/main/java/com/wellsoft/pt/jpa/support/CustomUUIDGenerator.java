package com.wellsoft.pt.jpa.support;

import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.util.SnowFlake;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

import java.io.Serializable;

/**
 * 自定义UUID生成器
 *
 * @author xiem
 */
public class CustomUUIDGenerator extends org.hibernate.id.UUIDGenerator {
    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        if (object instanceof JpaEntity) {
            JpaEntity entity = (JpaEntity) object;
            if (entity.getUuid() != null && StringUtils.isNotBlank(entity.getUuid().toString())) {
                return entity.getUuid();
            }
        }
        return SnowFlake.getId() + "";
    }
}
