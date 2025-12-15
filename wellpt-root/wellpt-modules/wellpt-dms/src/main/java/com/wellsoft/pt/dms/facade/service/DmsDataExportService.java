package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.pt.basicdata.datastore.support.export.ExportRows;

import javax.activation.DataSource;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年06月14日   chenq	 Create
 * </pre>
 */
public interface DmsDataExportService {

    public DataSource exportRows(ExportRows rows);

}
