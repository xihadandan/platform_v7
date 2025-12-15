/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.view;

import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月26日.1	zhulh		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
public interface FileViewer {

    boolean matches(String contentType);

    String getViewUrl(DmsFile dmsFile, HttpServletRequest request, HttpServletResponse response);

    DyFormData getDyformData(DmsFile dmsFile);
}
