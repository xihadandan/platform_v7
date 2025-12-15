/*
 * @(#)Jan 5, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.pt.dms.file.action.FileAction;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 5, 2018.1	zhulh		Jan 5, 2018		Create
 * </pre>
 * @date Jan 5, 2018
 */
public class FileActionExecutorMethodFactory {

    private static Map<String, FileAction> fileActionMap = new HashMap<String, FileAction>();
    private static Map<Class<?>, FileActionExecutorMethod> fileActionExecutorMap = new HashMap<Class<?>, FileActionExecutorMethod>();

    public static final FileActionExecutorMethod getFileActionExecutorMethod(String action) {
        FileAction fileAction = getFileAction(action);
        if (fileActionExecutorMap.isEmpty() || !fileActionExecutorMap.containsKey(fileAction.getClass())) {
            return getFileActionExecutorMethod(fileAction);
        }
        return fileActionExecutorMap.get(fileAction.getClass());
    }

    /**
     * 如何描述该方法
     *
     * @param action
     * @return
     */
    private static FileAction getFileAction(String action) {
        if (fileActionMap.isEmpty()) {
            Map<String, FileAction> actionMap = ApplicationContextHolder.getApplicationContext().getBeansOfType(
                    FileAction.class);
            for (Entry<String, FileAction> entry : actionMap.entrySet()) {
                FileAction fileAction = entry.getValue();
                fileActionMap.put(fileAction.getId(), fileAction);
            }
        }
        return fileActionMap.get(action);
    }

    /**
     * @param fileAction
     * @return
     */
    private static FileActionExecutorMethod getFileActionExecutorMethod(FileAction fileAction) {
        if (fileActionExecutorMap.isEmpty() || !fileActionExecutorMap.containsKey(fileAction.getClass())) {
            Map<String, FileActionExecutor> actionExecutorMap = ApplicationContextHolder.getApplicationContext()
                    .getBeansOfType(FileActionExecutor.class);
            for (Entry<String, FileActionExecutor> entry : actionExecutorMap.entrySet()) {
                FileActionExecutor fileActionExecutor = entry.getValue();
                Class<?> fileActionParamType = null;
                Class<?> fileActionResultType = null;
                Class<?> actionClass = null;
                Type[] genericInterfaces = fileActionExecutor.getClass().getGenericInterfaces();
                if (genericInterfaces.length > 0) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];
                    Type[] argumentTypes = parameterizedType.getActualTypeArguments();
                    fileActionParamType = (Class<?>) argumentTypes[0];
                    fileActionResultType = (Class<?>) argumentTypes[1];
                    actionClass = ReflectionUtils.getSuperClassGenricType(fileActionParamType);
                } else {
                    fileActionParamType = ReflectionUtils.getSuperClassGenricType(fileActionExecutor.getClass(), 0);
                    fileActionResultType = ReflectionUtils.getSuperClassGenricType(fileActionExecutor.getClass(), 1);
                    actionClass = ReflectionUtils.getSuperClassGenricType(fileActionParamType);
                }

                FileActionExecutorMethod fileActionExecutorMethod = new FileActionExecutorMethod();
                fileActionExecutorMethod.setFileActionExecutor(fileActionExecutor);
                fileActionExecutorMethod.setFileActionParamType(fileActionParamType);
                fileActionExecutorMethod.setFileActionResultType(fileActionResultType);
                fileActionExecutorMap.put(actionClass, fileActionExecutorMethod);
            }
        }
        return fileActionExecutorMap.get(fileAction.getClass());
    }
}
