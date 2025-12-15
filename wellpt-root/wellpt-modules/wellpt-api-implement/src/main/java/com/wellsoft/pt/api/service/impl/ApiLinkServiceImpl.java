package com.wellsoft.pt.api.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.api.dao.ApiLinkDao;
import com.wellsoft.pt.api.dao.impl.ApiBodySchemaDaoImpl;
import com.wellsoft.pt.api.dao.impl.ApiInvokeLogDaoImpl;
import com.wellsoft.pt.api.dao.impl.ApiOperationDaoImpl;
import com.wellsoft.pt.api.dao.impl.ApiOperationParamDaoImpl;
import com.wellsoft.pt.api.entity.*;
import com.wellsoft.pt.api.service.ApiLinkService;
import com.wellsoft.pt.app.service.AppModuleService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2025年05月13日   chenq	 Create
 * </pre>
 */
@Service
public class ApiLinkServiceImpl extends AbstractJpaServiceImpl<ApiLinkEntity, ApiLinkDao, Long> implements ApiLinkService {

    @Autowired
    ApiOperationDaoImpl apiOperationDao;
    @Autowired
    ApiOperationParamDaoImpl apiOperationParamDao;
    @Autowired
    ApiBodySchemaDaoImpl apiBodySchemaDao;
    @Autowired
    AppModuleService appModuleService;
    @Autowired
    ApiInvokeLogDaoImpl apiInvokeLogDao;

    @Override
    @Transactional
    public Long saveApiLink(ApiLinkEntity dto) {
        ApiLinkEntity entity = dto.getUuid() == null ? new ApiLinkEntity() : getOne(dto.getUuid());
        BeanUtils.copyProperties(dto, entity, entity.BASE_FIELDS);
        save(entity);
        if (dto.getUuid() != null) {
            if (dto.getApiOperations() != null) {
                // 删除旧的操作
                apiOperationParamDao.deleteByHQL("delete from ApiOperationParamEntity p where exists (" +
                                "select 1 from ApiOperationEntity o where o.apiLinkUuid=:uuid and o.uuid = p.operationUuid)"
                        , ImmutableMap.<String, Object>builder().put("uuid", dto.getUuid()).build());
                apiBodySchemaDao.deleteByHQL("delete from ApiOperationBodySchemaEntity p where exists (" +
                        "select 1 from ApiOperationEntity o where o.apiLinkUuid=:uuid and o.uuid = p.operationUuid)", ImmutableMap.<String, Object>builder().put("operationUuid", dto.getUuid()).build());
                apiOperationDao.deleteByHQL("delete from ApiOperationEntity where apiLinkUuid=:uuid"
                        , ImmutableMap.<String, Object>builder().put("uuid", dto.getUuid()).build());
            }

        }
        if (dto.getApiOperations() != null) {
            for (ApiOperationEntity operationEntity : dto.getApiOperations()) {
                operationEntity.setApiLinkUuid(entity.getUuid());
                apiOperationDao.save(operationEntity);
                if (operationEntity.getParameters() != null) {
                    for (ApiOperationParamEntity parameterEntity : operationEntity.getParameters()) {
                        parameterEntity.setOperationUuid(operationEntity.getUuid());
                        apiOperationParamDao.save(parameterEntity);
                    }
                }
            }
        }

        return entity.getUuid();
    }

