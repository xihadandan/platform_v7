/*
 * @(#)2016年9月5日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description: 门面服务功能，用于JDS数据服务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年9月5日.1	zhulh		2016年9月5日		Create
 * </pre>
 * @date 2016年9月5日
 */
@Service
@Transactional(readOnly = true)
public class FacadeServiceAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    @Autowired
    private List<BaseService> services;

    // 过滤的门面服务包
    private String requireFacedeServicePackage = "facade.service";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.FacedeService;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        for (BaseService service : services) {
            Class<?> userServiceCls = ClassUtils.getUserClass(service.getClass());
            Class<?>[] serviceClasses = ClassUtils.getAllInterfacesForClass(userServiceCls);
            for (Class<?> serviceCls : serviceClasses) {
                String packageName = ClassUtils.getPackageName(serviceCls);
                if (StringUtils.isBlank(packageName) || !StringUtils.contains(packageName,
                        requireFacedeServicePackage)) {
                    continue;
                }
                Method[] methods = serviceCls.getMethods();

                //按同名方法分组
                ImmutableListMultimap<String, Method> methodMap = Multimaps.index(
                        Lists.newArrayList(methods), new Function<Method, String>() {
                            @Nullable
                            @Override
                            public String apply(@Nullable Method method) {
                                return method.getName();
                            }
                        });
                Map<String, Collection<Method>> methodGroupMap = methodMap.asMap();
                Set<String> methodKeys = methodGroupMap.keySet();
                for (String m : methodKeys) {
                    List<Method> methodCollection = Lists.newArrayList(methodGroupMap.get(m));
                    if (methodCollection.size() > 1) {
                        for (int i = 1; i <= methodCollection.size(); i++) {
                            method2AppFunctionSource(methodCollection.get(i - 1), serviceCls,
                                    appFunctionSources, i);
                        }
                        continue;
                    }
                    method2AppFunctionSource(methodCollection.get(0), serviceCls,
                            appFunctionSources, 0);
                }


            }
        }
        return Arrays.asList(appFunctionSources.toArray(new AppFunctionSource[0]));
    }


    private void method2AppFunctionSource(Method method, Class<?> clazz,
                                          List<AppFunctionSource> appFunctionSources, int order) {
        String serviceName = ClassUtils.getShortName(clazz);
        String methodName = method.getName();
        String facadeService = StringUtils.uncapitalize(serviceName + "." + methodName);
        String uuid = DigestUtils.md5Hex(facadeService);
        String fullName = ClassUtils.getQualifiedName(clazz) + "." + methodName;
        String name = "门面服务功能_" + facadeService;
        String id = facadeService;
        String code = facadeService.hashCode() + StringUtils.EMPTY;
        String category = getAppFunctionType();
        String remark = com.wellsoft.pt.jpa.util.ClassUtils.getClassMethodDescriptions().get(
                clazz.getCanonicalName() + "." + methodName + (order == 0 ? "" : "#" + order));
        Map<String, Object> extras = new HashMap<String, Object>();
        appFunctionSources.add(
                new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null,
                        category, false, category, false, extras, remark));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader#getObjectIdentities(com.wellsoft.pt.app.function.AppFunctionSource)
     */
    @Override
    public Collection<Object> getObjectIdentities(AppFunctionSource appFunctionSource) {
        List<Object> objectIdentities = new ArrayList<Object>();
        objectIdentities.add(appFunctionSource.getId());
        return objectIdentities;
    }

}
