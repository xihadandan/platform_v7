/*
 * @(#)Jan 17, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.view;

import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 17, 2018.1	zhulh		Jan 17, 2018		Create
 * </pre>
 * @date Jan 17, 2018
 */
public interface FileDataView extends Ordered {

    String getName();

    String getType();

    void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

}
