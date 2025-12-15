/*
 * @(#)2013-12-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support.zipfile;

import org.apache.tools.zip.ZipFile;

import java.io.File;
import java.util.List;
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
 * 2013-12-26.1	zhulh		2013-12-26		Create
 * </pre>
 * @date 2013-12-26
 */
public interface ZipFileHandler {

    Map<String, String> handler(ZipFile zipFile) throws Exception;

    File generateZipFile(List<ZipFileData> datas) throws Exception;

}
