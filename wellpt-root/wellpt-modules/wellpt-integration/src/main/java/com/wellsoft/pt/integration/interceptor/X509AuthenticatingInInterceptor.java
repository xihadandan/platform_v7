/*
 * @(#)2013-11-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.interceptor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.integration.entity.ExchangeDataType;
import com.wellsoft.pt.integration.entity.ExchangeSystem;
import com.wellsoft.pt.integration.service.ExchangeDataConfigService;
import com.wellsoft.pt.integration.service.ExchangeDataTypeService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.TenantContextHolder;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.SecurityContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.auth.x500.X500Principal;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-18.1	zhulh		2013-11-18		Create
 * </pre>
 * @date 2013-11-18
 */
public class X509AuthenticatingInInterceptor extends AbstractPhaseInterceptor<Message> {

    public X509AuthenticatingInInterceptor() {
        super(Phase.PRE_INVOKE);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        SecurityContext context = message.get(SecurityContext.class);
        if (context == null || context.getUserPrincipal() == null) {
            throw new SecurityException("User Principal is not available on the current message");
        }

        X500Principal principal = (X500Principal) context.getUserPrincipal();

        String tenantId = TenantContextHolder.getTenantId();
        String loginName = principal.getName();

        UnitApiFacade unitApiFacade = ApplicationContextHolder.getBean(UnitApiFacade.class);
        ExchangeDataConfigService exchangeDataConfigService = ApplicationContextHolder
                .getBean(ExchangeDataConfigService.class);

        ExchangeSystem example1 = new ExchangeSystem();
        example1.setSubjectDN(loginName);
        List<ExchangeSystem> exchangeSystems = exchangeDataConfigService.findByExample(example1);
        if (exchangeSystems.isEmpty()) {
            throw new RuntimeException("找不到证书主体为[" + principal.getName() + "]的接入系统");
        }
        ExchangeSystem exchangeSystem = exchangeSystems.get(0);
        CommonUnit commonUnit = unitApiFacade.getCommonUnitById(exchangeSystem.getUnitId());

        ExchangeDataTypeService exchangeDataTypeService = ApplicationContextHolder
                .getBean(ExchangeDataTypeService.class);
        // ExchangeDataType example2 = new ExchangeDataType();
        // example2.setId(exchangeSystem.getTypeId());
        // List<ExchangeDataType> exchangeDataTypes =
        // exchangeDataTypeService.findByExample(example2);

        List<ExchangeDataType> exchangeDataTypes = exchangeDataTypeService.getExchangeDataTypesByTypeIds(exchangeSystem
                .getTypeId());
        if (exchangeDataTypes.isEmpty()) {
            throw new RuntimeException("接入系统[" + exchangeSystem.getName() + "]的数据类型没有设置业务类型");
        }
        ExchangeDataType exchangeDataType = exchangeDataTypes.get(0);

        // List<User> users =
        // unitApiFacade.getUserList(exchangeDataType.getBusinessTypeId(),
        // exchangeSystem.getUnitId(),
        // "1");
        // 获取单位业务负责人
        List<OrgUserVo> users = unitApiFacade.getBusinessUnitManagerById(exchangeDataType.getBusinessTypeId(),
                exchangeSystem.getUnitId());
        if (users.isEmpty()) {
            throw new RuntimeException("接入系统[" + exchangeSystem.getName() + "]所在单位没有设置业务管理员");
        }
        OrgUserVo user = users.get(0);

        TenantFacadeService tenantService = ApplicationContextHolder.getBean(TenantFacadeService.class);
        Tenant tenant = tenantService.getByAccount(tenantId);
        UserDetails userDetails = new DefaultUserDetails(tenant, user, AuthorityUtils.createAuthorityList());
        // userDetails.setCommonUnit(exchangeDataType.getBusinessTypeId(),
        // commonUnit);
        CommonUnit unit = new CommonUnit();
        BeanUtils.copyProperties(commonUnit, unit);
        userDetails.putExtraData("commonUnit", unit);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userDetails,
                userDetails.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authRequest);
    }
}
