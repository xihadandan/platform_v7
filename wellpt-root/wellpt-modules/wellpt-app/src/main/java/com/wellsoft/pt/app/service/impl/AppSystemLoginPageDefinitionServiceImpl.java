package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppSystemLoginPageDefinitionDao;
import com.wellsoft.pt.app.dao.impl.AppSystemLoginPolicyDaoImpl;
import com.wellsoft.pt.app.dto.AppSystemLoginPageDefinitionDto;
import com.wellsoft.pt.app.entity.AppSystemLoginPageDefinitionEntity;
import com.wellsoft.pt.app.entity.AppSystemLoginPolicyEntity;
import com.wellsoft.pt.app.service.AppSystemLoginPageDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年01月16日   chenq	 Create
 * </pre>
 */
@Service
public class AppSystemLoginPageDefinitionServiceImpl extends AbstractJpaServiceImpl<AppSystemLoginPageDefinitionEntity, AppSystemLoginPageDefinitionDao, Long> implements AppSystemLoginPageDefinitionService {

    @Autowired
    AppSystemLoginPolicyDaoImpl policyDao;

    @Override
    public List<AppSystemLoginPageDefinitionEntity> getAllLoginPage(String tenant, String system) {
        Assert.notNull(tenant, "租户参数不为空");
        Assert.notNull(system, "系统参数不为空");
        Map<String, Object> param = Maps.newHashMap();
        param.put("tenant", tenant);
        param.put("system", system);
        return dao.listByHQL("from AppSystemLoginPageDefinitionEntity where tenant=:tenant and system=:system order by createTime desc", param);
    }

    @Override
    @Transactional
    public boolean enableLoginPage(Long uuid, boolean enabled) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("uuid", uuid);
        param.put("enabled", enabled);
        AppSystemLoginPageDefinitionEntity entity = getOne(uuid);
        if (entity != null) {
            param.put("system", entity.getSystem());
            param.put("tenant", entity.getTenant());
            dao.updateByHQL("update AppSystemLoginPageDefinitionEntity set enabled=:enabled where uuid=:uuid", param);
            if (enabled) {
                // 禁用其他
                dao.updateByHQL("update AppSystemLoginPageDefinitionEntity set enabled=false where uuid <> :uuid " +
                        "and system=:system and tenant=:tenant", param);

            }
        }
        return true;
    }


    @Transactional
    public void setDefaultLoginDef(Long uuid) {
        AppSystemLoginPageDefinitionEntity login = getOne(uuid);
        if (login != null) {
            // 默认功能只有产品版本下的登录设计才有
            Map<String, Object> param = Maps.newHashMap();
            param.put("uuid", uuid);
            param.put("prodVersionUuid", login.getProdVersionUuid());
            param.put("isPc", login.getIsPc());
            this.updateByHQL("update AppSystemLoginPageDefinitionEntity set isDefault = true " +
                    "where prodVersionUuid=:prodVersionUuid and  uuid=:uuid", param);
            this.updateByHQL("update AppSystemLoginPageDefinitionEntity set isDefault = false where " +
                    " prodVersionUuid=:prodVersionUuid and  uuid <> :uuid and isPc = :isPc", param);
        }
    }

    @Override
    @Transactional
    public void updateLoginPageDefJson(String defJson, Long uuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("uuid", uuid);
        param.put("defJson", defJson);
        dao.updateByHQL("update AppSystemLoginPageDefinitionEntity set defJson=:defJson where uuid=:uuid", param);
    }

    @Override
    @Transactional
    public void updateLoginPageWithoutDefJson(AppSystemLoginPageDefinitionEntity body) {
        AppSystemLoginPageDefinitionEntity entity = getOne(body.getUuid());
        if (entity != null) {
            List<String> ignores = Lists.newArrayList(entity.BASE_FIELDS);
            BeanUtils.copyProperties(body, entity, ArrayUtils.addAll(entity.BASE_FIELDS, "defJson", "tenant", "system"));
            save(entity);
        }
    }

    @Override
    public AppSystemLoginPageDefinitionDto getEnableTenantSystemLoginPagePolicy(String tenant, String system) {
        AppSystemLoginPageDefinitionEntity example = new AppSystemLoginPageDefinitionEntity();
        example.setSystem(system);
        example.setTenant(tenant);
        example.setEnabled(true);
        List<AppSystemLoginPageDefinitionEntity> login = listByEntity(example);
        if (CollectionUtils.isNotEmpty(login)) {
            AppSystemLoginPageDefinitionDto dto = new AppSystemLoginPageDefinitionDto();
            BeanUtils.copyProperties(login.get(0), dto);

            AppSystemLoginPolicyEntity policy = new AppSystemLoginPolicyEntity();
            policy.setTenant(tenant);
            policy.setSystem(system);
            policy.setEnabled(true);
            dto.setLoginPolicy(policyDao.listByEntity(policy));
            return dto;
        }

        return null;
    }

    @Override
    public List<AppSystemLoginPageDefinitionEntity> getAllProdVersionLoginPage(Long prodVersionUuid) {
        return this.dao.listByFieldEqValue("prodVersionUuid", prodVersionUuid);
    }

    @Override
    public List<AppSystemLoginPageDefinitionEntity> getAllEnabledLoginPage() {
        return dao.listByFieldEqValue("enabled", true);
    }

    @Override
    @Transactional
    public Long copyLoginPage(Long uuid) {
        AppSystemLoginPageDefinitionEntity entity = getOne(uuid);
        AppSystemLoginPageDefinitionEntity copy = new AppSystemLoginPageDefinitionEntity();
        BeanUtils.copyProperties(entity, copy, copy.BASE_FIELDS);
        copy.setIsDefault(false);
        copy.setEnabled(false);
        save(copy);
        return copy.getUuid();
    }
}
