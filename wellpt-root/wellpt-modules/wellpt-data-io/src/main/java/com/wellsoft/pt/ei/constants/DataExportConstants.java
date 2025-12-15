package com.wellsoft.pt.ei.constants;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.ei.entity.DataExportTask;
import com.wellsoft.pt.ei.entity.DataImportTask;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Description: 数据导出常量类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/16.1	liuyz		2021/9/16		Create
 * </pre>
 * @date 2021/9/16
 */
public class DataExportConstants {
    /**
     * 数据类型
     */
    public static final String DATA_TYPE_ORG = "组织数据";
    public static final String DATA_TYPE_ORG_VERSION = "组织版本";
    public static final String DATA_TYPE_ORG_TREE = "组织架构";
    public static final String DATA_TYPE_ORG_TREE_NODE = "组织架构节点";
    public static final String DATA_TYPE_ORG_VERSION_O = "组织节点";
    public static final String DATA_TYPE_ORG_VERSION_B = "业务单位";
    public static final String DATA_TYPE_ORG_VERSION_D = "部门";
    public static final String DATA_TYPE_ORG_VERSION_J = "职位";
    public static final String DATA_TYPE_ORG_VERSION_V = "引用系统单位";
    public static final String DATA_TYPE_ORG_USER = "用户";
    public static final String DATA_TYPE_ORG_GROUP = "群组";
    public static final String DATA_TYPE_ORG_TYPE = "组织类型";
    public static final String DATA_TYPE_ORG_DUTY = "职务";
    public static final String DATA_TYPE_ORG_RANK = "职级";
    public static final String[] DATA_TYPE_ORG_VERSION_TYPE = new String[]{"组织节点", "业务单位", "部门", "职位", "系统单位"};
    public static final String DATA_TYPE_FLOW = "流程数据";
    public static final String DATA_TYPE_FLOW_ALL_FILE_NAME = "流程数据_已办结";
    public static final String DATA_TYPE_FLOW_ALL = "全部已办结流程";
    public static final String DATA_TYPE_MAIL = "邮件";
    public static final String DATA_TYPE_MAIL_EMAIL = "邮件";
    public static final String DATA_TYPE_MAIL_FOLDER = "文件夹";
    public static final String DATA_TYPE_MAIL_CONTACT_GROUP = "联系人分组";
    public static final String DATA_TYPE_MAIL_CONTACT = "联系人";
    public static final String DATA_TYPE_MAIL_TAG = "自定义标签";


    public static final String DATA_TYPE_ORG_FOLDER_NAME = "组织数据_附件";

    /**
     * 数据导入导出开关：开
     */
    public static final String DATA_SWITCH_ON = "1";

    /**
     * 数据导入导出任务状态
     */
    public static final Integer DATA_STATUS_CANCEL = 0;
    public static final Integer DATA_STATUS_FINISH = 1;
    public static final Integer DATA_STATUS_DOING = 2;
    public static final Integer DATA_STATUS_ERROR = 3;

    public static final String DATA_STATUS_CANCEL_CN = "取消";
    public static final String DATA_STATUS_FINISH_CN = "完成";
    public static final String DATA_STATUS_EXPORT_DOING_CN = "导出中";
    public static final String DATA_STATUS_ERROR_CN = "异常终止";

    public static final String BEGIN_PROCESS = "开始执行";
    public static final String TERMINATED = "已终止";
    public static final String APPLICATION_ERROR = "程序异常";
    public static final String EXIST_DATA_ERROR = "存在数据异常";
    public static final String DATA_TYPE_NO_MATCH = "数据类型不匹配";
    public static final String PROCESS_LOG_END = "。";
    public static final String EXPORT = "导出";
    public static final String IMPORT = "导入";
    public static final String TASK_EXECUTE_RESULT = "任务执行结果";
    public static final String DATA_EXPORT_SUCCESS = "数据导出完成";
    public static final String DATA_EXPOPT_ERROR = "数据导出终止";
    public static final String DATA_IMPORT_SUCCESS = "数据导入完成";
    public static final String DATA_IMPORT_ERROR = "数据导入终止";
    public static final String ALREADY_EXPORT_TO = "已成功导出至服务器以下目录";

    public static final String READ_DATA_PACKAGE_SUCCESS = "开始导入";
    public static final String READ_DATA_PACKAGE_ERROR = "未读取到数据包，请确认数据路径是否正确";

    public static final Integer INTEGER_TRUE = 1;
    public static final String DATA_TYPE_ORG_DATA = "org_data";
    public static final String DATA_TYPE_FLOW_DATA = "flow_data";
    public static final String DATA_TYPE_MAIL_DATA = "email_data";
    public static final String DATA_TYPE_MAIL_EMAIL_CODE = "email";

    /**
     * 数据导入导出任务日志状态
     */
    public static final Integer DATA_LOG_STATUS_ERROR = 0;
    public static final Integer DATA_LOG_STATUS_NORMAL = 1;

