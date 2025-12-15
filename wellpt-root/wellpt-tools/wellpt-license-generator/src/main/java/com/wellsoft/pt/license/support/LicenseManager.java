/*
 * @(#)2019年5月24日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.support;

import java.io.File;
import java.io.OutputStream;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月24日.1	zhulh		2019年5月24日		Create
 * </pre>
 * @date 2019年5月24日
 */
public interface LicenseManager {

    /**
     * 生成许可证
     *
     * @param content
     * @param licenseFile
     */
    void store(LicenseContent content, File licenseFile);

    /**
     * 生成许可证
     *
     * @param content
     * @param outputStream
     */
    void store(LicenseContent content, OutputStream outputStream);

    /**
     * 安装许可证
     *
     * @param licenseFile
     */
    void install(File licenseFile);

    /**
     * 验证许可证
     *
     * @return
     */
    String verify();


    /**
     * 签名
     *
     * @param content
     * @return
     */
    String sign(String content);
}
