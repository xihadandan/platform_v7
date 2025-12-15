/*
 * @(#)2018年7月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.file;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Description: 自定义处理下载输出流
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年7月19日.1	zhulh		2018年7月19日		Create
 * </pre>
 * @date 2018年7月19日
 */
public interface OutputStreamDownloadHanlder {

    void handle(OutputStream output) throws IOException;

}