    public static final String DATA_LOG_STATUS_ERROR_CN = "失败";
    public static final String DATA_LOG_STATUS_NORMAL_CN = "正常";

    /**
     * 重复策略：替换
     */
    public static final Integer DATA_REPEAT_STRATEGY_REPLACE = 1;

    /**
     * 重复策略：跳过
     */
    public static final Integer DATA_REPEAT_STRATEGY_SKIP = 2;

    /**
     * 异常策略：终止
     */
    public static final Integer DATA_ERROR_STRATEGY_END = 1;

    /**
     * 异常策略：跳过
     */
    public static final Integer DATA_ERROR_STRATEGY_SKIP = 2;

    /**
     * 导入导出操作类型
     */
    public static final String DATA_TYPE_OPERATE_EXPORT = "export";
    public static final String DATA_TYPE_OPERATE_IMPORT = "import";

    /**
     * 统计数据类型
     */
    public static final String DATA_TYPE_COUNT_REPEAT = "repeat";
    public static final String DATA_TYPE_COUNT_EXCEPTION = "exception";
    public static final String DATA_TYPE_COUNT_TOTAL = "total";


    /**
     * 获取勾选的导入类型
     *
     * @return
     */
    public static List<String> getChoiceImportType(String dataTypeJson, String dataType) {
        List<String> types = Lists.newArrayList();
        List<String> jsonTypes;
        JSONObject jsonObject = JSONObject.fromObject(dataTypeJson);

        switch (dataType) {
            case DATA_TYPE_ORG:
                jsonObject = jsonObject.getJSONObject("org_data");
                jsonTypes = Lists.newArrayList("version", "user", "group", "type", "duty", "rank");
                for (String str : jsonTypes) {
                    if (jsonObject.get(str).toString().equals("1")) {
                        types.add(getDataTypeName(str));
                    }
                }
                break;
            case DATA_TYPE_FLOW:
                if (jsonObject.get("flow_data").toString().equals("1")) {
                    types.add(DATA_TYPE_FLOW_ALL);
                }
                break;
            case DATA_TYPE_MAIL:
                jsonObject = jsonObject.getJSONObject("email_data");
                types.add(DATA_TYPE_MAIL_EMAIL);
                jsonTypes = Lists.newArrayList("folder", "contact_group", "contact", "tag");
                for (String str : jsonTypes) {
                    if (jsonObject.get(str).toString().equals("1")) {
                        types.add(getDataTypeName(str));
                    }
                }
                break;
        }

        return types;
    }

    /**
     * 获取数据类型
     */
    public static Map<String, List<String>> getDataType(String dataType) {
        Map<String, List<String>> dataTypeMap = new LinkedHashMap<>();
        List<String> orgList = new ArrayList<>();
        if (DataExportConstants.IMPORT.equals(dataType)) {
            orgList.add(DATA_TYPE_ORG_TREE_NODE);
        } else {
            orgList.add(DATA_TYPE_ORG_VERSION);
        }

        orgList.add(DATA_TYPE_ORG_USER);
        orgList.add(DATA_TYPE_ORG_GROUP);
        orgList.add(DATA_TYPE_ORG_TYPE);
        orgList.add(DATA_TYPE_ORG_DUTY);
        orgList.add(DATA_TYPE_ORG_RANK);
        dataTypeMap.put(DATA_TYPE_ORG, orgList);
        List<String> flowList = new ArrayList<>();
        flowList.add(DATA_TYPE_FLOW_ALL);
        dataTypeMap.put(DATA_TYPE_FLOW, flowList);
        List<String> mailList = new ArrayList<>();
        mailList.add(DATA_TYPE_MAIL_EMAIL);
        mailList.add(DATA_TYPE_MAIL_FOLDER);
        mailList.add(DATA_TYPE_MAIL_CONTACT_GROUP);
        mailList.add(DATA_TYPE_MAIL_CONTACT);
        mailList.add(DATA_TYPE_MAIL_TAG);
        // mailList.add("信纸");
        dataTypeMap.put(DATA_TYPE_MAIL, mailList);
        return dataTypeMap;
    }

    public static String getDataTypeName(String subDataType, String dataType) {
        Map<String, String> map = new HashMap<>();

        if (IMPORT.equals(dataType)) {
            map.put("version", DATA_TYPE_ORG_TREE);
        } else {
            map.put("version", DATA_TYPE_ORG_VERSION);
        }

        map.put("user", DATA_TYPE_ORG_USER);
        map.put("group", DATA_TYPE_ORG_GROUP);
        map.put("type", DATA_TYPE_ORG_TYPE);
        map.put("duty", DATA_TYPE_ORG_DUTY);
        map.put("rank", DATA_TYPE_ORG_RANK);
        map.put("email", DATA_TYPE_MAIL_EMAIL);
        map.put("folder", DATA_TYPE_MAIL_FOLDER);
        map.put("contact_group", DATA_TYPE_MAIL_CONTACT_GROUP);
        map.put("contact", DATA_TYPE_MAIL_CONTACT);
        map.put("tag", DATA_TYPE_MAIL_TAG);
        return map.get(subDataType);
    }

