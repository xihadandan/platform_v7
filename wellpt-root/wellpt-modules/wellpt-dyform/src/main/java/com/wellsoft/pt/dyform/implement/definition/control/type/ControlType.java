package com.wellsoft.pt.dyform.implement.definition.control.type;

import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumInputModeType;
import com.wellsoft.pt.dyform.implement.definition.control.bean.FieldPropertyBean;

import java.util.List;

public interface ControlType {

    /**
     * 控件类型
     *
     * @return
     */
    String getInputMode();

    /**
     * 值是否为Map类型,返回true时，可以通过dyformData.setValueByMap()来设值,
     * getDiaplayValue则返回key:value中的value
     *
     * @return
     */
    public boolean isValueAsMap();

    /**
     * 返回控件的类型
     *
     * @return
     */
    public EnumInputModeType getInputModeType();

    /**
     * 附件控件
     *
     * @return
     */
    public boolean isInputModeAsAttach();

    /**
     * 日期控件
     *
     * @return
     */
    public boolean isInputModeAsDate();

    /**
     * 数值控件
     *
     * @return
     */
    public boolean isInputModeAsNumber();

    /**
     * 组织控件
     * @return
     */
	/*
	public boolean isInputModeAsOrg();*/

    /**
     * 文本类型控件
     *
     * @return
     */
    public boolean isInputModeAsText();

    /**
     * 返回控件配置列表,<b><i>目前只提供布尔类型的配置列表</i></b>
     *
     * @return
     */
    public List<FieldPropertyBean> getFieldPropertys();

}
