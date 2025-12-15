/*
 * @(#)2018年3月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailSignatureDto;
import com.wellsoft.pt.webmail.service.WmMailSignatureService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 邮件写信模板内容解析器
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月12日.1	chenqiong		2018年3月12日		Create
 * </pre>
 * @date 2018年3月12日
 */
public class MailTemplateExplainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailTemplateExplainer.class);

    private static Pattern p = Pattern.compile("\\{个人签名\\..+\\}|\\{SIGN\\..+\\}");// 正则表达式

    public static final String renderMailTemplateContent(String templateName, String content) {
        Configuration cfg = new Configuration();
        StringTemplateLoader loader = new StringTemplateLoader();
        Map<String, Object> root = rootParam();
        loader.putTemplate(templateName, dealSignatureContent(root, content));
        cfg.setTemplateLoader(loader);
        cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        Writer writer = new StringWriter();
        try {
            Template template = cfg.getTemplate(templateName, Encoding.UTF8.getValue());
            template.process(root, writer);
            writer.flush();
        } catch (Exception e) {
            LOGGER.error("写信模板解析器内容解析异常：", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return writer.toString();
    }

    private static final Map<String, Object> rootParam() {
        Map<String, Object> param = Maps.newHashMap();
        param.put("BODY", "</br></br>");// 占位符
        param.put("内容", "</br></br>");
        try {
            UserDetails detail = SpringSecurityUtils.getCurrentUser();
            param.put("发件人", detail.getUserName());
            param.put("FROM", detail.getUserName());
            param.put("部门", StringUtils.isNotBlank(detail.getMainDepartmentName()) ? detail.getMainDepartmentName()
                    : "");
            param.put("DEPT", param.get("部门"));
            param.put("岗位", StringUtils.isNotBlank(detail.getMainJobName()) ? detail.getMainJobName() : "");
            param.put("DUTY", param.get("岗位"));
        } catch (Exception e) {
            LOGGER.error("邮件模板内容用户信息解析异常：", e);
        }

        Calendar now = Calendar.getInstance();
        param.put("年", String.valueOf(now.get(Calendar.YEAR)));
        param.put("YEAR", String.valueOf(now.get(Calendar.YEAR)));
        param.put("月", now.get(Calendar.MONTH) + 1);
        param.put("MONTH", now.get(Calendar.MONTH) + 1);
        param.put("日", now.get(Calendar.DAY_OF_MONTH));
        param.put("DAY", now.get(Calendar.DAY_OF_MONTH));
        param.put("时", now.get(Calendar.HOUR_OF_DAY));
        param.put("HOUR", now.get(Calendar.HOUR_OF_DAY));
        param.put("分", now.get(Calendar.MINUTE));
        param.put("MINUTE", now.get(Calendar.MINUTE));
        param.put("秒", now.get(Calendar.SECOND));
        param.put("SECOND", now.get(Calendar.SECOND));
        return param;
    }

    private static String dealSignatureContent(Map<String, Object> param, String content) {
        if (content.indexOf("个人签名.") != -1 || content.indexOf("SIGNATURE.") != -1) {
            try {
                Matcher m = p.matcher(content);
                String signName = null;
                while (m.find()) {
                    signName = m.group(0).substring(1, m.group(0).indexOf("}"));
                    String[] splits = signName.split("\\.");
                    if (splits.length == 2) {
                        signName = splits[1];
                        break;
                    }
                }
                if (signName != null) {
                    WmMailSignatureService mailtemplateService = ApplicationContextHolder
                            .getBean(WmMailSignatureService.class);
                    WmMailSignatureDto dto = mailtemplateService.getMailSignatureByNameAndUserId(signName,
                            SpringSecurityUtils.getCurrentUserId());
                    if (dto != null) {
                        content = content.replace("${个人签名." + signName + "}", "${signature}");
                        content = content.replace("${SIGN." + signName + "}", "${signature}");
                        String signatureHtml = "<div class=\"well_sign_div\"><div class=\"mail_sign_split\" style=\"color:#909090;font-family:Arial Narrow;font-size:12px;\">---------------------</div>"
                                + "<div id=\"well_signature\" sign_uuid=\""
                                + dto.getUuid()
                                + "\">"
                                + dto.getSignatureContent() + "</div>" + "</div>";
                        param.put("signature", signatureHtml);
                    } else {
                        param.put("signature", "");
                        content = content.replace("${个人签名." + signName + "}", "${signature}");
                        content = content.replace("${SIGN." + signName + "}", "${signature}");
                        LOGGER.warn("未查询到签名名称[{}]的签名内容", signName);
                    }
                    return content;
                }

            } catch (Exception e) {
                LOGGER.error("写信模板解析个人签名异常：", e);
            }
        }

        return content;

    }

}
