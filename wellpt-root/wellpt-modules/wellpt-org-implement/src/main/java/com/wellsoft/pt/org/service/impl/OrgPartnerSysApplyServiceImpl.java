package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgPartnerSysApplyDaoImpl;
import com.wellsoft.pt.org.entity.OrgPartnerSysApplyEntity;
import com.wellsoft.pt.org.entity.OrgPartnerSysOrgEntity;
import com.wellsoft.pt.org.service.OrgPartnerSysApplyService;
import com.wellsoft.pt.org.service.OrgPartnerSysOrgService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月07日   chenq	 Create
 * </pre>
 */
@Service
public class OrgPartnerSysApplyServiceImpl extends AbstractJpaServiceImpl<OrgPartnerSysApplyEntity, OrgPartnerSysApplyDaoImpl, Long> implements OrgPartnerSysApplyService {


    @Autowired
    OrgPartnerSysOrgService orgPartnerSysOrgService;

//    public static void main(String[] args) {
//        System.out.println(new String(Base64Utils.decodeFromString("ZGFua29nYWk=")));
//    }

    @Override
    @Transactional
    public Long addOrgPartnerSysApply(OrgPartnerSysApplyEntity temp) {
        OrgPartnerSysApplyEntity apply = new OrgPartnerSysApplyEntity();
        apply.setState(OrgPartnerSysApplyEntity.State.WAIT_CONFIRM);
        apply.setApplyReason(temp.getApplyReason());
        apply.setCategoryUuid(temp.getCategoryUuid());
        apply.setReqSystem(RequestSystemContextPathResolver.system());
        apply.setReqTenant(SpringSecurityUtils.getCurrentTenantId());
        apply.setSystem(RequestSystemContextPathResolver.system());
        apply.setResSystemCode(temp.getResSystemCode());
//        // 系统标识组成部分：租户ID/系统ID/租户名称/系统名称
//        String[] parts = new String(Base64Utils.decodeFromString(temp.getResSystemCode())).split(Separator.SLASH.getValue());
        apply.setApplyTime(new Date());
        apply.setResTenant(temp.getResTenant());
        apply.setResSystem(temp.getResSystem());
        apply.setResName(temp.getResName());
        save(apply);
        return apply.getUuid();
    }

    @Override
    @Transactional
    public void updateOrgPartnerSysApplyState(Long uuid, OrgPartnerSysApplyEntity.State state) {
        OrgPartnerSysApplyEntity applyEntity = getOne(uuid);
        if (applyEntity != null) {
            applyEntity.setState(state);
            save(applyEntity);

            if (state.equals(OrgPartnerSysApplyEntity.State.PASSED)) {

                // 各自建立外部组织
                OrgPartnerSysOrgEntity requesterOrg = new OrgPartnerSysOrgEntity();
                requesterOrg.setId(IdPrefix.PARTER_SYS_ORG.getValue() + Separator.UNDERLINE.getValue() + SnowFlake.getId());
                requesterOrg.setName(applyEntity.getReqName());
                requesterOrg.setRemark(applyEntity.getApplyReason());
                requesterOrg.setTenant(applyEntity.getReqTenant());
                requesterOrg.setSystem(applyEntity.getReqSystem());
                orgPartnerSysOrgService.save(requesterOrg);

                OrgPartnerSysOrgEntity responseOrg = new OrgPartnerSysOrgEntity();
                responseOrg.setId(IdPrefix.PARTER_SYS_ORG.getValue() + Separator.UNDERLINE.getValue() + SnowFlake.getId());
                responseOrg.setName(applyEntity.getResName());
                responseOrg.setRemark(applyEntity.getApplyReason());
                responseOrg.setTenant(applyEntity.getResTenant());
                responseOrg.setSystem(applyEntity.getResSystem());
                orgPartnerSysOrgService.save(responseOrg);
            }
        }
    }

    @Override
    @Transactional
    public void updateCategoryIsNullByCategoryUuid(Long categoryUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("categoryUuid", categoryUuid);
        this.dao.updateByHQL("update OrgPartnerSysApplyEntity set categoryUuid = null where categoryUuid=:categoryUuid", param);
    }

    @Override
    public long countByStateAndSystemAndTenant(OrgPartnerSysApplyEntity.State state, String system, String tenant) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("state", state);
        param.put("system", system);
        param.put("tenant", tenant);
        return this.dao.countByHQL("from OrgPartnerSysApplyEntity where state=:state and resSystem=:system and resTenant=:tenant", param);
    }
}
