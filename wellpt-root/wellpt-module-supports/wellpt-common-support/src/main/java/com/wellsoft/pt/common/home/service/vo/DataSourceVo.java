package com.wellsoft.pt.common.home.service.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yt
 * @title: TypeVo
 * @date 2020/7/20 17:25
 * <p>
 * 数据源vo
 */
public class DataSourceVo implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 颜色
     */
    private String color;

    /**
     * 提示信息
     */
    private String tips;

    /**
     * 数据项集合
     */
    private List<DataItemVo> items = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public List<DataItemVo> getItems() {
        return items;
    }

    public void setItems(List<DataItemVo> items) {
        this.items = items;
    }
}
