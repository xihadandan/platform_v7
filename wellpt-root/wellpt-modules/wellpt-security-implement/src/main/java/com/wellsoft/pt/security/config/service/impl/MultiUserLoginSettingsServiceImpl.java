/*
 * @(#)2021-11-18 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.config.dao.MultiUserLoginSettingsDao;
import com.wellsoft.pt.security.config.dto.MultiUserLoginSettingsDto;
import com.wellsoft.pt.security.config.entity.MultiUserLoginSettingsEntity;
import com.wellsoft.pt.security.config.service.MultiUserLoginSettingsService;
import com.wellsoft.pt.security.enums.UserLoginSettings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表MULTI_USER_LOGIN_SETTINGS的service服务接口实现类
 *
 * @author baozh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-11-18.1	baozh		2021-11-18		Create
 * </pre>
 * @date 2021-11-18
 */
@Service
public class MultiUserLoginSettingsServiceImpl extends AbstractJpaServiceImpl<MultiUserLoginSettingsEntity, MultiUserLoginSettingsDao, String> implements MultiUserLoginSettingsService {


    @Override
    public List<MultiUserLoginSettingsDto> getBySystemUnitId(String systemUnitId) {
        MultiUserLoginSettingsEntity entity = getLoginSettingsEntity(systemUnitId);
        return convert(entity);
    }

    @Override
    public MultiUserLoginSettingsEntity getLoginSettingsEntity(String systemUnitId) {
        List<MultiUserLoginSettingsEntity> list = dao.listByFieldEqValue("systemUnitId",
                systemUnitId);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public MultiUserLoginSettingsEntity getLoginSettingsEntity() {
        List<MultiUserLoginSettingsEntity> list = dao.listByFieldEqValue("systemUnitId",
                MultiOrgSystemUnit.PT_ID);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }


    private List<MultiUserLoginSettingsDto> convert(MultiUserLoginSettingsEntity entity) {
        List<MultiUserLoginSettingsDto> list = new ArrayList<>(7);
        MultiUserLoginSettingsDto dto = null;
        for (UserLoginSettings setting : UserLoginSettings.values()) {
            dto = new MultiUserLoginSettingsDto(setting);
            switch (setting) {
                case ACCOUNT:
                    dto.setEnable(1);
                    dto.setLoginTypeAlias(entity == null ? "用户名" : entity.getAccountAlias());
                    break;
                case ACCOUNTZH:
                    dto.setEnable(entity == null ? 0 : entity.getAccountZhEnable());
                    dto.setLoginTypeAlias(entity == null ? "姓名" : entity.getAccountZhAlias());
                    break;
                case NAMEEN:
                    dto.setEnable(entity == null ? 0 : entity.getNameEnEnable());
                    dto.setLoginTypeAlias(entity == null ? null : entity.getNameEnAlias());
                    break;
                case TELL:
                    dto.setEnable(entity == null ? 0 : entity.getTellEnable());
                    dto.setLoginTypeAlias(entity == null ? null : entity.getTellAlias());
                    break;
                case IDENTIFIERCODE:
                    dto.setEnable(entity == null ? 0 : entity.getIdentifierCodeEnable());
                    dto.setLoginTypeAlias(entity == null ? null : entity.getIdentifierCodeAlias());
                    break;
                case EMAIL:
                    dto.setEnable(entity == null ? 0 : entity.getEmailEnable());
                    dto.setLoginTypeAlias(entity == null ? null : entity.getEmailAlias());
                    break;
                case EMPCODE:
                    dto.setEnable(entity == null ? 0 : entity.getEmpCodeEnable());
                    dto.setLoginTypeAlias(entity == null ? null : entity.getEmpCodeAlias());
                    break;
            }
            list.add(dto);
        }
        return list;
    }

    @Transactional
    @Override
    public boolean saveLoginSettingsEntity(MultiUserLoginSettingsEntity settingsEntity) {
        MultiUserLoginSettingsEntity entity = getLoginSettingsEntity();
        if (entity == null) {
            entity = new MultiUserLoginSettingsEntity();
        }

        //清空临时表
        getDao().deleteByNamedSQL("deleteReduplicate", new HashMap<>());

        BeanUtils.copyProperties(settingsEntity, entity, new String[]{"uuid", "systemUnitId"});
        save(entity);
        //验证重复数据
        Map<String, Object> param = new HashMap<>();
        param.putAll(JSONObject.parseObject(JSONObject.toJSONString(settingsEntity)));

        //将重复数据插入临时表
        getDao().updateByNamedSQL("insertReduplicate", param);
        List<QueryItem> list = listQueryItemByNameSQLQuery("checkUserLoginDoubleQuery", param, null);
        return !CollectionUtils.isEmpty(list);
    }


    @Override
    public String getUserNamePlaceholder(String systemUnitId) {
        MultiUserLoginSettingsEntity loginSettings = getLoginSettingsEntity();
        StringBuilder sb = new StringBuilder("请输入用户名");
        if (loginSettings != null && Config.DEFAULT_TENANT.equals(systemUnitId)) {//租户才需要提示
            if (StringUtils.isNotBlank(loginSettings.getAccountAlias())) {
                sb = new StringBuilder("请输入" + loginSettings.getAccountAlias());
            }
            appendMsg(sb, loginSettings.getAccountZhEnable(), loginSettings.getAccountZhAlias(), "中文账号");
            appendMsg(sb, loginSettings.getNameEnEnable(), loginSettings.getNameEnAlias(), "英文名");
            appendMsg(sb, loginSettings.getTellEnable(), loginSettings.getTellAlias(), "手机号");
            appendMsg(sb, loginSettings.getIdentifierCodeEnable(), loginSettings.getIdentifierCodeAlias(), "身份证号");
            appendMsg(sb, loginSettings.getEmailEnable(), loginSettings.getEmailAlias(), "邮箱");
            appendMsg(sb, loginSettings.getEmpCodeEnable(), loginSettings.getEmpCodeAlias(), "员工编号");
        }
        return sb.toString();
    }

    /**
     * 追加提示
     *
     * @param enable 1为开启，0为关闭
     * @return
     * @author baozh
     * @date 2021/11/23 19:07
     */
    private StringBuilder appendMsg(StringBuilder sb, int enable, String param, String defaultParam) {
        if (enable == 1) {
            sb.append("/");
            if (StringUtils.isNotBlank(param)) {
                sb.append(param);
            } else {
                sb.append(defaultParam);
            }
        }
        return sb;
    }

    @Override
    public List<QueryItem> queryUserLoginDoubleInfo(Map<String, Object> queryParams, PagingInfo pagingInfo) {
        return dao.listQueryItemByNameSQLQueryHash("userLoginDoubleInfo", queryParams, pagingInfo);
    }
}
