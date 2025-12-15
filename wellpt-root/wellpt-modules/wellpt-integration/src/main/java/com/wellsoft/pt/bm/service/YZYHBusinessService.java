package com.wellsoft.pt.bm.service;

import java.io.File;
import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-7-5.1	Administrator		2016-7-5		Create
 * </pre>
 * @date 2016-7-5
 */
public interface YZYHBusinessService {
    public File generateExcel(Collection<String> uuids) throws Exception;
}
