/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.service;

import com.wellsoft.pt.api.domain.RequestParam;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

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
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public interface WellptWebService {

    String execute(RequestParam param, MultipartBody multipartBody, HttpServletRequest httpServletRequest,
                   HttpServletResponse httpServletResponse);

    String executeFile(@Multipart MultipartBody multipartBody, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse);

}
