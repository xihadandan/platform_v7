package com.wellsoft.pt.dyform.implement.definition.control.type;

import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumInputModeType;
import com.wellsoft.pt.dyform.implement.definition.control.bean.FieldPropertyBean;
import com.wellsoft.pt.dyform.implement.definition.control.enums.EnumCommonFieldProperties;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractControlType implements ControlType {

    @Override
    public abstract String getInputMode();

    @Override
    public abstract boolean isValueAsMap();

    @Override
    public abstract EnumInputModeType getInputModeType();

    @Override
    public boolean isInputModeAsAttach() {
        return this.getInputModeType().equals(EnumInputModeType.ATTACH);
    }

    @Override
    public boolean isInputModeAsDate() {
        return this.getInputModeType().equals(EnumInputModeType.DATE);
    }

    @Override
    public boolean isInputModeAsNumber() {
        return this.getInputModeType().equals(EnumInputModeType.NUMBER);
    }

	/*	@Override
		public boolean isInputModeAsOrg() {
			return this.getInputModeType().equals(EnumInputModeType.ORG);
		}*/

    @Override
    public boolean isInputModeAsText() {
        return this.getInputModeType().equals(EnumInputModeType.TEXT);
    }

    @Override
    public final List<FieldPropertyBean> getFieldPropertys() {
        List<FieldPropertyBean> beans = new ArrayList<FieldPropertyBean>();
		/*permissions.add(new BooleanConfigBean("readOnly", "只读"));
		permissions.add(new BooleanConfigBean("editable", "可编辑"));
		permissions.add(new BooleanConfigBean("required", "必填"));
		permissions.add(new BooleanConfigBean("hidden", "隐藏"));*/
        beans.addAll(EnumCommonFieldProperties.toFieldPropertyBeanList());
        customedFieldPropertys(beans);
        return beans;
    }

    /**
     * 可通过该方法对权限进行处理，例如删除权限项或者新增权限项
     *
     * @param permission
     * @return
     */
    protected void customedFieldPropertys(List<FieldPropertyBean> permissions) {
    }

}
