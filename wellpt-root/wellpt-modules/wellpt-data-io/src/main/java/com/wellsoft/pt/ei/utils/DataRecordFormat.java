package com.wellsoft.pt.ei.utils;

import com.alibaba.fastjson.JSONObject;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 * 数据记录处理
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/19   Create
 * </pre>
 */
@Component
public class DataRecordFormat {

    public String dataType2ZH(String dataTypeJsonString, String dataType) {
        if (StringUtils.isBlank(dataTypeJsonString)) {
            return null;
        }

        String[] orgArr = new String[]{"version", "user", "group", "type", "duty", "rank"};
        String[] emailArr = new String[]{"email", "folder", "contact_group", "contact", "tag"};
        try {
            StringBuffer sb = new StringBuffer();
            JSONObject dataTypeJson = JSONObject.parseObject(dataTypeJsonString);
            //组织数据
            if (dataTypeJson.containsKey(DataExportConstants.DATA_TYPE_ORG_DATA)) {
                JSONObject orgData = dataTypeJson.getJSONObject(DataExportConstants.DATA_TYPE_ORG_DATA);

                List<String> collect = Arrays.asList(orgArr).stream()
                        .filter(key -> DataExportConstants.INTEGER_TRUE.equals(orgData.get(key)))
                        .map(key -> DataExportConstants.getDataTypeName(key, dataType))
                        .collect(Collectors.toList());
                if (!collect.isEmpty()) {
                    sb.append(DataExportConstants.DATA_TYPE_ORG);
                    sb.append("(" + StringUtils.join(collect, "，") + ")");
                }
            }
            //流程数据
            if (dataTypeJson.containsKey(DataExportConstants.DATA_TYPE_FLOW_DATA) && DataExportConstants.INTEGER_TRUE.equals(dataTypeJson.get(DataExportConstants.DATA_TYPE_FLOW_DATA))) {
                if (!"".equals(sb.toString())) {
                    sb.append("<br/>");
                }
                sb.append(DataExportConstants.DATA_TYPE_FLOW + "(" + DataExportConstants.DATA_TYPE_FLOW_ALL + ")");
            }
            //邮件
            if (dataTypeJson.containsKey(DataExportConstants.DATA_TYPE_MAIL_DATA)) {
                JSONObject emailData = dataTypeJson.getJSONObject(DataExportConstants.DATA_TYPE_MAIL_DATA);
                List<String> collect = Arrays.asList(emailArr).stream()
                        .filter(key -> DataExportConstants.INTEGER_TRUE.equals(emailData.get(key)))
                        .map(key -> DataExportConstants.getDataTypeName(key, ""))
                        .collect(Collectors.toList());
                boolean emailExist = false;
                if (emailData.containsKey(DataExportConstants.DATA_TYPE_MAIL_EMAIL_CODE)) {
                    JSONObject email = emailData.getJSONObject(DataExportConstants.DATA_TYPE_MAIL_EMAIL_CODE);
                    for (Map.Entry<String, Object> entry : email.entrySet()) {
                        if (DataExportConstants.INTEGER_TRUE.equals(entry.getValue())) {
                            emailExist = true;
                            break;
                        }
                    }
                }
                if (emailExist || !collect.isEmpty()) {
                    if (!"".equals(sb.toString())) {
                        sb.append("<br/>");
                    }
                    sb.append(DataExportConstants.DATA_TYPE_MAIL);
                    List<String> emailList = new ArrayList<>();
                    if (emailExist) {
                        emailList.add(DataExportConstants.DATA_TYPE_MAIL_EMAIL);
                    }
                    emailList.addAll(collect);
                    sb.append("(" + StringUtils.join(emailList, "，") + ")");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
