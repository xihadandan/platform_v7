/*
 * @(#)2013-2-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.aop;

import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.entity.SysEntity;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;

import java.util.Calendar;
import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-8.1	zhulh		2013-2-8		Create
 * </pre>
 * @date 2013-2-8
 */
@Aspect
public class HibernateDaoAspect implements Ordered {


    /**
     * Before *SimpleHibernateDao.save
     * 赋值系统字段属于系统核心功能，非可插拔的切面功能
     *
     * @param entity
     */
    public static void beforeSave(JoinPoint jp, JpaEntity entity) {
        if (entity.getUuid() == null || (StringUtils.isBlank(entity.getUuid().toString()))) {
            Calendar calendar = Calendar.getInstance();
            UserDetails user = SpringSecurityUtils.getCurrentUser();
            if (IgnoreLoginUtils.isIgnoreLogin()) {
                user = IgnoreLoginUtils.getUserDetails();
            }
            entity.setCreateTime(calendar.getTime());
            entity.setModifyTime(calendar.getTime());
            entity.setCreator(user == null ? null : user.getUserId());
            entity.setModifier(user == null ? null : user.getUserId());
        } else {
            Calendar calendar = Calendar.getInstance();
            UserDetails user = SpringSecurityUtils.getCurrentUser();
            if (IgnoreLoginUtils.isIgnoreLogin()) {
                user = IgnoreLoginUtils.getUserDetails();
            }
            if (UUIDGeneratorIndicate.class.isAssignableFrom(entity.getClass())) {
                if (StringUtils.isBlank(entity.getCreator())) {
                    entity.setCreateTime(calendar.getTime());
                    entity.setCreator(user == null ? null : user.getUserId());
                }
            }
            entity.setModifyTime(calendar.getTime());
            entity.setModifier(user == null ? null : user.getUserId());
            // 设置系统标识
            if (entity instanceof SysEntity) {
                SysEntity sysEntity = (SysEntity) entity;
                if (StringUtils.isBlank(sysEntity.getSystem())) {
                    sysEntity.setSystem(RequestSystemContextPathResolver.system());
                    sysEntity.setTenant(user.getTenantId());
                }
            }
        }
    }

    /**
     * Before *SimpleHibernateDao.saveAll
     * 赋值系统字段属于系统核心功能，非可插拔的切面功能
     *
     * @param entity
     */
    public static void beforeSaveAll(JoinPoint jp, Collection<? extends JpaEntity> entities) {
        for (JpaEntity entity : entities) {

            if (entity instanceof JpaEntity && (entity.getUuid() == null || (StringUtils.isBlank(entity.getUuid().toString())))) {
                Calendar calendar = Calendar.getInstance();
                UserDetails user = SpringSecurityUtils.getCurrentUser();
                if (IgnoreLoginUtils.isIgnoreLogin()) {
                    user = IgnoreLoginUtils.getUserDetails();
                }
                entity.setCreateTime(calendar.getTime());
                entity.setModifyTime(calendar.getTime());
                entity.setCreator(user == null ? null : user.getUserId());
                entity.setModifier(user == null ? null : user.getUserId());
            } else if (entity instanceof JpaEntity) {
                Calendar calendar = Calendar.getInstance();
                UserDetails user = SpringSecurityUtils.getCurrentUser();
                if (IgnoreLoginUtils.isIgnoreLogin()) {
                    user = IgnoreLoginUtils.getUserDetails();
                }
                if (UUIDGeneratorIndicate.class.isAssignableFrom(entity.getClass())) {
                    if (StringUtils.isBlank(entity.getCreator())) {
                        entity.setCreateTime(calendar.getTime());
                        entity.setCreator(user == null ? null : user.getUserId());
                    }
                }
                entity.setModifyTime(calendar.getTime());
                entity.setModifier(user == null ? null : user.getUserId());
                // 设置系统标识
                if (entity instanceof SysEntity) {
                    SysEntity sysEntity = (SysEntity) entity;
                    if (StringUtils.isBlank(sysEntity.getSystem())) {
                        sysEntity.setSystem(RequestSystemContextPathResolver.system());
                        sysEntity.setTenant(user.getTenantId());
                    }
                }
            }
        }
    }

