package com.wellsoft.pt.org.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.entity.BizOrgElementEntity;
import com.wellsoft.pt.org.entity.BizOrgElementPathChainEntity;
import com.wellsoft.pt.org.entity.BizOrgElementPathEntity;
import com.wellsoft.pt.org.entity.BizOrganizationEntity;
import com.wellsoft.pt.org.service.BizOrganizationService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
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
 * 2024年11月28日   chenq	 Create
 * </pre>
 */
@Repository
public class BizOrgElementPathDaoImpl extends AbstractJpaDaoImpl<BizOrgElementPathEntity, Long> {
    @Resource
    BizOrgElementDaoImpl bizOrgElementDao;

    @Resource
    BizOrgElementPathChainDaoImpl bizOrgElementPathChainDao;

    @Resource
    BizOrganizationService bizOrganizationService;

    @Transactional
    public void updateBizOrgElementPath(BizOrgElementEntity entity) {
        BizOrgElementPathEntity pathEntity = entity.getUuid() == null ? new BizOrgElementPathEntity() : this.getOneByFieldEq("bizOrgElementId", entity.getId());
        pathEntity.setBizOrgElementId(entity.getId());
        if (entity.getParentUuid() != null) {
            BizOrgElementEntity parentEntity = bizOrgElementDao.getOneByFieldEq("uuid", entity.getParentUuid());
            if (parentEntity != null) {
                BizOrgElementPathEntity parentPath = this.getOneByFieldEq("bizOrgElementId", parentEntity.getId());
                if (parentPath != null) {
                    pathEntity.setIdPath(parentPath.getIdPath() + Separator.SLASH.getValue() + entity.getId());
                    pathEntity.setCnPath(parentPath.getCnPath() + Separator.SLASH.getValue() + entity.getName());
                    pathEntity.setPinYinPath(parentPath.getPinYinPath() + Separator.SLASH.getValue()
                            + PinyinUtil.getPinYinMulti(entity.getName(), true));
                    pathEntity.setLeaf(entity.getUuid() == null || bizOrgElementDao.getOneByFieldEq("parentUuid", entity.getUuid()) == null);
                    if (BooleanUtils.isNotTrue(entity.getIsDimension())) {
                        // 组织节点要设置直接父级的维度节点，用于同步使用或者获取该维度下的所有相关节点
                        String[] idPath = parentPath.getIdPath().split(Separator.SLASH.getValue());
                        ArrayUtils.reverse(idPath);
                        for (int i = 0, len = idPath.length; i < len; i++) {
                            if (idPath[i].startsWith(IdPrefix.BIZ_ORG_DIM.getValue())) {
                                BizOrgElementEntity parentDimEntity = bizOrgElementDao.getOneByFieldEq("id", idPath[i]);
                                if (parentDimEntity != null) {
                                    entity.setParentDimensionUuid(parentDimEntity.getUuid());
                                }
                                break;
                            }
                        }
                    }


                }
            }
        } else {
            pathEntity.setIdPath(entity.getId());
            pathEntity.setCnPath(entity.getName());
            pathEntity.setPinYinPath(PinyinUtil.getPinYinMulti(entity.getName(), true));
            pathEntity.setLeaf(true);
            entity.setParentDimensionUuid(null);
        }
        pathEntity.setSystem(StringUtils.defaultIfBlank(entity.getSystem(), RequestSystemContextPathResolver.system()));
        pathEntity.setTenant(StringUtils.defaultIfBlank(entity.getTenant(), SpringSecurityUtils.getCurrentTenantId()));
        pathEntity.setBizOrgUuid(entity.getBizOrgUuid());
        pathEntity.setModifier(SpringSecurityUtils.getCurrentUserId());
        pathEntity.setModifyTime(new Date());
        if (pathEntity.getUuid() == null) {
            pathEntity.setCreator(SpringSecurityUtils.getCurrentUserId());
            pathEntity.setCreateTime(pathEntity.getModifyTime());
        }
        this.save(pathEntity);
        boolean parentChanged = false;
        List<String> oldSubIds = Lists.newArrayList();
        if (entity.getUuid() != null) {
            BizOrgElementEntity oldOne = bizOrgElementDao.getOne(entity.getUuid());
            parentChanged = (oldOne.getParentUuid() != null && !oldOne.getParentUuid().equals(entity.getParentUuid()))
                    || (entity.getParentUuid() != null && !entity.getParentUuid().equals(oldOne.getParentUuid()));
            if (parentChanged) {
                // 删除当前节点产生的路径链数据
                Map<String, Object> param = Maps.newHashMap();
                param.put("id", oldOne.getId());
                oldSubIds = bizOrgElementPathChainDao.listCharSequenceByHQL("select subId from BizOrgElementPathChainEntity where id=:id", param);
                this.deleteByBizOrgElementId(entity.getId());
            }
        }
        List<BizOrgElementPathChainEntity> chains = Lists.newArrayList();
        if (entity.getUuid() == null || parentChanged) {
            // 根据id路径生成链
            String[] ids = pathEntity.getIdPath().split(Separator.SLASH.getValue());
            String[] before = null;
            Map<String, Object> p = Maps.newHashMap();
            List<String> idList = Lists.newArrayList(oldSubIds);
            idList.addAll(Lists.newArrayList(ids));
            p.put("ids", idList);
            List<QueryItem> items = bizOrgElementDao.listQueryItemBySQL("select id,element_type  from biz_org_element where id in (:ids)", p, null);
            Map<String, String> idTypes = Maps.newHashMap();
            for (QueryItem item : items) {
                idTypes.put(item.getString("id"), item.getString("elementType"));
            }
            if (ids.length > 1) {
                before = ArrayUtils.subarray(ids, 0, ArrayUtils.indexOf(ids, entity.getId()));
                for (int i = 0, len = before.length; i < len; i++) {
                    BizOrgElementPathChainEntity chainEntity = new BizOrgElementPathChainEntity();
                    chainEntity.setId(before[i]);
                    chainEntity.setElementType(idTypes.get(before[i]));
                    chainEntity.setSubId(entity.getId());
                    chainEntity.setSubElementType(entity.getElementType());
                    chainEntity.setLevel(len - i);
                    chainEntity.setSystem(pathEntity.getSystem());
                    chainEntity.setTenant(pathEntity.getTenant());
                    chainEntity.setBizOrgUuid(entity.getBizOrgUuid());
                    chains.add(chainEntity);
                }
            }

            if (parentChanged && CollectionUtils.isNotEmpty(oldSubIds)) {
                // 重新计算子级链以及相关子节点的路径
                List<BizOrgElementPathEntity> subPaths = listByFieldInValues("bizOrgElementId", oldSubIds);
                String[] parentIdPaths = pathEntity.getIdPath().split(Separator.SLASH.getValue());
                String[] parentCnPaths = pathEntity.getCnPath().split(Separator.SLASH.getValue());
                String[] pinyinPaths = pathEntity.getPinYinPath().split((Separator.SLASH.getValue()));
                for (BizOrgElementPathEntity subPath : subPaths) {
                    String[] idPaths = subPath.getIdPath().split(Separator.SLASH.getValue());
                    String[] cnPaths = subPath.getCnPath().split(Separator.SLASH.getValue());
                    String[] pinyPaths = subPath.getPinYinPath().split(Separator.SLASH.getValue());
                    int i = ArrayUtils.indexOf(idPaths, pathEntity.getBizOrgElementId());
                    subPath.setIdPath(StringUtils.join(ArrayUtils.addAll(parentIdPaths, ArrayUtils.subarray(idPaths, i + 1, idPaths.length)), Separator.SLASH.getValue()));
                    subPath.setCnPath(StringUtils.join(ArrayUtils.addAll(parentCnPaths, ArrayUtils.subarray(cnPaths, i + 1, idPaths.length)), Separator.SLASH.getValue()));
                    subPath.setPinYinPath(StringUtils.join(ArrayUtils.addAll(pinyinPaths, ArrayUtils.subarray(pinyPaths, i + 1, idPaths.length)), Separator.SLASH.getValue()));
                    // 更新直接父级维度
                    String[] idPath = subPath.getIdPath().split(Separator.SLASH.getValue());
                    ArrayUtils.reverse(idPath);
                    BizOrgElementEntity subElement = bizOrgElementDao.getOneByFieldEq("id", subPath.getBizOrgElementId());
                    for (int j = 0, len = idPath.length; j < len; j++) {
                        if (idPath[j].startsWith(IdPrefix.BIZ_ORG_DIM.getValue())) {
                            BizOrgElementEntity parentDimEntity = bizOrgElementDao.getOneByFieldEq("id", idPath[j]);
                            if (parentDimEntity != null) {
                                subElement.setParentDimensionUuid(parentDimEntity.getUuid());
                            }
                            break;
                        }
                    }

                    before = subPath.getIdPath().split(Separator.SLASH.getValue());
                    for (int j = 0, len = before.length; j < len; j++) {
                        BizOrgElementPathChainEntity chainEntity = new BizOrgElementPathChainEntity();
                        chainEntity.setId(before[j]);
                        chainEntity.setElementType(idTypes.get(before[j]));
                        chainEntity.setSubId(subPath.getBizOrgElementId());
                        chainEntity.setSubElementType(subElement.getElementType());
                        chainEntity.setLevel(len - j);
                        chainEntity.setSystem(pathEntity.getSystem());
                        chainEntity.setTenant(pathEntity.getTenant());
                        chainEntity.setBizOrgUuid(entity.getBizOrgUuid());
                        chains.add(chainEntity);
                    }


                }

            }

        }
        if (!chains.isEmpty()) {
            bizOrgElementPathChainDao.saveAll(chains);
        }

    }


