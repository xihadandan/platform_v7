package com.wellsoft.pt.bpm.engine.parser;

import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.dyform.implement.definition.control.enums.EnumCommonFieldProperties;
import com.wellsoft.pt.dyform.implement.definition.control.enums.EnumFileuploadFieldProperties;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author wujx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-7-28.1	wujx	2016-7-28		Create
 * </pre>
 * @date 2016-7-28
 */
public class TaskFieldPropertyParser {

    public TaskFieldProperty parser(TaskElement taskElement) {
        TaskFieldProperty taskFieldProperty = new TaskFieldProperty();
        String fieldPropertyValues = taskElement.getFieldPropertyValues();
        if (StringUtils.isNotBlank(fieldPropertyValues)) {
            String[] fieldPropertyValueArray = fieldPropertyValues.split(";");
            for (String fieldPropValue : fieldPropertyValueArray) {
                String fieldName = fieldPropValue.substring(0, fieldPropValue.indexOf("."));
                String fieldProps = fieldPropValue.substring(fieldPropValue.indexOf(".") + 1);
                for (String fieldProp : fieldProps.split("\\.")) {
                    // FIXME:EnumCommonFieldProperties
                    if (EnumCommonFieldProperties.EDITABLE.getValue().equals(fieldProp)) {
                        taskFieldProperty.getEditFields().add(fieldName);
                    } else if (EnumCommonFieldProperties.READONLY.getValue().equals(fieldProp)) {
                        taskFieldProperty.getOnlyReadFields().add(fieldName);
                    } else if (EnumCommonFieldProperties.HIDDEN.getValue().equals(fieldProp)) {
                        taskFieldProperty.getHideFields().add(fieldName);
                    } else if (EnumCommonFieldProperties.REQURIED.getValue().equals(fieldProp)) {
                        taskFieldProperty.getNotNullFields().add(fieldName);
                    } else if (EnumFileuploadFieldProperties.ALLOW_UPLOAD.getValue().equals(fieldProp)) {
                        taskFieldProperty.getAllowUploadFields().add(fieldName);
                    } else if (EnumFileuploadFieldProperties.ALLOW_DOWNLOAD.getValue().equals(fieldProp)) {
                        taskFieldProperty.getAllowDownloadFields().add(fieldName);
                    } else if (EnumFileuploadFieldProperties.ALLOW_DELETED.getValue().equals(fieldProp)) {
                        taskFieldProperty.getAllowDeleteFields().add(fieldName);
                    }
                }
            }

        }
        return taskFieldProperty;
    }
}
