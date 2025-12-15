package com.wellsoft.pt.ureport.datastore;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description:数据DTO的数据仓库
 *
 * @author chenq
 * @date 2018/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/25    chenq		2018/9/25		Create
 * </pre>
 */
public interface DtoDatastore {


    /**
     * 返回一个map的集合类型
     *
     * @param dsName
     * @param datasetName
     * @param parameters
     * @return
     */
    public List<Map<String, Object>> loadReportMapData(String dsName, String datasetName,
                                                       Map<String, Object> parameters);

    /**
     * 返回一个可序列号化的对象集合类型
     *
     * @param dsName
     * @param datasetName
     * @param parameters
     * @return
     */
    public List<? extends Serializable> loadReportObjectData(String dsName, String datasetName,
                                                             Map<String, Object> parameters);
}
