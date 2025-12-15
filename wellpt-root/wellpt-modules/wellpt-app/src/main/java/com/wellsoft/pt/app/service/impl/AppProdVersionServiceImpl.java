package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.app.dao.AppProdVersionDao;
import com.wellsoft.pt.app.dao.impl.AppProdModuleDaoImpl;
import com.wellsoft.pt.app.dao.impl.AppProdRelaPageDaoImpl;
import com.wellsoft.pt.app.dao.impl.AppProdVersionLogDaoImpl;
import com.wellsoft.pt.app.dao.impl.AppProdVersionLoginDaoImpl;
import com.wellsoft.pt.app.dto.AppProdVersionDto;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月31日   chenq	 Create
 * </pre>
 */
@Service
public class AppProdVersionServiceImpl extends AbstractJpaServiceImpl<AppProdVersionEntity, AppProdVersionDao, Long> implements AppProdVersionService {

    @Autowired
    AppProductService appProductService;

    @Autowired
    AppProdModuleDaoImpl appProdModuleDao;

    @Autowired
    AppModuleService appModuleService;

    @Autowired
    AppProdVersionLogDaoImpl prodVersionLogDao;

    @Autowired
    AppProdVersionSettingService appProdVersionSettingService;

    @Autowired
    AppProdAnonUrlService appProdAnonUrlService;

    @Autowired
    AppPageDefinitionService appPageDefinitionService;

    @Autowired
    AppProdRelaPageDaoImpl appProdRelaPageDao;
    @Autowired
    AppSystemLoginPageDefinitionService appSystemLoginPageDefinitionService;

    @Autowired
    AppProdVersionLoginDaoImpl appProdVersionLoginDao;

    @Autowired
    AppProdVersionParamService appProdVersionParamService;

    @Autowired
    SecurityApiFacade securityApiFacade;


