package com.wellsoft.pt.ei.processor.utils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.service.ExpImpService;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @Auther: yt
 * @Date: 2021/9/29 14:47
 * @Description:
 */
public class ExpImpServiceBeanUtils {

    public static final String configName = "_数据说明";
    public static final String attachPath = "_附件";

    public static String configFileName(String rootPath, ExpImpService expImpService) {
        String fileName = rootPath + File.separator + expImpService.filePath() +
                File.separator + expImpService.filePath() + configName +
                File.separator + expImpService.fileName() + configName + ".json";
        return fileName;
    }

    public static String attachPathName(String rootPaht, ExpImpService expImpService) {
        String fileName = rootPaht + File.separator + expImpService.filePath() + File.separator + expImpService.filePath() + attachPath;
        return fileName;
    }

    public static List<ExpImpService> flowExpImpServices(String json) {
        JSONObject type = JSONObject.fromObject(json);
        Object flowDataObject = type.get("flow_data");
        List<ExpImpService> expImpServices = new ArrayList<>();
        if (flowDataObject != null && flowDataObject.toString().contains(DataExportConstants.DATA_SWITCH_ON)) {
            ExpImpService expImpService = ApplicationContextHolder.getBean("flowExpImpService", ExpImpService.class);
            expImpServices.add(expImpService);
        }
        return expImpServices;
    }

    public static List<ExpImpService> orgExpImpServices(String json) {
        JSONObject type = JSONObject.fromObject(json);
        JSONObject orgData = type.getJSONObject("org_data");
        List<ExpImpService> expImpServices = new ArrayList<>();
        String head = "org";
        String tail = "ExpImpService";

        // 导入顺序：组织架构节点>职务>职级>用户>群组
        // String[] services = new String[]{"orgDutyExpImpService", "orgVersionExpImpService", "orgRankExpImpService", "orgUserExpImpService", "orgGroupExpImpService"};
        // for (String service : services) {
        for (Object key : orgData.keySet()) {
            if (DataExportConstants.DATA_SWITCH_ON.equals(orgData.optString(key.toString()))) {
                if ("version".equalsIgnoreCase(key.toString())) {
                    // 组织版本不处理基本信息，都是处理的节点、业务单位、部门、职位、引用系统单位等组织元素的信息
                    addExpImpService(expImpServices, head, tail, key.toString() + "_conf");
                } else {
                    addExpImpService(expImpServices, head, tail, key.toString());
                }
            }
        }
        // }

        expImpServices.sort(new Comparator<ExpImpService>() {
            @Override
            public int compare(ExpImpService o1, ExpImpService o2) {
                return o1.order() - o2.order();
            }
        });
        return expImpServices;
    }

    public static List<ExpImpService> mailExpImpServices(String json) {
        JSONObject type = JSONObject.fromObject(json);
        JSONObject emailData = type.getJSONObject("email_data");
        List<ExpImpService> expImpServices = new ArrayList<>();

        JSONObject emailJson = emailData.getJSONObject("email");
        Set<String> mailBoxSet = Sets.newHashSet();
        for (Object key : emailJson.keySet()) {
            if (DataExportConstants.DATA_SWITCH_ON.equals(emailJson.optString(key.toString()))) {
                mailBoxSet.add(key.toString());
            }
        }
        ExpImpService expImpService = (ExpImpService) ApplicationContextHolder.getApplicationContext().getBean("mailBoxExpImpService", mailBoxSet);
        expImpServices.add(expImpService);

        String head = "mail";
        String tail = "ExpImpService";
        for (Object key : emailData.keySet()) {
            if ("email".equals(key.toString())) {
                continue;
            }
            if (DataExportConstants.DATA_SWITCH_ON.equals(emailData.optString(key.toString()))) {
                addExpImpService(expImpServices, head, tail, key.toString());
            }
        }
        expImpServices.sort(new Comparator<ExpImpService>() {
            @Override
            public int compare(ExpImpService o1, ExpImpService o2) {
                return o1.order() - o2.order();
            }
        });
        return expImpServices;
    }

    public static void addExpImpService(List<ExpImpService> expImpServices, String head, String tail, String key) {
        String serviceName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, key);
        String beanName = head + serviceName + tail;
        if (ApplicationContextHolder.containsBean(beanName)) {
            ExpImpService expImpService = ApplicationContextHolder.getBean(beanName, ExpImpService.class);
            expImpServices.add(expImpService);
        }

    }

    public static String linuxFileNameTooLongConvert(String originFileName) {
        if (StringUtils.isBlank(originFileName)) {
            return StringUtils.EMPTY;
        }

        if (originFileName.length() < 55) {
            return originFileName;
        } else {
            int lastIndex = originFileName.lastIndexOf(".");
            String suffix = StringUtils.EMPTY;
            if (lastIndex > -1) {
                // 有 .
                if (originFileName.substring(lastIndex).length() < 10) {
                    // 后缀10个字符，大于10不算后缀
                    suffix = originFileName.substring(lastIndex);
                } else {
                    lastIndex = originFileName.length();
                }
            } else {
                // 无 .
                lastIndex = originFileName.length();
            }

            String fileName = originFileName.substring(0, lastIndex);
            fileName = fileName.substring(0, 47) + "..." + fileName.substring(fileName.length() - 3);
            return fileName + suffix;
        }

    }
}
