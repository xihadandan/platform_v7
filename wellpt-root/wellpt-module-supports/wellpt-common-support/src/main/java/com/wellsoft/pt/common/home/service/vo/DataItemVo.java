package com.wellsoft.pt.common.home.service.vo;

import java.io.Serializable;

/**
 * @author yt
 * @title: DataItemVo
 * @date 2020/7/20 17:35
 */
public class DataItemVo implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 数量
     */
    private long count;

    /**
     * url
     */
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
