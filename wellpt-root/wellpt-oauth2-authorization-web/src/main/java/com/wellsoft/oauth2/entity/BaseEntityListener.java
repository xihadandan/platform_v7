package com.wellsoft.oauth2.entity;

import com.wellsoft.oauth2.utils.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Calendar;
import java.util.Date;


public class BaseEntityListener {
    private Logger logger = LoggerFactory.getLogger(BaseEntityListener.class);

    @PrePersist
    public void prePersist(Object object) {
        if (object instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) object;
            if (baseEntity.getUuid() == null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null) {
                    baseEntity.setCreateBy(authentication.getName());
                }
                baseEntity.setUuid(IdUtils.id());
                baseEntity.setCreateTime(Calendar.getInstance().getTime());
                baseEntity.setModifyTime(baseEntity.getCreateTime());
                baseEntity.setModifyBy(baseEntity.getCreateBy());
            }
        }
    }


    @PreUpdate
    public void preUpdate(Object object) {
        if (object instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) object;
            baseEntity.setModifyTime(new Date());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                baseEntity.setModifyBy(authentication.getName());
            }
        }
    }
}
