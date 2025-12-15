package com.wellsoft.pt.app.feishu.service.impl;

import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.feishu.dao.FeishuConfigDao;
import com.wellsoft.pt.app.feishu.entity.FeishuConfigEntity;
import com.wellsoft.pt.app.feishu.facade.service.FeishuEventCallbackFacadeService;
import com.wellsoft.pt.app.feishu.service.FeishuConfigService;
import com.wellsoft.pt.app.feishu.utils.FeishuApiUtils;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeishuConfigServiceImpl extends AbstractJpaServiceImpl<FeishuConfigEntity, FeishuConfigDao, Long> implements FeishuConfigService {

    private static final String CACHE_NAME = "Basic Data";

    @Autowired
    private FeishuEventCallbackFacadeService feishuEventCallbackFacadeService;

    @Override
    public FeishuConfigVo query() {
        FeishuConfigEntity configEntity = this.getConfigEntity();
        FeishuConfigVo feishuConfigVo = FeishuConfigVo.create(configEntity);
        return feishuConfigVo;
    }

    @Override
    @Cacheable(value = CACHE_NAME)
    public FeishuConfigVo getBySystemAndTenant(String system, String tenant) {
        FeishuConfigEntity entity = new FeishuConfigEntity();
        entity.setSystem(system);
        entity.setTenant(tenant);
        List<FeishuConfigEntity> list = this.dao.listByEntity(entity);
        return FeishuConfigVo.create(CollectionUtils.isNotEmpty(list) ? list.get(0) : null);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public Long save(FeishuConfigVo feishuConfigVo) {
        String appId = feishuConfigVo.getAppId();
        if (StringUtils.isNotBlank(appId) && this.countByAppId(appId) > 1) {
            throw new IllegalArgumentException("应用ID已在使用");
        }

        FeishuConfigEntity configEntity = this.getConfigEntity();
        if (configEntity == null) {
            configEntity = new FeishuConfigEntity();
        }
        BeanUtils.copyProperties(feishuConfigVo, configEntity, JpaEntity.BASE_FIELDS);
        configEntity.setDefinitionJson(JsonUtils.object2Json(feishuConfigVo.getConfiguration()));
//        configEntity.setSyncConfJson(JSON.toJSONString(feishuConfigVo.getSyncConf()));
//        configEntity.setEventConfJson(JSON.toJSONString(feishuConfigVo.getEventConfs()));
//        configEntity.setApprovalConfJson(JSON.toJSONString(feishuConfigVo.getApprovalConf()));
        configEntity.setSystem(RequestSystemContextPathResolver.system());
        configEntity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        this.save(configEntity);

        feishuEventCallbackFacadeService.createWsClient(feishuConfigVo);
        return configEntity.getUuid();
    }

    private long countByAppId(String appId) {
        FeishuConfigEntity entity = new FeishuConfigEntity();
        entity.setAppId(appId);
        return this.dao.countByEntity(entity);
    }

    @Override
    public void testCreateToken(String appId, String appSecret, String baseUrl) {
        String accessToken = FeishuApiUtils.createAccessToken(appId, appSecret);
        logger.info("accessToken: " + accessToken);
        if (StringUtils.isBlank(accessToken)) {
            throw new RuntimeException("获取access_token失败");
        }
    }

    @Override
    @Transactional
    public void syncOrg(FeishuConfigVo feishuConfigVo) {
        this.save(feishuConfigVo);
    }

    @Override
    public FeishuConfigEntity getByAppId(String appId) {
        FeishuConfigEntity feishuConfigEntity = new FeishuConfigEntity();
        feishuConfigEntity.setAppId(appId);
        List<FeishuConfigEntity> list = this.dao.listByEntity(feishuConfigEntity);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    private FeishuConfigEntity getConfigEntity() {
        String tenant = SpringSecurityUtils.getCurrentTenantId();
        String system = RequestSystemContextPathResolver.system();
        Assert.notNull(tenant, "租户不能为空");
        Assert.notNull(system, "系统不能为空");
        Map<String, Object> params = new HashMap<>();
        params.put("tenant", tenant);
        params.put("system", system);
        String hql = "from FeishuConfigEntity where tenant =:tenant and system =:system";
        List<FeishuConfigEntity> list = this.listByHQL(hql, params);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

}
