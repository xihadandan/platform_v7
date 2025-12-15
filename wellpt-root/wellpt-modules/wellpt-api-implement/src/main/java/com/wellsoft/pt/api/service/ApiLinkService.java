package com.wellsoft.pt.api.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.api.dao.ApiLinkDao;
import com.wellsoft.pt.api.entity.ApiInvokeLogEntity;
import com.wellsoft.pt.api.entity.ApiLinkEntity;
import com.wellsoft.pt.api.entity.ApiOperationEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

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
public interface ApiLinkService extends JpaService<ApiLinkEntity, ApiLinkDao, Long> {

    Long saveApiLink(ApiLinkEntity dto);

    ApiLinkEntity getApiLinkDetails(Long uuid);

    Long saveApiOperation(ApiOperationEntity operationEntity);

    void deleteApiOperation(Long uuid);

    void delete(Long uuid);

    void deleteByUuids(List<Long> uuids);

    List<ApiLinkEntity> getApiLinksByAppId(String appId);

    ApiOperationEntity getApiOperationDetails(Long uuid);

    ApiLinkEntity getApiLinkById(String id);

    List<ApiOperationEntity> getApiOperationsByAppId(String appId);

    void saveApiInvokeLog(ApiInvokeLogEntity logEntity);

    List<QueryItem> listPageSimpleApiInvokeLogs(Long apiLinkUuid, Long apiOperationUuid, PagingInfo pagingInfo);

    ApiInvokeLogEntity getApiInvokeLogDetails(Long uuid);

    List<ApiOperationEntity> listApiOperationsByApiLinkUuid(Long uuid);
}
