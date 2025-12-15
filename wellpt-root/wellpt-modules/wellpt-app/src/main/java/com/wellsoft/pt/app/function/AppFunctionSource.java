/*
 * @(#)2016年8月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function;

import java.io.Serializable;
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
 * 2016年8月2日.1	zhulh		2016年8月2日		Create
 * </pre>
 * @date 2016年8月2日
 */
public interface AppFunctionSource extends Serializable {
    // UUID
    String getUuid();

    // 名称
    String getName();

    // 全名
    String getFullName();

    // ID
    String getId();

    // 编号
    String getCode();

    // 动作
    String getAction();

    // 数据
    String getData();

    // 分类
    String getCategory();

    // 功能信息是否可导出
    boolean exportable();

    // 功能导出类型
    String getExportType();

    // 功能信息ID是否可重复
    boolean repeatable();

    // 额外信息
    Map<String, Object> getExtras();

    String getRemark();


}
