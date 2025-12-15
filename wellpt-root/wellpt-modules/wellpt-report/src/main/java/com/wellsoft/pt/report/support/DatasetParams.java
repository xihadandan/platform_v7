package com.wellsoft.pt.report.support;

import java.io.Serializable;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/5/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/14    chenq		2019/5/14		Create
 * </pre>
 */
public class DatasetParams implements Serializable {
    private static final long serialVersionUID = -8031044569700299595L;

    private String datasetLoadClass;

    private Map<String, Object> params;

    public String getDatasetLoadClass() {
        return datasetLoadClass;
    }

    public void setDatasetLoadClass(String datasetLoadClass) {
        this.datasetLoadClass = datasetLoadClass;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
