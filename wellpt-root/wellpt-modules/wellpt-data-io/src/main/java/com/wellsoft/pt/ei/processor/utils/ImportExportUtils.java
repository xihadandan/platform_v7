package com.wellsoft.pt.ei.processor.utils;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wellsoft.pt.ei.constants.ExportFieldTypeEnum.*;

public class ImportExportUtils {

    public static String getDictValueStr(String fieldValue, Map<String, Object> dictMap) {
        if (StringUtils.isBlank(fieldValue)) {
            return StringUtils.EMPTY;
        }

        String splitValue = Separator.COMMA.getValue();
        if (fieldValue.contains(Separator.COMMA.getValue())) {
            splitValue = Separator.COMMA.getValue();
        } else if (fieldValue.contains(Separator.SEMICOLON.getValue())) {
            splitValue = Separator.SEMICOLON.getValue();
        }
        String[] split = StringUtils.split(fieldValue, splitValue);
        List<String> displayValueList = new ArrayList<>();
        for (String s : split) {
            Object o = dictMap.get(s);
            if (o != null) {
                displayValueList.add(o.toString());
            }
        }

        return StringUtils.join(displayValueList, Separator.SEMICOLON.getValue());
    }

    public static Map<String, String> getDyFormInputModeDataTypeMap() {
        Map<String, String> dyFormInputModeDataTypeMap = new HashMap<>();
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_Text, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_TEXTAREA, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_CKEDIT, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_NUMBER, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_DATE, DATE.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_RADIO, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_CHECKBOX, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_SELECTMUTILFASE, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_SELECT, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_COMBOSELECT, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_TREESELECT, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_SerialNumber, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_UNEDITSERIALUMBER, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECT, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTSTAFF, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTDEPARTMENT, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTSTADEP, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTADDRESS, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTJOB, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTPUBLICGROUP, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTMYDEPT, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTMYPARENTDEPT, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTMYUNIT, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTPUBLICGROUPSTA, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECTGROUP, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ORGSELECT2, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_TIMEEMPLOY, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_TIMEEMPLOYFORMEET, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_TIMEEMPLOYFORCAR, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_TIMEEMPLOYFORDRIVER, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_VIEWDISPLAY, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_DIALOG, STRING.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ACCESSORY, FILE.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ACCESSORY3, FILE.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ACCESSORY2, FILE.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ACCESSORY1, FILE.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_ACCESSORYIMG, FILE.getValue());
        dyFormInputModeDataTypeMap.put(DyFormConfig.INPUTMODE_EMBEDDED, STRING.getValue());
        return dyFormInputModeDataTypeMap;
    }

}