    @Override
    public ApiLinkEntity getApiLinkDetails(Long uuid) {
        ApiLinkEntity linkEntity = getOne(uuid);
        if (linkEntity != null) {
            List<ApiOperationEntity> operationEntityList = apiOperationDao.listByHQL("from ApiOperationEntity where apiLinkUuid=:uuid order by createTime asc",
                    ImmutableMap.<String, Object>builder().put("uuid", uuid).build());
            List<ApiOperationParamEntity> parameterEntities = apiOperationParamDao.listByHQL("from ApiOperationParamEntity p where exists (" +
                    " select 1 from ApiOperationEntity o where o.apiLinkUuid=:uuid and o.uuid = p.operationUuid)", ImmutableMap.<String, Object>builder().put("uuid", uuid).build());
            List<ApiOperationBodySchemaEntity> bodySchemaEntities = apiBodySchemaDao.listByHQL("from ApiOperationBodySchemaEntity p where exists (" +
                    " select 1 from ApiOperationEntity o where o.apiLinkUuid=:uuid and o.uuid = p.operationUuid)", ImmutableMap.<String, Object>builder().put("uuid", uuid).build());
            linkEntity.setApiOperations(operationEntityList);
            Map<Long, List<ApiOperationParamEntity>> map = Maps.newHashMap();
            Map<Long, List<ApiOperationBodySchemaEntity>> schemaMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(parameterEntities)) {
                for (ApiOperationParamEntity p : parameterEntities) {
                    if (!map.containsKey(p.getOperationUuid())) {
                        map.put(p.getOperationUuid(), Lists.newArrayList());
                    }
                    map.get(p.getOperationUuid()).add(p);
                }
            }
            if (CollectionUtils.isNotEmpty(bodySchemaEntities)) {
                for (ApiOperationBodySchemaEntity p : bodySchemaEntities) {
                    if (!schemaMap.containsKey(p.getOperationUuid())) {
                        schemaMap.put(p.getOperationUuid(), Lists.newArrayList());
                    }
                    schemaMap.get(p.getOperationUuid()).add(p);
                }
            }
            for (ApiOperationEntity entity : operationEntityList) {
                if (map.containsKey(entity.getUuid())) {
                    entity.setParameters(map.get(entity.getUuid()));
                }
                if (schemaMap.containsKey(entity.getUuid())) {
                    entity.setBodySchema(schemaMap.get(entity.getUuid()));
                }
            }
            return linkEntity;
        }
        return null;
    }

    @Override
    @Transactional
    public Long saveApiOperation(ApiOperationEntity operationEntity) {
        ApiOperationEntity entity = operationEntity.getUuid() == null ? new ApiOperationEntity() : apiOperationDao.getOne(operationEntity.getUuid());
        BeanUtils.copyProperties(operationEntity, entity, entity.BASE_FIELDS);
        if (operationEntity.getUuid() != null) {
            apiBodySchemaDao.deleteByHQL("delete from ApiOperationBodySchemaEntity where operationUuid=:operationUuid", ImmutableMap.<String, Object>builder().put("operationUuid", operationEntity.getUuid()).build());
            apiBodySchemaDao.deleteByHQL("delete from ApiOperationParamEntity where operationUuid=:operationUuid", ImmutableMap.<String, Object>builder().put("operationUuid", operationEntity.getUuid()).build());
        }
        apiOperationDao.save(entity);
        if (operationEntity.getBodySchema() != null) {
            for (ApiOperationBodySchemaEntity schemaEntity : operationEntity.getBodySchema()) {
                schemaEntity.setOperationUuid(entity.getUuid());
                apiBodySchemaDao.save(schemaEntity);
            }
        }
        if (operationEntity.getParameters() != null) {
            for (ApiOperationParamEntity paramEntity : operationEntity.getParameters()) {
                ApiOperationParamEntity p = new ApiOperationParamEntity();
                BeanUtils.copyProperties(paramEntity, p, p.BASE_FIELDS);
                p.setOperationUuid(entity.getUuid());
                apiOperationParamDao.save(p);
            }
        }

        return entity.getUuid();
    }

    @Override
    @Transactional
    public void deleteApiOperation(Long uuid) {
        apiOperationDao.delete(uuid);
        apiBodySchemaDao.deleteByHQL("delete from ApiOperationBodySchemaEntity where operationUuid=:operationUuid", ImmutableMap.<String, Object>builder().put("operationUuid", uuid).build());
        apiBodySchemaDao.deleteByHQL("delete from ApiOperationParamEntity where operationUuid=:operationUuid", ImmutableMap.<String, Object>builder().put("operationUuid", uuid).build());

    }

    @Override
    @Transactional
    public void delete(Long uuid) {
        this.dao.delete(uuid);
        apiOperationParamDao.deleteByHQL("delete from ApiOperationParamEntity p where exists (" +
                        "select 1 from ApiOperationEntity o where o.apiLinkUuid=:uuid and o.uuid = p.operationUuid)"
                , ImmutableMap.<String, Object>builder().put("uuid", uuid).build());
        apiBodySchemaDao.deleteByHQL("delete from ApiOperationBodySchemaEntity p where exists (" +
                "select 1 from ApiOperationEntity o where o.apiLinkUuid=:uuid and o.uuid = p.operationUuid)", ImmutableMap.<String, Object>builder().put("uuid", uuid).build());
        apiOperationDao.deleteByHQL("delete from ApiOperationEntity where apiLinkUuid=:uuid"
                , ImmutableMap.<String, Object>builder().put("uuid", uuid).build());
    }

    @Override
    @Transactional
    public void deleteByUuids(List<Long> uuid) {
        for (Long u : uuid) {
            delete(u);
        }
    }

    @Override
    public List<ApiLinkEntity> getApiLinksByAppId(String appId) {
        Set<String> systemIds = appModuleService.getModuleRelaSystems(appId);
        systemIds.add(appId);
        List<ApiLinkEntity> apiLinkEntities = dao.listByFieldInValues("system", Lists.newArrayList(systemIds));
        if (CollectionUtils.isNotEmpty(apiLinkEntities)) {
            List<ApiOperationEntity> apiOperationEntities = apiOperationDao.listByHQL("from ApiOperationEntity o where exists (" +
                    " select 1 from ApiLinkEntity a where a.uuid = o.apiLinkUuid and o.system in (:system) )", ImmutableMap.<String, Object>builder().put("system", Lists.newArrayList(systemIds)).build());
            Map<Long, ApiLinkEntity> map = Maps.newHashMap();
            for (ApiLinkEntity entity : apiLinkEntities) {
                entity.setApiOperations(Lists.newArrayList());
                map.put(entity.getUuid(), entity);
            }
            if (CollectionUtils.isNotEmpty(apiOperationEntities)) {
                for (ApiOperationEntity operationEntity : apiOperationEntities) {
                    if (map.containsKey(operationEntity.getApiLinkUuid())) {
                        ApiLinkEntity linkEntity = map.get(operationEntity.getApiLinkUuid());
                        if (linkEntity.getApiOperations() == null) {
                            linkEntity.setApiOperations(Lists.newArrayList());
                        }
                        linkEntity.getApiOperations().add(operationEntity);
                    }
                }
            }
        }
        return apiLinkEntities;
    }

    @Override
    public ApiOperationEntity getApiOperationDetails(Long uuid) {
        ApiOperationEntity apiOperationEntity = apiOperationDao.getOne(uuid);
        if (apiOperationEntity != null) {
            apiOperationEntity.setParameters(apiOperationParamDao.listByFieldEqValue("operationUuid", uuid));
            apiOperationEntity.setBodySchema(apiBodySchemaDao.listByFieldEqValue("operationUuid", uuid));
            apiOperationEntity.setApiLink(getOne(apiOperationEntity.getApiLinkUuid()));
        }
        return apiOperationEntity;
    }

    @Override
    public ApiLinkEntity getApiLinkById(String id) {
        List<ApiLinkEntity> entities = dao.listByFieldEqValue("id", id);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }


    @Override
    public List<ApiOperationEntity> getApiOperationsByAppId(String appId) {
        Set<String> systemIds = appModuleService.getModuleRelaSystems(appId);
        systemIds.add(appId);
        List<ApiOperationEntity> apiOperationEntities = apiOperationDao.listByHQL("from ApiOperationEntity o where" +
                " exists ( select 1 from ApiLinkEntity a where a.uuid = o.apiLinkUuid and o.system in :system)", ImmutableMap.<String, Object>builder().put("system", Lists.newArrayList(systemIds)).build());
        List<ApiLinkEntity> apiLinkEntities = dao.listByFieldInValues("system", Lists.newArrayList(systemIds));
        if (CollectionUtils.isNotEmpty(apiOperationEntities)) {
            Map<Long, ApiLinkEntity> apiLinkEntityMap = Maps.newHashMap();
            for (ApiLinkEntity apiLinkEntity : apiLinkEntities) {
                apiLinkEntityMap.put(apiLinkEntity.getUuid(), apiLinkEntity);
            }
            for (ApiOperationEntity o : apiOperationEntities) {
                o.setApiLink(apiLinkEntityMap.get(o.getApiLinkUuid()));
            }
        }
        return apiOperationEntities;
    }

    @Override
    @Transactional
    public void saveApiInvokeLog(ApiInvokeLogEntity logEntity) {
        apiInvokeLogDao.save(logEntity);
    }


    @Override
    public List<QueryItem> listPageSimpleApiInvokeLogs(Long apiLinkUuid, Long apiOperationUuid, PagingInfo pagingInfo) {
        StringBuilder sql = new StringBuilder("select uuid, invoke_url,res_status,res_time,req_time, latency ,req_method  from api_invoke_log");
        sql.append(" where api_link_uuid = :apiLinkUuid");
        if (apiOperationUuid != null) {
            sql.append(" and api_operation_uuid=:apiOperationUuid");
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("apiLinkUuid", apiLinkUuid);
        params.put("apiOperationUuid", apiOperationUuid);
        if (pagingInfo.isAutoCount()) {
            long total = apiInvokeLogDao.countBySQL("select count(1) " + sql.toString().substring(sql.toString().indexOf("from")), params);
            pagingInfo.setTotalCount(total);
        }
        sql.append(" order by req_time desc ");
        return apiInvokeLogDao.listQueryItemBySQL(sql.toString(), params, pagingInfo);
    }

    @Override
    public ApiInvokeLogEntity getApiInvokeLogDetails(Long uuid) {
        return apiInvokeLogDao.getOne(uuid);
    }

    @Override
    public List<ApiOperationEntity> listApiOperationsByApiLinkUuid(Long uuid) {
        return apiOperationDao.listByFieldEqValue("apiLinkUuid", uuid);
    }

}
