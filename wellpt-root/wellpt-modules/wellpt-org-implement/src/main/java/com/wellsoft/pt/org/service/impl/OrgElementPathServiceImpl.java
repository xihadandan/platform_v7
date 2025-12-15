package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgElementPathDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementI18nEntity;
import com.wellsoft.pt.org.entity.OrgElementPathEntity;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.org.service.OrgElementI18nService;
import com.wellsoft.pt.org.service.OrgElementPathService;
import com.wellsoft.pt.org.service.OrgElementService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月23日   chenq	 Create
 * </pre>
 */
@Service
public class OrgElementPathServiceImpl extends AbstractJpaServiceImpl<OrgElementPathEntity, OrgElementPathDaoImpl, Long> implements OrgElementPathService {

    @Resource
    OrgElementService orgElementService;

    @Resource
    OrgElementI18nService orgElementI18nService;


    @Override
    public OrgElementPathEntity getByOrgEleUuid(Long orgElementUuid) {
        return this.dao.getOneByFieldEq("orgElementUuid", orgElementUuid);
    }

    @Override
    public OrgElementPathEntity getByOrgEleId(String orgElementId) {
        String hql = "from OrgElementPathEntity where orgElementId = :orgElementId and orgVersionUuid in (select uuid from OrgVersionEntity where state = :orgVersionState)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementId", orgElementId);
        params.put("orgVersionState", OrgVersionEntity.State.PUBLISHED);
        List<OrgElementPathEntity> pathEntities = this.dao.listByHQL(hql, params);
        return CollectionUtils.isNotEmpty(pathEntities) ? pathEntities.get(0) : null;
    }

    @Override
    public List<OrgElementPathEntity> listByOrgEleIds(List<String> orgElementIds) {
        String hql = "from OrgElementPathEntity where orgElementId in :orgElementIds and orgVersionUuid in (select uuid from OrgVersionEntity where state = :orgVersionState)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementIds", orgElementIds);
        params.put("orgVersionState", OrgVersionEntity.State.PUBLISHED);
        List<OrgElementPathEntity> pathEntities = this.dao.listByHQL(hql, params);
        return pathEntities;
    }

    @Override
    @Transactional
    public void deleteByIdsAndOrgVersionUuid(List<String> ids, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        params.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete OrgElementPathEntity where orgElementId in (:ids) and orgVersionUuid = :orgVersionUuid", params);
    }

    @Override
    public List<OrgElementPathEntity> listByOrgVersionUuid(Long orgVersionUuid) {
        return dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    @Transactional
    public void deleteByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete from OrgElementPathEntity where orgVersionUuid=:orgVersionUuid", params);
    }


    @Override
    public List<OrgElementPathEntity> listByOrgElementIdsAndOrgVersionUuid(List<String> orgElementIds, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("orgElementIds", orgElementIds);
        return dao.listByHQL("from OrgElementPathEntity where  orgElementId in (:orgElementIds) "
                + (orgVersionUuid != null ? " and orgVersionUuid=:orgVersionUuid" :
                ""), params);
    }

    @Override
    public OrgElementPathEntity getByIdPathAndOrgVersionUuid(String idPath, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("idPath", idPath);
        params.put("orgVersionUuid", orgVersionUuid);
        List<OrgElementPathEntity> pathEntities = dao.listByHQL("from OrgElementPathEntity where  idPath = :idPath "
                + (orgVersionUuid != null ? " and orgVersionUuid=:orgVersionUuid" :
                ""), params);
        return CollectionUtils.isNotEmpty(pathEntities) ? pathEntities.get(0) : null;
    }


    @Override
    public OrgElementPathEntity getByOrgElementIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid) {
        List<OrgElementPathEntity> pathEntities = this.listByOrgElementIdsAndOrgVersionUuid(Lists.newArrayList(orgElementId), orgVersionUuid);
        return CollectionUtils.isNotEmpty(pathEntities) ? pathEntities.get(0) : null;
    }

    @Override
    public OrgElementPathEntity getByOrgElementIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid, String locale) {
        OrgElementPathEntity pathEntity = getByOrgElementIdAndOrgVersionUuid(orgElementId, orgVersionUuid);
        if (pathEntity != null) {
            String[] ids = pathEntity.getIdPath().split(Separator.SLASH.getValue());
            List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.getOrgElementI18ns(Sets.newHashSet(ids), "name", locale);
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                String[] paths = pathEntity.getCnPath().split(Separator.SLASH.getValue());
                for (OrgElementI18nEntity i18n : i18nEntities) {
                    int i = ArrayUtils.indexOf(ids, i18n.getDataId());
                    if (i > 0) {
                        paths[i] = i18n.getContent();
                    }
                }
                pathEntity.setCnPath(StringUtils.join(paths, Separator.SLASH.getValue()));

            }
            return pathEntity;
        }
        return null;
    }

    @Override
    public String getLocaleOrgElementPath(String orgElementId, Long orgVersionUuid, String locale) {
        OrgElementPathEntity pathEntity = getByOrgElementIdAndOrgVersionUuid(orgElementId, orgVersionUuid);
        if (pathEntity != null) {
            return this.resolveOrgElementPathI18n(pathEntity, locale);
        }
        return null;
    }


    @Override
    public List<OrgElementPathEntity> getOrgUserElementPaths(String userId, List<Long> orgVersionUuids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("orgVersionUuid", orgVersionUuids);
        return this.dao.listByHQL("from OrgElementPathEntity o where orgVersionUuid in :orgVersionUuid and " +
                " exists ( select 1 from OrgUserEntity u where u.userId=:userId and u.orgVersionUuid =o.orgVersionUuid )", params);
    }

    @Override
    public List<OrgElementPathEntity> getOrgElementPathLikeSuffixPath(String suffixPath, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("suffixPath", "%" + suffixPath);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.listByHQL("from OrgElementPathEntity o where o.cnPath like :suffixPath and  orgVersionUuid = :orgVersionUuid", params);
    }

    @Override
    public String getLocaleOrgElementPath(Long orgElementUuid, String locale) {
        OrgElementPathEntity pathEntity = this.dao.getOneByFieldEq("orgElementUuid", orgElementUuid);
        return this.resolveOrgElementPathI18n(pathEntity, locale);
    }

    private String resolveOrgElementPathI18n(OrgElementPathEntity pathEntity, String locale) {
        if (pathEntity != null) {
            String[] ids = pathEntity.getIdPath().split(Separator.SLASH.getValue());
            Map<String, Long> idUuidMap = orgElementService.getUuidsByIds(ids, pathEntity.getOrgVersionUuid());
            List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.listOrgElementI18ns(Sets.newHashSet(idUuidMap.values()), "name", locale);
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                String[] paths = pathEntity.getCnPath().split(Separator.SLASH.getValue());
                for (OrgElementI18nEntity i18n : i18nEntities) {
                    int i = ArrayUtils.indexOf(ids, i18n.getDataId());
                    if (i > -1) {
                        paths[i] = i18n.getContent();
                    }
                }
                return StringUtils.join(paths, Separator.SLASH.getValue());
            }
            return pathEntity.getCnPath();
        }
        return null;
    }

    @Override
    public Map<Long, String> getLocaleOrgElementPaths(List<Long> orgElementUuids, String locale) {
        List<OrgElementPathEntity> pathEntities = this.dao.listByFieldInValues("orgElementUuid", orgElementUuids);
        Map<Long, String> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(pathEntities)) {
            Set<String> eleIds = Sets.newHashSet();
            for (OrgElementPathEntity p : pathEntities) {
                eleIds.addAll(Lists.newArrayList(p.getIdPath().split(Separator.SLASH.getValue())));
            }
            Map<String, Long> idUuidMap = orgElementService.getUuidsByIds(eleIds.toArray(new String[]{}), pathEntities.get(0).getOrgVersionUuid());
            List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.listOrgElementI18ns(Sets.newHashSet(idUuidMap.values()), "name", locale);
            Map<String, OrgElementI18nEntity> i18nEntityMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                for (OrgElementI18nEntity i18n : i18nEntities) {
                    i18nEntityMap.put(i18n.getDataId(), i18n);
                }
            }
            for (OrgElementPathEntity pathEntity : pathEntities) {
                String[] paths = pathEntity.getCnPath().split(Separator.SLASH.getValue());
                String[] ids = pathEntity.getIdPath().split(Separator.SLASH.getValue());
                for (String id : ids) {
                    if (i18nEntityMap.containsKey(id)) {
                        int i = ArrayUtils.indexOf(ids, id);
                        if (i > -1) {
                            paths[i] = i18nEntityMap.get(id).getContent();
                        }
                    }
                }
                map.put(pathEntity.getOrgElementUuid(), StringUtils.join(paths, Separator.SLASH.getValue()));
            }
        }
        return map;
    }


}