    @Override
    @Transactional
    public AppProdVersionEntity saveAsNewVersion(Long fromUuid, String version, String prodId) {
        AppProdVersionEntity entity = new AppProdVersionEntity();
        entity.setProdId(prodId);
        entity.setVersion(version);
        entity.setVersionId("PROD_VER_" + SnowFlake.getId());
        entity.setStatus(AppProdVersionEntity.Status.BUILDING);
        if (fromUuid != null) {
            save(entity);
            AppProdVersionEntity fromVersion = getOne(fromUuid);
            // 保存版本下的模块
            List<AppModule> modules = this.getVersionModulesByVersionUuid(fromVersion.getUuid());
            if (CollectionUtils.isNotEmpty(modules)) {
                List<AppProdModuleEntity> prodModuleList = Lists.newArrayListWithCapacity(modules.size());
                for (AppModule am : modules) {
                    AppProdModuleEntity m = new AppProdModuleEntity();
                    m.setProdVersionUuid(entity.getUuid());
                    m.setModuleId(am.getId());
                    m.setModuleUuid(am.getUuid());
                    prodModuleList.add(m);
                }
                appProdModuleDao.saveAll(prodModuleList);
            }

            // 保存版本的设置
            AppProdVersionSettingEntity settingEntity = appProdVersionSettingService.getByProdVersionUuid(fromUuid);
            if (settingEntity != null) {
                AppProdVersionSettingEntity newSettingEntity = new AppProdVersionSettingEntity();
                BeanUtils.copyProperties(settingEntity, newSettingEntity, newSettingEntity.BASE_FIELDS);
                newSettingEntity.setProdVersionUuid(entity.getUuid());
                newSettingEntity.setProdVersionId(entity.getVersionId());
//                if (StringUtils.isNotBlank(settingEntity.getPcIndexUrl()) && settingEntity.getPcIndexUrl().startsWith("/webapp/" + entity.getProdId() + "/" + fromUuid + "/")) {
//                    // 更新版本默认访问地址为当前新版本下
//                    newSettingEntity.setPcIndexUrl(settingEntity.getPcIndexUrl().replaceAll(fromUuid.toString(), newSettingEntity.getProdVersionUuid().toString()));
//                }
                appProdVersionSettingService.saveAppProdVersionSetting(newSettingEntity);
            }

            // 保存版本的匿名地址
            List<AppProdAnonUrlEntity> urls = appProdAnonUrlService.listByProdVersionUuid(fromUuid);
            if (CollectionUtils.isNotEmpty(urls)) {
                List<AppProdAnonUrlEntity> anonUrl = Lists.newArrayListWithCapacity(urls.size());
                for (AppProdAnonUrlEntity u : urls) {
                    AppProdAnonUrlEntity url = new AppProdAnonUrlEntity();
                    BeanUtils.copyProperties(u, url, url.BASE_FIELDS);
                    url.setProdVersionId(entity.getVersionId());
                    url.setProdVersionUuid(entity.getUuid());
                    anonUrl.add(url);
                }
                appProdAnonUrlService.saveAll(anonUrl);
            }

            // 保存页面与版本关系
            List<AppProdRelaPageEntity> pages = appProdRelaPageDao.listByFieldEqValue("prodVersionUuid", fromUuid);
            if (CollectionUtils.isNotEmpty(pages)) {
                List<AppProdRelaPageEntity> relas = Lists.newArrayListWithCapacity(pages.size());
                for (AppProdRelaPageEntity p : pages) {
                    AppProdRelaPageEntity page = new AppProdRelaPageEntity();
                    page.setPageId(p.getPageId());
                    page.setPageUuid(p.getPageUuid());
                    page.setProdVersionUuid(entity.getUuid());
                    page.setProdId(entity.getProdId());
                    relas.add(page);
                }
                appProdRelaPageDao.saveAll(relas);
            }

            // 保存登录页设置
            List<AppSystemLoginPageDefinitionEntity> logins = appSystemLoginPageDefinitionService.getAllProdVersionLoginPage(fromUuid);
            if (CollectionUtils.isNotEmpty(logins)) {
                List<AppSystemLoginPageDefinitionEntity> newLogins = Lists.newArrayListWithCapacity(logins.size());
                for (AppSystemLoginPageDefinitionEntity l : logins) {
                    AppSystemLoginPageDefinitionEntity e = new AppSystemLoginPageDefinitionEntity();
                    BeanUtils.copyProperties(l, e, AppSystemLoginPageDefinitionEntity.BASE_FIELDS);
                    e.setProdVersionUuid(entity.getUuid());
                    newLogins.add(e);
                }
                appSystemLoginPageDefinitionService.saveAll(newLogins);
            }

            List<AppProdVersionParamEntity> params = appProdVersionParamService.getAllParamsDetail(fromUuid);
            if (CollectionUtils.isNotEmpty(params)) {
                List<AppProdVersionParamEntity> newParams = Lists.newArrayListWithCapacity(pages.size());
                for (AppProdVersionParamEntity p : params) {
                    AppProdVersionParamEntity newParam = new AppProdVersionParamEntity();
                    BeanUtils.copyProperties(p, newParam, AppProdVersionParamEntity.BASE_FIELDS);
                    newParam.setProdVersionUuid(entity.getUuid());
                    newParams.add(newParam);
                }
                appProdVersionParamService.saveAll(newParams);
            }

            // FIXME: 拷贝角色、以及角色关联的资源，角色ID重新生成 ???
        } else {
            save(entity);
        }
        return entity;
    }


    @Override
    @Transactional
    public void updateStatus(Long uuid, AppProdVersionEntity.Status status) {
        AppProdVersionEntity entity = getOne(uuid);
        if (entity != null) {
            entity.setStatus(status);
            if (AppProdVersionEntity.Status.PUBLISHED.equals(status)) {
                entity.setPublishTime(new Date());
            }
            save(entity);
        }
    }

