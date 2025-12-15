package com.wellsoft.pt.basicdata.datastore.support.export;

import javax.activation.DataSource;

public interface DataStoreExport {
    /**
     * 文件导出
     *
     * @param params
     * @return
     */
    public DataSource export(ExportParams params);

    /**
     * 类型
     *
     * @return
     */
    public String getType();

    /**
     * 名称
     *
     * @return
     */
    public String getName();

}