    /**
     * Before *SimpleHibernateDao.save
     *
     * @param entity
     */
    @Before("( execution(* com.wellsoft.pt.jpa.dao.JpaDao.save(..)) || execution(* com.wellsoft.pt.jpa.hibernate.SimpleHibernateDao.save(..)) || execution(* com.wellsoft.pt.jpa.dao.UniversalDao.save(..))) && args(entity)")
    public void beforeSaveModel(JoinPoint jp, JpaEntity entity) {
        if (entity.getUuid() == null || (StringUtils.isBlank(entity.getUuid().toString()))) {
            Calendar calendar = Calendar.getInstance();
            UserDetails user = SpringSecurityUtils.getCurrentUser();
            // 如果当前处于忽略登录中，则使用忽略登录的用户信息
            if (IgnoreLoginUtils.isIgnoreLogin()) {
                user = IgnoreLoginUtils.getUserDetails();
            }
            entity.setCreateTime(calendar.getTime());
            entity.setModifyTime(calendar.getTime());
            entity.setCreator(user == null ? null : user.getUserId());
            entity.setModifier(user == null ? null : user.getUserId());
            if (entity instanceof TenantEntity) {// 租户级实体类
                ((TenantEntity) entity).setSystemUnitId(user == null ? null : user.getSystemUnitId());
            }

            // 设置系统标识
            if (entity instanceof SysEntity) {
                SysEntity sysEntity = (SysEntity) entity;
                if (StringUtils.isBlank(sysEntity.getSystem())) {
                    sysEntity.setSystem(RequestSystemContextPathResolver.system());
                    sysEntity.setTenant(user.getTenantId());
                }
            }
        } else {
            Calendar calendar = Calendar.getInstance();
            // 如果当前处于忽略登录中，则使用忽略登录的用户信息
            UserDetails user = SpringSecurityUtils.getCurrentUser();
            if (IgnoreLoginUtils.isIgnoreLogin()) {
                user = IgnoreLoginUtils.getUserDetails();
            }
            if (UUIDGeneratorIndicate.class.isAssignableFrom(entity.getClass())) {
                if (StringUtils.isBlank(entity.getCreator())) {
                    entity.setCreateTime(calendar.getTime());
                    entity.setCreator(user == null ? null : user.getUserId());
                    // 租户级实体类
                    if (entity instanceof TenantEntity) {
                        ((TenantEntity) entity).setSystemUnitId(user == null ? null : user.getSystemUnitId());
                    }
                }
            }
            entity.setModifyTime(calendar.getTime());
            entity.setModifier(user == null ? null : user.getUserId());

            // 设置系统标识
            if (entity instanceof SysEntity) {
                SysEntity sysEntity = (SysEntity) entity;
                if (StringUtils.isBlank(sysEntity.getSystem())) {
                    sysEntity.setSystem(RequestSystemContextPathResolver.system());
                    sysEntity.setTenant(user.getTenantId());
                }
            }
        }
    }

    /**
     * Before *SimpleHibernateDao.saveAll
     *
     * @param entity
     */
    @Before("(execution(* com.wellsoft.pt.jpa.dao.JpaDao.saveAll(..)) || execution(* com.wellsoft.pt.jpa.hibernate.SimpleHibernateDao.saveAll(..)) || execution(* com.wellsoft.pt.jpa.dao.UniversalDao.saveAll(..))) && args(entities)")
    public void beforeSaveAllModels(JoinPoint jp, Collection<? extends JpaEntity> entities) {
        for (JpaEntity entity : entities) {
            if (entity.getUuid() == null || (StringUtils.isBlank(entity.getUuid().toString()))) {
                Calendar calendar = Calendar.getInstance();
                UserDetails user = SpringSecurityUtils.getCurrentUser();
                // 如果当前处于忽略登录中，则使用忽略登录的用户信息
                if (IgnoreLoginUtils.isIgnoreLogin()) {
                    user = IgnoreLoginUtils.getUserDetails();
                }
                entity.setCreateTime(calendar.getTime());
                entity.setModifyTime(calendar.getTime());
                entity.setCreator(user.getUserId());
                entity.setModifier(user.getUserId());
                if (entity instanceof TenantEntity) {// 租户级实体类
                    ((TenantEntity) entity).setSystemUnitId(user.getSystemUnitId());
                }
                // 设置系统标识
                if (entity instanceof SysEntity) {
                    SysEntity sysEntity = (SysEntity) entity;
                    if (StringUtils.isBlank(sysEntity.getSystem())) {
                        sysEntity.setSystem(RequestSystemContextPathResolver.system());
                        sysEntity.setTenant(user.getTenantId());
                    }
                }
            } else {
                Calendar calendar = Calendar.getInstance();
                UserDetails user = SpringSecurityUtils.getCurrentUser();
                // 如果当前处于忽略登录中，则使用忽略登录的用户信息
                if (IgnoreLoginUtils.isIgnoreLogin()) {
                    user = IgnoreLoginUtils.getUserDetails();
                }
                if (UUIDGeneratorIndicate.class.isAssignableFrom(entity.getClass())) {
                    if (StringUtils.isBlank(entity.getCreator())) {
                        entity.setCreateTime(calendar.getTime());
                        entity.setCreator(user.getUserId());
                        // 租户级实体类
                        if (entity instanceof TenantEntity) {
                            ((TenantEntity) entity).setSystemUnitId(user.getSystemUnitId());
                        }
                    }
                }
                entity.setModifyTime(calendar.getTime());
                entity.setModifier(user.getUserId());
                // 设置系统标识
                if (entity instanceof SysEntity) {
                    SysEntity sysEntity = (SysEntity) entity;
                    if (StringUtils.isBlank(sysEntity.getSystem())) {
                        sysEntity.setSystem(RequestSystemContextPathResolver.system());
                        sysEntity.setTenant(user.getTenantId());
                    }
                }

            }
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}