    public static String getDataTypeName(String subDataType) {
        Map<String, String> map = new HashMap<>();

        map.put("version", DATA_TYPE_ORG_TREE_NODE);
        map.put("user", DATA_TYPE_ORG_USER);
        map.put("group", DATA_TYPE_ORG_GROUP);
        map.put("type", DATA_TYPE_ORG_TYPE);
        map.put("duty", DATA_TYPE_ORG_DUTY);
        map.put("rank", DATA_TYPE_ORG_RANK);
        map.put("email", DATA_TYPE_MAIL_EMAIL);
        map.put("folder", DATA_TYPE_MAIL_FOLDER);
        map.put("contact_group", DATA_TYPE_MAIL_CONTACT_GROUP);
        map.put("contact", DATA_TYPE_MAIL_CONTACT);
        map.put("tag", DATA_TYPE_MAIL_TAG);
        return map.get(subDataType);
    }

    public static String getDataTypeHtml(String type) {
        return "<span class=\"data-import-log-type\">" + type + "</span>";
    }

    public static String getExportLogHtml(DataExportTask task, Integer status, String errorMessage) {
        StringBuffer html = new StringBuffer("<div class= 'data-import-log-item'>");
        switch (status) {
            case 0:// 取消
                html.append(DateUtils.formatDateTime(new Date()) + Separator.SPACE.getValue() + DATA_STATUS_CANCEL_CN + DataExportConstants.getDataTypeHtml(task.getDataType()) + EXPORT + PROCESS_LOG_END);
                break;
            case 1:// 完成
                html.append(DateUtils.formatDateTime(task.getFinishTime()) + Separator.SPACE.getValue() + DATA_STATUS_FINISH_CN + DataExportConstants.getDataTypeHtml(task.getDataType()) + EXPORT + PROCESS_LOG_END);
                break;
            case 2:// 执行
                html.append(DateUtils.formatDateTime(new Date()) + Separator.SPACE.getValue() + BEGIN_PROCESS + DataExportConstants.getDataTypeHtml(task.getDataType()) + EXPORT + PROCESS_LOG_END);
                break;
            case 3:// 异常
                if (StringUtils.isNotBlank(errorMessage)) {
                    html.append(DateUtils.formatDateTime(new Date()) + Separator.SPACE.getValue() + errorMessage + "，" + TERMINATED + getDataTypeHtml(task.getDataType()) + EXPORT + PROCESS_LOG_END);
                } else {
                    html.append(DateUtils.formatDateTime(new Date()) + Separator.SPACE.getValue() + APPLICATION_ERROR + "，" + TERMINATED + getDataTypeHtml(task.getDataType()) + EXPORT + PROCESS_LOG_END);
                }
                break;
            default:
                break;
        }
        html.append("</div>");
        return html.toString();
    }

    public static String getImportLogHtml(DataImportTask task, Integer status, String errorMessage) {
        StringBuffer html = new StringBuffer("<div class= 'data-import-log-item'>");

        switch (status) {
            case 0:// 取消
                html.append(DateUtils.formatDateTime(new Date()) + Separator.SPACE.getValue() + DataExportConstants.DATA_STATUS_CANCEL_CN + DataExportConstants.getDataTypeHtml(task.getDataType()) + DataExportConstants.IMPORT + DataExportConstants.PROCESS_LOG_END);
                break;
            case 1:// 完成
                html.append(DateUtils.formatDateTime(task.getFinishTime()) + Separator.SPACE.getValue() + DATA_STATUS_FINISH_CN + DataExportConstants.getDataTypeHtml(task.getDataType()) + IMPORT + PROCESS_LOG_END);
                break;
            case 2:// 执行
                html.append(DateUtils.formatDateTime(task.getImportTime()) + Separator.SPACE.getValue() + BEGIN_PROCESS + DataExportConstants.getDataTypeHtml(task.getDataType()) + IMPORT + PROCESS_LOG_END);
                break;
            case 3:// 异常
                if (StringUtils.isNotBlank(errorMessage)) {
                    html.append(DateUtils.formatDateTime(new Date()) + Separator.SPACE.getValue() + errorMessage + Separator.COMMA.getValue() + TERMINATED + DataExportConstants.getDataTypeHtml(task.getDataType()) + IMPORT + PROCESS_LOG_END);
                } else {
                    html.append(DateUtils.formatDateTime(new Date()) + Separator.SPACE.getValue() + EXIST_DATA_ERROR + Separator.COMMA.getValue() + TERMINATED + DataExportConstants.getDataTypeHtml(task.getDataType()) + IMPORT + PROCESS_LOG_END);
                }
                break;
        }

        html.append("</div>");
        return html.toString();
    }

}
