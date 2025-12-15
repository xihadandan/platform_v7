/*
 * @(#)Jan 2, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.context.util.json.JsonUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 2, 2018.1	zhulh		Jan 2, 2018		Create
 * </pre>
 * @date Jan 2, 2018
 */
public class FileActionExecutorMethod {

    private FileActionExecutor<?, ?> fileActionExecutor;

    private Class<?> fileActionParamType;

    private Class<?> fileActionResultType;

    /**
     * @return the fileActionExecutor
     */
    public FileActionExecutor<?, ?> getFileActionExecutor() {
        return fileActionExecutor;
    }

    /**
     * @param fileActionExecutor 要设置的fileActionExecutor
     */
    public void setFileActionExecutor(FileActionExecutor<?, ?> fileActionExecutor) {
        this.fileActionExecutor = fileActionExecutor;
    }

    /**
     * @return the fileActionParamType
     */
    public Class<?> getFileActionParamType() {
        return fileActionParamType;
    }

    /**
     * @param fileActionParamType 要设置的fileActionParamType
     */
    public void setFileActionParamType(Class<?> fileActionParamType) {
        this.fileActionParamType = fileActionParamType;
    }

    /**
     * @return the fileActionResultType
     */
    public Class<?> getFileActionResultType() {
        return fileActionResultType;
    }

    /**
     * @param fileActionResultType 要设置的fileActionResultType
     */
    public void setFileActionResultType(Class<?> fileActionResultType) {
        this.fileActionResultType = fileActionResultType;
    }

    /**
     * @param actionParam
     */
    public Object execute(String json) {
        try {
            return this.fileActionExecutor.getClass().getMethod("execute", FileActionParam.class)
                    .invoke(this.fileActionExecutor, JsonUtils.json2Object(json, fileActionParamType));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends FileActionResult<?>> T execute(FileActionParam<?> listFolderAction) {
        try {
            return (T) this.fileActionExecutor.getClass().getMethod("execute", FileActionParam.class)
                    .invoke(this.fileActionExecutor, listFolderAction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
