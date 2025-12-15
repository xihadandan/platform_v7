/*
 * @(#)2014-3-5 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-5.1	Administrator		2014-3-5		Create
 * </pre>
 * @date 2014-3-5
 */
public class PrinttemplateDateUtil {

    protected static Logger logger = LoggerFactory.getLogger(PrinttemplateDateUtil.class);

    /**
     * word套打去掉内容主体中的html标签
     *
     * @param sourceMap
     * @return
     */
    public static Map<String, List<Map<String, Object>>> getPrintText(Map<String, List<Map<String, Object>>> sourceMap) {
        for (String formId : sourceMap.keySet()) {
            List<Map<String, Object>> formDatas = sourceMap.get(formId);
            if (formDatas == null || formDatas.isEmpty()) {
                continue;
            }
            for (Map<String, Object> formData : formDatas) {
                for (String fieldName : formData.keySet()) {
                    // 信息格式清除HTML标签的字段值
                    Object recordFieldValue = formData.get(fieldName);
                    if (recordFieldValue == null) {
                        continue;
                    }
                    String inputString = null;
                    try {
                        if (recordFieldValue instanceof Clob) {
                            inputString = IOUtils.toString(((Clob) recordFieldValue).getCharacterStream());
                        } else if (recordFieldValue instanceof String) {
                            inputString = (String) recordFieldValue;
                        } else {
                            continue;
                        }
                        String textValue = TextUtils.html2Text("<div>" + inputString + "</div>");
                        formData.put(fieldName, textValue);
                    } catch (IOException ex) {
                        logger.warn(ex.getMessage(), ex);
                    } catch (SQLException ex) {
                        logger.warn(ex.getMessage(), ex);
                    }
                }
            }
        }
        return sourceMap;
    }

    public static Map<String, Object> getPrinttemplateMap(String main_form_id,
                                                          Map<String, List<Map<String, Object>>> sourceMap) {
        return getPrinttemplateMap(main_form_id, sourceMap, true);
    }

    /**
     * word套打去掉指定主表内容主体中的html标签
     *
     * @param main_form_id
     * @param sourceMap
     * @return
     */
    public static Map<String, Object> getPrinttemplateMap(String main_form_id,
                                                          Map<String, List<Map<String, Object>>> sourceMap, boolean cleanHtml) {
        Map<String, Object> printtemplateMap = new HashMap<String, Object>();// 传入打印模板接口map集合
        for (String key : sourceMap.keySet()) {
            // 如果为主表formId
            if (key.equals(main_form_id)) {
                Map<String, Map<String, Object>> dataMap1 = new HashMap<String, Map<String, Object>>();
                // 获取对应的数据集合
                List<Map<String, Object>> dataMapList = sourceMap.get(key);
                Map<String, Object> dataMap2 = dataMapList.get(0);// 主表的话List中只有一条数据
                for (String fieldName : dataMap2.keySet()) {
                    // 信息格式清除HTML标签的字段值
                    Object recordFieldValue = dataMap2.get(fieldName);
                    if (recordFieldValue != null) {
                        String inputString = null;
                        try {
                            if (recordFieldValue instanceof Clob) {
                                inputString = IOUtils.toString(((Clob) recordFieldValue).getCharacterStream());
                            } else if (recordFieldValue instanceof String) {
                                inputString = (String) recordFieldValue;
                            } else {
                                continue;
                            }
                            inputString = inputString.replaceAll("</p>", "</p> \n");
                            inputString = inputString.replaceAll("<br>", " \n");
                            String textValue = cleanHtml == false ? inputString : TextUtils.html2Text("<div>"
                                    + inputString + "</div>");
                            dataMap2.put(fieldName, textValue);
                        } catch (IOException ex) {
                            logger.warn(ex.getMessage(), ex);
                        } catch (SQLException ex) {
                            logger.warn(ex.getMessage(), ex);
                        }
                    }
                }
                dataMap1.put(key, dataMap2);
                printtemplateMap.putAll(dataMap1);
            } else {
                List<Map<String, Object>> dataMapList = sourceMap.get(key);
                for (Map<String, Object> formData : dataMapList) {
                    for (String fieldName : formData.keySet()) {
                        // 信息格式清除HTML标签的字段值
                        Object recordFieldValue = formData.get(fieldName);
                        if (recordFieldValue != null) {
                            String inputString = null;
                            try {
                                if (recordFieldValue instanceof Clob) {
                                    inputString = IOUtils.toString(((Clob) recordFieldValue).getCharacterStream());
                                } else if (recordFieldValue instanceof String) {
                                    inputString = (String) recordFieldValue;
                                } else {
                                    continue;
                                }
                                inputString = inputString.replaceAll("</p>", "</p> \n");
                                inputString = inputString.replaceAll("<br>", " \n");
                                String textValue = cleanHtml == false ? inputString : TextUtils.html2Text("<div>"
                                        + inputString + "</div>");
                                formData.put(fieldName, textValue);
                            } catch (IOException ex) {
                                logger.warn(ex.getMessage(), ex);
                            } catch (SQLException ex) {
                                logger.warn(ex.getMessage(), ex);
                            }
                        }
                    }
                }
                printtemplateMap.put(key, dataMapList);
            }
        }
        return printtemplateMap;
    }
}
