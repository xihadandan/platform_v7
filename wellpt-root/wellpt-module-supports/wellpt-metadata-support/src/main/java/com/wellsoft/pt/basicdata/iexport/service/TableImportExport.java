package com.wellsoft.pt.basicdata.iexport.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月08日   chenq	 Create
 * </pre>
 */
public interface TableImportExport<UUID extends Serializable> {

    Map<String, Object> getRows(UUID uuid);

    String table();


}