    public void deleteByBizOrgElementId(String id) {
        // 删除当前节点产生的路径链数据
        Map<String, Object> chainParam = Maps.newHashMap();
        chainParam.put("id", id);
        // 删除该节点的所有父级节点与其下的子节点的关系
        bizOrgElementPathChainDao.deleteByHQL("delete from BizOrgElementPathChainEntity c" +
                " where exists (" +
                "   select 1 from BizOrgElementPathChainEntity b where b.subId =c.subId and b.id=:id" +
                ") ", chainParam);
        // 删除该节点的所有下级节点，以及以该节点为下级节点的关系
        bizOrgElementPathChainDao.deleteByHQL("delete from BizOrgElementPathChainEntity c" +
                " where c.id = :id or c.subId=:id", chainParam);
    }

    @Transactional
    public void resetBizOrgElementPathChains(Long bizOrgUuid) {
        Map<String, Object> chainParam = Maps.newHashMap();
        chainParam.put("bizOrgUuid", bizOrgUuid);
        bizOrgElementPathChainDao.deleteByHQL("delete from BizOrgElementPathChainEntity c" +
                " where c.bizOrgUuid=:bizOrgUuid", chainParam);
        List<BizOrgElementPathEntity> pathEntities = listByFieldEqValue("bizOrgUuid", bizOrgUuid);
        List<BizOrgElementPathChainEntity> chainEntities = Lists.newArrayList();
        BizOrganizationEntity bizOrganizationEntity = bizOrganizationService.getOne(bizOrgUuid);
        Set<String> chains = Sets.newHashSet();
        for (BizOrgElementPathEntity pathEntity : pathEntities) {
            String[] idPaths = pathEntity.getIdPath().split(Separator.SLASH.getValue());
            if (idPaths.length > 1) {
                for (int i = 0, len = idPaths.length; i < len; i++) {
                    int level = 1;
                    for (int j = 1 + i, jlen = idPaths.length; j < jlen; j++) {
                        BizOrgElementPathChainEntity chain = new BizOrgElementPathChainEntity();
                        chain.setBizOrgUuid(bizOrgUuid);
                        chain.setTenant(bizOrganizationEntity.getTenant());
                        chain.setSystem(bizOrganizationEntity.getSystem());
                        if (chains.add(idPaths[i] + idPaths[j])) {
                            BizOrgElementEntity element = bizOrgElementDao.getOneByFieldEq("id", idPaths[i]);
                            BizOrgElementEntity subElement = bizOrgElementDao.getOneByFieldEq("id", idPaths[j]);
                            if (element != null && subElement != null) {
                                chain.setElementType(element.getElementType());
                                chain.setSubElementType(subElement.getElementType());
                                chain.setId(element.getId());
                                chain.setSubId(subElement.getId());
                                chain.setLevel(level++);
                                chainEntities.add(chain);
                            }
                        } else {
                            level++;
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(chainEntities)) {
            bizOrgElementPathChainDao.saveAll(chainEntities);
        }
    }
}
