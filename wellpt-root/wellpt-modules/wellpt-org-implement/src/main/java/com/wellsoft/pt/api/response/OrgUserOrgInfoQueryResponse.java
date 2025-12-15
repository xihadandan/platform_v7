package com.wellsoft.pt.api.response;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.api.WellptResponse;

import java.util.List;

/**
 * Description: 用户组织信息查询相应
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-19.1  zhengky	2014-9-19	  Create
 * </pre>
 * @date 2014-9-19
 */
public class OrgUserOrgInfoQueryResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1861967946134370712L;
    private String data;
    private List<QueryItem> dataList;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     * @return the dataList
     */
    public List<QueryItem> getDataList() {
        return dataList;
    }

    /**
     * @param dataList 要设置的dataList
     */
    public void setDataList(List<QueryItem> dataList) {
        this.dataList = dataList;
    }

}
