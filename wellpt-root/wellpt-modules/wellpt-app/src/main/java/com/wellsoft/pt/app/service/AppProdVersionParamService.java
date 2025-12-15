package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppProdVersionParamDao;
import com.wellsoft.pt.app.entity.AppProdVersionParamEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年09月11日   chenq	 Create
 * </pre>
 */
public interface AppProdVersionParamService extends JpaService<AppProdVersionParamEntity, AppProdVersionParamDao, Long> {

    List<AppProdVersionParamEntity> getAllParamsDetail(Long prodVersionUuid);

    List<AppProdVersionParamEntity> getAllParamProp(Long prodVersionUuid);

    void deleteVersionParams(Long prodVersionUuid);

    void deleteParam(Long uuid);

    Long saveParam(AppProdVersionParamEntity temp);

    AppProdVersionParamEntity getParam(Long prodVersionUuid, String propKey);

    void deleteParams(List<Long> uuid);
}
