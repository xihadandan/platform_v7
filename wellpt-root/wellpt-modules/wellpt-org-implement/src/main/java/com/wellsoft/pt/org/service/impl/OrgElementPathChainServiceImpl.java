package com.wellsoft.pt.org.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgElementPathChainDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementEntity;
import com.wellsoft.pt.org.entity.OrgElementPathChainEntity;
import com.wellsoft.pt.org.entity.OrgElementPathEntity;
import com.wellsoft.pt.org.service.OrgElementPathChainService;
import com.wellsoft.pt.org.service.OrgElementPathService;
import com.wellsoft.pt.org.service.OrgElementService;
import com.wellsoft.pt.org.service.OrgUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
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
public class OrgElementPathChainServiceImpl extends AbstractJpaServiceImpl<OrgElementPathChainEntity, OrgElementPathChainDaoImpl, Long> implements OrgElementPathChainService {

    @Resource
    OrgElementPathService orgElementPathService;

    @Resource
    OrgElementService orgElementService;

    @Resource
    OrgUserService orgUserService;

    @Override
    public List<OrgElementPathChainEntity> getAllSuperiors(Long orgVersionUuid, String orgElementId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("subOrgElementId", orgElementId);
        params.put("orgVersionUuid", orgVersionUuid);
        return dao.listByHQL("from OrgElementPathChainEntity where subOrgElementId = :subOrgElementId and orgVersionUuid =:orgVersionUuid order by level asc", params);
    }

    @Override
    public OrgElementPathChainEntity getNearestSuperior(String subOrgElementId, String orgElementType, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("subOrgElementId", subOrgElementId);
        params.put("orgVersionUuid", orgVersionUuid);
        if (orgElementType != null) {
            params.put("type", orgElementType);
        }
        List<OrgElementPathChainEntity> list = dao.listByHQL("from OrgElementPathChainEntity where subOrgElementId = :subOrgElementId " + (orgElementType != null ? " and orgElementType=:type " : "") + " and orgVersionUuid =:orgVersionUuid order by level desc", params);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }


    @Override
    public List<OrgElementPathChainEntity> getAllSubordinate(Long orgVersionUuid, String orgElementId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementId", orgElementId);
        params.put("orgVersionUuid", orgVersionUuid);
        return dao.listByHQL("from OrgElementPathChainEntity where orgElementId = :orgElementId and orgVersionUuid =:orgVersionUuid order by level asc", params);
    }

    @Override
    @Transactional
    public void deleteBySubOrgEleIdsAndOrgVersionUuid(List<String> subOrgEleIds, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("subOrgElementIds", subOrgEleIds);
        params.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete OrgElementPathChainEntity where subOrgElementId in (:subOrgElementIds) and orgVersionUuid = :orgVersionUuid", params);
    }

    @Override
    public void deleteByOrgEleIdsAndOrgVersionUuid(List<String> ids, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        params.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete OrgElementPathChainEntity where orgElementId in (:ids) and orgVersionUuid = :orgVersionUuid", params);
    }

    @Override
    public List<OrgElementPathChainEntity> listByOrgVersionUuid(Long orgVersionUuid) {
        return dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    @Transactional
    public void deleteByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete from OrgElementPathChainEntity where orgVersionUuid=:orgVersionUuid", params);
    }


