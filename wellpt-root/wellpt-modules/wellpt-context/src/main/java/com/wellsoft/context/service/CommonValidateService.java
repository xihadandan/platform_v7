/*
 * @(#)2013-5-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.service;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-30.1	rzhu		2013-5-30		Create
 * </pre>
 * @date 2013-5-30
 */
public interface CommonValidateService {
    boolean checkExists(String checkType, String fieldName, String fieldValue);

    /**
     * @param uuid
     * @param checkType
     * @param fieldName
     * @param fieldValue
     * @return
     */
    boolean checkUnique(String uuid, String checkType, String fieldName, String fieldValue);
}
