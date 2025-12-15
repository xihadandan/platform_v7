package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作日服务响应
 *
 * @author jackl
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-2-3.1	jackl		2015-2-3		Create
 * </pre>
 * @date 2015-2-3
 */
public class WorkHourResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2421534821706457462L;

    private Map<String, String> pMap = new HashMap<String, String>();

    public void setValue(String key, String value) {
        pMap.put(key, value);
    }

    public String getValue(String key) {
        return pMap.get(key);
    }

    public Map<String, String> getpMap() {
        return pMap;
    }

    public void setpMap(Map<String, String> pMap) {
        this.pMap = pMap;
    }

}