    @Override
    @Transactional
    public void saveOrUpdatePathChainByOrgElementUuid(Long orgElementUuid, Long newParentUuid, Long oldParentUuid, String oldElementName) {

        OrgElementEntity orgElementEntity = orgElementService.getOne(orgElementUuid);
        OrgElementPathEntity pathEntity = this.orgElementPathService.getByOrgEleUuid(orgElementUuid);
        boolean isNew = false;
        if (pathEntity == null) { // 新增路径
            pathEntity = new OrgElementPathEntity();
            pathEntity.setOrgElementId(orgElementEntity.getId());
            pathEntity.setOrgElementUuid(orgElementEntity.getUuid());
            pathEntity.setOrgVersionUuid(orgElementEntity.getOrgVersionUuid());
            pathEntity.setSystem(orgElementEntity.getSystem());
            pathEntity.setTenant(orgElementEntity.getTenant());
            pathEntity.setLeaf(true);
            isNew = true;
        }

        OrgElementPathEntity parentPathEntity = null;
        if (newParentUuid != null) { // 更新路径拼接上父级路径
            parentPathEntity = orgElementPathService.getByOrgEleUuid(newParentUuid);
            // 更新父级为非叶子节点
            Map<String, Object> params = Maps.newHashMap();
            params.put("uuid", parentPathEntity.getUuid());
            orgElementPathService.updateByHQL("update OrgElementPathEntity set leaf = false where uuid=:uuid", params);
        }
        // 更新组织单元实例路径名称
        String currentPinYin = PinyinUtil.getPinYinMulti(orgElementEntity.getName(), true);
        if (parentPathEntity != null) {
            pathEntity.setCnPath(parentPathEntity.getCnPath() + Separator.SLASH.getValue() + orgElementEntity.getName());
            pathEntity.setPinYinPath(parentPathEntity.getPinYinPath() + Separator.SLASH.getValue() + currentPinYin);
            pathEntity.setIdPath(parentPathEntity.getIdPath() + Separator.SLASH.getValue() + orgElementEntity.getId());
        } else {
            pathEntity.setCnPath(orgElementEntity.getName());
            pathEntity.setPinYinPath(currentPinYin);
            pathEntity.setIdPath(orgElementEntity.getId());
        }
        orgElementPathService.save(pathEntity);
        boolean updatePathChain = false;
        if (newParentUuid != null && oldParentUuid == null) { // 新增的父子关系
            String[] parentIdPaths = parentPathEntity.getIdPath().split(Separator.SLASH.getValue());
            List<OrgElementPathChainEntity> waitSaveChains = Lists.newArrayList();
            List<OrgElementEntity> orgElementsInPath = orgElementService.listByIdsAndOrgVersionId(parentIdPaths, orgElementEntity.getOrgVersionUuid());
            ImmutableMap<String, OrgElementEntity> map = Maps.uniqueIndex(orgElementsInPath, OrgElementEntity::getId);
            // 保存新增节点与所有上级节点的上级关系
            for (int i = 0, len = parentIdPaths.length; i < len; i++) {
                String idType = map.containsKey(parentIdPaths[i]) ? map.get(parentIdPaths[i]).getType() : null;
                OrgElementPathChainEntity subordinate = new OrgElementPathChainEntity();
                subordinate.setOrgElementId(parentIdPaths[i]);
                subordinate.setOrgElementType(idType);
                subordinate.setOrgVersionUuid(orgElementEntity.getOrgVersionUuid());
                subordinate.setSubOrgElementId(orgElementEntity.getId());
                subordinate.setSubOrgElementType(orgElementEntity.getType());
                subordinate.setLevel(len - i);
                subordinate.setSystem(orgElementEntity.getSystem());
                subordinate.setTenant(orgElementEntity.getTenant());
                waitSaveChains.add(subordinate);
            }
            saveAll(waitSaveChains);

            // 更新新父级路径与其下的子节点路径
            updatePathChain = true;

        } else if (oldParentUuid != null && !oldParentUuid.equals(newParentUuid)) { // 父级界定发生了变更的情况:
            /**
             * 删除当前节点的所有父级节点与当前节点的所有下级节点的关系链
             */
            List<OrgElementPathChainEntity> subordinates = this.getAllSubordinate(orgElementEntity.getOrgVersionUuid(), orgElementEntity.getId());
            Map<String, String> idTypeMap = Maps.newHashMap();
            idTypeMap.put(orgElementEntity.getId(), orgElementEntity.getType());
            List<String> subordinateIds = Lists.newArrayList(orgElementEntity.getId());
            Maps.uniqueIndex(subordinates, new Function<OrgElementPathChainEntity, String>() {
                @Nullable
                @Override
                public String apply(@Nullable OrgElementPathChainEntity input) {
                    idTypeMap.put(input.getSubOrgElementId(), input.getSubOrgElementType());
                    subordinateIds.add(input.getSubOrgElementId());
                    return input.getSubOrgElementId();
                }
            });


            List<OrgElementPathChainEntity> superiros = this.getAllSuperiors(orgElementEntity.getOrgVersionUuid(), orgElementEntity.getId());
            List<String> superiroIds = Lists.newArrayList();
            Maps.uniqueIndex(superiros, new Function<OrgElementPathChainEntity, String>() {
                @Nullable
                @Override
                public String apply(@Nullable OrgElementPathChainEntity input) {
                    idTypeMap.put(input.getOrgElementId(), input.getOrgElementType());
                    superiroIds.add(input.getOrgElementId());
                    return input.getOrgElementId();
                }
            });

            // 删除下级关系
            Map<String, Object> params = Maps.newHashMap();
            params.put("orgVersionUuid", orgElementEntity.getOrgVersionUuid());
            params.put("orgElementIds", superiroIds);
            params.put("subOrgElementIds", subordinateIds);
            this.dao.deleteByHQL("delete OrgElementPathChainEntity where orgElementId in (:orgElementIds) and subOrgElementId in (:subOrgElementIds) " +
                    " and orgVersionUuid=:orgVersionUuid", params);
            // 查询旧父级节点是否还有子节点
            OrgElementPathEntity oldParentPath = orgElementPathService.getByOrgEleUuid(oldParentUuid);
            params.put("orgElementId", oldParentPath.getOrgElementId());
            long cnt = this.dao.countByHQL("from OrgElementPathChainEntity where orgElementId = :orgElementId and orgVersionUuid=:orgVersionUuid and level = 1 ", params);
            if (cnt == 0) {
                oldParentPath.setLeaf(true);
                orgElementPathService.save(oldParentPath);
            }

            // 更新旧节点下的子节点序号
            List<OrgElementEntity> oldChildElements = orgElementService.listByParentUUid(oldParentUuid);
            List<OrgElementEntity> updatedSeq = Lists.newArrayList();
            int seq = 1;
            for (OrgElementEntity entity : oldChildElements) {
                if (!entity.getUuid().equals(orgElementEntity.getUuid())) {
                    entity.setSeq(seq++);
                    updatedSeq.add(entity);
                }
            }
            orgElementService.saveAll(updatedSeq);


            if (newParentUuid != null) {
                List<OrgElementPathChainEntity> waitSaveChains = Lists.newArrayList();
                // 通过新的父级节点的所有父级节点重新建立父子关系链
                List<OrgElementPathChainEntity> newSuperiorChain = getAllSuperiors(orgElementEntity.getOrgVersionUuid()
                        , parentPathEntity.getOrgElementId());
                OrgElementPathChainEntity parent = new OrgElementPathChainEntity();
                parent.setOrgElementId(parentPathEntity.getOrgElementId());
                parent.setOrgElementType(orgElementService.getOne(newParentUuid).getType());
                parent.setLevel(0);
                newSuperiorChain.add(parent);

                // 建立父节点与子节点的关系
                for (String subId : subordinateIds) {
                    int level = 1;
                    for (OrgElementPathChainEntity superirorChain : newSuperiorChain) {
                        OrgElementPathChainEntity superior = new OrgElementPathChainEntity();
                        superior.setOrgElementId(superirorChain.getOrgElementId());
                        superior.setOrgElementType(superirorChain.getOrgElementType());
                        superior.setOrgVersionUuid(orgElementEntity.getOrgVersionUuid());
                        superior.setSubOrgElementId(subId);
                        superior.setSubOrgElementType(idTypeMap.containsKey(subId) ? idTypeMap.get(subId) : null);
                        superior.setLevel(superirorChain.getLevel() + level++);
                        superior.setTenant(orgElementEntity.getTenant());
                        superior.setSystem(orgElementEntity.getSystem());
                        waitSaveChains.add(superior);

                    }
                }
                this.saveAll(waitSaveChains);
            }

            // 更新路径
            updatePathChain = true;

        } else if (!isNew && !orgElementEntity.getName().equals(oldElementName)) {
            // 改了名称，要更新子节点的路径名称
            updatePathChain = true;
        }


        // 更新下级节点路径名称
        if (updatePathChain) {
            List<OrgElementPathChainEntity> subordinates = this.getAllSubordinate(orgElementEntity.getOrgVersionUuid(), orgElementEntity.getId());
            if (CollectionUtils.isNotEmpty(subordinates)) {
                List<String> subordinateIds = Lists.newArrayList(orgElementEntity.getId());
                Maps.uniqueIndex(subordinates, new Function<OrgElementPathChainEntity, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable OrgElementPathChainEntity input) {
                        subordinateIds.add(input.getSubOrgElementId());
                        return input.getSubOrgElementId();
                    }
                });
                Map<String, String> orgElementPathMap = Maps.newHashMap();
                orgElementPathMap.put(pathEntity.getOrgElementId(), pathEntity.getIdPath());
                // 更新子节点路径
                List<OrgElementPathEntity> subordinatePaths = this.orgElementPathService.listByOrgElementIdsAndOrgVersionUuid(subordinateIds, orgElementEntity.getOrgVersionUuid());
                Set<String> ids = Sets.newHashSet();
                for (OrgElementPathEntity p : subordinatePaths) {
                    ids.addAll(Lists.newArrayList(p.getIdPath().split(Separator.SLASH.getValue())));
                }
                List<OrgElementEntity> orgElementEntities = this.orgElementService.listByIdsAndOrgVersionId(ids.toArray(new String[]{}), orgElementEntity.getOrgVersionUuid());
                ImmutableMap<String, OrgElementEntity> elementEntityMap = Maps.uniqueIndex(orgElementEntities, new Function<OrgElementEntity, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable OrgElementEntity input) {
                        return input.getId();
                    }
                });
                String parentIdPath = parentPathEntity != null ? StringUtils.join(new String[]{parentPathEntity.getIdPath(), orgElementEntity.getId()}, Separator.SLASH.getValue()) : orgElementEntity.getId();
                String parentCnPath = parentPathEntity != null ? StringUtils.join(new String[]{parentPathEntity.getCnPath(), orgElementEntity.getName()}, Separator.SLASH.getValue()) : orgElementEntity.getName();
                String parentPyPath = parentPathEntity != null ? StringUtils.join(new String[]{parentPathEntity.getPinYinPath(), currentPinYin}, Separator.SLASH.getValue()) : currentPinYin;
                for (OrgElementPathEntity subpath : subordinatePaths) {
                    // 去重脏数据处理
                    String[] idPaths = Lists.newArrayList(Sets.newLinkedHashSet(Lists.newArrayList(subpath.getIdPath().split(Separator.SLASH.getValue())))).toArray(new String[]{});
                    idPaths = ArrayUtils.subarray(idPaths, ArrayUtils.lastIndexOf(idPaths, orgElementEntity.getId()) + 1, idPaths.length);
                    List<String> paths = Lists.newArrayList();
                    List<String> pinYins = Lists.newArrayList();
                    for (String id : idPaths) {
                        if (elementEntityMap.containsKey(id)) {
                            OrgElementEntity e = elementEntityMap.get(id);
                            paths.add(e.getName());
                            pinYins.add(PinyinUtil.getPinYinMulti(e.getName(), true));
                        }
                    }
                    subpath.setIdPath(idPaths.length > 0 ? StringUtils.join(new String[]{parentIdPath, StringUtils.join(idPaths, Separator.SLASH.getValue())}, Separator.SLASH.getValue()) : parentIdPath);
                    orgElementPathMap.put(subpath.getOrgElementId(), subpath.getIdPath());
                    subpath.setCnPath(paths.size() > 0 ? StringUtils.join(new String[]{parentCnPath, StringUtils.join(paths, Separator.SLASH.getValue())}, Separator.SLASH.getValue()) : parentCnPath);
                    subpath.setPinYinPath(pinYins.size() > 0 ? StringUtils.join(new String[]{parentPyPath, StringUtils.join(pinYins, Separator.SLASH.getValue())}, Separator.SLASH.getValue()) : parentPyPath);
                }
                orgElementPathService.saveAll(subordinatePaths);
                orgUserService.updateOrgUserPath(orgElementPathMap);
            }
        }

    }

    /**
     * 判断是否包含指定的组织元素
     *
     * @param orgElementIds
     * @param subOrgElementId
     * @param orgVersionIds
     * @return
     */
    @Override
    public boolean containsOrgElement(List<String> orgElementIds, String subOrgElementId, String[] orgVersionIds) {
        Assert.notEmpty(orgElementIds, "上级组织元素ID列表不能为空！");
        Assert.hasLength(subOrgElementId, "下级组织单元ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        String hql = "from OrgElementPathChainEntity t where t.orgElementId in(:orgElementIds) and t.subOrgElementId = :subOrgElementId and t.orgVersionUuid in(select uuid from OrgVersionEntity where id in(:orgVersionIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementIds", orgElementIds);
        params.put("subOrgElementId", subOrgElementId);
        params.put("orgVersionIds", orgVersionIds);
        return this.dao.countByHQL(hql, params) > 0;
    }

}
