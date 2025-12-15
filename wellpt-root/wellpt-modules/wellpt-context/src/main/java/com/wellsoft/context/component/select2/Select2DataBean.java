package com.wellsoft.context.component.select2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: Select2 数据类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年1月29日.1	Xiem		2016年1月29日		Create
 * </pre>
 * @date 2016年1月29日
 */
@ApiModel(value = "下拉框")
public class Select2DataBean {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("显示文本")
    private String text;

    public Select2DataBean(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode()) + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Select2DataBean)) {
            return false;
        }
        Select2DataBean o = (Select2DataBean) obj;
        if ((id == null && o.getId() != null) || (id != null && !id.equals(o.getId()))) {
            return false;
        }
        if ((text == null && o.getText() != null) || (text != null && !text.equals(o.getText()))) {
            return false;
        }
        return true;
    }
}
