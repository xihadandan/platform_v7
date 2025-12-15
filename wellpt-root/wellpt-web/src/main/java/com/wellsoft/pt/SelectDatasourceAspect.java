package com.wellsoft.pt;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.wellsoft.pt.basicdata.datastore.service.DbLinkConfigService;
import com.wellsoft.pt.jpa.datasource.DataSourceContextHolder;
import com.wellsoft.pt.jpa.datasource.SelectDatasource;
import groovy.util.logging.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Objects;


/**
 * Description: 数据源切换切面
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本  修改人    修改日期      修改内容
 * V1.0   liuxj    2025/1/16    Create
 * </pre>
 * @date 2025/1/16
 */
@Aspect
@Component
@Slf4j
public class SelectDatasourceAspect {

    @Resource
    private DynamicDataSource dynamicDataSource;

    @Resource
    private DbLinkConfigService dbLinkConfigService;


    @Pointcut("@annotation(com.wellsoft.pt.jpa.datasource.SelectDatasource)")
    public void switchDataSource() {
    }

    @Around("switchDataSource()")
    public Object datasourceAround(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        MethodSignature signature = (MethodSignature) point.getSignature();
        HttpServletRequest request = attributes.getRequest();
        String dbLinkConfUuid = request.getHeader("dbLinkConfUuid");
        Method method = signature.getMethod();
        if (StrUtil.isNotBlank(dbLinkConfUuid)) {
            SelectDatasource selectDatasource = method.getAnnotation(SelectDatasource.class);
            if (Objects.nonNull(selectDatasource)) {
                if (!dynamicDataSource.existDataSource(dbLinkConfUuid)) {
                    DataSource dataSource = dbLinkConfigService.buildSQLDataSource(Convert.toLong(dbLinkConfUuid));
                    if (dataSource != null) {
                        dynamicDataSource.addDataSource(dbLinkConfUuid, dataSource);
                    }
                }
                DataSourceContextHolder.setDataSource(dbLinkConfUuid);
            }
            try {
                return point.proceed();
            } finally {
                DataSourceContextHolder.removeDataSource();
            }
        }
        return point.proceed();
    }
}