/*
 * @(#)2020年1月19日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.convert;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月19日.1	zhongzh		2020年1月19日		Create
 * </pre>
 * @date 2020年1月19日
 */
public interface FileConvertService {

    public abstract void officeToOFD(File srcFile, OutputStream out, Map<String, String> metas);

    public abstract void officeToPDF(File srcFile, OutputStream out, Map<String, String> metas);

//	public abstract void officeToPDF(File srcFile, String targetPath);

}
