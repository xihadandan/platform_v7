package com.wellsoft.pt.security.passport.service.impl;

import com.wellsoft.pt.security.passport.service.AbstractTokenService;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年12月16日   chenq	 Create
 * </pre>
 */
@Service
@Conditional(MockTokenServiceImpl.MockTokenCondition.class)
public class MockTokenServiceImpl extends AbstractTokenService {
    @Override
    public void verifyToken(String token, String appId) throws AccessDeniedException {
        try {
            JwtTokenUtil.getClaims(token);
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }


    public static class MockTokenCondition implements Condition {
        public MockTokenCondition() {
        }

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return "true".equalsIgnoreCase(context.getEnvironment().getProperty("test.mockeTokenService.enable"));
        }
    }


}
