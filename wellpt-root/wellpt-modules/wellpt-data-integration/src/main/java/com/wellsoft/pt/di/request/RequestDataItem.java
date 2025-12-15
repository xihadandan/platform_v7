package com.wellsoft.pt.di.request;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/13    chenq		2019/8/13		Create
 * </pre>
 */
public class RequestDataItem<T extends Serializable> implements Serializable {


    private static final long serialVersionUID = -4363116602136172158L;
    // (统一查询号)
    protected String dataId;

    // 版本号
    protected int recVer;

    protected Map<String, String> params;

    protected List<T> itemList;

    protected String text;

    protected List<RequestStream> streamingDatas = Lists.newArrayList();

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public int getRecVer() {
        return recVer;
    }

    public void setRecVer(int recVer) {
        this.recVer = recVer;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<RequestStream> getStreamingDatas() {
        return streamingDatas;
    }

    public void setStreamingDatas(List<RequestStream> streamingDatas) {
        this.streamingDatas = streamingDatas;
    }

    public List<T> getItemList() {
        return itemList;
    }

    public void setItemList(List<T> itemList) {
        this.itemList = itemList;
    }
}
