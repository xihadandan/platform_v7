/*
 * @(#)2018-12-07 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.config.dao.AppLoginPageConfigDao;
import com.wellsoft.pt.security.config.entity.AppLoginPageConfigEntity;
import com.wellsoft.pt.security.config.service.AppLoginPageConfigService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletContext;
import javax.sql.rowset.serial.SerialClob;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表APP_LOGIN_PAGE_CONFIG的service服务接口实现类
 *
 * @author linst
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-12-07.1	leo		2018-12-07		Create
 * </pre>
 * @date 2018-12-07
 */
@Service
public class AppLoginPageConfigServiceImpl extends
        AbstractJpaServiceImpl<AppLoginPageConfigEntity, AppLoginPageConfigDao, String> implements
        AppLoginPageConfigService {
    @Autowired
    private ServletContext servletContext;

    @Override
    @Transactional
    public void saveAppLoginPageConfigEntity(AppLoginPageConfigEntity po) {
        dao.save(po);
        InputStream in = null;
        OutputStream out = null;
        try {

            /**
             * FIXME:
             * 1.单点登录的配置应该是系统启动的时候或者系统运行中自动更新加载到属性内存上
             * 2.spring security 相关cas配置从xml改成代码配置方式
             */
            // 1、单点登录的配置应该是系统启动的时候自动加载到属性内存上
			/*in = this.getClass().getResourceAsStream("/system-security.properties");
			Properties pro = new Properties();
			pro.load(in);

			out = new FileOutputStream(this.getClass().getResource("/").getPath() + "system-security.properties");
			if ("0".equals(po.getLoginBoxCas())) {
				pro.setProperty("security.cas.isuse", "true");
			} else {
				pro.setProperty("security.cas.isuse", "false");
			}
			pro.setProperty("security.cas.url", po.getLoginBoxCasUrl());
			pro.setProperty("security.cas.application.url", po.getLoginBoxCasAppUrl());

			pro.store(out, "单点配置");*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public AppLoginPageConfigEntity getBySystemUnitId(String systemUnitId) {
        List<AppLoginPageConfigEntity> list = this.dao.listByFieldEqValue("systemUnitId",
                systemUnitId);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    @Transactional
    public AppLoginPageConfigEntity saveInitPageConfig(String systemUnitId) {
        AppLoginPageConfigEntity entity = this.getBySystemUnitId(systemUnitId);
        if (entity != null) {
            return entity;
        }
        AppLoginPageConfigEntity configEntity = new AppLoginPageConfigEntity();
        configEntity.setPageTitle("业务基础（云）平台");
        configEntity.setLoginBoxAccountRememberPas("0");
        configEntity.setLoginBoxAccountRememberUse("0");
        try {
            IgnoreLoginUtils.loginSuperadmin();
            String path = servletContext.getRealPath("/resources/pt/login");
            String style = "def";
            if (MultiOrgSystemUnit.PT_ID.equalsIgnoreCase(systemUnitId)) {
                //超管页面初始化
                configEntity.setPageTitle(configEntity.getPageTitle() + " - 超级管理员后台");
                configEntity.setPageStyle("_right");//靠右登录框
                configEntity.setSystemUnitId(MultiOrgSystemUnit.PT_ID);
                style = "right";
            } else {
                //默认的单位登录页面
                configEntity.setPageStyle("");//默认中间登录框
                //FIXME:单位ID由页面or参数指定？
                configEntity.setSystemUnitId(systemUnitId);
            }
            String pageBackgroundImagePath = path + File.separator + style + File.separator + "imgs" + File.separator
                    + "body.png";
            String pageLogoPath = path + File.separator + style + File.separator + "imgs" + File.separator + "header.png";
            configEntity.setPageBackgroundImage(new SerialClob(
                    base64EndocdeFile(new File(pageBackgroundImagePath)).toCharArray()));
            configEntity.setPageLogo(new SerialClob(
                    base64EndocdeFile(new File(pageLogoPath)).toCharArray()));

            this.save(configEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
        }

        return configEntity;
    }

    private String base64EndocdeFile(File file) {
        if (file.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                BASE64Encoder encoder = new BASE64Encoder();
                return encoder.encode(
                        IOUtils.toByteArray(inputStream));
            } catch (Exception e) {
                logger.error("file base64 encode error", e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }

        }

        return "";
    }

    @Override
    @Transactional
    public AppLoginPageConfigEntity getLoginConfigBySystemUnitId(String systemUnitId,
                                                                 boolean image2base64) {
        AppLoginPageConfigEntity pageConfig = this.getBySystemUnitId(systemUnitId);
        if (pageConfig == null) {
            pageConfig = this.saveInitPageConfig(systemUnitId);
        }
        if (image2base64) {
            try {
                String imageBase64 = IOUtils.toString(
                        pageConfig.getPageBackgroundImage().getCharacterStream());
                if (StringUtils.isNotBlank(imageBase64)) {
                    pageConfig.setPageBackgroundImageBase64(imageBase64);
                }

                imageBase64 = IOUtils.toString(pageConfig.getPageLogo().getCharacterStream());
                if (StringUtils.isNotBlank(imageBase64)) {
                    pageConfig.setPageLogoBase64(imageBase64);
                }
            } catch (Exception e) {
                logger.error("登录页配置图片转码异常");
            }
        }
        pageConfig.setPwdEncryptKey(RandomStringUtils.randomNumeric(32));

        if (!Config.DEFAULT_TENANT.equals(systemUnitId) && !MultiOrgSystemUnit.PT_ID.equals(systemUnitId)) {
            // 如果是获取单位登录页配置信息，则其它信息，从超管设置的单位登录页信息继承
            AppLoginPageConfigEntity entity = getBySystemUnitId(Config.DEFAULT_TENANT);
            BeanUtils.copyProperties(entity, pageConfig, new String[]{"uuid", "pageTitle", "pageBackgroundImage", "pageBackgroundImageBase64", "pageStyle", "pageLogo", "pageLogoBase64", "footerContent", "unitLoginPageUri", "unitLoginPageSwitch", "systemUnitId"});
        }

        return pageConfig;
    }

    public List<AppLoginPageConfigEntity> listByLoginPageUrl(String loginUrl) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loginUrl", StringUtils.lowerCase(loginUrl));
        String sql = " from AppLoginPageConfigEntity where lower(unitLoginPageUri) = :loginUrl ";
        return this.dao.listByHQL(sql, paramMap);
//        return this.dao.listBySQL(sql,paramMap);
    }

    public void turnOffLoginPageSwitch() {
        this.dao.updateByHQL(" update AppLoginPageConfigEntity set unitLoginPageSwitch = '0' where unitLoginPageSwitch = '1' ", null);
    }

}
