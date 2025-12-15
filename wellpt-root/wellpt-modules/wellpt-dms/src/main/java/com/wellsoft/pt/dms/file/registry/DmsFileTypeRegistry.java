/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.registry;

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
 * 2018年9月26日.1	zhulh		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
public interface DmsFileTypeRegistry {

    /**
     * 注册文件类型
     *
     * @param type
     * @param name
     * @return
     */
    DmsFileTypeRegistry register(String type, String name, boolean downloadable);

    /**
     * 判断文件类型是否已注册
     *
     * @param type
     * @return
     */
    boolean isRegistered(String type);

    /**
     * 根据类型获取名称
     *
     * @param type
     * @return
     */
    String getName(String type);

    /**
     * 获取已注册的类型名称
     *
     * @return
     */
    Map<String, String> getTypeNameAsMap();

    /**
     * 获取已注册的可下载类型
     *
     * @return
     */
    Map<String, Boolean> getDownloadableAsMap();

}
