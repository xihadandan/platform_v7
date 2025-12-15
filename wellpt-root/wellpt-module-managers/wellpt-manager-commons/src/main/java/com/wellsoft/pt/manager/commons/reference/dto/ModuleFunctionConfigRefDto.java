package com.wellsoft.pt.manager.commons.reference.dto;

import com.wellsoft.pt.manager.commons.reference.entity.ModuleFunctionConfigRefEntity;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/6
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/6    chenq		2019/6/6		Create
 * </pre>
 */
public class ModuleFunctionConfigRefDto extends ModuleFunctionConfigRefEntity {
    private static final long serialVersionUID = -6691092819718529092L;

    private String functionType;//功能类型

    private String entityClass;//引用配置保存的实体类

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }
}