    @Override
    public List<AppProdVersionEntity> queryLatestPubVersions(List<String> prodIds) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("prodId", prodIds);
        param.put("status", AppProdVersionEntity.Status.PUBLISHED);
        return this.dao.listByHQL("from AppProdVersionEntity a where a.status=:status" +
                " and  a.prodId " + (prodIds.size() == 1 ? "= :prodId " : "in :prodId ") + "and  a.publishTime = (" +
                "select max(v.publishTime) from  AppProdVersionEntity v where v.prodId = a.prodId and v.status =:status )", param);

    }

    @Override
    @Transactional
    public Long saveVersion(AppProdVersionDto versionDto) {
        if (versionDto.getUuid() != null) {
            Map<String, Object> param = Maps.newHashMap();
            param.put("uuid", versionDto.getUuid());
            AppProdVersionEntity version = dao.getOne(versionDto.getUuid());
            if (version != null) {
                // 关联模块
                this.dao.deleteBySQL("DELETE FROM APP_PROD_MODULE WHERE PROD_VERSION_UUID=:uuid", param);
                if (CollectionUtils.isNotEmpty(versionDto.getModules())) {
                    List<AppProdModuleEntity> modules = Lists.newArrayListWithCapacity(versionDto.getModules().size());
                    for (AppProdModuleEntity m : versionDto.getModules()) {
                        AppProdModuleEntity module = new AppProdModuleEntity();
                        module.setModuleId(m.getModuleId());
                        module.setModuleUuid(m.getModuleUuid());
                        module.setModuleName(m.getModuleName());
                        module.setProdVersionUuid(versionDto.getUuid());
                        modules.add(module);
                    }
                    appProdModuleDao.saveAll(modules);
                }

                // 关联角色

                // 关联权限

                // 关联其他配置
                if (versionDto.getSetting() != null) {
                    versionDto.getSetting().setProdVersionUuid(version.getUuid());
                    versionDto.getSetting().setProdVersionId(version.getVersionId());
                    appProdVersionSettingService.saveAppProdVersionSetting(versionDto.getSetting());
                }
                // 保存匿名地址
                if (versionDto.getAnonUrls() != null) {
                    appProdAnonUrlService.saveProdVersionAnonUrls(version.getUuid(), versionDto.getAnonUrls());
                }

                return version.getUuid();
            }

        }

        return null;
    }

    @Override
    public List<AppModule> getVersionModulesByVersionUuid(Long prodVersionUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("prodVersionUuid", prodVersionUuid);
        return appModuleService.getDao().listByNameHQLQuery("queryProdVersionModules", param);
    }


    @Override
    public AppProdVersionEntity queryLatestPubVersion(String prodId) {
        List<AppProdVersionEntity> list = queryLatestPubVersions(Lists.newArrayList(prodId));
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<AppProdVersionEntity> getAllByProdId(String prodId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("prodId", prodId);
        return dao.listByHQL("from AppProdVersionEntity where prodId=:prodId order by createTime desc", param);
    }

    @Override
    public List<Role> getVersionRolesByVersionId(String prodVersionId) {
        return null;
    }

    @Override
    public List<Privilege> getVersionPrivilegesByVersionId(String prodVersionId) {
        return null;
    }

    @Override
    @Transactional
    public void deleteByProdId(String prodId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("prodId", prodId);
        // 删除产品版本管理的其他数据
        appProdModuleDao.deleteByHQL("delete from AppProdModuleEntity m where exists ( select 1 from AppProdVersionEntity v where v.prodId=:prodId and v.uuid = m.prodVersionUuid)", param);
        dao.deleteByHQL("delete from AppProdVersionEntity where prodId=:prodId", param);

        List<AppProdRelaPageEntity> pages = appProdRelaPageDao.listByFieldEqValue("prodId", prodId);
        if (CollectionUtils.isNotEmpty(pages)) {
            Set<String> deleted = Sets.newHashSet();
            for (AppProdRelaPageEntity p : pages) {
                // 删除页面
                if (deleted.add(p.getPageId())) {
                    appPageDefinitionService.deleteById(p.getPageId());
                }
            }
        }
        // 删除产品版本下的页面关系
        dao.deleteByHQL("delete from AppProdRelaPageEntity where prodId=:prodId", param);
        // 删除产品版本下的页面关系
        appProdVersionLoginDao.deleteByHQL("delete from AppProdVersionLoginEntity where prodId=:prodId", param);
    }

    @Override
    @Transactional
    public void deleteVersion(Long uuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("prodVersionUuid", uuid);
        AppProdVersionEntity version = getOne(uuid);
        param.put("prodVersionId", version.getVersionId());
        // 删除产品版本管理的其他数据
        appProdModuleDao.deleteByHQL("delete from AppProdModuleEntity m where exists ( select 1 from AppProdVersionEntity v where v.uuid=:prodVersionUuid and v.uuid = m.prodVersionUuid)", param);
        dao.deleteByHQL("delete from AppProdVersionEntity where uuid=:prodVersionUuid", param);
        dao.deleteByHQL("delete from AppProductVersionLogEntity where prodVersionUuid=:prodVersionUuid", param);
        dao.deleteByHQL("delete from AppProdAnonUrlEntity where prodVersionUuid=:prodVersionUuid", param);
        dao.deleteByHQL("delete from AppProdVersionSettingEntity where prodVersionUuid=:prodVersionUuid", param);
        dao.deleteByHQL("delete from AppProdRelaPageEntity where prodVersionUuid=:prodVersionUuid", param);
        appProdVersionLoginDao.deleteByHQL("delete from AppProdVersionLoginEntity where prodVersionUuid=:prodVersionUuid", param);

    }

    @Override
    @Transactional
    public Long saveProdVersionLog(AppProdVersionDto versionDto) {
        AppProductVersionLogEntity log = prodVersionLogDao.getVersionLogByVersionUuid(versionDto.getUuid());
        if (log != null) {
            log.setDetail(versionDto.getLog().getDetail());
        } else {
            log = new AppProductVersionLogEntity();
            log.setDetail(versionDto.getLog().getDetail());
            log.setProdVersionUuid(versionDto.getUuid());
        }
        if (StringUtils.isNotBlank(versionDto.getVersion())) {
            AppProdVersionEntity versionEntity = getOne(versionDto.getUuid());
            if (!versionEntity.getVersion().equals(versionDto.getVersion())) {
                versionEntity.setVersion(versionDto.getVersion());
                save(versionEntity);
            }
        }
        prodVersionLogDao.save(log);
        return log.getUuid();
    }

    @Override
    public AppProdVersionEntity queryLatestCreate(String prodId) {
        List<AppProdVersionEntity> prodVersionEntities = this.queryLatestCreateVersions(Lists.newArrayList(prodId));
        return CollectionUtils.isNotEmpty(prodVersionEntities) ? prodVersionEntities.get(0) : null;
    }

    @Override
    public List<AppProdVersionEntity> queryLatestCreateVersions(List<String> prodIds) {
        if (CollectionUtils.isNotEmpty(prodIds)) {
            Map<String, Object> param = Maps.newHashMap();
            param.put("prodId", prodIds);
            return this.dao.listByHQL("from AppProdVersionEntity a where " +
                    " a.prodId  " + (prodIds.size() == 1 ? "=" : "in") + " :prodId and a.createTime = (" +
                    "select max(v.createTime) from  AppProdVersionEntity v where v.prodId = a.prodId )", param);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public AppProductVersionLogEntity getProdVersionLog(Long prodVersionUuid) {
        return prodVersionLogDao.getVersionLogByVersionUuid(prodVersionUuid);
    }

    @Override
    public AppProdVersionEntity queryEarliestCreateVersion(String prodId) {
        if (StringUtils.isNotBlank(prodId)) {
            Map<String, Object> param = Maps.newHashMap();
            param.put("prodId", prodId);
            List<AppProdVersionEntity> list = this.dao.listByHQL("from AppProdVersionEntity a where " +
                    " a.prodId =:prodId and a.createTime = (" +
                    "select min(v.createTime) from  AppProdVersionEntity v where v.prodId = a.prodId )", param);
        }
        return null;
    }

    @Override
    public List<AppModule> getVersionModulesByVersionId(String prodVersionId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("prodVersionId", prodVersionId);
        return appModuleService.getDao().listByNameHQLQuery("queryProdVersionModules", param);
    }

    @Override
    public List<AppProdModuleEntity> getModulesByProdVersionUuid(Long prodVersionUuid) {
        return appProdModuleDao.listByFieldEqValue("prodVersionUuid", prodVersionUuid);
    }

    @Override
    public List<AppPageDefinition> getProdVersionPages(Long prodVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("prodVersionUuid", prodVersionUuid);
        return appPageDefinitionService.listByNameSQLQuery("queryProdVersionPages", params);
    }

    @Override
    @Transactional
    public void deleteProdVersionPage(String pageId, Long prodVersionUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("prodVersionUuid", prodVersionUuid);
        param.put("pageId", pageId);
        dao.deleteByHQL("delete from AppProdRelaPageEntity where prodVersionUuid=:prodVersionUuid and pageId=:pageId", param);
        AppProdRelaPageEntity page = appProdRelaPageDao.getOneByHQL("from AppProdRelaPageEntity where pageId=:pageId", param);
        if (page == null) {
            appPageDefinitionService.deleteById(pageId);
        }
    }


    @Override
    @Transactional
    public void updateProdVersionPage(Long prodVersionUuid, String pageFormUuid, String pageToUuid) {
        AppProdVersionEntity versionEntity = getOne(prodVersionUuid);
        if (versionEntity != null) {
            if (StringUtils.isNotBlank(pageFormUuid)) {
                AppProdRelaPageEntity example = new AppProdRelaPageEntity();
                example.setPageUuid(pageFormUuid);
                example.setProdVersionUuid(prodVersionUuid);
                List<AppProdRelaPageEntity> relas = appProdRelaPageDao.listByEntity(example);
                if (CollectionUtils.isNotEmpty(relas)) {
                    relas.get(0).setPageUuid(pageToUuid);
                    appProdRelaPageDao.save(relas.get(0));
                }
            } else {
                AppProdRelaPageEntity page = new AppProdRelaPageEntity();
                page.setProdId(versionEntity.getProdId());
                page.setProdVersionUuid(prodVersionUuid);
                page.setPageUuid(pageToUuid);
                page.setPageId(appPageDefinitionService.getPageIdByUuid(pageToUuid));
                appProdRelaPageDao.save(page);
            }
        }

    }

    @Override
    public String getProdVersionPageUuid(Long prodVersionUuid, String pageId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("prodVersionUuid", prodVersionUuid);
        param.put("pageId", pageId);
        return appProdRelaPageDao.getCharSequenceByHQL("select pageUuid from AppProdRelaPageEntity where prodVersionUuid=:prodVersionUuid and pageId=:pageId", param);
    }

    @Override
    @Transactional
    public Long saveVersionLoginDef(AppProdVersionLoginEntity data) {
        AppProdVersionLoginEntity login = data.getUuid() != null ? appProdVersionLoginDao.getOne(data.getUuid()) : new AppProdVersionLoginEntity();
        if (data.getUuid() != null && login == null) {

        }
        if (login.getUuid() == null) {
            login.setIsDefault(data.getIsDefault());
            login.setIsPc(data.getIsPc());
            login.setProdVersionUuid(data.getProdVersionUuid());
            if (login.getProdVersionUuid() != null) {
                AppProdVersionEntity versionEntity = getOne(login.getProdVersionUuid());
                if (versionEntity != null) {
                    login.setProdId(versionEntity.getProdId());
                    login.setProdVersionId(versionEntity.getVersionId());
                    login.setProdVersionUuid(login.getProdVersionUuid());
                    AppProdVersionLoginEntity existDefault = getDefaultLoginDef(versionEntity.getUuid(), login.getIsPc());
                    if (existDefault == null) {
                        login.setIsDefault(true);
                    }
                }
            }
        }
        if (data.getRemark() != null) {
            login.setRemark(data.getRemark());
        }
        if (data.getName() != null) {
            login.setName(data.getName());
        }
        if (data.getTitle() != null) {
            login.setTitle(data.getTitle());
        }
        if (data.getDefJson() != null) {
            login.setDefJson(data.getDefJson());
        }
        appProdVersionLoginDao.save(login);
        return login.getUuid();
    }

    @Override
    @Transactional
    public void deleteVersionLoginDef(List<Long> uuids) {
        appProdVersionLoginDao.deleteByUuids(uuids);
    }

    @Override
    public List<AppProdVersionLoginEntity> queryProdVersionLoginByProdVersionUuid(Long prodVersionUuid) {
        return appProdVersionLoginDao.listByFieldEqValue("prodVersionUuid", prodVersionUuid);
    }

    @Override
    public AppProdVersionLoginEntity getDefaultLoginDef(Long prodVersionUuid, Boolean isPc) {
        AppProdVersionLoginEntity example = new AppProdVersionLoginEntity();
        example.setProdVersionUuid(prodVersionUuid);
        example.setIsPc(isPc);
        example.setIsDefault(true);
        List<AppProdVersionLoginEntity> list = appProdVersionLoginDao.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public AppProdVersionLoginEntity getLoginDef(Long loginUuid) {
        return appProdVersionLoginDao.getOne(loginUuid);
    }


    @Override
    public AppProdVersionEntity getByVersionAndProdId(String version, String prodId) {
        AppProdVersionEntity example = new AppProdVersionEntity();
        example.setVersion(version);
        example.setProdId(prodId);
        List<AppProdVersionEntity> versionEntities = dao.listByEntity(example);
        return CollectionUtils.isNotEmpty(versionEntities) ? versionEntities.get(0) : null;
    }


    @Override
    public String getProdVersionPageTheme(Long prodVersionUuid, String pageId) {
        AppProdRelaPageEntity example = new AppProdRelaPageEntity();
        example.setProdVersionUuid(prodVersionUuid);
        example.setPageId(pageId);
        List<AppProdRelaPageEntity> pages = appProdRelaPageDao.listByEntity(example);
        return CollectionUtils.isNotEmpty(pages) ? pages.get(0).getTheme() : null;
    }

    @Override
    @Transactional
    public void updateProdVersionRelaPageTheme(Long prodVersionUuid, List<AppProdRelaPageEntity> pages) {
        appProdRelaPageDao.updateProdVersionRelaPageTheme(prodVersionUuid, pages);
    }

    @Override
    public List<AppProdRelaPageEntity> getProdVersionRelaPage(Long prodVersionUuid) {
        return appProdRelaPageDao.listByFieldEqValue("prodVersionUuid", prodVersionUuid);
    }

    @Override
    public List<AppProdVersionEntity> listProdVersionByIds(List<String> id) {
        List<AppProdVersionEntity> versionEntities = dao.listByFieldInValues("versionId", id);
        if (CollectionUtils.isNotEmpty(versionEntities)) {
            for (AppProdVersionEntity entity : versionEntities) {
                AppProduct appProduct = appProductService.getProductByProdVersionUuid(entity.getUuid());
                if (appProduct != null) {
                    entity.setProdName(appProduct.getName());
                }
            }
        }
        return versionEntities;
    }

    @Override
    public AppProdVersionEntity getProdVersionByVersionId(String prodVersionId) {
        return dao.getOneByFieldEq("versionId", prodVersionId);
    }

    @Override
    @Transactional
    public void setDefaultLoginDef(Long uuid) {
        AppProdVersionLoginEntity login = appProdVersionLoginDao.getOne(uuid);
        if (login != null) {
            Map<String, Object> param = Maps.newHashMap();
            param.put("uuid", uuid);
            param.put("prodVersionUuid", login.getProdVersionUuid());
            param.put("isPc", login.getIsPc());
            appProdVersionLoginDao.updateByHQL("update AppProdVersionLoginEntity set  isDefault = true " +
                    "where prodVersionUuid=:prodVersionUuid and  uuid=:uuid", param);
            appProdVersionLoginDao.updateByHQL("update AppProdVersionLoginEntity set  isDefault = false where " +
                    " prodVersionUuid=:prodVersionUuid and  uuid <> :uuid and isPc = :isPc", param);
        }
    }

    @Override
    public boolean isProdVersionPage(String pageUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("pageUuid", pageUuid);
        return dao.countByHQL("select count(1) from AppProdRelaPageEntity where pageUuid=:pageUuid", param) > 0;
    }

    @Override
    public List<String> getProdVersionAuthenticatePageUuids(Long prodVersionUuid, String prodId) {
        if (prodVersionUuid == null && StringUtils.isNotBlank(prodId)) {
            AppProdVersionEntity appProdVersionEntity = this.queryLatestPubVersion(prodId);
            prodVersionUuid = appProdVersionEntity.getUuid();
        }
        List<AppPageDefinition> pageDefinitions = this.getProdVersionPages(prodVersionUuid);
        AppProdVersionSettingEntity settingEntity = appProdVersionSettingService.getByProdVersionUuid(prodVersionUuid);
        String indexPageId = null;
        if (settingEntity != null && StringUtils.isNotBlank(settingEntity.getPcIndexUrl()) && settingEntity.getPcIndexUrl().startsWith("/")) {
            String[] parts = settingEntity.getPcIndexUrl().split("/");
            indexPageId = parts[parts.length - 1];
        }
        if (CollectionUtils.isNotEmpty(pageDefinitions)) {
            List<String> uuids = Lists.newArrayList();

            for (AppPageDefinition page : pageDefinitions) {
                if (true ||
                        //FIXME: 待新版组织用户登录集成后放开
                        securityApiFacade.isGranted(page.getId(), AppFunctionType.AppPageDefinition)) {
                    if (page.getId().equals(indexPageId)) {
                        uuids.add(0, page.getUuid());
                    } else {
                        uuids.add(page.getUuid());
                    }
                }
            }

            return uuids;
        }
        return null;
    }

    @Override
    public AppProdVersionDto getProdVersionDetails(Long prodVersionUuid) {
        AppProdVersionDto dto = new AppProdVersionDto();
        AppProdVersionEntity entity = getOne(prodVersionUuid);
        BeanUtils.copyProperties(entity, dto);
        dto.setProduct(appProductService.getProductDetailById(entity.getProdId()));
        return dto;
    }

    @Override
    public List<AppPageDefinition> getPageInfosIgnoreDefinitionUnderProdVersion(Long prodVersionUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("prodVersionUuid", prodVersionUuid);
        return this.appPageDefinitionService.listBySQL(" select id, uuid ,name ,title from app_page_definition a where exists ( select 1 from " +
                "app_prod_rela_page r where r.page_uuid = a.uuid and r.prod_version_uuid =:prodVersionUuid" +
                ")", param);
    }

    @Override
    @Transactional
    public void removeUnusedProdModuleNestedRole(Long prodVersionUuid, List<String> moduleIds) {
        Map<String, Object> params = Maps.newHashMap();
        AppProdVersionEntity versionEntity = getOne(prodVersionUuid);
        params.put("prodVersionUuid", versionEntity.getUuid());
        params.put("prodVersionId", versionEntity.getVersionId());
        params.put("moduleIds", moduleIds);
        int row = this.dao.deleteBySQL("delete from audit_role_nested_role n where exists (\n" +
                "    select 1 from audit_role r,audit_nested_role nr ,audit_role mr where r.app_id = :prodVersionId and r.uuid = n.role_uuid\n" +
                "    and nr.uuid = n.nested_role_uuid and mr.uuid = nr.role_uuid and  mr.app_id in (:moduleIds) " +
                ")\n", params);
        this.dao.deleteBySQL("delete from audit_nested_role nr where not exists ( select 1 from audit_role_nested_role n where nr.uuid = n.nested_role_uuid) ", params);
    }


}
