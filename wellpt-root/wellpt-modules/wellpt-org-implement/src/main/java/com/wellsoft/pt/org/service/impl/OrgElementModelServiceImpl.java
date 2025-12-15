package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.OrgElementModelDefDao;
import com.wellsoft.pt.org.dao.impl.OrgElementModelDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementModelDefEntity;
import com.wellsoft.pt.org.entity.OrgElementModelEntity;
import com.wellsoft.pt.org.entity.OrgSettingEntity;
import com.wellsoft.pt.org.service.OrgElementModelService;
import com.wellsoft.pt.org.service.OrgElementService;
import com.wellsoft.pt.org.service.OrgSettingService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Service
public class OrgElementModelServiceImpl extends AbstractJpaServiceImpl<OrgElementModelEntity, OrgElementModelDaoImpl, Long> implements OrgElementModelService {


    @Resource
    OrgElementModelDefDao orgElementModelDefDao;

    @Resource
    OrgSettingService orgSettingService;

    @Resource
    OrgElementService orgElementService;

    @Override
    @Transactional
    public boolean enableOrgElementModel(Long uuid, boolean enable) {
        OrgElementModelEntity entity = this.getOne(uuid);
        if (entity != null) {
            entity.setEnable(enable);
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean deleteOrgElementModel(Long uuid) {
        OrgElementModelEntity entity = this.getOne(uuid);
        if (entity != null) {
            if (orgElementService.existOrgElementType(entity.getId(), RequestSystemContextPathResolver.system())) {
                return false;
            }
            this.orgElementModelDefDao.deleteByIdAndSystem(entity.getId(), entity.getSystem());
            this.delete(entity);
            // 删除模型参数
            this.orgSettingService.deleteByAttrKeyAndSystem(entity.getId().toUpperCase() + "_MOUNT_SET", entity.getSystem());
        }
        return true;
    }

    @Override
    public List<OrgElementModelEntity> listOrgElementModels(String tenant, String system) {
        OrgElementModelEntity example = new OrgElementModelEntity();
        example.setTenant(tenant);
        example.setSystem(system);
        return this.dao.listByEntityAndPage(example, null, "uuid,createTime asc");
//        return this.dao.listByEntity(example);
    }

    @Override
    public List<OrgElementModelEntity> listOrgElementModelsBySystemIsNull() {
        return this.dao.listByHQL("from OrgElementModelEntity where system is null order by uuid,createTime asc", null);
    }

    @Override
    @Transactional
    public Long saveOrgElementModel(OrgElementModelEntity orgElementModel) {
        OrgElementModelEntity entity = orgElementModel;
        OrgElementModelDefEntity def = null;
        if (orgElementModel.getUuid() != null) {
            entity = this.getOne(orgElementModel.getUuid());
            BeanUtils.copyProperties(orgElementModel, entity, entity.BASE_FIELDS);
            if (StringUtils.isNotBlank(orgElementModel.getDefJson())) {
                def = this.orgElementModelDefDao.getByIdAndSystem(orgElementModel.getId(), orgElementModel.getSystem());
                if (def != null) {
                    def.setDefJson(orgElementModel.getDefJson());
                } else {
                    def = new OrgElementModelDefEntity();
                    def.setDefJson(orgElementModel.getDefJson());
                    def.setOrgElementModelId(orgElementModel.getId());
                    def.setSystem(orgElementModel.getSystem());
                    def.setTenant(orgElementModel.getTenant());
                    this.orgElementModelDefDao.save(def);
                }
            }
        } else {
            if (!ArrayUtils.contains(OrgElementModelEntity.DEFAULT_ID, entity.getId())) {
                // 新增挂载校验属性
                OrgSettingEntity settingEntity = new OrgSettingEntity();
                settingEntity.setSystem(entity.getSystem());
                settingEntity.setTenant(entity.getTenant());
                settingEntity.setAttrKey(entity.getId().toUpperCase() + "_MOUNT_SET");
                orgSettingService.save(settingEntity);
            }
        }
        this.save(entity);
        return entity.getUuid();
    }

    @Override
    public OrgElementModelEntity getOrgElementModelByIdAndSystem(String id, String system) {
        OrgElementModelEntity example = new OrgElementModelEntity();
        example.setId(id);
        example.setSystem(system);
        if (StringUtils.isNotBlank(system)) {
            example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        List<OrgElementModelEntity> list = this.dao.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public String getOrgElementModelDefJson(String id, String system) {
        OrgElementModelDefEntity def = orgElementModelDefDao.getByIdAndSystem(id, system);
        return def != null ? def.getDefJson() : null;
    }

    @Override
    @Transactional
    public void createOrgElementModelAndSetting(String system, String tenant) {
        // 拷贝平台默认的配置
        List<OrgSettingEntity> settings = orgSettingService.listBySystemAndTenant(system, tenant);
        if (CollectionUtils.isEmpty(settings)) {
            List<OrgSettingEntity> defaultSettings = orgSettingService.listBySystemIsNull();
            if (CollectionUtils.isNotEmpty(defaultSettings)) {
                settings = Lists.newArrayListWithCapacity(defaultSettings.size());
                for (OrgSettingEntity entity : defaultSettings) {
                    OrgSettingEntity setting = new OrgSettingEntity();
                    BeanUtils.copyProperties(entity, setting, setting.BASE_FIELDS);
                    setting.setSystem(system);
                    setting.setTenant(tenant);
                    settings.add(setting);
                }
                orgSettingService.saveAll(settings);
            }

        }
        List<OrgElementModelEntity> models = listOrgElementModels(tenant, system);
        if (CollectionUtils.isEmpty(models)) {
            List<OrgElementModelEntity> defaultOrgElementModels = this.dao.listByFieldIsNull("system");
            if (CollectionUtils.isNotEmpty(defaultOrgElementModels)) {
                for (OrgElementModelEntity entity : defaultOrgElementModels) {
                    OrgElementModelEntity model = new OrgElementModelEntity();
                    BeanUtils.copyProperties(entity, model, model.BASE_FIELDS);
                    model.setSystem(system);
                    model.setTenant(tenant);
                    this.save(model);
                    OrgElementModelDefEntity defEntity = this.orgElementModelDefDao.getByIdAndNullSystem(model.getId());
                    if (defEntity != null) {
                        OrgElementModelDefEntity modelDef = new OrgElementModelDefEntity();
                        modelDef.setDefJson(defEntity.getDefJson());
                        modelDef.setOrgElementModelId(defEntity.getOrgElementModelId());
                        modelDef.setSystem(model.getSystem());
                        modelDef.setTenant(model.getTenant());
                        this.orgElementModelDefDao.save(modelDef);
                    }

                }
            }

        }
    }

    @Override
    public OrgElementModelEntity getDetailByUuid(Long uuid) {
        OrgElementModelEntity entity = getOne(uuid);
        if (entity != null) {
            entity.setDefJson(getOrgElementModelDefJson(entity.getId(), entity.getSystem()));
        }
        return entity;
    }
}
