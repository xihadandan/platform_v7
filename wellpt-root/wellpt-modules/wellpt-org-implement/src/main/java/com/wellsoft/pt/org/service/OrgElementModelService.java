package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgElementModelDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementModelEntity;

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
public interface OrgElementModelService extends JpaService<OrgElementModelEntity, OrgElementModelDaoImpl, Long> {

    /**
     * 启用或者停用组织单元模型
     *
     * @param uuid
     * @param enable
     * @return
     */
    boolean enableOrgElementModel(Long uuid, boolean enable);

    boolean deleteOrgElementModel(Long uuid);

    /**
     * 查询归属租户、归属系统的组织单元模型
     *
     * @param system
     * @return
     */
    List<OrgElementModelEntity> listOrgElementModels(String tenant, String system);

    List<OrgElementModelEntity> listOrgElementModelsBySystemIsNull();


    Long saveOrgElementModel(OrgElementModelEntity orgElementModel);

    OrgElementModelEntity getOrgElementModelByIdAndSystem(String id, String system);

    String getOrgElementModelDefJson(String id, String system);

    void createOrgElementModelAndSetting(String system, String tenant);

    OrgElementModelEntity getDetailByUuid(Long uuid);
}
