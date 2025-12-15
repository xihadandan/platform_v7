package com.wellsoft.context.component.select2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: select2 分组 数据类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年1月29日.1	zyguo		2016年1月29日		Create
 * </pre>
 * @date 2018年1月29日
 */
@ApiModel("select2分组数据项")
public class Select2GroupBean implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7555676526943397065L;
    @ApiModelProperty("分组名称")
    private String text;
    @ApiModelProperty("分组数据列表")
    private List<Select2DataBean> children = new ArrayList<Select2DataBean>();

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text 要设置的text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the children
     */
    public List<Select2DataBean> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(List<Select2DataBean> children) {
        this.children = children;
    }

}
