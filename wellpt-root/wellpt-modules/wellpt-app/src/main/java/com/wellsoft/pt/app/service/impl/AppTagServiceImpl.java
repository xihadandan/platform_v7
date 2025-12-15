package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppTagDao;
import com.wellsoft.pt.app.dao.impl.AppDataTagDaoImpl;
import com.wellsoft.pt.app.entity.AppDataTagEntity;
import com.wellsoft.pt.app.entity.AppTagEntity;
import com.wellsoft.pt.app.service.AppTagService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月28日   chenq	 Create
 * </pre>
 */
@Service
public class AppTagServiceImpl extends AbstractJpaServiceImpl<AppTagEntity, AppTagDao, Long> implements AppTagService {

    @Autowired
    AppDataTagDaoImpl dataTagDao;

    @Override
    @Transactional
    public Long createTag(String name, String applyTo) {
        AppTagEntity example = new AppTagEntity();
        example.setName(name);
        example.setApplyTo(applyTo);
        List<AppTagEntity> tagEntityList = dao.listByEntity(example);
        if (CollectionUtils.isNotEmpty(tagEntityList)) {
            return tagEntityList.get(0).getUuid();
        }
        Long uuid = null;
        try {
            save(example);
            return example.getUuid();
        } catch (Exception e) {
            // 保存异常可能是并发同时保存的同名标签，则查询返回
            tagEntityList = dao.listByEntity(example);
            if (CollectionUtils.isNotEmpty(tagEntityList)) {
                uuid = tagEntityList.get(0).getUuid();
            }
        }
        return uuid;
    }

    private void saveDataTag(String dataId, List<Long> tagUuids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", dataId);
        dataTagDao.deleteByHQL("delete from AppDataTagEntity where dataId=:id", params);
        if (CollectionUtils.isNotEmpty(tagUuids)) {
            List<AppDataTagEntity> dataTagEntities = Lists.newArrayList();
            int i = 1;
            for (Long uuid : tagUuids) {
                dataTagEntities.add(new AppDataTagEntity(uuid, dataId, i++));
            }
            dataTagDao.saveAll(dataTagEntities);
        }
    }

    @Override
    @Transactional
    public void saveDataTag(List<String> dataIds, List<Long> tagUuids) {
        if (CollectionUtils.isNotEmpty(dataIds)) {
            for (String id : dataIds) {
                this.saveDataTag(id, tagUuids);
            }
        }

    }

    @Override
    @Transactional
    public void deleteDataTag(String dataId, Long tagUuid) {
        Map<String, Object> params = Maps.newHashMap();
        if (tagUuid != null) {
            params.put("tagUuid", tagUuid);
        }
        if (StringUtils.isNotBlank(dataId)) {
            params.put("id", dataId);
            dataTagDao.deleteByHQL("delete from AppDataTagEntity where dataId=:id " + (tagUuid != null ? "and tagUuid=:tagUuid" : ""), params);
        }
        if (tagUuid != null) {
            dataTagDao.deleteByHQL("delete from AppTagEntity a where a.uuid=:tagUuid and not exists ( select 1 from AppDataTagEntity t where t.tagUuid = a.uuid ) ", params);
        }

    }

    @Override
    public List<AppTagEntity> getTagsByDataId(String dataId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", dataId);
        return this.dao.listByHQL("select a from AppTagEntity a , AppDataTagEntity d where  " +
                "  d.tagUuid = a.uuid and d.dataId=:id" +
                " order by d.seq asc", params);
    }

    @Override
    public List<AppTagEntity> getTagsByApplyTo(String applyTo) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("applyTo", applyTo);
        return dao.listByHQL("from AppTagEntity where applyTo=:applyTo order by createTime desc ", params);
    }

    @Override
    @Transactional
    public void deleteTag(Long uuid) {
        dao.delete(uuid);
        Map<String, Object> params = Maps.newHashMap();
        params.put("tagUuid", uuid);
        dao.deleteByHQL("delete from AppDataTagEntity where tagUuid=:tagUuid", params);
    }

    @Override
    @Transactional
    public void deleteUnUsedTag() {
        dataTagDao.deleteByHQL("delete from AppTagEntity a where  not exists ( select 1 from AppDataTagEntity t where t.tagUuid = a.uuid ) ", null);

    }


}
